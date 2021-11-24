package com.vegeta.client.event;

import com.vegeta.client.tool.ThreadPoolBuilder;
import com.vegeta.client.tool.thread.QueueTypeEnum;
import com.vegeta.global.function.NoArgsConsumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 事件执行器
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
public class EventExecutor {

    private static final ExecutorService EVENT_EXECUTOR = ThreadPoolBuilder.builder()
            .threadFactory("event-executor")
            .corePoolSize(Runtime.getRuntime().availableProcessors())
            .maxPoolNum(Runtime.getRuntime().availableProcessors())
            .workQueue(QueueTypeEnum.ARRAY_BLOCKING_QUEUE, 2048)
            .rejected(new ThreadPoolExecutor.DiscardPolicy())
            .build();

    /**
     * 发布事件.
     *
     * @param consumer
     */
    public static void publishEvent(NoArgsConsumer consumer) {
        EVENT_EXECUTOR.execute(consumer::accept);
    }
}
