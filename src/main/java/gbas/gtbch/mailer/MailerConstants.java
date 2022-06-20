package gbas.gtbch.mailer;

/**
 * mailer constants
 */
public class MailerConstants {
    // custom properties names
    /**
     * use value in this propertie as name in 'from' field
     */
    public final static String MAILER_CONFIG_FROM = "gt.mailer.from";
    /**
     * use value in this propertie as name (or names divided by ',') in 'to' field
     */
    public final static String MAILER_CONFIG_TO = "gt.mailer.to";
    /**
     * use value of this propertie in 'subject' field
     */
    public final static String MAILER_CONFIG_SUBJECT = "gt.mailer.subject";

    /**
     * prefix for mailer event
     */
    public static final String MAILER_CONFIG_EVENT_PREFIX = "gt.mailer.event";

    /**
     * errors event
     */
    public static final String MAILER_CONFIG_EVENT_ERROR = "error";

    /**
     * currency's rates event
     */
    public static final String MAILER_CONFIG_EVENT_CURRENCY_RATES = "rates";


    // default values
    /**
     * default subject
     */
    public final static String MAILER_DEFAULT_SUBJECT = "AS GT";
}
