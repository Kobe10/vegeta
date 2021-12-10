package com.vegeta.client.spi;

import java.util.concurrent.BlockingQueue;

/**
 * 自定义阻塞队列
 *
 * @Author fuzhiqiang
 * @Date 2021/12/4
 */
public interface CustomBlockingQueue {

    Integer getType();

    BlockingQueue generateBlockingQueue();
}