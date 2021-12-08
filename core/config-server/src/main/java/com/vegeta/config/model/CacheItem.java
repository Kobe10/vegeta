package com.vegeta.config.model;

import com.vegeta.config.toolkit.Md5ConfigUtil;
import com.vegeta.config.toolkit.SimpleReadWriteLock;
import com.vegeta.config.toolkit.SingletonRepository;
import com.vegeta.datasource.model.ConfigAllInfo;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.util.MD5Utils;
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

    public CacheItem(String groupKey, String md5) {
        this.md5 = md5;
        this.groupKey = SingletonRepository.GroupIdCache.getSingleton(groupKey);
    }

    public CacheItem(String groupKey, ConfigAllInfo configAllInfo) {
        this.configAllInfo = configAllInfo;
        this.md5 = Md5ConfigUtil.getTpContentMd5(configAllInfo);
        this.groupKey = SingletonRepository.GroupIdCache.getSingleton(groupKey);
    }

    final String groupKey;

    public volatile String md5 = Constants.NULL;

    public volatile long lastModifiedTs;

    public volatile ConfigAllInfo configAllInfo;


    public SimpleReadWriteLock rwLock = new SimpleReadWriteLock();

    public String type;
}
