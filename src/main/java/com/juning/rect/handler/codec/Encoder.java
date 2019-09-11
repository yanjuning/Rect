package com.juning.rect.handler.codec;

import com.juning.rect.model.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import com.juning.rect.common.RectException;
import com.juning.rect.common.serialization.Serializer;
import com.juning.rect.common.serialization.support.FastJsonSerializer;
import com.juning.rect.model.Request;

import static com.juning.rect.model.RectCode.REQUEST;
import static com.juning.rect.model.RectCode.RESPONSE;

/**
 * @author yanjun
 */
public class Encoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Serializer serializer = FastJsonSerializer.getSerializer();
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        if (msg instanceof Request) {
            out.writeByte(REQUEST);
        } else if (msg instanceof Response){
            out.writeByte(RESPONSE);
        } else {
            throw new RectException("rect cannot recognize msg=" + msg);
        }
        out.writeBytes(bytes);
    }
}