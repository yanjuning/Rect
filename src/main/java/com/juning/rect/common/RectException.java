package com.juning.rect.common;

import java.util.concurrent.TimeoutException;

/**
 * @author yanjun
 */
public class RectException extends Exception {
    private static final long serialVersionUID = -3130468665510098625L;

    private byte code;
    private String msg;

    public RectException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public RectException(byte code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public RectException(String message, Throwable cause) {
        super(message, cause);
        this.msg = msg;
    }

    public RectException(Throwable cause) {
        super(cause);
    }

    /** getters and setters **/
    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
