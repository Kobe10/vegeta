package com.xbk.spring.web.exception.report;

/**
 * 消息上报
 */
public interface InfoReport {

    /**
     * 上报消息
     *
     * @param message 待上报的消息
     */
    void reportInfo(String message);

}       
