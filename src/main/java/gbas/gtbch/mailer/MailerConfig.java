package gbas.gtbch.mailer;

import gbas.gtbch.util.jndi.JndiLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Session;
import javax.naming.NamingException;
import java.util.Map;
import java.util.Properties;

@Configuration
public class MailerConfig {

    private static Logger logger = LoggerFactory.getLogger(MailerConfig.class);

    @Bean
    @ConfigurationProperties("app.mailer")
    public CustomMailProperties mailProperties() {
        return new CustomMailProperties();
    }

    @Bean
    @ConditionalOnExpression("'${app.mailer.jndi-name:}'.isEmpty()")
    @Qualifier("mailSender")
    JavaMailSenderImpl mailSenderProps(CustomMailProperties properties) {
        if (properties.isEnable()) {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            applyProperties(properties, sender);
            return sender;
        } else {
            return null;
        }
    }

    private void applyProperties(MailProperties properties, JavaMailSenderImpl sender) {
        sender.setHost(properties.getHost());
        if (properties.getPort() != null) {
            sender.setPort(properties.getPort());
        }
        sender.setUsername(properties.getUsername());
        sender.setPassword(properties.getPassword());
        sender.setProtocol(properties.getProtocol());
        if (properties.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(properties.getDefaultEncoding().name());
        }
        if (!properties.getProperties().isEmpty()) {
            sender.setJavaMailProperties(asProperties(properties.getProperties()));
        }
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }

    @Bean
    @ConditionalOnExpression("!'${app.mailer.jndi-name:}'.isEmpty()")
    @Qualifier("mailSender")
    JavaMailSenderImpl mailSenderJndi(@Autowired(required = false) Session session, CustomMailProperties mailProperties) {
        if (mailProperties.isEnable() && session != null) {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setDefaultEncoding(mailProperties.getDefaultEncoding().name());
            sender.setSession(session);
            sender.setJavaMailProperties(session.getProperties());
            return sender;
        }
        return null;
    }

    @Bean
    @ConditionalOnExpression("!'${app.mailer.jndi-name:}'.isEmpty()")
    public Session mailSessionJndi(CustomMailProperties mailProperties) {
        if (mailProperties.isEnable()) {
            String jndiName = mailProperties.getJndiName();
            try {
                return new JndiLookup<>(Session.class).getResource("mail/GT.MAIL");
            } catch (NamingException ex) {
                logger.info("cannot find resource {}", jndiName);
            }
        }
        return null;
    }


}
