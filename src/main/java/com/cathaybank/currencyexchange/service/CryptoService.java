package com.cathaybank.currencyexchange.service;

public interface CryptoService {

    String encrypt(String data);

    String decrypt(String encryptedData);
}