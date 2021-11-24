package com.vegeta.global.function;

/**
 * 无参消费者
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
@FunctionalInterface
public interface NoArgsConsumer {

    /**
     * 方法执行
     */
    void accept();
}
