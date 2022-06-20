package gbas.gtbch.mailer;

import org.springframework.boot.autoconfigure.mail.MailProperties;

import java.util.Arrays;

import static gbas.gtbch.mailer.MailerConstants.MAILER_CONFIG_EVENT_ERRORS;
import static gbas.gtbch.mailer.MailerConstants.MAILER_CONFIG_EVENT_STARTUP;

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
        return Boolean.TRUE.toString().equals(getProperties().getOrDefault(MailerConstants.MAILER_CONFIG_EVENT_PREFIX + "." + event, Boolean.toString(getDefaultEnable(event))));
    }

    /**
     * get default enabled values for events
     * @param event
     * @return
     */
    private boolean getDefaultEnable(String event) {
        return  Arrays.asList(MAILER_CONFIG_EVENT_ERRORS, MAILER_CONFIG_EVENT_STARTUP).contains(event);
    }
}
