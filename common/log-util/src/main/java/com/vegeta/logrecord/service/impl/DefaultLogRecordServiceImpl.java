package com.vegeta.logrecord.service.impl;

import com.vegeta.logrecord.model.LogRecordInfo;
import com.vegeta.logrecord.service.LogRecordService;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认实现日志存储.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
@Slf4j
public class DefaultLogRecordServiceImpl implements LogRecordService {

    @Override
    public void record(LogRecordInfo logRecordInfo) {
        log.info("Log print :: {}", logRecordInfo);
    }
}