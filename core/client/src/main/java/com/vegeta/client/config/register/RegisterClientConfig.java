package com.vegeta.client.config.register;

import cn.hutool.core.util.StrUtil;
import com.vegeta.client.config.bootstrap.BootstrapProperties;
import com.vegeta.client.core.RegistryClient;
import com.vegeta.client.oapi.HttpAgent;
import com.vegeta.client.tool.inet.InetUtils;
import com.vegeta.global.model.InstanceInfo;
import com.vegeta.global.util.ContentUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;

import static com.vegeta.client.tool.CommonIdUtil.getDefaultInstanceId;
import static com.vegeta.client.tool.CommonIdUtil.getIpApplicationName;
import static com.vegeta.global.consts.Constants.*;

/**
 * 注册客户端到   mete server中 (保持服务端和客户端的心跳连接)
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
@AllArgsConstructor
public class RegisterClientConfig {
    // 当前环境的所有的配置文件信息
    private final ConfigurableEnvironment environment;

    // 动态线程池客户端配置属性
    private final BootstrapProperties properties;

    private final InetUtils inetUtils;

    @Bean
    @SneakyThrows
    public InstanceInfo instanceConfig() {
        InstanceInfo instanceInfo = new InstanceInfo();
        instanceInfo.setInstanceId(
                        // hostIp + applicationName + spring.application.instance_id(默认端口号)
                        getDefaultInstanceId(environment, inetUtils))
                // hostIp + applicationName
                .setIpApplicationName(getIpApplicationName(environment, inetUtils))
                .setHostName(InetAddress.getLocalHost().getHostAddress())
                .setAppName(environment.getProperty(SPRING_APPLICATION_NAME))
                // content-path
                .setClientBasePath(environment.getProperty(SERVER_SERVLET_CONTENT_PATH))
                // （appId + namespace）
                .setGroupKey(ContentUtil.getGroupKey(properties.getAppId(), properties.getNamespace()));

        // 回调地址基础路径
        String callBackUrl = instanceInfo.getHostName() + ":" +
                environment.getProperty(SERVER_PORT) + instanceInfo.getClientBasePath();
        instanceInfo.setCallBackUrl(callBackUrl);

        String ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
        String port = environment.getProperty(SERVER_PORT);
        // 身份标识
        String identification = StrUtil.builder(ip, ":", port).toString();
        instanceInfo.setIdentify(identification);
        return instanceInfo;
    }

    @Bean
    public RegistryClient registryClient(HttpAgent httpAgent, InstanceInfo instanceInfo) {
        return new RegistryClient(httpAgent, instanceInfo);
    }
}