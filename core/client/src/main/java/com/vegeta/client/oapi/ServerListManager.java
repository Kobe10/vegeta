package com.vegeta.client.oapi;

import com.google.common.collect.Lists;
import com.vegeta.client.config.bootstrap.BootstrapProperties;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Server list manager.
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
@Slf4j
public class ServerListManager {

    private static final String HTTPS = "https://";

    private static final String HTTP = "http://";

    private String serverAddrsStr;

    volatile List<String> serverUrls = new ArrayList();

    private volatile String currentServerAddr;

    private Iterator<String> iterator;

    private final BootstrapProperties properties;

    /**
     * 从client配置文件中初始化属性
     *
     * @param dynamicThreadPoolProperties 动态线程客户端配置属性
     * @return
     * @Author fuzhiqiang
     * @Date 2021/11/24
     */
    public ServerListManager(BootstrapProperties dynamicThreadPoolProperties) {
        this.properties = dynamicThreadPoolProperties;
        serverAddrsStr = properties.getServerAddr();
        if (!StringUtils.isEmpty(serverAddrsStr)) {
            List<String> serverAddrs = Lists.newArrayList();
            String[] serverAddrsArr = this.serverAddrsStr.split(",");
            for (String serverAddr : serverAddrsArr) {
                if (serverAddr.startsWith(HTTPS) || serverAddr.startsWith(HTTP)) {
                    // TODO Temporarily fixed write, later optimized
                    currentServerAddr = serverAddr;
                    serverAddrs.add(serverAddr);
                }
            }
            this.serverUrls = serverAddrs;
        }
    }

    public String getCurrentServerAddr() {
        if (StringUtils.isEmpty(currentServerAddr)) {
            iterator = iterator();
            currentServerAddr = iterator.next();
        }
        return currentServerAddr;
    }

    Iterator<String> iterator() {
        if (serverUrls.isEmpty()) {
            log.error("[iterator-serverList] No server address defined!");
        }
        return new ServerAddressIterator(serverUrls);
    }

    private static class ServerAddressIterator implements Iterator<String> {
        final List<RandomizedServerAddress> sorted;
        final Iterator<RandomizedServerAddress> iter;

        public ServerAddressIterator(List<String> source) {
            sorted = Lists.newArrayList();
            for (String address : source) {
                sorted.add(new RandomizedServerAddress(address));
            }
            Collections.sort(sorted);
            iter = sorted.iterator();
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public String next() {
            return null;
        }

        static class RandomizedServerAddress implements Comparable<RandomizedServerAddress> {
            static Random random = new Random();
            String serverIp;
            int priority = 0;
            int seed;

            public RandomizedServerAddress(String ip) {
                try {
                    this.serverIp = ip;
                    this.seed = random.nextInt(Integer.MAX_VALUE);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public int compareTo(RandomizedServerAddress other) {
                if (this.priority != other.priority) {
                    return other.priority - this.priority;
                } else {
                    return other.seed - this.seed;
                }
            }
        }
    }
}