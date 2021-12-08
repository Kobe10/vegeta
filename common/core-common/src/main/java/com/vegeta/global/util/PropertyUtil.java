package com.vegeta.global.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Properties util.
 *
 * @author fuzhiqiang
 */
@Slf4j
public class PropertyUtil {

    private static int maxContent = 10 * 1024 * 1024;

    public static int getMaxContent() {
        return maxContent;
    }

    public static void setMaxContent(int maxContent) {
        PropertyUtil.maxContent = maxContent;
    }
}
