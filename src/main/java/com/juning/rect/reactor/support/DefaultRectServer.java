package com.juning.rect.reactor.support;

import com.juning.rect.handler.ServerHandler;
import com.juning.rect.handler.codec.Encoder;
import com.juning.rect.model.Request;
import com.juning.rect.model.Response;
import com.juning.rect.reactor.config.RectServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.juning.rect.common.RectException;
import com.juning.rect.handler.codec.Decoder;
import com.juning.rect.interceptor.RectInterceptor;
import com.juning.rect.reactor.RectServer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yanjun
 */
public class DefaultRectServer implements RectServer {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRectServer.class);

    private ServerBootstrap serverBootstrap;
    private RectServerConfig config;

    private EventLoopGroup mainEventLoopGroup;
    private EventLoopGroup subEventLoopGroup;
    private EventLoopGroup taskEventLoopGroup;

    private Map<Channel, String> channelMap = new ConcurrentHashMap<>();
    private LinkedList<RectInterceptor> rectInterceptors = new LinkedList<>();
    private Thread shutdownHook;

    public DefaultRectServer(RectServerConfig config) {
        this.serverBootstrap = new ServerBootstrap();
        this.config = config;
    }

    @Override
    public void start() {
        mainEventLoopGroup = new NioEventLoopGroup(config.getMainEventLoopGroupThreads(), new ThreadFactory() {
            AtomicInteger tid = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("rectserver,mainEventLoopGroup-%d", tid.getAndIncrement()));
            }
        });
        subEventLoopGroup = new NioEventLoopGroup(config.getSubEventLoopGroupThreads(), new ThreadFactory() {
            AtomicInteger tid = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("rectserver,subEventLoopGroup-%d", tid.getAndIncrement()));
            }
        });
        taskEventLoopGroup = new NioEventLoopGroup(config.getTaskExectorGroupThreads(), new ThreadFactory() {
            AtomicInteger tid = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("rectserver,taskEventLoopGroup-%d", tid.getAndIncrement()));
            }
        });
        ChannelHandler serverHandler = new ServerHandler(this);
        serverBootstrap.group(mainEventLoopGroup, subEventLoopGroup)
                .channel(determainServerSocketChannel())
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(taskEventLoopGroup)
                                .addLast("decode", new Decoder())
                                .addLast("encode", new Encoder())
                                .addLast("idleStateHandler", new IdleStateHandler(config.getReaderIdleTimeSeconds(),
                                        config.getWriterIdleTimeSeconds(), 0))
                                .addLast("rectclienthandler", serverHandler);
                    }
                });
        try {
            ChannelFuture chanleFuture = serverBootstrap.bind(config.getHost(), config.getPort());
            chanleFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    logger.info("Successfully started rectserver, bind {}:{}", config.getHost(), config.getPort());
                }
            });
            chanleFuture.sync();
            chanleFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    logger.info("Successfully closed rectserver.");
                }
            });
            this.registerShutdownHook(shutdownHook);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * ordered, may be duplicate
     */
    @Override
    public void registerInterceptor(RectInterceptor interceptor) {
        this.rectInterceptors.add(interceptor);
    }

    @Override
    public void dispatch(Request request, Response response) throws RectException {
        // interceptors
        Iterator<RectInterceptor> iterator = rectInterceptors.iterator();
        while (iterator.hasNext()) {
            RectInterceptor interceptor = iterator.next();
            if (!interceptor.intercept(request, response)) {
                break;
            }
        }
    }

    private Class<? extends ServerSocketChannel> determainServerSocketChannel() {
        return NioServerSocketChannel.class;
    }

    @Override
    public void stop() {
        for (Channel channel: channelMap.keySet()) {
            if (channel.isActive()) {
                channel.close();
            }
        }
        if (mainEventLoopGroup != null && !mainEventLoopGroup.isShutdown()) {
            mainEventLoopGroup.shutdownGracefully();
        }
        if (subEventLoopGroup != null && !subEventLoopGroup.isShutdown()) {
            subEventLoopGroup.shutdownGracefully();
        }
        if (taskEventLoopGroup != null && !taskEventLoopGroup.isShutdown()) {
            taskEventLoopGroup.shutdownGracefully();
        }
    }
}
