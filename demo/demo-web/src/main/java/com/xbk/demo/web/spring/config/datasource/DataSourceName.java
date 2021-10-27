package com.xbk.demo.web.spring.config.datasource;

/**
 * 数据源名称配置，该名称必须对应 spring.datasource.dynamicDataSource 下的名称
 *
 */
public final class DataSourceName {

    /**
     * 读库
     */
    public static final String READ = "read";

    /**
     * 写库
     */
    public static final String WRITE = "write";
}
