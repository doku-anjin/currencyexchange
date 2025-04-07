package com.cathaybank.currencyexchange.repository;

import com.cathaybank.currencyexchange.entity.Currency;
import com.cathaybank.currencyexchange.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, String> {

    @Query("SELECT er FROM ExchangeRate er WHERE er.baseCurrency.code = :baseCode " +
            "AND er.quoteCurrency.code = :quoteCode " +
            "AND er.date BETWEEN :startDate AND :endDate " +
            "ORDER BY er.date ASC")
    List<ExchangeRate> findByBaseCurrencyCodeAndQuoteCurrencyCodeAndDateBetween(
            @Param("baseCode") String baseCode,
            @Param("quoteCode") String quoteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT er FROM ExchangeRate er WHERE er.baseCurrency.code = :baseCode " +
            "AND er.quoteCurrency.code = :quoteCode " +
            "ORDER BY er.date DESC LIMIT 1")
    Optional<ExchangeRate> findLatestByBaseCurrencyCodeAndQuoteCurrencyCode(
            @Param("baseCode") String baseCode,
            @Param("quoteCode") String quoteCode);

    @Query("SELECT er FROM ExchangeRate er WHERE er.baseCurrency = :baseCurrency " +
            "AND er.quoteCurrency = :quoteCurrency " +
            "AND er.date = :date")
    Optional<ExchangeRate> findByBaseCurrencyAndQuoteCurrencyAndDate(
            @Param("baseCurrency") Currency baseCurrency,
            @Param("quoteCurrency") Currency quoteCurrency,
            @Param("date") LocalDateTime date);
}