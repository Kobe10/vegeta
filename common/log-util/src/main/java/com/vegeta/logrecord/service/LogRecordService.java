package com.vegeta.logrecord.service;

import com.vegeta.logrecord.model.LogRecordInfo;

/**
 * 日志记录.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
public interface LogRecordService {

    /**
     * 保存日志.
     *
     * @param logRecordInfo
     */
    void record(LogRecordInfo logRecordInfo);
}