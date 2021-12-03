package com.vegeta.logrecord.config;

import com.vegeta.logrecord.parse.LogRecordOperationSource;
import com.vegeta.logrecord.parse.LogRecordValueParser;
import com.vegeta.logrecord.service.FunctionService;
import com.vegeta.logrecord.service.LogRecordService;
import com.vegeta.logrecord.service.OperatorGetService;
import com.vegeta.logrecord.service.ParseFunction;
import com.vegeta.logrecord.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import java.util.List;

/**
 * 日志记录配置.
 *
 * @author chen.ma
 * @date 2021/10/23 22:49
 */
@Configuration
public class LogRecordConfig {

    @Bean
    @ConditionalOnMissingBean(ParseFunction.class)
    public DefaultParseFunction parseFunction() {
        return new DefaultParseFunction();
    }


    @Bean
    public ParseFunctionFactory parseFunctionFactory(@Autowired List<ParseFunction> parseFunctions) {
        return new ParseFunctionFactory(parseFunctions);
    }

    @Bean
    @ConditionalOnMissingBean(FunctionService.class)
    public FunctionService functionService(ParseFunctionFactory parseFunctionFactory) {
        return new DefaultFunctionServiceImpl(parseFunctionFactory);
    }

    @Bean
    @Role(BeanDefinition.ROLE_APPLICATION)
    @ConditionalOnMissingBean(OperatorGetService.class)
    public OperatorGetService operatorGetService() {
        return new DefaultOperatorGetServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(LogRecordService.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public LogRecordService recordService() {
        return new DefaultLogRecordServiceImpl();
    }

    @Bean
    public LogRecordValueParser logRecordValueParser() {
        return new LogRecordValueParser();
    }

    @Bean
    public LogRecordOperationSource logRecordOperationSource() {
        return new LogRecordOperationSource();
    }

}
