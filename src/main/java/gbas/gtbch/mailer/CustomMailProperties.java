package gbas.gtbch.mailer;

import org.springframework.boot.autoconfigure.mail.MailProperties;

/**
 * custom {@link MailProperties} with additional field(s)
 */
public class CustomMailProperties extends MailProperties {

    /**
     * enable mailer
     */
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * send currency rates
     * @return true, if needed
     */
    public boolean isSendCurrencyRates() {
        return Boolean.TRUE.toString().equals(getProperties().getOrDefault(MailerConstants.CONFIG_MAILER_SEND_CURRENCY_RATES, Boolean.FALSE.toString()));
    }
}
