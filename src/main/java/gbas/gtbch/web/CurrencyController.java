package gbas.gtbch.web;

import gbas.gtbch.sapod.model.Currency;
import gbas.gtbch.sapod.model.ExchangeRate;
import gbas.gtbch.sapod.model.RecalcRate;
import gbas.gtbch.sapod.service.CurrencyService;
import gbas.gtbch.sapod.service.ExchangeRateService;
import gbas.gtbch.sapod.service.RecalcRateService;
import gbas.gtbch.util.UtilDate8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class CurrencyController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final ExchangeRateService exchangeRateService;

    private final CurrencyService currencyService;

    private final RecalcRateService recalcRateService;

    public CurrencyController(ExchangeRateService exchangeRateService, CurrencyService currencyService, RecalcRateService recalcRateService) {
        this.exchangeRateService = exchangeRateService;
        this.currencyService = currencyService;
        this.recalcRateService = recalcRateService;
    }

    /**
     * currency page
     * @return
     */
    @GetMapping("/user/nsi/currency")
    public ModelAndView currencyPage() {
        ModelAndView model = new ModelAndView("user/nsi/currency");
        return model;
    }

    /**
     * exchange rate fragment
     * @return
     */
    @GetMapping("/user/nsi/currency/rates")
    public ModelAndView exchangeRateFragment(@RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Date date) {
        ModelAndView model = new ModelAndView("fragments/currency :: rates");

        List<ExchangeRate> exchangeRates = getRates(date);

        model.addObject("rates", exchangeRates);
        model.addObject("requestDate", date);

        //model.addObject("rates", exchangeRateService.getRates(date));

        return model;
    }

    /**
     * rate of recalc fragment
     * @return
     */
    @GetMapping("/user/nsi/currency/recalcrates")
    public ModelAndView recalcRateFragment() {
        ModelAndView model = new ModelAndView("fragments/currency :: recalcrate");

        List<RecalcRate> recalcRates = recalcRateService.getRecalcRates();

        model.addObject("recalcRates", recalcRates);

        return model;
    }

    private List<ExchangeRate> getRates(Date date) {
        List<ExchangeRate> exchangeRates = new ArrayList<>();

        for (Currency c : currencyService.findCurrencyWithRates()) {
            if (Arrays.asList("BYR", "BYN").contains(c.getShortName())) continue;

            ExchangeRate r = exchangeRateService.getRate(c.getShortName(), date);
            if (r != null) {
                if (!UtilDate8.getStringDate(date).equals(UtilDate8.getStringDate(r.getFromDate()))) {
                    r.setComment(" (c " + UtilDate8.getStringDate(r.getFromDate()) + ")");
                }
                exchangeRates.add(r);
            }
        }
        return exchangeRates;
    }

    @RequestMapping(value = "/api/currency/rates", method = RequestMethod.GET)
    public ResponseEntity<List<ExchangeRate>> getExchangeRates(@RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Date date) {
        return new ResponseEntity<>(getRates(date), HttpStatus.OK);
    }

    /**
     * get exchange rate by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/currency/rate/{id:[\\d]+}", method = RequestMethod.GET)
    public ResponseEntity<ExchangeRate> getExchangeRate(@PathVariable int id) {
        return new ResponseEntity<>(exchangeRateService.findExchangeRateById(id), HttpStatus.OK);
    }

    /**
     * get currency by mnemocode
     * @param mnemo mnemocode of currency
     * @return
     */
    @RequestMapping(value = "/api/currency", method = RequestMethod.GET)
    public ResponseEntity<Currency> getCurrency(@PathVariable String mnemo) {
        return new ResponseEntity<>(currencyService.findCurrencyByShortName(mnemo), HttpStatus.OK);
    }

    /**
     * get currency's list
     * @return
     */
    @RequestMapping(value = "/api/currencies", method = RequestMethod.GET)
    public ResponseEntity<List<Currency>> getCurrencies() {
        List<Currency> currencyList = currencyService.getCurrencies();
        return new ResponseEntity<>(currencyList, HttpStatus.OK);
    }

    /**
     * delete exchange rate by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/currency/rate/{id:[\\d]+}", method = RequestMethod.DELETE)
    public ResponseEntity deleteExchangeRate(@PathVariable int id) {

        exchangeRateService.delete(id);

        return ResponseEntity.ok().build();
    }

    /**
     * update {@link ExchangeRate}
     * @param id id of {@link ExchangeRate} to save
     * @param rate {@link ExchangeRate} to save
     * @return
     */
    @RequestMapping(value = "/api/currency/rate/{id:[\\d]+}", method = RequestMethod.PUT)
    public ResponseEntity<Integer> updateExchangeRate(@PathVariable int id, @RequestBody ExchangeRate rate) {

        rate.setId(id);
        ExchangeRate savedRate = exchangeRateService.save(fillCurrencies(rate));

        return savedRate != null && savedRate.getId() != 0 ? ResponseEntity.ok(savedRate.getId()) : ResponseEntity.badRequest().build();
    }

    /**
     * save {@link ExchangeRate}
     * @param rate {@link ExchangeRate} to save
     * @return
     */
    @RequestMapping(value = "/api/currency/rate", method = RequestMethod.POST)
    public ResponseEntity<Integer> saveExchangeRate(@RequestBody ExchangeRate rate) {

        exchangeRateService.delete(exchangeRateService.getRates(rate.getCurrency().getShortName(), rate.getBaseCurrency().getShortName(), rate.getFromDate()));

        rate.setId(0);
        ExchangeRate savedRate = exchangeRateService.save(fillCurrencies(rate));

        return savedRate != null && savedRate.getId() != 0 ? ResponseEntity.created(URI.create("/api/currency/rate/" + savedRate.getId())).body(savedRate.getId()) : ResponseEntity.badRequest().build();
    }

    /**
     * fill {@link Currency} objects based on mnemocode {@see Currency#getShortName}
     * @param r
     * @return
     */
    private ExchangeRate fillCurrencies(ExchangeRate r) {
        r.setCurrency(currencyService.findCurrencyByShortName(r.getCurrency().getShortName()));
        r.setBaseCurrency(currencyService.findCurrencyByShortName(r.getBaseCurrency().getShortName()));
        return r;
    }

    @RequestMapping(value = "/api/currency/recalcrates", method = RequestMethod.GET)
    public ResponseEntity<List<RecalcRate>> getRecalcRates() {
        return new ResponseEntity<>(recalcRateService.getRecalcRates(), HttpStatus.OK);
    }

    /**
     * get exchange rate by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/currency/recalcrate/{id:[\\d]+}", method = RequestMethod.GET)
    public ResponseEntity<RecalcRate> getRecalcRate(@PathVariable int id) {
        return new ResponseEntity<>(recalcRateService.findRecalcRateById(id), HttpStatus.OK);
    }

    /**
     * delete {@link RecalcRate} by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/currency/recalcrate/{id:[\\d]+}", method = RequestMethod.DELETE)
    public ResponseEntity deleteRecalcRate(@PathVariable int id) {

        recalcRateService.delete(id);

        return ResponseEntity.ok().build();
    }

    /**
     * update {@link RecalcRate}
     * @param id id of {@link RecalcRate} to save
     * @param recalcRate {@link RecalcRate} to save
     * @return
     */
    @RequestMapping(value = "/api/currency/recalcrate/{id:[\\d]+}", method = RequestMethod.PUT)
    public ResponseEntity<Integer> updateRecalcRate(@PathVariable int id, @RequestBody RecalcRate recalcRate) {

        recalcRate.setId(id);
        RecalcRate savedRate = recalcRateService.save(recalcRate);

        return savedRate != null && savedRate.getId() != 0 ? ResponseEntity.ok(savedRate.getId()) : ResponseEntity.badRequest().build();
    }

    /**
     * save {@link RecalcRate}
     * @param recalcRate {@link RecalcRate} to save
     * @return
     */
    @RequestMapping(value = "/api/currency/recalcrate", method = RequestMethod.POST)
    public ResponseEntity<Integer> saveRecalcRate(@RequestBody RecalcRate recalcRate) {

        recalcRate.setId(0);
        RecalcRate savedRate = recalcRateService.save(recalcRate);

        return savedRate != null && savedRate.getId() != 0 ? ResponseEntity.created(URI.create("/api/currency/recalcrate/" + savedRate.getId())).body(savedRate.getId()) : ResponseEntity.badRequest().build();
    }

}
