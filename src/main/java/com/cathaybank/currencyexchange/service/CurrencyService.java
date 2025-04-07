package com.cathaybank.currencyexchange.service;

import com.cathaybank.currencyexchange.dto.CurrencyDTO;

import java.util.List;

public interface CurrencyService {

    List<CurrencyDTO> getAllCurrencies();

    CurrencyDTO getCurrencyById(String id);

    CurrencyDTO getCurrencyByCode(String code);

    CurrencyDTO createCurrency(CurrencyDTO currencyDTO);

    CurrencyDTO updateCurrency(String id, CurrencyDTO currencyDTO);

    void deleteCurrency(String id);

    boolean existsByCode(String code);
}