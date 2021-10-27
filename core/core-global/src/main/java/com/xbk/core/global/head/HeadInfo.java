package com.xbk.core.global.head;

import lombok.experimental.UtilityClass;

/**
 * <p></p>
 * <p> 请求头的基本字段
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author fuzq
 * @date Created in 2021年06月01日 19:55
 * @version 1.0
 * @since 1.0
 */
@UtilityClass
public class HeadInfo {

    /**
     * token
     */
    public static final String TOKEN = "token";

    /**
     * 是否需要 Token,此值是为了兼容老版本系统，不推荐使用
     */
    public static final String NEED_TOKEN = "needToken";


    /**
     * UID
     */
    public static final String UID = "uid";

    /**
     * 系统名，eg: app
     */
    public static final String SYS = "Sys";

    /**
     * 请求时间戳，值从网关服务器获取，eg: 1571749695570
     */
    public static final String TIME_STAMP = "timeStamp";


    /**
     * 设备编号，eg: 1507346720
     */
    public static final String APP_ID = "appId";

    /**
     * app版本，eg: 50206
     */
    public static final String APP_VERSION = "appVersion";

    /**
     * 平台 android / iOS，eg: android
     */
    public static final String OS_TYPE = "osType";

    /**
     * 移动设备操作系统版本，eg: 5.0
     */
    public static final String OS_VERSION = "osVersion";

    /**
     * 移动设备设备类型，eg: Lenovo+K50-t5
     */
    public static final String PHONE_NAME = "phoneName";

    /**
     * imei 设备号， iOS（openudid）
     */
    public static final String IMEI = "imei";


    /**
     * 请求ID
     */
    public static final String REQUEST_ID = "Request-Id";

    /**
     * 请求数据类型
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * 可接受数据类型
     */
    public static final String ACCEPT = "Accept";
}
