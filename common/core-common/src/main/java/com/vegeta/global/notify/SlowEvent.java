package com.vegeta.global.notify;

/**
 * This event share one event-queue.
 *
 * @author fuzhiqiang
 */
@SuppressWarnings("all")
public abstract class SlowEvent extends Event {
    
    @Override
    public long sequence() {
        return 0;
    }
}