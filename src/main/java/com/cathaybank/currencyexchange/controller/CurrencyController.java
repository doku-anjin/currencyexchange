package com.cathaybank.currencyexchange.controller;

import com.cathaybank.currencyexchange.dto.ApiResponse;
import com.cathaybank.currencyexchange.dto.CurrencyDTO;
import com.cathaybank.currencyexchange.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
@Tag(name = "Currency", description = "Currency management APIs")
@Slf4j
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    @Operation(summary = "Get all currencies", description = "Retrieves all currencies sorted by code")
    public ResponseEntity<ApiResponse<List<CurrencyDTO>>> getAllCurrencies() {
        log.info("GET /currencies - Retrieving all currencies");
        List<CurrencyDTO> currencies = currencyService.getAllCurrencies();
        return ResponseEntity.ok(ApiResponse.success(currencies));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get currency by ID", description = "Retrieves a currency by its ID")
    public ResponseEntity<ApiResponse<CurrencyDTO>> getCurrencyById(@PathVariable String id) {
        log.info("GET /currencies/{} - Retrieving currency by ID", id);
        CurrencyDTO currency = currencyService.getCurrencyById(id);
        return ResponseEntity.ok(ApiResponse.success(currency));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get currency by code", description = "Retrieves a currency by its code")
    public ResponseEntity<ApiResponse<CurrencyDTO>> getCurrencyByCode(@PathVariable String code) {
        log.info("GET /currencies/code/{} - Retrieving currency by code", code);
        CurrencyDTO currency = currencyService.getCurrencyByCode(code);
        return ResponseEntity.ok(ApiResponse.success(currency));
    }

    @PostMapping
    @Operation(summary = "Create currency", description = "Creates a new currency")
    public ResponseEntity<ApiResponse<CurrencyDTO>> createCurrency(@Valid @RequestBody CurrencyDTO currencyDTO) {
        log.info("POST /currencies - Creating new currency: {}", currencyDTO);
        CurrencyDTO createdCurrency = currencyService.createCurrency(currencyDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Currency created successfully", createdCurrency));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update currency", description = "Updates an existing currency")
    public ResponseEntity<ApiResponse<CurrencyDTO>> updateCurrency(
            @PathVariable String id,
            @Valid @RequestBody CurrencyDTO currencyDTO) {
        log.info("PUT /currencies/{} - Updating currency: {}", id, currencyDTO);
        CurrencyDTO updatedCurrency = currencyService.updateCurrency(id, currencyDTO);
        return ResponseEntity.ok(ApiResponse.success("Currency updated successfully", updatedCurrency));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete currency", description = "Deletes a currency by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteCurrency(@PathVariable String id) {
        log.info("DELETE /currencies/{} - Deleting currency", id);
        currencyService.deleteCurrency(id);
        return ResponseEntity.ok(ApiResponse.success("Currency deleted successfully", null));
    }
}