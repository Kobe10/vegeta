package com.vegeta.global.notify;

import com.vegeta.global.exception.vegeta.VegetaRuntimeException;
import com.vegeta.global.notify.listener.SmartSubscriber;
import com.vegeta.global.notify.listener.Subscriber;
import com.vegeta.global.spi.VegetaCommonServiceLoader;
import com.vegeta.global.util.ClassUtils;
import com.vegeta.global.util.MapUtil;
import com.vegeta.global.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.vegeta.global.exception.vegeta.VegetaException.SERVER_ERROR;


/**
 * 统一事件通知中心
 *
 * @author fuzhiqiang
 */
@Slf4j
public class NotifyCenter {

    public static int ringBufferSize;

    public static int shareBufferSize;

    private static final AtomicBoolean CLOSED = new AtomicBoolean(false);

    private static final EventPublisherFactory DEFAULT_PUBLISHER_FACTORY;

    private static final NotifyCenter INSTANCE = new NotifyCenter();

    private DefaultSharePublisher sharePublisher;

    private static Class<? extends EventPublisher> clazz;

    /**
     * Publisher management container.
     * key --- event class name   value ---  EventPublisher 实现类
     */
    private final Map<String, EventPublisher> publisherMap = new ConcurrentHashMap<>(16);

    static {
        // Internal ArrayBlockingQueue buffer size. For applications with high write throughput,
        // this value needs to be increased appropriately. default value is 16384
        String ringBufferSizeProperty = "com.vegeta.notify.ring-buffer-size";
        ringBufferSize = Integer.getInteger(ringBufferSizeProperty, 16384);

        // The size of the public publisher's message staging queue buffer
        // 公共发布者的消息暂存队列缓冲区的大小
        String shareBufferSizeProperty = "com.vegeta.notify.share-buffer-size";
        shareBufferSize = Integer.getInteger(shareBufferSizeProperty, 1024);

        final Collection<EventPublisher> publishers = VegetaCommonServiceLoader.load(EventPublisher.class);
        Iterator<EventPublisher> iterator = publishers.iterator();

        if (iterator.hasNext()) {
            clazz = iterator.next().getClass();
        } else {
            clazz = DefaultPublisher.class;
        }
        // 函数式接口 实现
        DEFAULT_PUBLISHER_FACTORY = (cls, buffer) -> {
            try {
                EventPublisher publisher = clazz.newInstance();
                publisher.init(cls, buffer);
                return publisher;
            } catch (Throwable ex) {
                log.error("Service class newInstance has error : ", ex);
                throw new VegetaRuntimeException(SERVER_ERROR, ex);
            }
        };
        try {
            // Create and init DefaultSharePublisher instance.
            INSTANCE.sharePublisher = new DefaultSharePublisher();
            INSTANCE.sharePublisher.init(SlowEvent.class, shareBufferSize);
        } catch (Throwable ex) {
            log.error("Service class newInstance has error : ", ex);
        }
        ThreadUtils.addShutdownHook(NotifyCenter::shutdown);
    }

    public static Map<String, EventPublisher> getPublisherMap() {
        return INSTANCE.publisherMap;
    }

    public static EventPublisher getPublisher(Class<? extends Event> topic) {
        if (ClassUtils.isAssignableFrom(SlowEvent.class, topic)) {
            return INSTANCE.sharePublisher;
        }
        return INSTANCE.publisherMap.get(topic.getCanonicalName());
    }

    public static EventPublisher getSharePublisher() {
        return INSTANCE.sharePublisher;
    }

    /**
     * Shutdown the several publisher instance which notify center has.
     */
    public static void shutdown() {
        if (!CLOSED.compareAndSet(false, true)) {
            return;
        }
        log.warn("[NotifyCenter] Start destroying Publisher");

        for (Map.Entry<String, EventPublisher> entry : INSTANCE.publisherMap.entrySet()) {
            try {
                EventPublisher eventPublisher = entry.getValue();
                eventPublisher.shutdown();
            } catch (Throwable e) {
                log.error("[EventPublisher] shutdown has error : ", e);
            }
        }

        try {
            INSTANCE.sharePublisher.shutdown();
        } catch (Throwable e) {
            log.error("[SharePublisher] shutdown has error : ", e);
        }

        log.warn("[NotifyCenter] Destruction of the end");
    }

