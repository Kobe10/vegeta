package com.vegeta.config.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.alibaba.nacos.config.server.constant.Constants;
import com.alibaba.nacos.config.server.model.ConfigInfoBase;
import com.alibaba.nacos.config.server.model.event.LocalDataChangeEvent;
import com.alibaba.nacos.config.server.service.repository.PersistService;
import com.alibaba.nacos.config.server.utils.DiskUtil;
import com.alibaba.nacos.config.server.utils.GroupKey;
import com.alibaba.nacos.config.server.utils.GroupKey2;
import com.alibaba.nacos.config.server.utils.PropertyUtil;
import com.google.common.collect.Maps;
import com.vegeta.config.model.CacheItem;
import com.vegeta.config.service.biz.ConfigService;
import com.vegeta.global.util.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static com.alibaba.nacos.config.server.utils.LogUtil.*;

/**
 * Config service.
 *
 * @author fuzhiqiang
 */
@Slf4j
public class ConfigCacheService {

    private static ConfigService configService = null;

    /**
     * TODO: 数据结构、客户端停机时 remove 操作待重构
     * <p>
     * key: message-produce+dynamic-threadpool-example+prescription+192.168.20.227:8088
     * val:
     * key: 192.168.20.227:8088
     * val:  {@link CacheItem}
     */
    private static final ConcurrentHashMap<String, Map<String, CacheItem>> CACHE = new ConcurrentHashMap();

    public static boolean isUpdateData(String groupKey, String md5, String ip) {
        String contentMd5 = ConfigCacheService.getContentMd5IsNullPut(groupKey, ip);
        return Objects.equals(contentMd5, md5);
    }

    /**
     * Get Md5.
     *
     * @param groupKey
     * @param ip
     * @return
     */
    private synchronized static String getContentMd5IsNullPut(String groupKey, String ip) {
        Map<String, CacheItem> cacheItemMap = Optional.ofNullable(CACHE.get(groupKey)).orElse(Maps.newHashMap());

        CacheItem cacheItem = null;
        if (CollUtil.isNotEmpty(cacheItemMap) && (cacheItem = cacheItemMap.get(ip)) != null) {
            return cacheItem.md5;
        }

        if (configService == null) {
            configService = ApplicationContextHolder.getBean(ConfigService.class);
        }
        String[] params = groupKey.split("\\+");
        ConfigAllInfo config = configService.findConfigRecentInfo(params);
        if (config != null && !org.springframework.util.StringUtils.isEmpty(config.getTpId())) {
            cacheItem = new CacheItem(groupKey, config);
            cacheItemMap.put(ip, cacheItem);
            CACHE.put(groupKey, cacheItemMap);
        }

        return (cacheItem != null) ? cacheItem.md5 : Constants.NULL;
    }

    public static String getContentMd5(String groupKey) {
        if (configService == null) {
            configService = ApplicationContextHolder.getBean(ConfigService.class);
        }

        String[] params = groupKey.split("\\+");
        ConfigAllInfo config = configService.findConfigRecentInfo(params);
        if (config == null || StringUtils.isEmpty(config.getTpId())) {
            String errorMessage = String.format("config is null. tpId :: %s, itemId :: %s, tenantId :: %s", params[0], params[1], params[2]);
            throw new RuntimeException(errorMessage);
        }

        return Md5Util.getTpContentMd5(config);
    }

    public static void updateMd5(String groupKey, String ip, String md5) {
        CacheItem cache = makeSure(groupKey, ip);
        if (cache.md5 == null || !cache.md5.equals(md5)) {
            cache.md5 = md5;
            String[] params = groupKey.split("\\+");
            ConfigAllInfo config = configService.findConfigRecentInfo(params);
            cache.configAllInfo = config;
            cache.lastModifiedTs = System.currentTimeMillis();
            NotifyCenter.publishEvent(new LocalDataChangeEvent(ip, groupKey));
        }
    }

    public synchronized static CacheItem makeSure(String groupKey, String ip) {
        Map<String, CacheItem> ipCacheItemMap = CACHE.get(groupKey);
        CacheItem item = ipCacheItemMap.get(ip);
        if (null != item) {
            return item;
        }

        CacheItem tmp = new CacheItem(groupKey);
        Map<String, CacheItem> cacheItemMap = Maps.newHashMap();
        cacheItemMap.put(ip, tmp);
        CACHE.putIfAbsent(groupKey, cacheItemMap);

        return tmp;
    }

    public static Map<String, CacheItem> getContent(String identification) {
        List<String> identificationList = MapUtil.parseMapForFilter(CACHE, identification);
        Map<String, CacheItem> returnStrCacheItemMap = Maps.newHashMap();
        identificationList.forEach(each -> returnStrCacheItemMap.putAll(CACHE.get(each)));
        return returnStrCacheItemMap;
    }

    /**
     * Remove config cache.
     *
     * @param groupKey 租户 + 项目 + IP
     */
    public synchronized static void removeConfigCache(String groupKey) {
        // 模糊搜索
        List<String> identificationList = MapUtil.parseMapForFilter(CACHE, groupKey);
        for (String cacheMapKey : identificationList) {
            Map<String, CacheItem> removeCacheItem = CACHE.remove(cacheMapKey);
            log.info("Remove invalidated config cache. config info :: {}", JSON.toJSONString(removeCacheItem));
        }
    }
}
