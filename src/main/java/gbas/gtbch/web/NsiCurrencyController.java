package gbas.gtbch.web;

import gbas.gtbch.sapod.model.Currency;
import gbas.gtbch.sapod.model.ExchangeRate;
import gbas.gtbch.sapod.repository.ExchangeRateRepository;
import gbas.gtbch.sapod.service.CurrencyService;
import gbas.gtbch.sapod.service.ExchangeRateService;
import gbas.gtbch.util.UtilDate8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user/nsi/currency")
public class NsiCurrencyController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * currency page
     * @return
     */
    @GetMapping("")
    public ModelAndView currencyPage() {
        ModelAndView model = new ModelAndView("user/nsi/currency");
        return model;
    }

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private CurrencyService currencyService;
    /**
     * exchange rate fragment
     * @return
     */
    @GetMapping("rates")
    public ModelAndView exchangeRateFragment(@RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Date date) {
        ModelAndView model = new ModelAndView("fragments/currency :: rates");

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
        model.addObject("rates", exchangeRates);

        //model.addObject("rates", exchangeRateService.getRates(date));

        return model;
    }

}
