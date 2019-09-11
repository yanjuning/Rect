package com.juning.rect.reactor;

import com.juning.rect.model.Request;
import com.juning.rect.model.Response;
import com.juning.rect.common.RectException;
import com.juning.rect.interceptor.RectInterceptor;

/**
 * @author yanjun
 */
public interface RectServer extends LifeCycle {

    /**
     * 注册拦截器，拦截器用于上层应用处理请求和响应
     * @param interceptor
     */
    public void registerInterceptor(RectInterceptor interceptor);

    /**
     * 请求和响应分发
     * @param request
     * @param response
     * @throws RectException
     */
    public void dispatch(Request request, Response response) throws RectException;
}
