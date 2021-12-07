package com.vegeta.config.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vegeta.config.model.event.LocalDataChangeEvent;
import com.vegeta.config.toolkit.ConfigExecutor;
import com.vegeta.config.toolkit.Md5ConfigUtil;
import com.vegeta.config.toolkit.RequestUtil;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.http.result.base.Results;
import com.vegeta.global.notify.Event;
import com.vegeta.global.notify.NotifyCenter;
import com.vegeta.global.notify.listener.Subscriber;
import com.vegeta.global.util.CollectionUtils;
import com.vegeta.global.util.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.springframework.stereotype.Service;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * <p></p>
 * <p> xx
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author fuzq
 * @version 1.0
 * @date 2021年12月06日 16:03
 * @since 1.0
 */
@Slf4j
@Service
public class LongPollingService {
    public static final String LONG_POLLING_HEADER = "Long-Pulling-Timeout";

    /**
     * 长轮询超时不挂断 开关
     */
    public static final String LONG_POLLING_NO_HANG_UP_HEADER = "Long-Pulling-Timeout-No-Hangup";

    private static final int FIXED_POLLING_INTERVAL_MS = 10000;

    private static final String TRUE_STR = "true";

    private Map<String, Long> retainIps = Maps.newConcurrentMap();


    /**
     * @description: 初始化所有订阅的客户端，启动统计客户端数量定时任务
     * @return:
     * @author: fuzhiqiang
     * @date:
     */
    @SuppressWarnings("all")
    public LongPollingService() {
        allSubs = new ConcurrentLinkedQueue<>();

        // 统计客户端订阅数量  10s一次
        ConfigExecutor.scheduleLongPolling(new StatTask(), 0L, 10L, TimeUnit.SECONDS);

        // Register LocalDataChangeEvent to NotifyCenter.
        // 注册一个 LocalDataChangeEvent 的发布器
        NotifyCenter.registerToPublisher(LocalDataChangeEvent.class, NotifyCenter.ringBufferSize);

        // Register A Subscriber to subscribe LocalDataChangeEvent.
        // 注册一个订阅者去订阅 LocalDataChangeEvent 事件
        NotifyCenter.registerSubscriber(new Subscriber() {
            // 监听事件回调
            @Override
            public void onEvent(Event event) {
                if (isFixedPolling()) {
                    // Ignore.
                } else {
                    if (event instanceof LocalDataChangeEvent) {
                        LocalDataChangeEvent evt = (LocalDataChangeEvent) event;
                        ConfigExecutor.executeLongPolling(new DataChangeTask(evt.groupKey, evt.betaIps));
                    }
                }
            }

            // 订阅的事件类型
            @Override
            public Class<? extends Event> subscribeType() {
                return LocalDataChangeEvent.class;
            }
        });
    }

    class DataChangeTask implements Runnable {

        @Override
        public void run() {
            try {
                // 遍历所有的订阅者
                for (Iterator<ClientLongPolling> iter = allSubs.iterator(); iter.hasNext(); ) {
                    ClientLongPolling clientSub = iter.next();

                    String identity = groupKey + Constants.GROUP_KEY_DELIMITER + identify;
                    List<String> parseMapForFilter = Lists.newArrayList(identity);
                    if (StringUtils.isBlank(identify)) {
                        parseMapForFilter = MapUtil.parseMapForFilter(clientSub.clientMd5Map, groupKey);
                    }

                    parseMapForFilter.forEach(each -> {
                        if (clientSub.clientMd5Map.containsKey(each)) {
                            getRetainIps().put(clientSub.ip, System.currentTimeMillis());
                            ConfigCacheService.updateMd5(each, clientSub.ip, ConfigCacheService.getContentMd5(groupKey));
                            iter.remove();
                            clientSub.sendResponse(Collections.singletonList(groupKey));
                        }
                    });
                }
            } catch (Throwable t) {
                log.error("【Data change error: ", t);
            }
        }

        DataChangeTask(String groupKey, String identify) {
            this.groupKey = groupKey;
            this.identify = identify;
        }

