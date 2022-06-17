package gbas.gtbch.mailer;

/**
 * interface for mailer
 */
public interface MailService {


    /**
     * send simple mail message
     * @param to
     * @param subject
     * @param text
     */
    void sendSimpleMessage(String to, String subject, String text);

    /**
     * send simple mail message
     * @param from
     * @param to
     * @param subject
     * @param text
     */
    void sendSimpleMessage(String from, String to, String subject, String text);

    /**
     * send html mail message
     * @param to
     * @param subject
     * @param html
     */
    void sendHtmlMessage(String to, String subject, String html);

    /**
     * send html mail message
     * @param from
     * @param to
     * @param subject
     * @param html
     */
    void sendHtmlMessage(String from, String to, String subject, String html);

    /**
     * get mail properties
     * @return
     */
    CustomMailProperties getMailProperties();
}
