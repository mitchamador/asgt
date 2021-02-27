package gbas.gtbch.schedule;

import gbas.gtbch.sapod.model.Currency;
import gbas.gtbch.sapod.model.ExchangeRate;
import gbas.gtbch.sapod.service.CurrencyService;
import gbas.gtbch.sapod.service.ExchangeRateService;
import gbas.gtbch.util.ServerJob;
import gbas.gtbch.util.UtilDate8;
import gbas.tvk.interaction.nbrb.CurrencyDownloader;
import gbas.tvk.nsi.currency.service.CurrencyRate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
public class NbrbCurrencyDownloaderJob extends ServerJob {

    private final CurrencyDownloader cd;

    private void log(String s, boolean addJobLog) {
        if (addJobLog) {
            super.log("" + UtilDate8.getStringFullDate(new Date()) + " - " + s);
        }
        logger.info(s);
    }

    public NbrbCurrencyDownloaderJob(final CurrencyService currencyService, ExchangeRateService exchangeRateService) {
        cd = new CurrencyDownloader("BYN") {
            @Override
            public boolean setCurrencyRate(CurrencyRate currencyRate) {
                if (currencyRate != null) {
                    Currency c1 = currencyService.findCurrencyByShortName(currencyRate.symbol);
                    Currency c2 = currencyService.findCurrencyByShortName(currencyRate.baseSymbol);

                    if (c1 != null && c2 != null) {
                        exchangeRateService.delete(exchangeRateService.getRates(c1.getShortName(), c2.getShortName(), currencyRate.date));

                        ExchangeRate exchangeRate = new ExchangeRate();
                        exchangeRate.setCurrency(c1);
                        exchangeRate.setBaseCurrency(c2);
                        exchangeRate.setFromDate(currencyRate.date);
                        exchangeRate.setHowMuch(currencyRate.how_much);
                        exchangeRate.setRate(currencyRate.rate);
                        exchangeRateService.save(exchangeRate);

                        return true;
                    }
                }
                return false;
            }

            @Override
            public void logger(String s) {
                log(s, true);
            }
        };
    }

    @Value("${app.jobs.nbrb-downloader.cron:-}")
    protected String cronString;

    @Override
    public String getJobName() {
        return "NBRB currency rate downloader";
    }

    @Scheduled(cron = "${app.jobs.nbrb-downloader.cron:-}")
    public void run() {
        run(() -> cd.download(false));
    }

    @PostConstruct
    public void runPostConstruct() {
        if (getJobEnabled(cronString)) {
            //run();
        }
    }

}
