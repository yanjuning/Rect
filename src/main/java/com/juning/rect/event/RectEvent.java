package com.juning.rect.event;

import java.util.EventObject;

/**
 * @author yanjun
 */
public class RectEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public RectEvent(Object source) {
        super(source);
    }
}