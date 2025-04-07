package com.cathaybank.currencyexchange.service;

import com.cathaybank.currencyexchange.dto.CurrencyDTO;
import com.cathaybank.currencyexchange.entity.Currency;
import com.cathaybank.currencyexchange.exception.ResourceNotFoundException;
import com.cathaybank.currencyexchange.repository.CurrencyRepository;
import com.cathaybank.currencyexchange.service.impl.CurrencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    private Currency usdCurrency;
    private Currency eurCurrency;
    private CurrencyDTO currencyDTO;

    @BeforeEach
    void setUp() {
        usdCurrency = new Currency();
        usdCurrency.setId("1");
        usdCurrency.setCode("USD");
        usdCurrency.setName("US Dollar");
        usdCurrency.setCreatedAt(LocalDateTime.now());
        usdCurrency.setUpdatedAt(LocalDateTime.now());

        eurCurrency = new Currency();
        eurCurrency.setId("2");
        eurCurrency.setCode("EUR");
        eurCurrency.setName("Euro");
        eurCurrency.setCreatedAt(LocalDateTime.now());
        eurCurrency.setUpdatedAt(LocalDateTime.now());

        currencyDTO = new CurrencyDTO();
        currencyDTO.setCode("JPY");
        currencyDTO.setName("Japanese Yen");
    }

    @Test
    void getAllCurrencies_ReturnsAllCurrencies() {
        // Arrange
        when(currencyRepository.findAllByOrderByCodeAsc()).thenReturn(Arrays.asList(eurCurrency, usdCurrency));

        // Act
        List<CurrencyDTO> result = currencyService.getAllCurrencies();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("EUR", result.get(0).getCode());
        assertEquals("USD", result.get(1).getCode());
        verify(currencyRepository).findAllByOrderByCodeAsc();
    }

    @Test
    void getCurrencyById_WhenCurrencyExists_ReturnsCurrency() {
        // Arrange
        when(currencyRepository.findById("1")).thenReturn(Optional.of(usdCurrency));

        // Act
        CurrencyDTO result = currencyService.getCurrencyById("1");

        // Assert
        assertNotNull(result);
        assertEquals("USD", result.getCode());
        assertEquals("US Dollar", result.getName());
        verify(currencyRepository).findById("1");
    }

    @Test
    void getCurrencyById_WhenCurrencyDoesNotExist_ThrowsException() {
        // Arrange
        when(currencyRepository.findById("3")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> currencyService.getCurrencyById("3"));
        verify(currencyRepository).findById("3");
    }

    @Test
    void getCurrencyByCode_WhenCurrencyExists_ReturnsCurrency() {
        // Arrange
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usdCurrency));

        // Act
        CurrencyDTO result = currencyService.getCurrencyByCode("USD");

        // Assert
        assertNotNull(result);
        assertEquals("USD", result.getCode());
        assertEquals("US Dollar", result.getName());
        verify(currencyRepository).findByCode("USD");
    }

    @Test
    void getCurrencyByCode_WhenCurrencyDoesNotExist_ThrowsException() {
        // Arrange
        when(currencyRepository.findByCode("JPY")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> currencyService.getCurrencyByCode("JPY"));
        verify(currencyRepository).findByCode("JPY");
    }

    @Test
    void createCurrency_WhenCodeDoesNotExist_CreatesCurrency() {
        // Arrange
        when(currencyRepository.existsByCode("JPY")).thenReturn(false);
        when(currencyRepository.save(any(Currency.class))).thenAnswer(invocation -> {
            Currency savedCurrency = invocation.getArgument(0);
            savedCurrency.setId("3");
            return savedCurrency;
        });

        // Act
        CurrencyDTO result = currencyService.createCurrency(currencyDTO);

        // Assert
        assertNotNull(result);
        assertEquals("3", result.getId());
        assertEquals("JPY", result.getCode());
        assertEquals("Japanese Yen", result.getName());
        verify(currencyRepository).existsByCode("JPY");
        verify(currencyRepository).save(any(Currency.class));
    }

    @Test
    void createCurrency_WhenCodeExists_ThrowsException() {
        // Arrange
        when(currencyRepository.existsByCode("JPY")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> currencyService.createCurrency(currencyDTO));
        verify(currencyRepository).existsByCode("JPY");
        verify(currencyRepository, never()).save(any(Currency.class));
    }

    @Test
    void updateCurrency_WhenCurrencyExists_UpdatesCurrency() {
        // Arrange
        CurrencyDTO updateDTO = new CurrencyDTO();
        updateDTO.setCode("USD");
        updateDTO.setName("Updated US Dollar");

        when(currencyRepository.findById("1")).thenReturn(Optional.of(usdCurrency));
        when(currencyRepository.save(any(Currency.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CurrencyDTO result = currencyService.updateCurrency("1", updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("USD", result.getCode());
        assertEquals("Updated US Dollar", result.getName());
        verify(currencyRepository).findById("1");
        verify(currencyRepository).save(any(Currency.class));
    }

    @Test
    void updateCurrency_WhenCurrencyDoesNotExist_ThrowsException() {
        // Arrange
        when(currencyRepository.findById("3")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                currencyService.updateCurrency("3", currencyDTO));
        verify(currencyRepository).findById("3");
        verify(currencyRepository, never()).save(any(Currency.class));
    }

    @Test
    void deleteCurrency_WhenCurrencyExists_DeletesCurrency() {
        // Arrange
        when(currencyRepository.existsById("1")).thenReturn(true);
        doNothing().when(currencyRepository).deleteById("1");

        // Act
        currencyService.deleteCurrency("1");

        // Assert
        verify(currencyRepository).existsById("1");
        verify(currencyRepository).deleteById("1");
    }

    @Test
    void deleteCurrency_WhenCurrencyDoesNotExist_ThrowsException() {
        // Arrange
        when(currencyRepository.existsById("3")).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> currencyService.deleteCurrency("3"));
        verify(currencyRepository).existsById("3");
        verify(currencyRepository, never()).deleteById(anyString());
    }
}