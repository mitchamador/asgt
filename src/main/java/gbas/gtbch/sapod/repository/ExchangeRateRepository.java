package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.Currency;
import gbas.gtbch.sapod.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Integer> {
    List<ExchangeRate> findAllByCurrencyAndBaseCurrencyAndFromDate(Currency currency, Currency baseCurrency, Date fromDate);
}