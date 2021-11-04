package com.xbk.spring.web.taskexecutor;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TaskBeanNameCommon {

    /**
     * 基础线程池
     */
    public static final String COMMON = "ThreadPoolTaskExecutorCommon";

    /**
     * 任务线程池
     */
    public static final String JOB = "ThreadPoolTaskExecutorJob";
}
