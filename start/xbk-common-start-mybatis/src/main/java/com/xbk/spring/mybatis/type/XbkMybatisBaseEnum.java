package com.xbk.spring.mybatis.type;

public interface XbkMybatisBaseEnum<T> {

    /**
     * 获取与数据库匹配的 code
     *
     * @return 与数据库匹配的 code
     */
    T getDBValue();

}
