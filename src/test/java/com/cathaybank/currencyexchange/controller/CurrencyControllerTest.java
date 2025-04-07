package com.cathaybank.currencyexchange.controller;

import com.cathaybank.currencyexchange.dto.CurrencyDTO;
import com.cathaybank.currencyexchange.exception.ResourceNotFoundException;
import com.cathaybank.currencyexchange.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CurrencyService currencyService;

    private CurrencyDTO usdCurrency;
    private CurrencyDTO eurCurrency;
    private List<CurrencyDTO> currencies;

    @BeforeEach
    void setUp() {
        usdCurrency = new CurrencyDTO();
        usdCurrency.setId("1");
        usdCurrency.setCode("USD");
        usdCurrency.setName("US Dollar");

        eurCurrency = new CurrencyDTO();
        eurCurrency.setId("2");
        eurCurrency.setCode("EUR");
        eurCurrency.setName("Euro");

        currencies = Arrays.asList(usdCurrency, eurCurrency);
    }

    @Test
    void getAllCurrencies_ReturnsAllCurrencies() throws Exception {
        // Arrange
        when(currencyService.getAllCurrencies()).thenReturn(currencies);

        // Act & Assert
        mockMvc.perform(get("/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].code", is("USD")))
                .andExpect(jsonPath("$.data[1].code", is("EUR")));

        verify(currencyService).getAllCurrencies();
    }

    @Test
    void getCurrencyById_WhenCurrencyExists_ReturnsCurrency() throws Exception {
        // Arrange
        when(currencyService.getCurrencyById("1")).thenReturn(usdCurrency);

        // Act & Assert
        mockMvc.perform(get("/currencies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is("1")))
                .andExpect(jsonPath("$.data.code", is("USD")))
                .andExpect(jsonPath("$.data.name", is("US Dollar")));

        verify(currencyService).getCurrencyById("1");
    }

    @Test
    void getCurrencyById_WhenCurrencyDoesNotExist_ReturnsNotFound() throws Exception {
        // Arrange
        when(currencyService.getCurrencyById("3")).thenThrow(
                new ResourceNotFoundException("Currency not found with id: 3"));

        // Act & Assert
        mockMvc.perform(get("/currencies/3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Currency not found")));

        verify(currencyService).getCurrencyById("3");
    }

    @Test
    void getCurrencyByCode_WhenCurrencyExists_ReturnsCurrency() throws Exception {
        // Arrange
        when(currencyService.getCurrencyByCode("USD")).thenReturn(usdCurrency);

        // Act & Assert
        mockMvc.perform(get("/currencies/code/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is("1")))
                .andExpect(jsonPath("$.data.code", is("USD")))
                .andExpect(jsonPath("$.data.name", is("US Dollar")));

        verify(currencyService).getCurrencyByCode("USD");
    }

    @Test
    void createCurrency_WithValidData_CreatesCurrency() throws Exception {
        // Arrange
        CurrencyDTO newCurrency = new CurrencyDTO();
        newCurrency.setCode("JPY");
        newCurrency.setName("Japanese Yen");

        CurrencyDTO createdCurrency = new CurrencyDTO();
        createdCurrency.setId("3");
        createdCurrency.setCode("JPY");
        createdCurrency.setName("Japanese Yen");

        when(currencyService.createCurrency(any(CurrencyDTO.class))).thenReturn(createdCurrency);

        // Act & Assert
        mockMvc.perform(post("/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCurrency)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("created successfully")))
                .andExpect(jsonPath("$.data.id", is("3")))
                .andExpect(jsonPath("$.data.code", is("JPY")))
                .andExpect(jsonPath("$.data.name", is("Japanese Yen")));

        verify(currencyService).createCurrency(any(CurrencyDTO.class));
    }

    @Test
    void createCurrency_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Arrange
        CurrencyDTO invalidCurrency = new CurrencyDTO();
        invalidCurrency.setCode("INVALID");  // Code should be 3 chars
        invalidCurrency.setName("Invalid Currency");

        // Act & Assert
        mockMvc.perform(post("/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCurrency)))
                .andExpect(status().isBadRequest());

        verify(currencyService, never()).createCurrency(any(CurrencyDTO.class));
    }

    @Test
    void updateCurrency_WithValidData_UpdatesCurrency() throws Exception {
        // Arrange
        CurrencyDTO updateCurrency = new CurrencyDTO();
        updateCurrency.setCode("USD");
        updateCurrency.setName("Updated US Dollar");

        CurrencyDTO updatedCurrency = new CurrencyDTO();
        updatedCurrency.setId("1");
        updatedCurrency.setCode("USD");
        updatedCurrency.setName("Updated US Dollar");

        when(currencyService.updateCurrency(eq("1"), any(CurrencyDTO.class))).thenReturn(updatedCurrency);

        // Act & Assert
        mockMvc.perform(put("/currencies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCurrency)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("updated successfully")))
                .andExpect(jsonPath("$.data.id", is("1")))
                .andExpect(jsonPath("$.data.code", is("USD")))
                .andExpect(jsonPath("$.data.name", is("Updated US Dollar")));

        verify(currencyService).updateCurrency(eq("1"), any(CurrencyDTO.class));
    }

    @Test
    void deleteCurrency_WhenCurrencyExists_DeletesCurrency() throws Exception {
        // Arrange
        doNothing().when(currencyService).deleteCurrency("1");

        // Act & Assert
        mockMvc.perform(delete("/currencies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("deleted successfully")));

        verify(currencyService).deleteCurrency("1");
    }

    @Test
    void deleteCurrency_WhenCurrencyDoesNotExist_ReturnsNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Currency not found with id: 3"))
                .when(currencyService).deleteCurrency("3");

        // Act & Assert
        mockMvc.perform(delete("/currencies/3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Currency not found")));

        verify(currencyService).deleteCurrency("3");
    }
}