package com.cathaybank.currencyexchange.service.impl;

import com.cathaybank.currencyexchange.dto.CurrencyDTO;
import com.cathaybank.currencyexchange.entity.Currency;
import com.cathaybank.currencyexchange.exception.ResourceNotFoundException;
import com.cathaybank.currencyexchange.repository.CurrencyRepository;
import com.cathaybank.currencyexchange.service.CurrencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CurrencyDTO> getAllCurrencies() {
        log.debug("Getting all currencies ordered by code");
        return currencyRepository.findAllByOrderByCodeAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyDTO getCurrencyById(String id) {
        log.debug("Getting currency by id: {}", id);
        return currencyRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyDTO getCurrencyByCode(String code) {
        log.debug("Getting currency by code: {}", code);
        return currencyRepository.findByCode(code)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with code: " + code));
    }

    @Override
    @Transactional
    public CurrencyDTO createCurrency(@Valid CurrencyDTO currencyDTO) {
        log.debug("Creating new currency: {}", currencyDTO);
        if (currencyRepository.existsByCode(currencyDTO.getCode())) {
            throw new IllegalArgumentException("Currency with code " + currencyDTO.getCode() + " already exists");
        }

        Currency currency = mapToEntity(currencyDTO);
        currency = currencyRepository.save(currency);
        return mapToDTO(currency);
    }

    @Override
    @Transactional
    public CurrencyDTO updateCurrency(String id, @Valid CurrencyDTO currencyDTO) {
        log.debug("Updating currency with id: {}", id);
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));

        if (!currency.getCode().equals(currencyDTO.getCode())
                && currencyRepository.existsByCode(currencyDTO.getCode())) {
            throw new IllegalArgumentException("Currency with code " + currencyDTO.getCode() + " already exists");
        }

        currency.setCode(currencyDTO.getCode());
        currency.setName(currencyDTO.getName());
        currency.setUpdatedAt(LocalDateTime.now());
        currency.setUpdatedBy("SYSTEM"); // In a real app, this would come from authentication

        currency = currencyRepository.save(currency);
        return mapToDTO(currency);
    }

    @Override
    @Transactional
    public void deleteCurrency(String id) {
        log.debug("Deleting currency with id: {}", id);
        if (!currencyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Currency not found with id: " + id);
        }
        currencyRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return currencyRepository.existsByCode(code);
    }

    private CurrencyDTO mapToDTO(Currency currency) {
        return CurrencyDTO.builder()
                .id(currency.getId())
                .code(currency.getCode())
                .name(currency.getName())
                .build();
    }

    private Currency mapToEntity(CurrencyDTO dto) {
        return Currency.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .build();
    }
}