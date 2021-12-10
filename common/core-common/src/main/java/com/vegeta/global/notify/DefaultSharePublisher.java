package com.vegeta.global.notify;

import com.google.common.collect.Sets;
import com.vegeta.global.notify.listener.Subscriber;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The default share event publisher implementation for slow event.
 * 慢事件的默认共享事件发布者实现。
 *
 * @author fuzhiqiang
 */
public class DefaultSharePublisher extends DefaultPublisher implements ShardedEventPublisher {

    private final Map<Class<? extends SlowEvent>, Set<Subscriber>> subMappings = new ConcurrentHashMap<>();

    private final Lock lock = new ReentrantLock();

    @Override
    public void addSubscriber(Subscriber subscriber, Class<? extends Event> subscribeType) {
        // Actually, do a classification based on the slowEvent type.
        // 其实就是根据slowEvent类型做一个分类。
        Class<? extends SlowEvent> subSlowEventType = (Class<? extends SlowEvent>) subscribeType;
        // For stop waiting subscriber, see {@link DefaultPublisher#openEventHandler}.
        subscribers.add(subscriber);

        // 加锁  把订阅者加入内存中
        lock.lock();
        try {
            Set<Subscriber> sets = subMappings.get(subSlowEventType);
            if (Objects.isNull(sets)) {
                Set<Subscriber> newSet = Sets.newConcurrentHashSet();
                newSet.add(subscriber);
                subMappings.put(subSlowEventType, newSet);
                return;
            }
            sets.add(subscriber);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeSubscriber(Subscriber subscriber, Class<? extends Event> subscribeType) {
        // Actually, do a classification based on the slowEvent type.
        Class<? extends SlowEvent> subSlowEventType = (Class<? extends SlowEvent>) subscribeType;
        // For removing to parent class attributes synchronization.
        subscribers.remove(subscriber);

        lock.lock();
        try {
            Set<Subscriber> sets = subMappings.get(subSlowEventType);

            if (sets != null) {
                sets.remove(subscriber);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void receiveEvent(Event event) {

        final long currentEventSequence = event.sequence();
        // get subscriber set based on the slow EventType.
        final Class<? extends SlowEvent> slowEventType = (Class<? extends SlowEvent>) event.getClass();

        // Get for Map, the algorithm is O(1).
        Set<Subscriber> subscribers = subMappings.get(slowEventType);
        if (null == subscribers) {
            LOGGER.debug("[NotifyCenter] No subscribers for slow event {}", slowEventType.getName());
            return;
        }

        // Notification single event subscriber
        for (Subscriber subscriber : subscribers) {
            // Whether to ignore expiration events
            if (subscriber.ignoreExpireEvent() && lastEventSequence > currentEventSequence) {
                LOGGER.debug("[NotifyCenter] the {} is unacceptable to this subscriber, because had expire", event.getClass());
                continue;
            }

            // Notify single subscriber for slow event.
            notifySubscriber(subscriber, event);
        }
    }
}
