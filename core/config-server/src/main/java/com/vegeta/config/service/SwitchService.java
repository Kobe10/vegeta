package com.vegeta.config.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * SwitchService
 *
 * @Author fuzhiqiang
 * @Date 2021/12/6
 */
@Slf4j
@Service
public class SwitchService {

    public static final String SWITCH_META_DATAID = "com.alibaba.nacos.meta.switch";

    public static final String FIXED_POLLING = "isFixedPolling";

    public static final String FIXED_POLLING_INTERVAL = "fixedPollingInertval";

    public static final String FIXED_DELAY_TIME = "fixedDelayTime";

    public static final String DISABLE_APP_COLLECTOR = "disableAppCollector";

    private static volatile Map<String, String> switches = new HashMap<String, String>();

    public static boolean getSwitchBoolean(String key, boolean defaultValue) {
        boolean rtn;
        try {
            String value = switches.get(key);
            rtn = value != null ? Boolean.parseBoolean(value) : defaultValue;
        } catch (Exception e) {
            rtn = defaultValue;
            log.error("corrupt switch value {}={}", key, switches.get(key));
        }
        return rtn;
    }

    public static int getSwitchInteger(String key, int defaultValue) {
        int rtn;
        try {
            String status = switches.get(key);
            rtn = status != null ? Integer.parseInt(status) : defaultValue;
        } catch (Exception e) {
            rtn = defaultValue;
            log.error("corrupt switch value {}={}", key, switches.get(key));
        }
        return rtn;
    }

    public static String getSwitchString(String key, String defaultValue) {
        String value = switches.get(key);
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    public static String getSwitches() {
        StringBuilder sb = new StringBuilder();

        String split = "";
        for (Map.Entry<String, String> entry : switches.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(split);
            sb.append(key);
            sb.append("=");
            sb.append(value);
            split = "; ";
        }

        return sb.toString();
    }
}
