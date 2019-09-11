package com.juning.rect.handler;

import com.juning.rect.model.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.juning.rect.common.RectException;
import com.juning.rect.model.RectCode;
import com.juning.rect.model.Request;
import com.juning.rect.reactor.support.DefaultRectServer;

import java.util.concurrent.atomic.AtomicInteger;

import static com.juning.rect.model.RectCode.HEARTBEAT;

/**
 * @author yanjun
 */
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<Request> {
    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private DefaultRectServer rectServer;
    private AtomicInteger counter = new AtomicInteger(0);

    public ServerHandler(DefaultRectServer rectServer) {
        this.rectServer = rectServer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        Response response = new Response();
        response.setReqId(request.getReqId());
        if (HEARTBEAT != request.getType()) {
            try {
                rectServer.dispatch(request, response);
            } catch (RectException rex) {
                response.setStatus(RectCode.SERVER_ERROR);
                response.setMsg(rex.getMessage());
                response.setData(null);
            }
        }
        ctx.writeAndFlush(response);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.close();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                ctx.close();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.error(String.valueOf(counter.incrementAndGet()) + ctx.toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}