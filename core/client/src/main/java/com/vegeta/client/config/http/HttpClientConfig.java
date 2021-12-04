package com.vegeta.client.config.http;

import com.vegeta.client.config.bootstrap.BootstrapProperties;
import com.vegeta.client.oapi.HttpAgent;
import com.vegeta.client.oapi.ServerHttpAgent;
import com.vegeta.global.util.HttpClientUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.concurrent.TimeUnit;

/**
 * Http client config.
 *
 * @author fuzq
 * @date 2021/6/10 13:28
 */
@Slf4j
public class HttpClientConfig {
    @Bean
    public OkHttpClient hippo4JOkHttpClient() {
        OkHttpClient.Builder build = new OkHttpClient.Builder();
        build.connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        supportHttps(build);
        return build.build();
    }

    @Bean
    public HttpClientUtil vegetaHttpClientUtil() {
        return new HttpClientUtil();
    }

    @Bean
    public HttpAgent httpAgent(BootstrapProperties properties, HttpClientUtil hippo4JHttpClientUtil) {
        return new ServerHttpAgent(properties, hippo4JHttpClientUtil);
    }

    // 支持https
    @SneakyThrows
    private void supportHttps(OkHttpClient.Builder builder) {
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        builder.hostnameVerifier((hostname, session) -> true);
    }
}