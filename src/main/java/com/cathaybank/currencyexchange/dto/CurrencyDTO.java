package com.cathaybank.currencyexchange.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDTO {

    private String id;

    @NotBlank(message = "{currency.code.notBlank}")
    @Size(min = 3, max = 3, message = "{currency.code.size}")
    @Pattern(regexp = "^[A-Z]{3}$", message = "{currency.code.pattern}")
    private String code;

    @NotBlank(message = "{currency.name.notBlank}")
    @Size(max = 100, message = "{currency.name.size}")
    private String name;
}