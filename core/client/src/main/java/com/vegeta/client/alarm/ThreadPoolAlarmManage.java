package com.vegeta.client.alarm;

import com.vegeta.client.core.DynamicThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * Alarm manage.
 *
 * @author chen.ma
 * @date 2021/8/15 14:13
 */
@Slf4j
public class ThreadPoolAlarmManage {
    public static Object checkPoolRejectAlarm(DynamicThreadPoolExecutor dynamicThreadPoolExecutor) {
        return null;
    }

    public static void checkPoolLivenessAlarm(boolean core, DynamicThreadPoolExecutor dynamicThreadPoolExecutor) {
    }

    public static void checkPoolCapacityAlarm(DynamicThreadPoolExecutor dynamicThreadPoolExecutor) {
    }

    /**
     * 发送消息
     */
//    private static final SendMessageService SEND_MESSAGE_SERVICE;
//
//    static {
//        SEND_MESSAGE_SERVICE = Optional.ofNullable(ApplicationContextHolder.getInstance())
//                .map(each -> each.getBean(MessageAlarmConfig.SEND_MESSAGE_BEAN_NAME, SendMessageService.class))
//                .orElse(null);
//        System.out.println();
//    }
//
//    /**
//     * Check thread pool capacity alarm.
//     *
//     * @param threadPoolExecutor
//     */
//    public static void checkPoolCapacityAlarm(DynamicThreadPoolExecutor threadPoolExecutor) {
//        if (SEND_MESSAGE_SERVICE == null) {
//            return;
//        }
//        ThreadPoolAlarm threadPoolAlarm = threadPoolExecutor.getThreadPoolAlarm();
//        BlockingQueue blockIngQueue = threadPoolExecutor.getQueue();
//
//        int queueSize = blockIngQueue.size();
//        int capacity = queueSize + blockIngQueue.remainingCapacity();
//        int divide = CalculateUtil.divide(queueSize, capacity);
//        boolean isSend = threadPoolAlarm.getIsAlarm()
//                && divide > threadPoolAlarm.getCapacityAlarm()
//                && isSendMessage(threadPoolExecutor, MessageTypeEnum.CAPACITY);
//        if (isSend) {
//            SEND_MESSAGE_SERVICE.sendAlarmMessage(MessageTypeEnum.CAPACITY, threadPoolExecutor);
//        }
//    }
//
//    /**
//     * Check thread pool activity alarm.
//     *
//     * @param isCore
//     * @param threadPoolExecutor
//     */
//    public static void checkPoolLivenessAlarm(boolean isCore, DynamicThreadPoolExecutor threadPoolExecutor) {
//        if (isCore || SEND_MESSAGE_SERVICE == null || !isSendMessage(threadPoolExecutor, MessageTypeEnum.LIVENESS)) {
//            return;
//        }
//        int activeCount = threadPoolExecutor.getActiveCount();
//        int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
//        int divide = CalculateUtil.divide(activeCount, maximumPoolSize);
//
//        ThreadPoolAlarm threadPoolAlarm = threadPoolExecutor.getThreadPoolAlarm();
//
//        boolean isSend = threadPoolAlarm.getIsAlarm()
//                && divide > threadPoolAlarm.getLivenessAlarm()
//                && isSendMessage(threadPoolExecutor, MessageTypeEnum.CAPACITY);
//        if (isSend) {
//            SEND_MESSAGE_SERVICE.sendAlarmMessage(MessageTypeEnum.CAPACITY, threadPoolExecutor);
//        }
//    }
//
//    /**
//     * Check thread pool reject policy alarm.
//     *
//     * @param threadPoolExecutor
//     */
//    public static void checkPoolRejectAlarm(DynamicThreadPoolExecutor threadPoolExecutor) {
//        if (SEND_MESSAGE_SERVICE == null) {
//            return;
//        }
//
//        ThreadPoolAlarm threadPoolAlarm = threadPoolExecutor.getThreadPoolAlarm();
//        if (threadPoolAlarm.getIsAlarm() && isSendMessage(threadPoolExecutor, MessageTypeEnum.REJECT)) {
//            SEND_MESSAGE_SERVICE.sendAlarmMessage(MessageTypeEnum.REJECT, threadPoolExecutor);
//        }
//    }
//
//    /**
//     * Send thread pool configuration change message.
//     *
//     * @param parameter
//     */
//    public static void sendPoolConfigChange(PoolParameterInfo parameter) {
//        if (SEND_MESSAGE_SERVICE == null) {
//            return;
//        }
//
//        SEND_MESSAGE_SERVICE.sendChangeMessage(parameter);
//    }
//
//    /**
//     * Is send message.
//     *
//     * @param threadPoolExecutor
//     * @param typeEnum
//     * @return
//     */
//    private static boolean isSendMessage(DynamicThreadPoolExecutor threadPoolExecutor, MessageTypeEnum typeEnum) {
//        // ignore
//        return true;
//    }

}
