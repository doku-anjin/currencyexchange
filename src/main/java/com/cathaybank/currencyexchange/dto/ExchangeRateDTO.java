package com.cathaybank.currencyexchange.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDTO {

    private String id;
    private String baseCurrencyCode;
    private String baseCurrencyName;
    private String quoteCurrencyCode;
    private String quoteCurrencyName;
    private BigDecimal rate;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime date;

    private String source;
}