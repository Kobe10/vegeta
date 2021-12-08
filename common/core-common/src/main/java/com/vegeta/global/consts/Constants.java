/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vegeta.global.consts;

import java.util.UUID;

/**
 * Constants.
 *
 * @author Nacos
 */
public class Constants {

    public static final String TP_ID = "tpId";

    public static final String APP_ID = "appId";

    public static final String NAMESPACE = "namespace";

    public static final String GROUP_KEY = "groupKey";

    public static final String DEFAULT_NAMESPACE_ID = "public";

    public static final String NULL = "";

    public static final String ENCODE = "UTF-8";

    public static final int CONFIG_LONG_POLL_TIMEOUT = 30000;

    public static final String LINE_SEPARATOR = Character.toString((char) 1);

    public static final String WORD_SEPARATOR = Character.toString((char) 2);

    public static final String GENERAL_SPLIT_SYMBOL = ",";

    public static final String LONG_POLLING_LINE_SEPARATOR = "\r\n";

    public static final String BASE_PATH = "/v1/cs";

    public static final String CONFIG_CONTROLLER_PATH = BASE_PATH + "/configs";
    // 获取具体配置信息
    public static final String CONFIG_DETAIL_PATH = CONFIG_CONTROLLER_PATH + "/detailConfigInfo";

    public static final String LISTENER_PATH = CONFIG_CONTROLLER_PATH + "/listener";

    public static final String PROBE_MODIFY_REQUEST = "Listening-Configs";

    public static final String LONG_PULLING_TIMEOUT = "Long-Pulling-Timeout";

    public static final String LONG_PULLING_TIMEOUT_NO_HANGUP = "Long-Pulling-Timeout-No-Hangup";

    public static final String LONG_PULLING_CLIENT_IDENTIFICATION = "Long-Pulling-Client-Identification";

    public static final String CLIENT_IDENTIFICATION_VALUE = UUID.randomUUID().toString();

    public static final String LISTENING_CONFIGS = "Listening-Configs";

    public static final String GROUP_KEY_DELIMITER = "+";

    public static final String GROUP_KEY_DELIMITER_TRANSLATION = "\\+";

    public static final long EVICTION_INTERVAL_TIMER_IN_MS = 60 * 1000;

    public static final int SCHEDULED_THREAD_CORE_NUM = 1;

    public static final int MAP_INITIAL_CAPACITY = 16;

    //    ------------------------------------------------------------------------------------
    public static final String SPRING_APPLICATION_NAME = "spring.application.name";
    public static final String SERVER_SERVLET_CONTENT_PATH = "server.servlet.context-path";
    public static final String SERVER_PORT = "server.port";


    //    ------------------------------------------------------------------------------------
    public static final String THREAD_REGISTER_CLIENT_HEARTBEAT_EXECUTOR = "DiscoveryClient-HeartbeatExecutor";
    public static final String THREAD_REGISTER_CLIENT_SCHEDULER = "RegistryClient-Scheduler";
}
