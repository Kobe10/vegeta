package com.xbk.spring.web.support;

import com.xbk.spring.web.core.lock.DistributedLock;
import com.xbk.spring.web.core.lock.redisLock.RedisDistributedLock;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@AutoConfigureAfter(RedisTemplate.class)
public class SelfRedisLockConfiguration {

    @Bean
    public DistributedLock distributedLock(RedisTemplate redisTemplate) {
        return new RedisDistributedLock(redisTemplate);
    }
}