    /**
     * Register a Subscriber. If the Publisher concerned by the Subscriber does not exist, then PublihserMap will
     * preempt a placeholder Publisher with default EventPublisherFactory first.、
     * <p>
     * 注册订阅者。如果 Subscriber 关注的 Publisher 不存在，则 PublihserMap 将
     * 首先使用默认的 EventPublisherFactory 抢占占位符发布者。
     *
     * @param consumer subscriber
     */
    public static void registerSubscriber(final Subscriber consumer) {
        registerSubscriber(consumer, DEFAULT_PUBLISHER_FACTORY);
    }

    /**
     * Register a Subscriber. If the Publisher concerned by the Subscriber does not exist, then PublihserMap will
     * preempt a placeholder Publisher with specified EventPublisherFactory first.
     *
     * @param consumer subscriber
     * @param factory  publisher factory.
     */
    public static void registerSubscriber(final Subscriber consumer, final EventPublisherFactory factory) {
        // If you want to listen to multiple events, you do it separately,
        // based on subclass's subscribeTypes method return list, it can register to publisher.
        if (consumer instanceof SmartSubscriber) {
            // 遍历所有的事件类型
            for (Class<? extends Event> subscribeType : ((SmartSubscriber) consumer).subscribeTypes()) {
                // For case, producer: defaultSharePublisher -> consumer: smartSubscriber.
                if (ClassUtils.isAssignableFrom(SlowEvent.class, subscribeType)) {
                    INSTANCE.sharePublisher.addSubscriber(consumer, subscribeType);
                } else {
                    // For case, producer: defaultPublisher -> consumer: subscriber.
                    addSubscriber(consumer, subscribeType, factory);
                }
            }
            return;
        }

        final Class<? extends Event> subscribeType = consumer.subscribeType();
        if (ClassUtils.isAssignableFrom(SlowEvent.class, subscribeType)) {
            INSTANCE.sharePublisher.addSubscriber(consumer, subscribeType);
            return;
        }

        addSubscriber(consumer, subscribeType, factory);
    }

    /**
     * Add a subscriber to publisher.
     *
     * @param consumer      subscriber instance.
     * @param subscribeType subscribeType.
     * @param factory       publisher factory.
     */
    private static void addSubscriber(final Subscriber consumer, Class<? extends Event> subscribeType, EventPublisherFactory factory) {

        // 获取事件的真实类名  当做topic
        final String topic = ClassUtils.getCanonicalName(subscribeType);
        synchronized (NotifyCenter.class) {
            // MapUtils.computeIfAbsent is a unsafe method.
            // 根据 具体的 Event实现  生成对应的 EventPublisher  存入map中   (INSTANCE.publisherMap)
            // 利用 EventPublisherFactory 函数式接口
            MapUtil.computeIfAbsent(INSTANCE.publisherMap, topic, factory, subscribeType, ringBufferSize);
        }
        EventPublisher publisher = INSTANCE.publisherMap.get(topic);
        if (publisher instanceof ShardedEventPublisher) {
            ((ShardedEventPublisher) publisher).addSubscriber(consumer, subscribeType);
        } else {
            publisher.addSubscriber(consumer);
        }
    }

    /**
     * Deregister subscriber.
     * 注销订阅者。
     *
     * @param consumer subscriber instance.
     */
    public static void deregisterSubscriber(final Subscriber consumer) {
        if (consumer instanceof SmartSubscriber) {
            for (Class<? extends Event> subscribeType : ((SmartSubscriber) consumer).subscribeTypes()) {
                if (ClassUtils.isAssignableFrom(SlowEvent.class, subscribeType)) {
                    INSTANCE.sharePublisher.removeSubscriber(consumer, subscribeType);
                } else {
                    removeSubscriber(consumer, subscribeType);
                }
            }
            return;
        }

        final Class<? extends Event> subscribeType = consumer.subscribeType();
        if (ClassUtils.isAssignableFrom(SlowEvent.class, subscribeType)) {
            INSTANCE.sharePublisher.removeSubscriber(consumer, subscribeType);
            return;
        }

        if (removeSubscriber(consumer, subscribeType)) {
            return;
        }
        throw new NoSuchElementException("The subscriber has no event publisher");
    }

