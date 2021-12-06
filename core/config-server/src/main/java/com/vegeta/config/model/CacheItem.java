package com.vegeta.config.model;

import com.vegeta.config.toolkit.SimpleReadWriteLock;
import com.vegeta.config.toolkit.SingletonRepository;
import com.vegeta.global.consts.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Cache item.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/6
 */
@Getter
@Setter
public class CacheItem {

    public CacheItem(String groupKey) {
        this.groupKey = SingletonRepository.GroupIdCache.getSingleton(groupKey);
    }

    final String groupKey;

    public volatile String md5 = Constants.NULL;

    public volatile long lastModifiedTs;

    /**
     * Use for beta.
     */
    public volatile boolean isBeta = false;

    public volatile String md54Beta = Constants.NULL;

    public volatile List<String> ips4Beta;

    public volatile long lastModifiedTs4Beta;

    public volatile Map<String, String> tagMd5;

    public volatile Map<String, Long> tagLastModifiedTs;

    public SimpleReadWriteLock rwLock = new SimpleReadWriteLock();

    public String type;
}
