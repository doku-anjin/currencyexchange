package com.cathaybank.currencyexchange.service;

import com.cathaybank.currencyexchange.dto.OandaApiResponse;

import java.time.LocalDate;

public interface OandaApiService {

    OandaApiResponse getExchangeRates(String baseCode, String quoteCode, LocalDate startDate, LocalDate endDate);
}