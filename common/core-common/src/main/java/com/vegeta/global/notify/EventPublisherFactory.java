package com.vegeta.global.notify;

import java.util.function.BiFunction;

/**
 * Event publisher factory.
 *
 * @author 付志强
 */
public interface EventPublisherFactory extends BiFunction<Class<? extends Event>, Integer, EventPublisher> {

    /**
     * Build an new {@link EventPublisher}.
     * 函数式接口编程
     *
     * @param eventType    eventType for {@link EventPublisher}
     * @param maxQueueSize max queue size for {@link EventPublisher}
     * @return new {@link EventPublisher}
     */
    @Override
    EventPublisher apply(Class<? extends Event> eventType, Integer maxQueueSize);
}
