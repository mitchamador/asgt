package gbas.gtbch.jobs.schedule;

import gbas.gtbch.jobs.AbstractServerJob;
import gbas.gtbch.jobs.annotations.ServerJob;
import gbas.gtbch.mailer.MailService;
import gbas.gtbch.sapod.model.Currency;
import gbas.gtbch.sapod.model.ExchangeRate;
import gbas.gtbch.sapod.service.CurrencyService;
import gbas.gtbch.sapod.service.ExchangeRateService;
import gbas.gtbch.util.SystemInfo;
import gbas.tvk.interaction.nbrb.CurrencyDownloader;
import gbas.tvk.nsi.cash.Func;
import gbas.tvk.nsi.currency.service.CurrencyRate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

import static gbas.gtbch.mailer.MailerConstants.MAILER_CONFIG_EVENT_CURRENCY_RATES;
import static gbas.gtbch.mailer.MailerConstants.MAILER_CONFIG_EVENT_ERRORS;

@Component
@ServerJob(alias = "nbrbdownloader", name = "NBRB currency rate downloader")
public class NbrbCurrencyDownloaderJob extends AbstractServerJob {

    private final CurrencyDownloader cd;
    private final MailService mailService;
    private final String host;

    public NbrbCurrencyDownloaderJob(final CurrencyService currencyService, ExchangeRateService exchangeRateService, MailService mailService, SystemInfo systemInfo) {
        this.mailService = mailService;
        this.host = systemInfo.getHost();
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
                log(s);
            }
        };
    }

    @Scheduled(cron = "${app.jobs.nbrb-downloader.cron:-}")
    public void run() {
        runTask(() -> {
            if (cd.download(false)) {
                if (!Func.isEmpty(cd.getRateString()) && mailService.getMailProperties().isEventEnabled(MAILER_CONFIG_EVENT_CURRENCY_RATES)) {
                    //TGCore.INSTANCE.asyncSendMessage(new SendMessage("НБРБ демон", cd.getRateString()));
                    mailService.sendHtmlMessage(null, "currency rates", cd.getRateStringHtml());
                }
            } else {
                if (!Func.isEmpty(cd.getErrorMessage()) && mailService.getMailProperties().isEventEnabled(MAILER_CONFIG_EVENT_ERRORS)) {
                    String htmlMessage = "<html>" +
                            "<body>" +
                            "<p><b>Currency download error on " + host + "</b></p>" +
                            "<span style=\"white-space: pre-line\">" + cd.getErrorMessage() + "</span>" +
                            "</body>" +
                            "</html>";
                    mailService.sendHtmlMessage(null, "currency download error", htmlMessage);
                }
            }
        });
    }

    @Override
    public Map<String, Object> getWebPageData() {
        Map<String, Object> map =  super.getWebPageData();
        map.put(SERVERJOB_MVC_PAGE_HEADER, "Прием курсов валют НБ РБ");
        map.put(SERVERJOB_MVC_LOG_TITLE, "Обновление курсов валют");
        map.put(SERVERJOB_MVC_START_BUTTON_TEXT, "Обновить курсы валют");
        return map;
    }
}
