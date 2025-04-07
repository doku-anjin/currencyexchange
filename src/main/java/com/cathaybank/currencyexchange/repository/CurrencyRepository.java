package com.cathaybank.currencyexchange.repository;

import com.cathaybank.currencyexchange.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {

    Optional<Currency> findByCode(String code);

    boolean existsByCode(String code);

    List<Currency> findAllByOrderByCodeAsc();
}