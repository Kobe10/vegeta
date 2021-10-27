package com.xbk.spring.mybatis.interceptor;

import lombok.experimental.UtilityClass;

@UtilityClass
class Common {

    /**
     * xml 文件后缀
     */
    protected final String PREFIX_XML = ".xml";

    /**
     * 字段 信息
     */
    protected final String FIELD_RESULT_MAP = "resultMaps";

    /**
     * 自增ID的 查询语句
     */
    protected final String SELECT_ID = "SELECT LAST_INSERT_ID()";
}       
