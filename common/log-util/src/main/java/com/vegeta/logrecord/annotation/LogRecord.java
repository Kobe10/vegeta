package com.vegeta.logrecord.annotation;

import com.vegeta.logrecord.enums.LogRecordTypeEnum;

import java.lang.annotation.*;

/**
 * 日志记录注解
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecord {

    /**
     * 业务前缀
     */
    String prefix();

    /**
     * 操作日志文本模版
     */
    String success();

    /**
     * 操作日志失败的文本
     */
    String fail() default "";

    /**
     * 操作人
     */
    String operator() default "";

    /**
     * 业务码
     */
    String bizNo();

    /**
     * 日志详情
     */
    String detail() default "";

    /**
     * 日志种类
     */
    String category();

    /**
     * 记录类型
     */
    LogRecordTypeEnum recordType() default LogRecordTypeEnum.COMPLETE;

    /**
     * 记录日志条件
     */
    String condition() default "";
}