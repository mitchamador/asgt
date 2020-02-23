package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.Currency;

import java.util.List;

public interface CurrencyService {

    Currency findCurrencyByShortName(String shortName);

    Currency findCurrencyById(int id);

    List<Currency> getCurrencies();

    Currency save(Currency currency);

    void deleteById(int id);

    void deleteByCurrency(Currency currency);

    void deleteByShortName(String shortName);

}
