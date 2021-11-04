package com.xbk.spring.web.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Jackson 配置
 * https://www.cnblogs.com/yuluoxingkong/p/7676089.html
 */
@Configuration
public class JacksonAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.createXmlMapper(false).build();
        /**
         * ALWAYS 默认 null 会被序列化
         * NON_DEFAULT 属性为默认值不序列化
         * NON_EMPTY 属性为 空（""） 或者为 NULL 都不序列化，则返回的json是没有这个字段的。
         * NON_NULL 属性为NULL 不序列化
         */
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        //设置无法转义的字符逻辑
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //允许出现特殊字符和转义符
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        //允许出现单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        //时间不转换成时间戳
        mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        //空对象
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        //时间格式化方式
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //默认时区
        mapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));

        mapper.setLocale(Locale.CHINA);

        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new XbkWebInfoEnumModule());

        return mapper;
    }

}
