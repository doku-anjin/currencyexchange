package com.cathaybank.currencyexchange.service.impl;

import com.cathaybank.currencyexchange.dto.ExchangeRateDTO;
import com.cathaybank.currencyexchange.dto.OandaApiResponse;
import com.cathaybank.currencyexchange.entity.Currency;
import com.cathaybank.currencyexchange.entity.ExchangeRate;
import com.cathaybank.currencyexchange.exception.ResourceNotFoundException;
import com.cathaybank.currencyexchange.repository.CurrencyRepository;
import com.cathaybank.currencyexchange.repository.ExchangeRateRepository;
import com.cathaybank.currencyexchange.service.ExchangeRateService;
import com.cathaybank.currencyexchange.service.OandaApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyRepository currencyRepository;
    private final OandaApiService oandaApiService;

    private static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final List<String> BASE_CURRENCIES = Arrays.asList("USD", "EUR", "JPY", "GBP");

    @Override
    @Transactional(readOnly = true)
    public List<ExchangeRateDTO> getExchangeRates(String baseCode, String quoteCode, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting exchange rates: base={}, quote={}, startDate={}, endDate={}",
                baseCode, quoteCode, startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return exchangeRateRepository.findByBaseCurrencyCodeAndQuoteCurrencyCodeAndDateBetween(
                        baseCode, quoteCode, startDateTime, endDateTime)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExchangeRateDTO getLatestExchangeRate(String baseCode, String quoteCode) {
        log.debug("Getting latest exchange rate: base={}, quote={}", baseCode, quoteCode);

        return exchangeRateRepository.findLatestByBaseCurrencyCodeAndQuoteCurrencyCode(baseCode, quoteCode)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exchange rate not found for base=" + baseCode + " and quote=" + quoteCode));
    }

    @Override
    @Transactional
    public ExchangeRateDTO createExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        log.debug("Creating exchange rate: {}", exchangeRateDTO);

        Currency baseCurrency = currencyRepository.findByCode(exchangeRateDTO.getBaseCurrencyCode())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Base currency not found: " + exchangeRateDTO.getBaseCurrencyCode()));

        Currency quoteCurrency = currencyRepository.findByCode(exchangeRateDTO.getQuoteCurrencyCode())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Quote currency not found: " + exchangeRateDTO.getQuoteCurrencyCode()));

        LocalDateTime date = exchangeRateDTO.getDate();

        // Check if exchange rate already exists for this date and currency pair
        exchangeRateRepository.findByBaseCurrencyAndQuoteCurrencyAndDate(baseCurrency, quoteCurrency, date)
                .ifPresent(er -> {
                    throw new IllegalArgumentException("Exchange rate already exists for the specified date and currency pair");
                });

        ExchangeRate exchangeRate = ExchangeRate.builder()
                .baseCurrency(baseCurrency)
                .quoteCurrency(quoteCurrency)
                .rate(exchangeRateDTO.getRate())
                .date(date)
                .source(exchangeRateDTO.getSource() != null ? exchangeRateDTO.getSource() : "MANUAL")
                .build();

        exchangeRate = exchangeRateRepository.save(exchangeRate);
        return mapToDTO(exchangeRate);
    }

    @Override
    @Transactional
    public void syncExchangeRates() {
        log.info("Starting exchange rate synchronization");

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // Get quotes for multiple base currencies
        for (String baseCode : BASE_CURRENCIES) {
            // For demo purposes, we're only getting rates against USD, EUR, and JPY
            for (String quoteCode : Arrays.asList("USD", "EUR", "JPY", "GBP")) {
                // Skip same currency pairs
                if (baseCode.equals(quoteCode)) {
                    continue;
                }

                try {
                    OandaApiResponse response = oandaApiService.getExchangeRates(
                            baseCode, quoteCode, yesterday, today);

                    saveExchangeRatesFromResponse(response);
                } catch (Exception e) {
                    log.error("Error syncing exchange rates for {}/{}: {}", baseCode, quoteCode, e.getMessage(), e);
                }
            }
        }

        log.info("Exchange rate synchronization completed");
    }

    private void saveExchangeRatesFromResponse(OandaApiResponse response) {
        if (response == null || response.getMeta() == null || response.getData() == null) {
            log.warn("Invalid API response received");
            return;
        }

        String baseCode = response.getMeta().getBase();
        String quoteCode = response.getMeta().getQuote();

        Currency baseCurrency = currencyRepository.findByCode(baseCode)
                .orElseThrow(() -> new ResourceNotFoundException("Base currency not found: " + baseCode));

        Currency quoteCurrency = currencyRepository.findByCode(quoteCode)
                .orElseThrow(() -> new ResourceNotFoundException("Quote currency not found: " + quoteCode));

        List<ExchangeRate> exchangeRates = new ArrayList<>();

        for (Map.Entry<String, List<OandaApiResponse.QuoteData>> entry : response.getData().entrySet()) {
            String dateStr = entry.getKey();
            List<OandaApiResponse.QuoteData> quotes = entry.getValue();

            if (quotes == null || quotes.isEmpty()) {
                continue;
            }

            OandaApiResponse.QuoteData quote = quotes.get(0);

            try {
                LocalDate date = LocalDate.parse(dateStr, API_DATE_FORMATTER);
                LocalDateTime dateTime = date.atTime(12, 0); // Noon time

                BigDecimal rate = new BigDecimal(quote.getClose());

                // Check if exchange rate already exists
                if (exchangeRateRepository.findByBaseCurrencyAndQuoteCurrencyAndDate(
                        baseCurrency, quoteCurrency, dateTime).isPresent()) {
                    log.debug("Exchange rate already exists for {}/{} on {}", baseCode, quoteCode, dateStr);
                    continue;
                }

                ExchangeRate exchangeRate = ExchangeRate.builder()
                        .baseCurrency(baseCurrency)
                        .quoteCurrency(quoteCurrency)
                        .rate(rate)
                        .date(dateTime)
                        .source("OANDA")
                        .build();

                exchangeRates.add(exchangeRate);
            } catch (DateTimeParseException e) {
                log.warn("Invalid date format: {}", dateStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid rate format: {}", quote.getClose());
            }
        }

        if (!exchangeRates.isEmpty()) {
            exchangeRateRepository.saveAll(exchangeRates);
            log.info("Saved {} exchange rates for {}/{}", exchangeRates.size(), baseCode, quoteCode);
        }
    }

    private ExchangeRateDTO mapToDTO(ExchangeRate exchangeRate) {
        return ExchangeRateDTO.builder()
                .id(exchangeRate.getId())
                .baseCurrencyCode(exchangeRate.getBaseCurrency().getCode())
                .baseCurrencyName(exchangeRate.getBaseCurrency().getName())
                .quoteCurrencyCode(exchangeRate.getQuoteCurrency().getCode())
                .quoteCurrencyName(exchangeRate.getQuoteCurrency().getName())
                .rate(exchangeRate.getRate())
                .date(exchangeRate.getDate())
                .source(exchangeRate.getSource())
                .build();
    }
}