package com.cathaybank.currencyexchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OandaApiResponse {

    private Meta meta;
    private Map<String, List<QuoteData>> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private String base;
        private String quote;
        @JsonProperty("data_type")
        private String dataType;
        @JsonProperty("start_date")
        private String startDate;
        @JsonProperty("end_date")
        private String endDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuoteData {
        private String date;
        private String close;
        private String high;
        private String low;
        private String open;
    }
}