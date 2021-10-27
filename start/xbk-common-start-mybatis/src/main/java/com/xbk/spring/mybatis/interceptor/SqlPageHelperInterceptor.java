package com.xbk.spring.mybatis.interceptor;

import com.github.pagehelper.PageInterceptor;
import lombok.experimental.UtilityClass;

import java.util.Properties;

@UtilityClass
public class SqlPageHelperInterceptor {

    /**
     * 获取拦截器
     */
    public static PageInterceptor init() {
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        //数据库
        properties.setProperty("helperDialect", "mysql");
        //是否将参数offset作为PageNum使用
        properties.setProperty("offsetAsPageNum", "true");
        //是否进行count查询
        properties.setProperty("rowBoundsWithCount", "true");
        //是否分页合理化
        properties.setProperty("reasonable", "false");
        interceptor.setProperties(properties);
        return interceptor;
    }
}       
