package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.Currency;
import gbas.gtbch.sapod.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Integer> {
    /**
     *
     * @param currency
     * @param baseCurrency
     * @param fromDate
     * @return
     */
    List<ExchangeRate> findAllByCurrencyAndBaseCurrencyAndFromDate(Currency currency, Currency baseCurrency, Date fromDate);

    /**
     *
     * @param dateFrom
     * @param dateTo
     * @return
     */
    List<ExchangeRate> findAllByFromDateBetween(Date dateFrom, Date dateTo);

    /**
     *
     * @param symbol
     * @param dateFrom
     * @param dateTo
     * @return
     */
    List<ExchangeRate> findAllByCurrency_ShortNameAndFromDateBetween(String symbol, Date dateFrom, Date dateTo);

    /**
     *
     * @param symbol
     * @param date
     * @return
     */
    ExchangeRate findFirstByCurrency_ShortNameAndFromDateLessThanEqualOrderByFromDateDesc(String symbol, Date date);

}