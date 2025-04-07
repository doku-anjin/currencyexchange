package com.cathaybank.currencyexchange.service.impl;

import com.cathaybank.currencyexchange.dto.OandaApiResponse;
import com.cathaybank.currencyexchange.service.OandaApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class OandaApiServiceImpl implements OandaApiService {

    @Value("${api.oanda.base-url}")
    private String baseUrl;

    @Value("${api.oanda.request-timeout:5000}")
    private int requestTimeout;

    private final WebClient.Builder webClientBuilder;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public OandaApiResponse getExchangeRates(String baseCode, String quoteCode, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching exchange rates from OANDA: base={}, quote={}, startDate={}, endDate={}",
                baseCode, quoteCode, startDate, endDate);

        String formattedStartDate = startDate.format(dateFormatter);
        String formattedEndDate = endDate.format(dateFormatter);

        WebClient webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .filter(logResponse())
                .build();

        OandaApiResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/currencies")
                        .queryParam("base", baseCode)
                        .queryParam("quote", quoteCode)
                        .queryParam("data_type", "chart")
                        .queryParam("start_date", formattedStartDate)
                        .queryParam("end_date", formattedEndDate)
                        .build())
                .retrieve()
                .bodyToMono(OandaApiResponse.class)
                .block();

        log.debug("Received response from OANDA: {}", response);
        return response;
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) ->
                    values.forEach(value -> log.debug("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.debug("Response status: {}", clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders().forEach((name, values) ->
                    values.forEach(value -> log.debug("{}={}", name, value)));
            return Mono.just(clientResponse);
        });
    }
}