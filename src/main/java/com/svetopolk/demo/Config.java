package com.svetopolk.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@Slf4j
public class Config {

    @Bean
    public WebClient getWebClient(
            @Value("${ads.server.url}") String url,
            @Value("${ads.server.timeout}") int timeout,
            @Value("${ads.server.login}") String login,
            @Value("${ads.server.pass}") String pass
    ) {
        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(timeout));
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .baseUrl(url)
                .clientConnector(connector)
                .defaultHeaders(headers -> headers.setBasicAuth(login, pass))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
