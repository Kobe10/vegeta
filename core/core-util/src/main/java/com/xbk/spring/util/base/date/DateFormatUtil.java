package com.xbk.spring.util.base.date;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.time.FastDateFormat;

import java.time.format.DateTimeFormatter;


@UtilityClass
public class DateFormatUtil {
    /**
     * ----------------- JDK 8- 格式化方式 -------------------
     */

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final FastDateFormat DEFAULT_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    /**
     * yyyy-MM
     */
    public static final FastDateFormat NORMAL_MONTH_FORMAT = FastDateFormat.getInstance("yyyy-MM");

    /**
     * yyyy-MM-dd
     */
    public static final FastDateFormat NORMAL_DAY_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

    /**
     * HH:mm:ss
     */
    public static final FastDateFormat NORMAL_TIME_FORMAT = FastDateFormat.getInstance("HH:mm:ss");

    /**
     * yyyyMMddHHmmssSSS
     */
    public static final FastDateFormat LONG_NUMBER_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmssSSS");

    /**
     * yyyyMMddHHmmss
     */
    public static final FastDateFormat NUMBER_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss");


    /**
     * ----------------- JDK 8+ 格式化方式 -------------------
     */

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter DATE_TIME_DEFAULT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * yyyy-MM
     */
    public static final DateTimeFormatter DATE_TIME_NORMAL_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * yyyy-MM-dd
     */
    public static final DateTimeFormatter DATE_TIME_NORMAL_DAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * HH:mm:s
     */
    public static final DateTimeFormatter DATE_TIME_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * yyyyMMddHHmmssSSS
     */
    public static final DateTimeFormatter DATE_TIME_LONG_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    /**
     * yyyyMMddHHmmss
     */
    public static final DateTimeFormatter DATE_TIME_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

}