    /**
     * Remove subscriber.
     *
     * @param consumer      subscriber instance.
     * @param subscribeType subscribeType.
     * @return whether remove subscriber successfully or not.
     */
    private static boolean removeSubscriber(final Subscriber consumer, Class<? extends Event> subscribeType) {

        final String topic = ClassUtils.getCanonicalName(subscribeType);
        EventPublisher eventPublisher = INSTANCE.publisherMap.get(topic);
        if (null == eventPublisher) {
            return false;
        }
        if (eventPublisher instanceof ShardedEventPublisher) {
            ((ShardedEventPublisher) eventPublisher).removeSubscriber(consumer, subscribeType);
        } else {
            eventPublisher.removeSubscriber(consumer);
        }
        return true;
    }

    /**
     * Request publisher publish event Publishers load lazily, calling publisher. Start () only when the event is
     * actually published.
     *
     * @param event class Instances of the event.
     */
    public static boolean publishEvent(final Event event) {
        try {
            return publishEvent(event.getClass(), event);
        } catch (Throwable ex) {
            log.error("There was an exception to the message publishing : ", ex);
            return false;
        }
    }

    /**
     * Request publisher publish event Publishers load lazily, calling publisher.
     * 发布事件
     *
     * @param eventType class Instances type of the event type.
     * @param event     event instance.
     */
    private static boolean publishEvent(final Class<? extends Event> eventType, final Event event) {
        if (ClassUtils.isAssignableFrom(SlowEvent.class, eventType)) {
            return INSTANCE.sharePublisher.publish(event);
        }

        final String topic = ClassUtils.getCanonicalName(eventType);

        EventPublisher publisher = INSTANCE.publisherMap.get(topic);
        if (publisher != null) {
            return publisher.publish(event);
        }
        log.warn("There are no [{}] publishers for this event, please register", topic);
        return false;
    }

    /**
     * Register to share-publisher.
     *
     * @param eventType class Instances type of the event type.
     * @return share publisher instance.
     */
    public static EventPublisher registerToSharePublisher(final Class<? extends SlowEvent> eventType) {
        return INSTANCE.sharePublisher;
    }

    /**
     * Register publisher with default factory.
     * 向默认工厂注册发布者。
     *
     * @param eventType    class Instances type of the event type.
     * @param queueMaxSize the publisher's queue max size.
     */
    public static EventPublisher registerToPublisher(final Class<? extends Event> eventType, final int queueMaxSize) {
        return registerToPublisher(eventType, DEFAULT_PUBLISHER_FACTORY, queueMaxSize);
    }

    /**
     * Register publisher with specified factory.
     * 向指定工厂注册发布者。
     *
     * @param eventType    class Instances type of the event type.
     * @param factory      publisher factory.
     * @param queueMaxSize the publisher's queue max size.
     */
    public static EventPublisher registerToPublisher(final Class<? extends Event> eventType, final EventPublisherFactory factory, final int queueMaxSize) {
        if (ClassUtils.isAssignableFrom(SlowEvent.class, eventType)) {
            // 返回统一的事件共享发布器
            return INSTANCE.sharePublisher;
        }

        // 获取事件类的名字    getCanonicalName()返回的是更容易理解的表示
        final String topic = ClassUtils.getCanonicalName(eventType);
        synchronized (NotifyCenter.class) {
            // MapUtils.computeIfAbsent is a unsafe method.
            MapUtil.computeIfAbsent(INSTANCE.publisherMap, topic, factory, eventType, queueMaxSize);
        }
        return INSTANCE.publisherMap.get(topic);
    }

    /**
     * Register publisher.
     * 注册发布者
     *
     * @param eventType class Instances type of the event type.
     * @param publisher the specified event publisher
     */
    public static void registerToPublisher(final Class<? extends Event> eventType, final EventPublisher publisher) {
        if (null == publisher) {
            return;
        }
        final String topic = ClassUtils.getCanonicalName(eventType);
        synchronized (NotifyCenter.class) {
            INSTANCE.publisherMap.putIfAbsent(topic, publisher);
        }
    }

    /**
     * Deregister publisher.
     * 注销发布者。
     *
     * @param eventType class Instances type of the event type.
     */
    public static void deregisterPublisher(final Class<? extends Event> eventType) {
        final String topic = ClassUtils.getCanonicalName(eventType);
        EventPublisher publisher = INSTANCE.publisherMap.remove(topic);
        try {
            publisher.shutdown();
        } catch (Throwable ex) {
            log.error("There was an exception when publisher shutdown : ", ex);
        }
    }
}