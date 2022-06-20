package gbas.gtbch.mailer;

import org.springframework.boot.autoconfigure.mail.MailProperties;

import static gbas.gtbch.mailer.MailerConstants.MAILER_CONFIG_EVENT_ERROR;

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
     * check, if mailer event is enabled
     * @param event
     * @return
     */
    public boolean isEventEnabled(String event) {
        return Boolean.TRUE.toString().equals(getProperties().getOrDefault(MailerConstants.MAILER_CONFIG_EVENT_PREFIX + "." + event, MAILER_CONFIG_EVENT_ERROR.equals(event) ? Boolean.TRUE.toString() : Boolean.FALSE.toString()));
    }
}
