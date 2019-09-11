package com.juning.rect.reactor.support;

import com.juning.rect.common.Utils;
import com.juning.rect.handler.ClientHandler;
import com.juning.rect.handler.codec.Encoder;
import com.juning.rect.model.Request;
import com.juning.rect.model.Response;
import com.juning.rect.model.ResponseFuture;
import com.juning.rect.reactor.CallBack;
import com.juning.rect.reactor.RectClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.juning.rect.common.RectException;
import com.juning.rect.handler.codec.Decoder;
import com.juning.rect.reactor.config.RectClientConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yanjun
 */
public class DefaultRectClient implements RectClient {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRectClient.class);

    private final Bootstrap bootstrap;
    private final RectClientConfig config;
    private EventLoopGroup selectorEventLoopGroup;
    private EventLoopGroup taskEventLoopGroup;

    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    private final Map<String, ResponseFuture> responseMap = new ConcurrentHashMap<>();
    private final Semaphore asyncSemphore;
    private final Semaphore onewaySemphore;
    private Thread shutdownHook;

    public DefaultRectClient(final RectClientConfig confg) {
        this.bootstrap = new Bootstrap();
        this.config = confg;
        this.asyncSemphore = new Semaphore(confg.getMaxAsyncRequest());
        this.onewaySemphore = new Semaphore(confg.getMaxOnewayRequest());
    }

    @Override
    public void start() {
        selectorEventLoopGroup = new NioEventLoopGroup(config.getSelectorEventLoopGroupThreads(), new ThreadFactory() {
            private AtomicInteger tid = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("rectclient,selectorEventLoopGroup-%d", tid.getAndIncrement()));
            }
        });
        taskEventLoopGroup = new NioEventLoopGroup(config.getTaskEventLoopGroupThreads(), new ThreadFactory() {
            private AtomicInteger tid = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("rectclient,taskEventLoopGroup-%d", tid.getAndIncrement()));
            }
        });
        ClientHandler clientHandler = new ClientHandler(this);
        bootstrap.group(selectorEventLoopGroup)
                .channel(determainSocketChannel())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeOutMills())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(taskEventLoopGroup)
                                .addLast("decode", new Decoder())
                                .addLast("encode", new Encoder())
                                .addLast("idleHandler", new IdleStateHandler(config.getReaderIdleTimeSeconds(),
                                        config.getWriterIdleTimeSeconds(), 0))
                                .addLast("rectclienthandler", clientHandler);
                    }
                });
        this.registerShutdownHook(shutdownHook);
    }

    @Override
    public Response invokeSync(final String address, final Request req, long timeout, TimeUnit timeUnit)
            throws RectException {
        try {
            ResponseFuture responseFuture = new ResponseFuture(req, null);
            responseMap.put(req.getReqId(), responseFuture);
            Channel channel = getChannel(address);
            channel.writeAndFlush(req).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        responseMap.remove(req.getReqId());
                        responseFuture.set(null);
                    }
                }
            });
            Response response = responseFuture.get(timeout, timeUnit);
            responseMap.remove(req.getReqId());
            return response;
        } catch (Exception e) {
            throw new RectException(e);
        }
    }

    @Override
    public void invokeAsync(final String address, final Request req, long timeout, TimeUnit timeUnit, CallBack callBack)
            throws RectException {
        try {
            if (!asyncSemphore.tryAcquire(1)) {
                throw new RectException("async requests exceed " + config.getMaxAsyncRequest());
            }
            Channel channel = getChannel(address);
            ResponseFuture responseFuture = new ResponseFuture(req, callBack);
            responseMap.put(req.getReqId(), responseFuture);
            channel.writeAndFlush(req).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        responseMap.remove(req.getReqId());
                        responseFuture.set(null);
                        logger.error(String.format("invoke async fail. Req:%s. Channel:%s", req, future));
                    }
                    asyncSemphore.release(1);
                }
            });
        } catch (Exception e) {
            throw new RectException(e);
        }
    }

    @Override
    public void invokeOneway(final String address, final Request req) throws RectException {
        try {
            if(!onewaySemphore.tryAcquire(1)) {
                throw new RectException("oneway requests reach" + config.getMaxOnewayRequest());
            }
            Channel channel = getChannel(address);
            channel.writeAndFlush(req).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                    } else {
                        logger.warn("request failed");
                    }
                    onewaySemphore.release(1);
                }
            });
        } catch (Exception e) {
            throw new RectException(e);
        }
    }

    private Class<? extends SocketChannel> determainSocketChannel() {
        return NioSocketChannel.class;
    }

    /**
     * 实现连接多路复用（multiplexing），单个服务端地址使用单连接，可优化
     * @param address 服务端地址
     * @return
     * @throws InterruptedException
     */
    private Channel getChannel(final String address) throws RectException, InterruptedException {
        Channel channel = channelMap.get(address);
        if (channel == null || !channel.isActive()) {
            synchronized (this) {
                if (channel == null || !channel.isActive()) {
                    ChannelFuture channelFuture = bootstrap.connect(Utils.toInetSocketAddress(address)).sync();
                    channel = channelFuture.channel();
                    channelMap.put(address, channel);
                    return channel;
                }
            }
        }
        return channel;
    }

    /**
     * 写入响应结果
     * @param response
     */
    public void addResponse(Response response) {
        ResponseFuture responseFuture = this.responseMap.get(response.getReqId());
        if (responseFuture != null) {
            responseFuture.set(response);
            if (responseFuture.getCallBack() != null) {
                responseFuture.getCallBack().call(response);
            }
        }
    }

    @Override
    public void stop() {
        if (selectorEventLoopGroup != null && !selectorEventLoopGroup.isShutdown()) {
            selectorEventLoopGroup.shutdownGracefully();
        }
        if (taskEventLoopGroup != null && !taskEventLoopGroup.isShutdown()) {
            taskEventLoopGroup.shutdownGracefully();
        }
    }

    public RectClientConfig getConfig() {
        return config;
    }
}
