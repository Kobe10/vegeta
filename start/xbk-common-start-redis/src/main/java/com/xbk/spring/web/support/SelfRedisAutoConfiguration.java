package com.xbk.spring.web.support;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.xbk.spring.web.common.Common;
import com.xbk.spring.web.json.JavaTimeModule;
import com.xbk.spring.web.serializer.StringRedisKeySerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnClass(RedisConnectionFactory.class)
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class SelfRedisAutoConfiguration {

    @Value("${spring.redis.need-prefix:false}")
    private Boolean needRedisPrefix;

    /**
     * 是否开启全局缓存
     */
    @Value("${spring.redis.key.random:false}")
    private Boolean randomKey;

    @Value("${spring.redis.prefix:unknown}")
    private String redisPrefix;

    @Value("${spring.application.name:unknown}")
    private String serverName;

    @Value("${spring.profiles.active:default}")
    private String active;

    /**
     * key 序列化方式
     */
    @Bean
    public StringRedisKeySerializer redisKeySerializer() {
        String prefix = "";
        if (needRedisPrefix) {
            prefix = ("unknown".equals(redisPrefix) ? serverName : redisPrefix) + ".";
        }
        prefix += active;
        return new StringRedisKeySerializer(prefix);
    }

    /**
     * 不带有 命名空间的 key 序列化
     */
    @Bean(name = "stringRedisKeySerializerWithoutServerName")
    public StringRedisSerializer stringRedisKeySerializerWithoutServerName() {
        return new StringRedisSerializer();
    }

    /**
     * 声明 jackson 序列化
     */
    @Bean
    public Jackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(om);
        return serializer;
    }

    /**
     * GenericJackson2JsonRedisSerializer 项目中使用的序列化方式
     */
    @Bean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
        ObjectMapper om = new ObjectMapper();
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule());
        om.registerModule((new SimpleModule()).addSerializer(new NullValueSerializer(null)));
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return new GenericJackson2JsonRedisSerializer(om);
    }

    /**
     * StringRedisTemplate
     */
    @Bean(name = Common.STRING_REDIS_TEMPLATE_NAME)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory,
                                                   StringRedisKeySerializer stringRedisKeySerializer,
                                                   GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer) {
        final StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
        stringRedisTemplate.setKeySerializer(stringRedisKeySerializer);
        stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //使用jackson2Json
        stringRedisTemplate.setDefaultSerializer(genericJackson2JsonRedisSerializer);
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }

    /**
     * 默认 redisTemplate
     */
    @Bean(name = Common.REDIS_TEMPLATE_NAME)
    public RedisTemplate redisTemplate(
            RedisConnectionFactory redisConnectionFactory,
            StringRedisKeySerializer stringRedisKeySerializer,
            GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer) {
        return redisTemplateFrom(redisConnectionFactory, stringRedisKeySerializer, genericJackson2JsonRedisSerializer);
    }

    @Bean(name = Common.REDIS_TEMPLATE_WITHOUT_PREFIX_NAME)
    public RedisTemplate redisTemplateWithoutPrefix(
            RedisConnectionFactory redisConnectionFactory,
            @Qualifier("stringRedisKeySerializerWithoutServerName") StringRedisSerializer stringRedisSerializer,
            GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer) {
        return redisTemplateFrom(redisConnectionFactory, stringRedisSerializer, genericJackson2JsonRedisSerializer);
    }

    /**
     * 构造 RedisTemplate
     *
     * @param redisConnectionFactory             连接信息
     * @param stringRedisKeySerializer           key 序列化方式
     * @param genericJackson2JsonRedisSerializer value 默认的序列化方式
     * @return redisTemplate
     */
    private RedisTemplate redisTemplateFrom(RedisConnectionFactory redisConnectionFactory,
                                            StringRedisSerializer stringRedisKeySerializer,
                                            GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer) {
        final RedisTemplate redisTemplate = new RedisTemplate() {
            //如果当前开启了随机key，返回所有的失效时间为 0x7fffffff
            @Override
            public Long getExpire(Object key) {
                return randomKey ? Integer.MAX_VALUE : super.getExpire(key);
            }

            @Override
            public Long getExpire(Object key, TimeUnit timeUnit) {
                return randomKey ? Integer.MAX_VALUE : super.getExpire(key, timeUnit);
            }
        };
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(stringRedisKeySerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setDefaultSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
