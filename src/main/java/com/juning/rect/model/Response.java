package com.juning.rect.model;

import java.io.Serializable;

/**
 * @author yanjun
 */
public class Response implements Message, Serializable {
    private static final long serialVersionUID = 6221808291782507973L;

    private String reqId;
    private byte status;
    private String msg;
    private Serializable data;

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Serializable data) {
        this.data = data;
    }

}
