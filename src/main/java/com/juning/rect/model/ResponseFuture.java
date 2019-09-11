package com.juning.rect.model;

import com.juning.rect.reactor.CallBack;

import java.util.concurrent.*;

/**
 * @author yanjun
 */
public class ResponseFuture {
    private Request request;
    private Response response;
    private CallBack callBack;

    private CountDownLatch latch = new CountDownLatch(1);

    public ResponseFuture(Request request, CallBack callBack) {
        this.request = request;
        this.callBack = callBack;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 响应时机，channelfuture请求失败，连接中断，请求抛出异常
     * @param response
     */
    public void set(Response response) {
        this.response = response;
        this.latch.countDown();
    }

    public Response get() throws InterruptedException, ExecutionException {
        latch.await();
        if (callBack != null) {
            callBack.call(response);
        }
        return response;
    }

    public Response get(long timeout, TimeUnit unit) throws InterruptedException {
        latch.await(timeout, unit);
        if (callBack != null) {
            callBack.call(response);
        }
        return response;
    }
}
