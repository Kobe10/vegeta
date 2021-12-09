package com.vegeta.global.util;

import com.vegeta.global.function.NoArgsConsumer;

/**
 * Condition util.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/9
 */
public class ConditionUtil {

    public static void condition(boolean condition, NoArgsConsumer trueConsumer, NoArgsConsumer falseConsumer) {
        if (condition) {
            trueConsumer.accept();
        } else {
            falseConsumer.accept();
        }
    }
}
