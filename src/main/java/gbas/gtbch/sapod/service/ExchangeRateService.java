package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.Currency;
import gbas.gtbch.sapod.model.ExchangeRate;

import java.util.Date;
import java.util.List;

public interface ExchangeRateService {

    ExchangeRate findExchangeRateById(Integer id);

    List<ExchangeRate> findAllExchangeRateByCurrencyAndBaseCurrencyAndFromDate(Currency currency, Currency baseCurrency, Date fromDate);

    ExchangeRate save(ExchangeRate rate);

    List<ExchangeRate> save(List<ExchangeRate> list);

    void delete(List<ExchangeRate> list);

}
