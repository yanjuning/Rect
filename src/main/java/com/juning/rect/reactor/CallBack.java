package com.juning.rect.reactor;

import java.util.concurrent.TimeoutException;

/**
 * @author yanjun
 */
public interface CallBack<T> {

    /**
     * 回调
     * @param t
     */
    void call(T t);

}
