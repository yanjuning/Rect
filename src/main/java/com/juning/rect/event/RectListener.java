package com.juning.rect.event;

import java.util.EventListener;

/**
 * @author yanjun
 */
public interface RectListener<E extends RectEvent> extends EventListener {

    /**
     * handle event
     * @param event
     */
    void onEvent(E event);
}
