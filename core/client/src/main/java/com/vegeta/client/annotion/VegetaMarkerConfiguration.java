package com.vegeta.client.annotion;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class VegetaMarkerConfiguration {

    @Bean
    public Marker vegetaMarkerBean() {
        return new Marker();
    }

    public class Marker {

    }
}
