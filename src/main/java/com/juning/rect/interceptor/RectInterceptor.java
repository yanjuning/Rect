package com.juning.rect.interceptor;

import com.juning.rect.model.Request;
import com.juning.rect.model.Response;
import com.juning.rect.common.RectException;

/**
 * @author yanjun
 */
public interface RectInterceptor {

    /**
     * continue process interceptor if true
     * @param request
     * @param response
     * @return
     * @throws RectException
     */
    boolean intercept(final Request request, final Response response) throws RectException;

}
