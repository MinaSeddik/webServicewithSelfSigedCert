package com.example.springbootproject.config;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.web.client.ClientHttpRequestFactorySupplier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();

        poolingConnectionManager.setMaxTotal(20);
        poolingConnectionManager.setDefaultMaxPerRoute(5);

        CloseableHttpClient client = HttpClientBuilder.create().setConnectionManager(poolingConnectionManager).build();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(client);


//        clientHttpRequestFactory.setConnectTimeout(restTemplateProps.getConnectionTimeout());
//        clientHttpRequestFactory.setReadTimeout(restTemplateProps.getSocketTimeout());
        return clientHttpRequestFactory;
    }

    @Bean
    public RestTemplate authRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.requestFactory(new ClientHttpRequestFactorySupplier()).build();
    }
}
