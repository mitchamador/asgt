package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.Currency;
import gbas.gtbch.sapod.model.ExchangeRate;

import java.util.Date;
import java.util.List;

public interface ExchangeRateService {

    /**
     *
     * @param id
     * @return
     */
    ExchangeRate findExchangeRateById(Integer id);

    /**
     *
     * @param currency
     * @param baseCurrency
     * @param fromDate
     * @return
     */
    List<ExchangeRate> findAllExchangeRateByCurrencyAndBaseCurrencyAndFromDate(Currency currency, Currency baseCurrency, Date fromDate);

    /**
     *
     * @param rate
     * @return
     */
    ExchangeRate save(ExchangeRate rate);

    /**
     *
     * @param list
     * @return
     */
    List<ExchangeRate> save(List<ExchangeRate> list);

    /**
     *
     * @param list
     */
    void delete(List<ExchangeRate> list);

    /**
     * delete {@link ExchangeRate}
     * @param id {@link ExchangeRate#getId()}
     */
    void delete(int id);

    /**
     * get exchange rates by period
     * @param dateFrom
     * @param dateTo
     * @return
     */
    List<ExchangeRate> getRates(Date dateFrom, Date dateTo);

    /**
     *
     * @param date
     * @return
     */
    List<ExchangeRate> getRates(Date date);

    /**
     * get exchange rates by period
     * @param shortName
     * @param dateFrom
     * @param dateTo
     * @return
     */
    List<ExchangeRate> getRates(String shortName, Date dateFrom, Date dateTo);

    /**
     *
     * @param shortName
     * @param date
     * @return
     */
    ExchangeRate getRate(String shortName, Date date);
}
