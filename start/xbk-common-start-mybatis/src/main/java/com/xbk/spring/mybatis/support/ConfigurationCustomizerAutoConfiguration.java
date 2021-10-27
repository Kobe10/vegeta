package com.xbk.spring.mybatis.support;

import com.xbk.spring.mybatis.language.CustomXmlLanguageDriver;
import com.xbk.spring.mybatis.type.XbkEnumTypeHandler;
import org.apache.ibatis.scripting.LanguageDriverRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.mapper.autoconfigure.ConfigurationCustomizer;


@Configuration
public class ConfigurationCustomizerAutoConfiguration implements ConfigurationCustomizer {

    /**
     * 添加自定义注册
     */
    @Override
    public void customize(org.apache.ibatis.session.Configuration configuration) {
        //枚举处理器
        TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
        registry.setDefaultEnumTypeHandler(XbkEnumTypeHandler.class);
        //sql 模板处理器
        LanguageDriverRegistry languageRegistry = configuration.getLanguageRegistry();
        languageRegistry.setDefaultDriverClass(CustomXmlLanguageDriver.class);
    }
}
