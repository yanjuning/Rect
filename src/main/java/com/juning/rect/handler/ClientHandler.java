package com.juning.rect.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import com.juning.rect.model.Request;
import com.juning.rect.model.Response;
import com.juning.rect.reactor.support.DefaultRectClient;

import static com.juning.rect.model.RectCode.HEARTBEAT;

/**
 * @author yanjun
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<Response> {

    private DefaultRectClient rectClient;

    public ClientHandler(DefaultRectClient rectClient) {
        this.rectClient = rectClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        rectClient.addResponse(response);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                // do nothing
            } else if (event.state() == IdleState.WRITER_IDLE) {
                // close
                Request request = new Request();
                request.setType(HEARTBEAT);
                ctx.channel().writeAndFlush(request);
            }
        }
    }
}