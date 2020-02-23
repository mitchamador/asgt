package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.Currency;
import gbas.gtbch.sapod.model.ExchangeRate;
import gbas.gtbch.sapod.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("exchangeRateService")
public class ExchangeRateServiceImpl implements ExchangeRateService {

    @Autowired
    ExchangeRateRepository exchangeRateRepository;

    @Override
    public ExchangeRate findExchangeRateById(Integer id) {
        return exchangeRateRepository.findById(id).orElse(null);
    }

    @Override
    public List<ExchangeRate> findAllExchangeRateByCurrencyAndBaseCurrencyAndFromDate(Currency currency, Currency baseCurrency, Date fromDate) {
        return exchangeRateRepository.findAllByCurrencyAndBaseCurrencyAndFromDate(currency, baseCurrency, fromDate);
    }

    @Override
    public ExchangeRate save(ExchangeRate rate) {
        return exchangeRateRepository.save(rate);
    }

    @Override
    public List<ExchangeRate> save(List<ExchangeRate> list) {
        return exchangeRateRepository.saveAll(list);
    }

    @Override
    public void delete(List<ExchangeRate> list) {
        exchangeRateRepository.deleteAll(list);
    }
}
