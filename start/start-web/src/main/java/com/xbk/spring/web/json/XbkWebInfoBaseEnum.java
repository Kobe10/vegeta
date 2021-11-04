package com.xbk.spring.web.json;

public interface XbkWebInfoBaseEnum {

    /**
     * 获取返回值中需要序列化的字段
     *
     * @return 序列化后的值
     */
    String getWebInfoCode();

    /**
     * 反序列化默认值
     *
     * @return 反序列化默认值
     */
    default XbkWebInfoBaseEnum deserializeDefaultValue() {
        return null;
    }

}       
