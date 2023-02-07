package com.svetopolk.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svetopolk.demo.dto.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdsService {

    private final WebClient webClient;
    private final ObjectMapper mapper;

    public Status execute(String countryCode) {

        AdsResponse res = webClient.get()
                .uri(builder -> builder.queryParam("countryCode", countryCode).build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(responseBody -> log.info("Response body: {}", responseBody))
                .doOnError(ex -> log.error("Ads request failed:" + ex.getLocalizedMessage()))
                .map(this::getAdsResponse)
                .onErrorResume(x -> Mono.just(new AdsResponse("undefined")))
                .block();

        if (res == null || res.ads() == null) {
            return Status.UNDEFINED;
        }
        return switch (res.ads()) {
            case ("sure, why not!") -> Status.ENABLED;
            case ("you shall not pass!") -> Status.DISABLED;
            default -> Status.UNDEFINED;
        };
    }

    private AdsResponse getAdsResponse(String x) {
        try {
            return mapper.readValue(x, AdsResponse.class);
        } catch (JsonProcessingException e) {
            return new AdsResponse("undefined");
        }
    }

    private static void logErrorResponse(ClientResponse response) {
        log.error("status: {}", response.statusCode());
        response.bodyToMono(String.class).subscribe(body -> log.error("body: {}", body));
    }

    record AdsResponse(String ads) {
    }
}
