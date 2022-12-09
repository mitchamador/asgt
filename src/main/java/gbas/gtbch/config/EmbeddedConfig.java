package gbas.gtbch.config;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQConnectionFactoryFactory;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueFactory;
import gbas.gtbch.mailer.CustomMailProperties;
import gbas.gtbch.mq.properties.JndiMQConfigurationProperties;
import gbas.gtbch.mq.properties.QueueConfigurationProperties;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * конфигурация для embedded tomcat
 */
@Configuration
@Profile("embedded")
public class EmbeddedConfig {

    @Bean
    public TomcatServletWebServerFactory containerFactory(
            final DataSourceProperties sapodDataSourceProperties,
            final DataSourceProperties pensiDataSourceProperties,
            @Autowired(required = false) final JndiMQConfigurationProperties jndiMqConfigurationProperties,
            @Autowired(required = false) @Qualifier("inboundQueueProperties") final QueueConfigurationProperties inboundQueueConfigurationProperties,
            @Autowired(required = false) @Qualifier("outboundQueueProperties") final QueueConfigurationProperties outboundQueueConfigurationProperties,
            @Autowired(required = false) final CustomMailProperties mailProperties
            ) {
        return new TomcatServletWebServerFactory() {

            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                // вкл. jndi для embedded tomcat
                tomcat.enableNaming();
                return super.getTomcatWebServer(tomcat);
            }

            @Override
            protected void postProcessContext(Context context) {
                // jndi ресурс для бд сапод
                if (sapodDataSourceProperties.getJndiName() != null) {
                    context.getNamingResources().addResource(createJndiDataSource(sapodDataSourceProperties));
                }
                // jndi ресурс для бд пэнси
                if (pensiDataSourceProperties.getJndiName() != null) {
                    context.getNamingResources().addResource(createJndiDataSource(pensiDataSourceProperties));
                }
                // jndi ресурс для jms connectionfactory
                if (jndiMqConfigurationProperties != null && jndiMqConfigurationProperties.getJndiName() != null) {
                    context.getNamingResources().addResource(createJndiConnectionFactory(jndiMqConfigurationProperties));
                }
                // jndi ресурс для inbound queue
                if (inboundQueueConfigurationProperties != null && inboundQueueConfigurationProperties.getJndiName() != null) {
                    context.getNamingResources().addResource(createJndiQueue(inboundQueueConfigurationProperties));
                }
                // jndi ресурс для outbound queue
                if (outboundQueueConfigurationProperties != null && outboundQueueConfigurationProperties.getJndiName() != null) {
                    context.getNamingResources().addResource(createJndiQueue(outboundQueueConfigurationProperties));
                }
                // jndi mail session
                if (mailProperties != null && mailProperties.getJndiName() != null) {
                    context.getNamingResources().addResource(createJndiMailSession(mailProperties));
                }
            }

        };
    }

    private ContextResource createJndiDataSource(DataSourceProperties dataSourceProperties) {
        ContextResource resource = new ContextResource();

        resource.setName(dataSourceProperties.getJndiName());
        resource.setType(DataSource.class.getName());
        resource.setProperty("factory", org.apache.tomcat.jdbc.pool.DataSourceFactory.class.getName());
        resource.setProperty("driverClassName", dataSourceProperties.getDriverClassName());
        resource.setProperty("url", dataSourceProperties.getUrl());
        resource.setProperty("username", dataSourceProperties.getUsername());
        resource.setProperty("password", dataSourceProperties.getPassword());
        resource.setProperty("removeAbandoned", "true");
        resource.setProperty("removeAbandonedTimeout", String.valueOf(TimeUnit.MINUTES.toSeconds(30)));
        resource.setProperty("testOnBorrow", "true");
        resource.setProperty("validationQuery", dataSourceProperties.getUrl().startsWith("jdbc:db2") ? "SELECT 1 FROM SYSIBM.SYSDUMMY1" : "SELECT 1");
        resource.setProperty("validationInterval", String.valueOf(TimeUnit.SECONDS.toMillis(30)));

        return resource;
    }

    /*
        <Resource
      name="jms/MyQCF"
      auth="Container"
      type="com.ibm.mq.jms.MQQueueConnectionFactory"
      factory="com.ibm.mq.jms.MQQueueConnectionFactoryFactory"
      description="JMS Queue Connection Factory for sending messages"
      HOST="<mymqserver>"
      PORT="1414"
      CHAN="<mychannel>"
      TRAN="1"
      QMGR="<myqueuemanager>"/>
     */
    private ContextResource createJndiConnectionFactory(JndiMQConfigurationProperties jndiMQConfigurationProperties) {
        ContextResource resource = new ContextResource();
        resource.setName(jndiMQConfigurationProperties.getJndiName());
        resource.setDescription("JMS Queue Connection Factory");
        resource.setAuth("Container");
        resource.setType(MQConnectionFactory.class.getName());

        resource.setProperty("factory", MQConnectionFactoryFactory.class.getName());

        // convert connName to HOST and PORT
        Matcher m = Pattern.compile("(.*)\\((\\d+)\\)").matcher(jndiMQConfigurationProperties.getConnName());
        if (m.find()) {
            resource.setProperty("HOST", m.group(1));
            resource.setProperty("PORT", m.group(2));
        }

        resource.setProperty("CHAN", jndiMQConfigurationProperties.getChannel());
        resource.setProperty("QMGR", jndiMQConfigurationProperties.getQueueManager());
        resource.setProperty("TRAN", "1");

        return resource;
    }

    /*
   <Resource
      name="jms/MyQ"
      auth="Container"
      type="com.ibm.mq.jms.MQQueue"
      factory="com.ibm.mq.jms.MQQueueFactory"
      description="JMS Queue for receiving messages"
      QU="<myqueue>"/>
     */
    private ContextResource createJndiQueue(QueueConfigurationProperties queueConfigurationProperties) {
        ContextResource resource = new ContextResource();
        resource.setName(queueConfigurationProperties.getJndiName());
        resource.setDescription("JMS Queue for sendind/receiving messages");
        resource.setAuth("Container");
        resource.setType(MQQueue.class.getName());

        resource.setProperty("factory", MQQueueFactory.class.getName());
        resource.setProperty("QU", queueConfigurationProperties.getName());

        return resource;
    }

    /*
        <!-- JavaMail JNDI Mail Session -->

    <Resource name="mail/Session" auth="Container"
        type="javax.mail.Session"
        username="my.smtp.user"
        password="my-secret"
        mail.debug="false"
        mail.transport.protocol="smtp"
        mail.smtp.host= "xxx.xxx.xxx.xxx"
        mail.smtp.auth= "true"
        mail.smtp.port= "25"
        mail.smtp.starttls.enable="true"
        description="Global E-Mail Resource"
    />
     */
    private ContextResource createJndiMailSession(CustomMailProperties mailProperties) {
        ContextResource resource = new ContextResource();

        resource.setName(mailProperties.getJndiName());
        resource.setDescription("Mail session");
        resource.setAuth("Container");
        resource.setType(javax.mail.Session.class.getName());

        resource.setProperty("username", mailProperties.getUsername());
        resource.setProperty("password", mailProperties.getPassword());
        resource.setProperty("mail.smtp.host", mailProperties.getHost());
        resource.setProperty("mail.smtp.port", String.valueOf(mailProperties.getPort()));

        if (mailProperties.getProperties() != null) {
            for (Map.Entry<String, String> entry : mailProperties.getProperties().entrySet()) {
                resource.setProperty(entry.getKey(), entry.getValue());
            }
        }

        return resource;
    }

}