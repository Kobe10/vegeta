package com.vegeta.client.config.alarm;

import com.vegeta.client.config.bootstrap.BootstrapProperties;
import com.vegeta.global.model.InstanceInfo;
import lombok.AllArgsConstructor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 信息报警通知配置
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
@AllArgsConstructor
public class MessageAlarmConfig {

    private final BootstrapProperties properties;

    private final InstanceInfo instanceInfo;

    private ConfigurableEnvironment environment;

    public static final String SEND_MESSAGE_BEAN_NAME = "vegetaSendMessageService";

//    @DependsOn("hippo4JApplicationContextHolder")
//    @Bean(MessageAlarmConfig.SEND_MESSAGE_BEAN_NAME)
//    public SendMessageService hippo4JSendMessageService(HttpAgent httpAgent, AlarmControlHandler alarmControlHandler) {
//        return new BaseSendMessageService(httpAgent, properties, alarmControlHandler);
//    }
//
//    @Bean
//    public SendMessageHandler dingSendMessageHandler() {
//        String active = environment.getProperty("spring.profiles.active", Strings.EMPTY);
//        return new DingSendMessageHandler(active, instanceInfo);
//    }
//
//    @Bean
//    public SendMessageHandler larkSendMessageHandler() {
//        String active = environment.getProperty("spring.profiles.active", Strings.EMPTY);
//        return new LarkSendMessageHandler(active, instanceInfo);
//    }
//
//    @Bean
//    public AlarmControlHandler alarmControlHandler() {
//        return new AlarmControlHandler();
//    }

}
