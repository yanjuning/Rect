package com.juning.rect.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author yanjun
 */
public class Request implements Message, Serializable {
    private static final long serialVersionUID = 2501458116353175656L;

    /**
     * request id = 唯一标识
     */
    private String reqId;
    private byte type;
    private Serializable data;

    public Request() {
    }

    public Request(Serializable data) {
        this.reqId = UUID.randomUUID().toString();
        this.data = data;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getReqId() {
        return reqId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Serializable data) {
        this.data = data;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }
}
