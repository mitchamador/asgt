package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.ExchangeRate;
import gbas.gtbch.sapod.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("exchangeRateService")
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateServiceImpl(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public ExchangeRate findExchangeRateById(Integer id) {
        return exchangeRateRepository.findById(id).orElse(null);
    }

    /**
     *
     * @param rate
     * @return
     */
    @Override
    public ExchangeRate save(ExchangeRate rate) {
        return exchangeRateRepository.save(rate);
    }

    /**
     *
     * @param list
     * @return
     */
    @Override
    public List<ExchangeRate> save(List<ExchangeRate> list) {
        return exchangeRateRepository.saveAll(list);
    }

    /**
     *
     * @param list
     */
    @Override
    public void delete(List<ExchangeRate> list) {
        exchangeRateRepository.deleteAll(list);
    }

    /**
     *
     * @param id {@link ExchangeRate#getId()}
     */
    @Override
    public void delete(int id) {
        exchangeRateRepository.deleteById(id);
    }

    /**
     *
     * @param dateFrom
     * @param dateTo
     * @return
     */
    @Override
    public List<ExchangeRate> getRates(Date dateFrom, Date dateTo) {
        return exchangeRateRepository.findAllByFromDateBetween(dateFrom, dateTo);
    }

    /**
     *
     * @param date
     * @return
     */
    @Override
    public List<ExchangeRate> getRates(Date date) {
        return exchangeRateRepository.findAllByFromDateBetween(date, date);
    }

    /**
     *
     * @param shortName
     * @param dateFrom
     * @param dateTo
     * @return
     */
    @Override
    public List<ExchangeRate> getRates(String shortName, Date dateFrom, Date dateTo) {
        return exchangeRateRepository.findAllByCurrency_ShortNameAndFromDateBetween(shortName, dateFrom, dateTo);
    }

    /**
     *
     * @param shortName
     * @param date
     * @return
     */
    @Override
    public ExchangeRate getRate(String shortName, Date date) {
        return exchangeRateRepository.findFirstByCurrency_ShortNameAndFromDateLessThanEqualOrderByFromDateDesc(shortName, date);
    }

    /**
     *
     * @param shortName
     * @param baseShortName
     * @param date
     * @return
     */
    @Override
    public List<ExchangeRate> getRates(String shortName, String baseShortName, Date date) {
        return exchangeRateRepository.findAllByCurrency_ShortNameAndBaseCurrency_ShortNameAndFromDate(shortName, baseShortName, date);
    }
}
