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

package com.vegeta.client.tool.inet;

import com.vegeta.client.tool.ThreadPoolBuilder;
import com.vegeta.global.util.IPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 网卡工具类
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
public class InetUtils implements Closeable {
    private final ExecutorService executorService;

    private final InetUtilsProperties properties;

    private static final Logger LOG = LoggerFactory.getLogger(InetUtils.class);

    public InetUtils(InetUtilsProperties properties) {
        this.properties = properties;
        this.executorService = ThreadPoolBuilder.builder()
                .threadFactory(InetUtilsProperties.PREFIX, Boolean.TRUE)
                .corePoolSize(1)
                .build();
    }

    @Override
    public void close() {
        this.executorService.shutdown();
    }

    /**
     * 获取本机服务器有效的host信息
     *
     * @return com.vegeta.client.tool.inet.InetUtils.HostInfo
     * @Author fuzhiqiang
     * @Date 2021/11/24
     */
    public HostInfo findFirstNonLoopbackHostInfo() {
        InetAddress address = findFirstNonLoopbackAddress();
        if (address != null) {
            return convertAddress(address);
        }
        HostInfo hostInfo = new HostInfo();
        hostInfo.setHostname(this.properties.getDefaultHostname());
        hostInfo.setIpAddress(this.properties.getDefaultIpAddress());
        return hostInfo;
    }


    /**
     * 该工具类会获取所有网卡，依次进行遍历，取ip地址合理、索引值最小且不在忽略列表的网卡的ip地址作为结果。如果仍然没有找到合适的IP,
     * 那么就将InetAddress.getLocalHost()做为最后的fallback方案。
     *
     * @return java.net.InetAddress
     * @Author fuzhiqiang
     * @Date 2021/11/24
     */
    public InetAddress findFirstNonLoopbackAddress() {
        InetAddress result = null;
        try {
            // 记录网卡最小索引
            int lowest = Integer.MAX_VALUE;
            // 获取所有网卡
            for (Enumeration<NetworkInterface> nics = NetworkInterface
                    .getNetworkInterfaces(); nics.hasMoreElements(); ) {
                NetworkInterface ifc = nics.nextElement();
                if (ifc.isUp()) {
                    LOG.debug("Testing interface: " + ifc.getDisplayName());
                    if (ifc.getIndex() < lowest || result == null) {
                        // 记录索引
                        lowest = ifc.getIndex();
                    } else {
                        continue;
                    }
                    // 是否是被忽略的网卡
                    if (!ignoreInterface(ifc.getDisplayName())) {
                        for (Enumeration<InetAddress> addrs = ifc.getInetAddresses(); addrs.hasMoreElements(); ) {
                            InetAddress address = addrs.nextElement();
                            //是IPV4和IPV6  && 不是回环地址(127.***)  && 有推荐网卡,判断是推荐网卡内的ip
                            boolean isLegalIpVersion = IPUtil.PREFER_IPV6_ADDRESSES ? address instanceof Inet6Address
                                    : address instanceof Inet4Address;
                            if (isLegalIpVersion && !address.isLoopbackAddress() && isPreferredAddress(address)) {
                                LOG.debug("Found non-loopback interface: " + ifc.getDisplayName());
                                result = address;
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            LOG.error("Cannot get first non-loopback address", ex);
        }

        if (result != null) {
            return result;
        }

        try {
            // 如果以上逻辑都没有找到合适的网卡，则使用JDK的InetAddress.getLocalhost()
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOG.warn("Unable to retrieve localhost");
        }

        return null;
    }

    boolean isPreferredAddress(InetAddress address) {

        if (this.properties.isUseOnlySiteLocalInterfaces()) {
            final boolean siteLocalAddress = address.isSiteLocalAddress();
            if (!siteLocalAddress) {
                LOG.debug("Ignoring address: " + address.getHostAddress());
            }
            return siteLocalAddress;
        }
        final List<String> preferredNetworks = this.properties.getPreferredNetworks();
        if (preferredNetworks.isEmpty()) {
            return true;
        }
        for (String regex : preferredNetworks) {
            final String hostAddress = address.getHostAddress();
            if (hostAddress.matches(regex) || hostAddress.startsWith(regex)) {
                return true;
            }
        }
        LOG.debug("Ignoring address: " + address.getHostAddress());
        return false;
    }

    boolean ignoreInterface(String interfaceName) {
        for (String regex : this.properties.getIgnoredInterfaces()) {
            if (interfaceName.matches(regex)) {
                LOG.debug("Ignoring interface: " + interfaceName);
                return true;
            }
        }
        return false;
    }

    public HostInfo convertAddress(final InetAddress address) {
        HostInfo hostInfo = new HostInfo();
        Future<String> result = this.executorService.submit(address::getHostName);

        String hostname;
        try {
            hostname = result.get(this.properties.getTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (Exception e) {
            LOG.info("Cannot determine local hostname");
            hostname = "localhost";
        }
        hostInfo.setHostname(hostname);
        hostInfo.setIpAddress(address.getHostAddress());
        return hostInfo;
    }

//    /**
//     * {@link com.alibaba.nacos.core.cluster.ServerMemberManager} is listener.
//     */
//    @SuppressWarnings({"PMD.ClassNamingShouldBeCamelRule", "checkstyle:AbbreviationAsWordInName"})
//    public static class IPChangeEvent extends SlowEvent {
//
//        private String oldIP;
//
//        private String newIP;
//
//        public String getOldIP() {
//            return oldIP;
//        }
//
//        public void setOldIP(String oldIP) {
//            this.oldIP = oldIP;
//        }
//
//        public String getNewIP() {
//            return newIP;
//        }
//
//        public void setNewIP(String newIP) {
//            this.newIP = newIP;
//        }
//
//        @Override
//        public String toString() {
//            return "IPChangeEvent{" + "oldIP='" + oldIP + '\'' + ", newIP='" + newIP + '\'' + '}';
//        }
//    }

    /**
     * Host information pojo.
     */
    public static class HostInfo {
        public boolean override;

        private String ipAddress;

        private String hostname;

        public HostInfo(String hostname) {
            this.hostname = hostname;
        }

        public HostInfo() {
        }

        public int getIpAddressAsInt() {
            InetAddress inetAddress = null;
            String host = this.ipAddress;
            if (host == null) {
                host = this.hostname;
            }
            try {
                inetAddress = InetAddress.getByName(host);
            } catch (final UnknownHostException e) {
                throw new IllegalArgumentException(e);
            }
            return ByteBuffer.wrap(inetAddress.getAddress()).getInt();
        }

        public boolean isOverride() {
            return this.override;
        }

        public void setOverride(boolean override) {
            this.override = override;
        }

        public String getIpAddress() {
            return this.ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getHostname() {
            return this.hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }
    }
}