        final String groupKey;

        final long changeTime = System.currentTimeMillis();

        final String identify;
    }


    /**
     * 状态任务  10秒一次  统计订阅的客户端总数
     */
    class StatTask implements Runnable {
        @Override
        public void run() {
            log.info("[vegeta-long-pulling] client count : " + allSubs.size());
        }
    }

    /**
     * 判断请求是否支持长轮询
     *
     * @param req 请求体
     * @return boolean
     * @Author fuzhiqiang
     * @Date 2021/12/6
     */
    public static boolean isSupportLongPolling(HttpServletRequest req) {
        return null != req.getHeader(LONG_POLLING_HEADER);
    }

    /**
     * 轮询时间是否固定
     *
     * @return boolean
     * @Author fuzhiqiang
     * @Date 2021/12/6
     */
    private static boolean isFixedPolling() {
        return SwitchService.getSwitchBoolean(SwitchService.FIXED_POLLING, false);
    }

    /**
     * 获取固定轮询时间
     *
     * @return int
     * @Author fuzhiqiang
     * @Date 2021/12/6
     */
    private static int getFixedPollingInterval() {
        return SwitchService.getSwitchInteger(SwitchService.FIXED_POLLING_INTERVAL, FIXED_POLLING_INTERVAL_MS);
    }

    public void addLongPollingClient(HttpServletRequest request, HttpServletResponse response, Map<String, String> clientMd5Map, int probeRequestSize) {
        //1、获取基本属性
        String str = request.getHeader(LongPollingService.LONG_POLLING_HEADER);
        String noHangUpFlag = request.getHeader(LongPollingService.LONG_POLLING_NO_HANG_UP_HEADER);
        String appName = request.getHeader(RequestUtil.CLIENT_APPNAME_HEADER);
        int delayTime = SwitchService.getSwitchInteger(SwitchService.FIXED_DELAY_TIME, 500);

        // 为LoadBalance增加延迟时间，提前500ms返回1个响应，避免客户端超时。
        long timeout = Math.max(10000, Long.parseLong(str) - delayTime);
        if (isFixedPolling()) {
            timeout = Math.max(10000, getFixedPollingInterval());
            // Do nothing but set fix polling timeout.
        } else {
            long start = System.currentTimeMillis();
            List<String> changedGroups = Md5ConfigUtil.compareMd5(request, clientMd5Map);
            if (changedGroups.size() > 0) {
                generateResponse(response, changedGroups);
                log.info("{}|{}|{}|{}|{}|{}|{}", System.currentTimeMillis() - start, "instant", RequestUtil.getRemoteIp(request), "polling", clientMd5Map.size(), probeRequestSize, changedGroups.size());
                return;
            } else if (noHangUpFlag != null && noHangUpFlag.equalsIgnoreCase(TRUE_STR)) {
                log.info("{}|{}|{}|{}|{}|{}|{}", System.currentTimeMillis() - start, "nohangup", RequestUtil.getRemoteIp(request), "polling", clientMd5Map.size(), probeRequestSize, changedGroups.size());
                return;
            }
        }
        String ip = RequestUtil.getRemoteIp(request);

        // Must be called by http thread, or send response.   必须由 http 线程调用，或发送响应。
        final AsyncContext asyncContext = request.startAsync();

        // AsyncContext.setTimeout() is incorrect, Control by oneself
        asyncContext.setTimeout(0L);
        ConfigExecutor.executeLongPolling(new ClientLongPolling(asyncContext, clientMd5Map, ip, probeRequestSize, timeout, appName));
    }

    final Queue<ClientLongPolling> allSubs;

