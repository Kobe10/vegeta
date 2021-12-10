package com.vegeta.client.spi;

import java.util.concurrent.RejectedExecutionHandler;

/**
 * 自定义拒绝策略处理器
 *
 * @Author fuzhiqiang
 * @Date 2021/12/4
 */
public interface CustomRejectedExecutionHandler {

    Integer getType();

    RejectedExecutionHandler generateRejected();
}
