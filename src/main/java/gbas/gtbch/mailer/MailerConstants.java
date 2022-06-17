package gbas.gtbch.mailer;

/**
 * mailer constants
 */
public class MailerConstants {
    // custom properties names
    /**
     * use value in this propertie as name in 'from' field
     */
    public final static String CONFIG_MAILER_FROM = "gt.mailer.from";
    /**
     * use value in this propertie as name (or names divided by ',') in 'to' field
     */
    public final static String CONFIG_MAILER_TO = "gt.mailer.to";
    /**
     * use value of this propertie in 'subject' field
     */
    public final static String CONFIG_MAILER_SUBJECT = "gt.mailer.subject";

    /**
     * send information about currency rates
     */
    public static final String CONFIG_MAILER_SEND_CURRENCY_RATES = "gt.mailer.rates";
    // default values
    /**
     * default subject
     */
    public final static String MAILER_DEFAULT_SUBJECT = "AS GT";
}
