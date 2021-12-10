package com.vegeta.logrecord.model;

import lombok.Builder;
import lombok.Data;

/**
 * 日志操作记录.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
@Data
@Builder
public class LogRecordOps {

    private String successLogTemplate;

    private String failLogTemplate;

    private String operatorId;

    private String bizKey;

    private String bizNo;

    private String category;

    private String detail;

    private String condition;
}