package com.juning.rect.handler.codec;

import com.juning.rect.model.RectCode;
import com.juning.rect.model.Request;
import com.juning.rect.model.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import com.juning.rect.common.RectException;
import com.juning.rect.common.serialization.Serializer;
import com.juning.rect.common.serialization.support.FastJsonSerializer;

import java.util.List;

/**
 * @author yanjun
 */
public class Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < Integer.BYTES) {
            return;
        }
        in.markReaderIndex();
        int length = in.readInt();
        if (in.readableBytes() < Byte.BYTES) {
            in.resetReaderIndex();
            return;
        }
        byte msgType = in.readByte();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[length];
        in.readBytes(data);

        Object res = doDecode(msgType, data);
        out.add(res);
    }

    private Object doDecode(byte msgType, byte[] data) throws RectException {
        Serializer serializer = FastJsonSerializer.getSerializer();
        switch (msgType) {
            case RectCode.REQUEST :
                return serializer.deserialize(Request.class, data);
            case RectCode.RESPONSE :
                return serializer.deserialize(Response.class, data);
            default:
                throw new RectException(RectCode.BAD_REQUEST, "rect cannot recognize msgtype-" + msgType);
        }
    }
}
