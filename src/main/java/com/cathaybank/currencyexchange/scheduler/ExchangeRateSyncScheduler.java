package com.cathaybank.currencyexchange.scheduler;

import com.cathaybank.currencyexchange.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateSyncScheduler {

    private final ExchangeRateService exchangeRateService;

    @Value("${scheduler.exchange-rate.enabled:true}")
    private boolean enabled;

    @Scheduled(cron = "${scheduler.exchange-rate.cron:0 0 * * * ?}")
    public void syncExchangeRates() {
        if (!enabled) {
            log.debug("Exchange rate sync scheduler is disabled");
            return;
        }

        log.info("Running scheduled exchange rate synchronization");
        try {
            exchangeRateService.syncExchangeRates();
            log.info("Scheduled exchange rate synchronization completed successfully");
        } catch (Exception e) {
            log.error("Error during scheduled exchange rate synchronization: {}", e.getMessage(), e);
        }
    }
}