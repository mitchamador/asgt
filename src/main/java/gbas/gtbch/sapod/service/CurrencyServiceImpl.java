package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.Currency;
import gbas.gtbch.sapod.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service("currencyService")
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    CurrencyRepository currencyRepository;

    @Override
    public Currency findCurrencyByShortName(String shortName) {
        List<Currency> list = currencyRepository.findAllByShortName(shortName);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Currency findCurrencyById(int id) {
        return currencyRepository.findById(id).orElse(null);
    }

    @Override
    public List<Currency> getCurrencies() {
        return currencyRepository.findAll();
    }

    @Override
    public Currency save(Currency currency) {
        return currencyRepository.save(currency);
    }

    @Override
    public void deleteById(int id) {
        currencyRepository.deleteById(id);
    }

    @Override
    public void deleteByCurrency(Currency currency) {
        currencyRepository.delete(currency);
    }

    @Override
    public void deleteByShortName(String shortName) {
        currencyRepository.deleteAllByShortName(shortName);
    }

    @Override
    public List<Currency> findCurrencyWithRates() {
        return currencyRepository.findAllByShortNameIsNotNullAndShortNameNot("");
    }
}
