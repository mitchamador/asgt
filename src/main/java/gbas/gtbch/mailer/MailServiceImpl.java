package gbas.gtbch.mailer;

import gbas.gtbch.util.CropString;
import gbas.gtbch.util.PlaceHolderProperties;
import gbas.tvk.nsi.cash.Func;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static gbas.gtbch.mailer.MailerConstants.MAILER_DEFAULT_SUBJECT;

@Component
public class MailServiceImpl implements MailService {

    private final static Logger logger = LoggerFactory.getLogger(MailServiceImpl.class.getName());

    private final JavaMailSender mailSender;
    private final CustomMailProperties mailProperties;
    private final PlaceHolderProperties placeHolderProperties;

    public MailServiceImpl(@Autowired(required = false) @Qualifier("mailSender") JavaMailSender mailSender, CustomMailProperties mailProperties, PlaceHolderProperties placeHolderProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
        this.placeHolderProperties = placeHolderProperties;
        if (mailSender instanceof JavaMailSenderImpl) {
            ((JavaMailSenderImpl) mailSender).getJavaMailProperties().forEach((key, value) -> {
                mailProperties.getProperties().put(String.valueOf(key), String.valueOf(value));
            });
        }
    }

    private String getFrom() {
        return mailProperties.getProperties().getOrDefault(MailerConstants.MAILER_CONFIG_FROM, mailProperties.getUsername());
    }

    @Override
    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        sendSimpleMessage(getFrom(), to, subject, text);
    }

    @Override
    @Async
    public void sendSimpleMessage(String from, String to, String subject, String text) {
        sendMessage(from, to, subject, text, false);
    }

    @Override
    @Async
    public void sendHtmlMessage(String to, String subject, String html) {
        sendHtmlMessage(getFrom(), to, subject, html);
    }

    @Override
    @Async
    public void sendHtmlMessage(String from, String to, String subject, String html) {
        sendMessage(from, to, subject, html, true);
    }

    @Override
    public CustomMailProperties getMailProperties() {
        return mailProperties;
    }

    private void sendMessage(String from, String to, String subject, String text, boolean html) {
        String defaultUsername = mailProperties.getProperties().getOrDefault(MailerConstants.MAILER_CONFIG_TO, mailProperties.getUsername());

        if (to == null) {
            to = defaultUsername;
        }

        if (from == null) {
            from = defaultUsername;
        }

        subject = getSubject(subject);

        logger.info("send mail message from: {} to: {} with subject '{}' and text: '{}' ",
                Func.isEmpty(from) ? "unknown sender" : from,
                Func.isEmpty(to) ? "unknown recepient(s)" : to,
                subject,
                CropString.getCroppedString(text));

        if (mailSender != null) {
            try {
                if (html) {
                    MimeMessage message = mailSender.createMimeMessage();
                    message.setSubject(subject);

                    MimeMessageHelper helper = new MimeMessageHelper(message, true);
                    helper.setFrom(from);
                    helper.setTo(getTo(to));
                    helper.setText(text, true);
                    mailSender.send(message);
                } else {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom(from);
                    message.setTo(getTo(to));
                    message.setSubject(subject);
                    message.setText(text);
                    mailSender.send(message);
                }
            } catch (NullPointerException | MessagingException e) {
                e.printStackTrace();
                logger.info("no message is sent, {}", e.getMessage());
            }
        } else {
            logger.info("no message is sent, {}", !mailProperties.isEnable() ? "mailer is disabled" : "no mailer defined");
        }
    }

    private String[] getTo(String to) {
        return to != null ? to.split(",") : new String[] {mailProperties.getUsername()};
    }

    private String getSubject(String subject) {
        String defaultSubject = placeHolderProperties.replacePlaceHoldersDefault(mailProperties.getProperties().getOrDefault(MailerConstants.MAILER_CONFIG_SUBJECT, MAILER_DEFAULT_SUBJECT));
        if (subject != null && !subject.trim().isEmpty()) {
            defaultSubject = defaultSubject + ": " + subject;
        }
        return defaultSubject;
    }
}
