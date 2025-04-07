package com.cathaybank.currencyexchange.service;

import com.cathaybank.currencyexchange.dto.ExchangeRateDTO;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeRateService {

    List<ExchangeRateDTO> getExchangeRates(String baseCode, String quoteCode, LocalDate startDate, LocalDate endDate);

    ExchangeRateDTO getLatestExchangeRate(String baseCode, String quoteCode);

    ExchangeRateDTO createExchangeRate(ExchangeRateDTO exchangeRateDTO);

    void syncExchangeRates();
}