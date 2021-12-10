package com.vegeta.client.tool.thread;

import com.vegeta.client.spi.CustomBlockingQueue;
import com.vegeta.client.spi.VegetaServiceLoader;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * 队列类型枚举   线程池队列
 *
 * @Author fuzhiqiang
 * @Date 2021/12/4
 */
public enum QueueTypeEnum {

    /**
     * {@link ArrayBlockingQueue}
     */
    ARRAY_BLOCKING_QUEUE(1, "ArrayBlockingQueue"),

    /**
     * {@link LinkedBlockingQueue}
     */
    LINKED_BLOCKING_QUEUE(2, "LinkedBlockingQueue"),

    /**
     * {@link LinkedBlockingDeque}
     */
    LINKED_BLOCKING_DEQUE(3, "LinkedBlockingDeque"),

    /**
     * {@link SynchronousQueue}
     */
    SYNCHRONOUS_QUEUE(4, "SynchronousQueue"),

    /**
     * {@link LinkedTransferQueue}
     */
    LINKED_TRANSFER_QUEUE(5, "LinkedTransferQueue"),

    /**
     * {@link PriorityBlockingQueue}
     */
    PRIORITY_BLOCKING_QUEUE(6, "PriorityBlockingQueue"),

    /**
     * {@link "io.dynamic.threadpool.starter.toolkit.thread.ResizableCapacityLinkedBlockIngQueue"}
     */
    RESIZABLE_LINKED_BLOCKING_QUEUE(9, "ResizableCapacityLinkedBlockIngQueue");

    public Integer type;

    public String name;

    QueueTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    // 启动初始化
    static {
        VegetaServiceLoader.register(CustomBlockingQueue.class);
    }

    /**
     * 根据队列类型创建阻塞队列  (spi)
     *
     * @param type     队列类型
     * @param capacity 容量
     * @return java.util.concurrent.BlockingQueue
     * @Author fuzhiqiang
     * @Date 2021/12/4
     */
    public static BlockingQueue createBlockingQueue(int type, Integer capacity) {
        BlockingQueue blockingQueue = null;
        if (Objects.equals(type, ARRAY_BLOCKING_QUEUE.type)) {
            blockingQueue = new ArrayBlockingQueue(capacity);
        } else if (Objects.equals(type, LINKED_BLOCKING_QUEUE.type)) {
            blockingQueue = new LinkedBlockingQueue(capacity);
        } else if (Objects.equals(type, LINKED_BLOCKING_DEQUE.type)) {
            blockingQueue = new LinkedBlockingDeque(capacity);
        } else if (Objects.equals(type, SYNCHRONOUS_QUEUE.type)) {
            blockingQueue = new SynchronousQueue();
        } else if (Objects.equals(type, LINKED_TRANSFER_QUEUE.type)) {
            blockingQueue = new LinkedTransferQueue();
        } else if (Objects.equals(type, PRIORITY_BLOCKING_QUEUE.type)) {
            blockingQueue = new PriorityBlockingQueue(capacity);
        } else if (Objects.equals(type, RESIZABLE_LINKED_BLOCKING_QUEUE.type)) {
            blockingQueue = new ResizableCapacityLinkedBlockIngQueue(capacity);
        }
        // 获取当前的自定义队列的实现类  (留存当前的实现类并创建对应的队列信息)
        Collection<CustomBlockingQueue> customBlockingQueues = VegetaServiceLoader
                .getSingletonServiceInstances(CustomBlockingQueue.class);
        blockingQueue = Optional.ofNullable(blockingQueue).orElseGet(() -> customBlockingQueues.stream()
                .filter(each -> Objects.equals(type, each.getType()))
                .map(CustomBlockingQueue::generateBlockingQueue)
                .findFirst()
                .orElse(new LinkedBlockingQueue(capacity)));
        return blockingQueue;
    }

    public static String getBlockingQueueNameByType(int type) {
        Optional<QueueTypeEnum> queueTypeEnum = Arrays.stream(QueueTypeEnum.values())
                .filter(each -> each.type == type)
                .findFirst();

        return queueTypeEnum.map(each -> each.name).orElse("");
    }

}
