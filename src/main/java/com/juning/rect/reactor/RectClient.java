package com.juning.rect.reactor;

import com.juning.rect.model.Response;
import com.juning.rect.common.RectException;
import com.juning.rect.model.Request;

import java.util.concurrent.TimeUnit;

/**
 * @author yanjun
 */
public interface RectClient extends LifeCycle {

    /**
     * 同步调用
     * @param address
     * @param req
     * @param timeout
     * @param timeUnit
     * @return
     * @throws RectException
     */
    public Response invokeSync(final String address, final Request req, long timeout, TimeUnit timeUnit)
            throws RectException;

    /**
     * 异步调用
     * @param address
     * @param req
     * @param timeout
     * @param timeUnit
     * @param callBack
     * @throws RectException
     */
    public void invokeAsync(final String address, final Request req, long timeout, TimeUnit timeUnit, CallBack callBack)
            throws RectException;

    /**
     * 单向调用
     * @param address
     * @param req
     * @throws RectException
     */
    public void invokeOneway(final String address, final Request req) throws RectException;
}
