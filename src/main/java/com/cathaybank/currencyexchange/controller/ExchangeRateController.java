package com.cathaybank.currencyexchange.controller;

import com.cathaybank.currencyexchange.dto.ApiResponse;
import com.cathaybank.currencyexchange.dto.ExchangeRateDTO;
import com.cathaybank.currencyexchange.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/exchange-rates")
@RequiredArgsConstructor
@Tag(name = "Exchange Rate", description = "Exchange Rate management APIs")
@Slf4j
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping
    @Operation(summary = "Get exchange rates", description = "Retrieves exchange rates for a currency pair within a date range")
    public ResponseEntity<ApiResponse<List<ExchangeRateDTO>>> getExchangeRates(
            @RequestParam String baseCode,
            @RequestParam String quoteCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /exchange-rates - Retrieving exchange rates: base={}, quote={}, startDate={}, endDate={}",
                baseCode, quoteCode, startDate, endDate);

        List<ExchangeRateDTO> exchangeRates = exchangeRateService.getExchangeRates(
                baseCode, quoteCode, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(exchangeRates));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest exchange rate", description = "Retrieves the latest exchange rate for a currency pair")
    public ResponseEntity<ApiResponse<ExchangeRateDTO>> getLatestExchangeRate(
            @RequestParam String baseCode,
            @RequestParam String quoteCode) {

        log.info("GET /exchange-rates/latest - Retrieving latest exchange rate: base={}, quote={}",
                baseCode, quoteCode);

        ExchangeRateDTO exchangeRate = exchangeRateService.getLatestExchangeRate(baseCode, quoteCode);

        return ResponseEntity.ok(ApiResponse.success(exchangeRate));
    }

    @PostMapping
    @Operation(summary = "Create exchange rate", description = "Creates a new exchange rate entry")
    public ResponseEntity<ApiResponse<ExchangeRateDTO>> createExchangeRate(
            @Valid @RequestBody ExchangeRateDTO exchangeRateDTO) {

        log.info("POST /exchange-rates - Creating new exchange rate: {}", exchangeRateDTO);

        ExchangeRateDTO createdExchangeRate = exchangeRateService.createExchangeRate(exchangeRateDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exchange rate created successfully", createdExchangeRate));
    }

    @PostMapping("/sync")
    @Operation(summary = "Sync exchange rates", description = "Triggers manual synchronization of exchange rates from external API")
    public ResponseEntity<ApiResponse<Void>> syncExchangeRates() {
        log.info("POST /exchange-rates/sync - Syncing exchange rates");

        exchangeRateService.syncExchangeRates();

        return ResponseEntity.ok(ApiResponse.success("Exchange rates synchronization started", null));
    }
}