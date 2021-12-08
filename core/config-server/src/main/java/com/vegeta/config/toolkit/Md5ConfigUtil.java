package com.vegeta.config.toolkit;

import com.google.common.collect.Lists;
import com.vegeta.config.service.ConfigCacheService;
import com.vegeta.datasource.model.ConfigAllInfo;
import com.vegeta.global.util.GroupKeyUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vegeta.global.consts.Constants.LINE_SEPARATOR;
import static com.vegeta.global.consts.Constants.WORD_SEPARATOR;


/**
 * Md5 config util.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/8
 * @return
 */
public class Md5ConfigUtil {

    static final char WORD_SEPARATOR_CHAR = (char) 2;

    static final char LINE_SEPARATOR_CHAR = (char) 1;

    /**
     * Get thread pool content md5
     *
     * @param config 配置信息
     */
    public static String getTpContentMd5(ConfigAllInfo config) {
        return Md5ConfigUtil.getTpContentMd5(config);
    }

    /**
     * Compare whether the client Md5 is consistent with the server.
     *
     * @param request
     * @param clientMd5Map
     * @return
     */
    public static List<String> compareMd5(HttpServletRequest request, Map<String, String> clientMd5Map) {
        List<String> changedGroupKeys = Lists.newArrayList();
        clientMd5Map.forEach((key, val) -> {
            String remoteIp = RequestUtil.getRemoteIp(request);
            boolean isUpdateData = ConfigCacheService.isUpdateData(key, val, remoteIp);
            if (!isUpdateData) {
                changedGroupKeys.add(key);
            }
        });

        return changedGroupKeys;
    }

    /**
     * md5 ----->>>>  map
     *
     * @param configKeysString 客户端 keys
     * @return java.util.Map<java.lang.String, java.lang.String>   key --- > groupKey   value -->  md5
     * @Author fuzhiqiang
     * @Date 2021/12/6
     */
    public static Map<String, String> getClientMd5Map(String configKeysString) {
        Map<String, String> md5Map = new HashMap<>(5);

        if (StringUtils.isEmpty(configKeysString)) {
            return md5Map;
        }
        int start = 0;
        List<String> tmpList = new ArrayList<>(3);
        for (int i = start; i < configKeysString.length(); i++) {
            char c = configKeysString.charAt(i);
            // 如果是 '2'  直接截取   包含: tpId appId tenantId identification
            if (c == WORD_SEPARATOR_CHAR) {
                tmpList.add(configKeysString.substring(start, i));
                start = i + 1;
                if (tmpList.size() > 4) {
                    // Malformed message and return parameter error.
                    throw new IllegalArgumentException("invalid protocol,too much key");
                }
            } else if (c == LINE_SEPARATOR_CHAR) {
                // 如果是 '1'   截取到  配置信息的md5 格式
                String endValue = "";
                if (start + 1 <= i) {
                    endValue = configKeysString.substring(start, i);
                }
                start = i + 1;
                String groupKey = getKey(tmpList.get(0), tmpList.get(1), tmpList.get(2), tmpList.get(3));
                groupKey = SingletonRepository.GroupIdCache.getSingleton(groupKey);
                md5Map.put(groupKey, endValue);
                tmpList.clear();
                // Protect malformed messages
                if (md5Map.size() > 10000) {
                    throw new IllegalArgumentException("invalid protocol, too much listener");
                }
            }
        }
        return md5Map;
    }

    public static String getKey(String dataId, String group) {
        StringBuilder sb = new StringBuilder();
        GroupKeyUtil.urlEncode(dataId, sb);
        sb.append('+');
        GroupKeyUtil.urlEncode(group, sb);
        return sb.toString();
    }

    public static String getKey(String dataId, String group, String tenant, String identify) {
        StringBuilder sb = new StringBuilder();
        GroupKeyUtil.urlEncode(dataId, sb);
        sb.append('+');
        GroupKeyUtil.urlEncode(group, sb);
        if (!StringUtils.isEmpty(tenant)) {
            sb.append('+');
            GroupKeyUtil.urlEncode(tenant, sb);
            sb.append("+").append(identify);
        }

        return sb.toString();
    }

    public static String compareMd5ResultString(List<String> changedGroupKeys) throws IOException {
        if (null == changedGroupKeys) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (String groupKey : changedGroupKeys) {
            String[] dataIdGroupId = GroupKeyUtil.parseKey(groupKey);
            sb.append(dataIdGroupId[0]);
            sb.append(WORD_SEPARATOR);
            sb.append(dataIdGroupId[1]);
            // if have tenant, then set it
            if (dataIdGroupId.length == 4) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(dataIdGroupId[2])) {
                    sb.append(WORD_SEPARATOR);
                    sb.append(dataIdGroupId[2]);
                }
            }
            sb.append(LINE_SEPARATOR);
        }
        return URLEncoder.encode(sb.toString(), "UTF-8");
    }
}