    class ClientLongPolling implements Runnable {
        @Override
        public void run() {
            asyncTimeoutFuture = ConfigExecutor.scheduleLongPolling(new Runnable() {
                @Override
                public void run() {
                    try {
                        getRetainIps().put(ClientLongPolling.this.ip, System.currentTimeMillis());

                        // Delete subscriber's relations.
                        boolean removeFlag = allSubs.remove(ClientLongPolling.this);

                        if (removeFlag) {
                            if (isFixedPolling()) {
                                log.info("{}|{}|{}|{}|{}|{}", (System.currentTimeMillis() - createTime), "fix", RequestUtil.getRemoteIp((HttpServletRequest) asyncContext.getRequest()), "polling", clientMd5Map.size(), probeRequestSize);
                                List<String> changedGroups = Md5ConfigUtil.compareMd5((HttpServletRequest) asyncContext.getRequest(), clientMd5Map);
                                if (changedGroups.size() > 0) {
                                    sendResponse(changedGroups);
                                } else {
                                    sendResponse(null);
                                }
                            } else {
                                LogUtil.CLIENT_LOG.info("{}|{}|{}|{}|{}|{}", (System.currentTimeMillis() - createTime), "timeout", RequestUtil.getRemoteIp((HttpServletRequest) asyncContext.getRequest()), "polling", clientMd5Map.size(), probeRequestSize);
                                sendResponse(null);
                            }
                        } else {
                            LogUtil.DEFAULT_LOG.warn("client subsciber's relations delete fail.");
                        }
                    } catch (Throwable t) {
                        LogUtil.DEFAULT_LOG.error("long polling error:" + t.getMessage(), t.getCause());
                    }

                }

            }, timeoutTime, TimeUnit.MILLISECONDS);

            allSubs.add(this);
        }

        void sendResponse(List<String> changedGroups) {
            // Cancel time out task.
            if (null != asyncTimeoutFuture) {
                asyncTimeoutFuture.cancel(false);
            }
            generateResponse(changedGroups);
        }

        void generateResponse(List<String> changedGroups) {
            if (null == changedGroups) {
                // Tell web container to send http response.
                asyncContext.complete();
                return;
            }
            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

            try {
                final String respString = Md5ConfigUtil.compareMd5ResultString(changedGroups);

                // Disable cache.
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setHeader("Cache-Control", "no-cache,no-store");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(respString);
                asyncContext.complete();
            } catch (Exception ex) {
                log.error(ex.toString(), ex);
                asyncContext.complete();
            }
        }

        ClientLongPolling(AsyncContext ac, Map<String, String> clientMd5Map, String ip, int probeRequestSize, long timeoutTime, String appName) {
            this.asyncContext = ac;
            this.clientMd5Map = clientMd5Map;
            this.probeRequestSize = probeRequestSize;
            this.createTime = System.currentTimeMillis();
            this.ip = ip;
            this.timeoutTime = timeoutTime;
            this.appName = appName;
        }

        final AsyncContext asyncContext;

        final Map<String, String> clientMd5Map;

        final long createTime;

        final String ip;

        final String appName;

        final int probeRequestSize;

        final long timeoutTime;

        Future<?> asyncTimeoutFuture;

        @Override
        public String toString() {
            return "ClientLongPolling{" + "clientMd5Map=" + clientMd5Map + ", createTime=" + createTime + ", ip='" + ip + '\'' + ", appName='" + appName + '\'' + ", probeRequestSize=" + probeRequestSize + ", timeoutTime=" + timeoutTime + '}';
        }
    }

    /**
     * 生成返回流
     *
     * @param response      返回流
     * @param changedGroups 改变的config List groups
     * @Author fuzhiqiang
     * @Date 2021/12/6
     */
    void generateResponse(HttpServletResponse response, List<String> changedGroups) {
        if (CollectionUtils.isNotEmpty(changedGroups)) {
            try {
                final String changedGroupKeStr = Md5ConfigUtil.compareMd5ResultString(changedGroups);
                final String respString = JSON.toJSONString(Results.success(changedGroupKeStr));
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setHeader("Cache-Control", "no-cache,no-store");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(respString);
            } catch (Exception ex) {
                log.error(ex.toString(), ex);
            }
        }
    }

    public Map<String, Long> getRetainIps() {
        return retainIps;
    }
}
