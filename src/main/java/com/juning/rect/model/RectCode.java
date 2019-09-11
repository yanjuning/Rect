package com.juning.rect.model;

/**
 * @author yanjun
 */
public class RectCode {

    /**
     * 消息类型
     **/
    public static final byte REQUEST = 0x01;
    public static final byte RESPONSE = 0x02;
    public static final byte HEARTBEAT = 0x03;

    /**
     * 请求方式
     **/
    public static final byte SYNC = 0x11;
    public static final byte ASYNC = 0x12;
    public static final byte ONE_WAY = 0x13;

    /**
     * 响应结果
     **/
    public static final byte OK = 0x21;
    public static final byte BAD_REQUEST = 0x22;
    public static final byte SERVER_ERROR = 0x23;
}