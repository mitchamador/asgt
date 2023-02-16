package gbas.gtbch.mq;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.spring.boot.MQConnectionFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import gbas.gtbch.mq.messagesender.MQMessageSender;
import gbas.gtbch.mq.messagesender.impl.MQMessageSenderConnectionFactory;
import gbas.gtbch.mq.messagesender.impl.MQMessageSenderJmsTemplate;
import gbas.gtbch.mq.properties.JndiMQConfigurationProperties;
import gbas.gtbch.mq.properties.QueueConfigurationProperties;
import gbas.gtbch.util.jndi.JndiLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.NamingException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableJms
@ConditionalOnExpression("'${app.mq.enable:}' == 'true'")
public class MQConfig {

    private final static Logger logger = LoggerFactory.getLogger(MQConfig.class);
    @Bean
    @ConfigurationProperties("app.mq.queue-manager")
    public JndiMQConfigurationProperties jndiMqConfigurationProperties() {
        return new JndiMQConfigurationProperties();
    }

    /**
     * enable usage of {@link CachingConnectionFactory} (not for was)
     */
    @Value("${app.mq.caching:false}")
    private boolean useCachingConnectionFactory;
    public boolean isUseCachingConnectionFactory() {
        return useCachingConnectionFactory;
    }
    public void setUseCachingConnectionFactory(boolean useCachingConnectionFactory) {
        this.useCachingConnectionFactory = useCachingConnectionFactory;
    }

    /**
     * @see DefaultMessageListenerContainer#setAutoStartup(boolean)
     */
    @Value("${app.mq.listener.autostart:true}")
    private boolean listenerAutoStart;

    /**
     * @see DefaultMessageListenerContainer#setCacheLevelName(String)
     */
    @Value("${app.mq.listener.cachelevel:none}")
    private String listenerCacheLevelName;

    /**
     * use {@link MQMessageSenderJmsTemplate} if true, {@link MQMessageSenderConnectionFactory} otherwise
     */
    @Value("${app.mq.sender.jmstemplate:true}")
    private boolean useJmsTemplate;

    /**
     * @see DefaultMessageListenerContainer#setReceiveTimeout(long)
     */
    @Value("${app.mq.listener.receivetimeout:60}")
    private long jmsReceiveTimeout;

    /**
     * create {@link CachingConnectionFactory} from {@link ConnectionFactory}
     * @param connectionFactory
     * @return
     */
    private ConnectionFactory createCachingConnectionFactory(ConnectionFactory connectionFactory) {
        if (useCachingConnectionFactory) {
            CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
            cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);
            cachingConnectionFactory.setSessionCacheSize(10);
            cachingConnectionFactory.setReconnectOnException(true);
            //cachingConnectionFactory.setCacheConsumers(false);
            return cachingConnectionFactory;
        }
        return connectionFactory;
    }

    /**
     * set user credentials for {@link MQConnectionFactory}
     * @param connectionFactory
     * @param jndiMqConfigurationProperties
     * @return
     * @throws JMSException
     */
    @Bean
    @Profile("embedded")
    @Primary
    public ConnectionFactory fillCredentialsForEmbeddedConnectionFactory(ConnectionFactory connectionFactory,
                                                              JndiMQConfigurationProperties jndiMqConfigurationProperties) throws JMSException {
        MQConnectionFactory mqConnectionFactory = null;
        if (connectionFactory instanceof CachingConnectionFactory && ((CachingConnectionFactory) connectionFactory).getTargetConnectionFactory() instanceof MQConnectionFactory) {
            mqConnectionFactory = (MQConnectionFactory) ((CachingConnectionFactory) connectionFactory).getTargetConnectionFactory();
        } else if (connectionFactory instanceof MQConnectionFactory) {
            mqConnectionFactory = (MQConnectionFactory) connectionFactory;
        }

        if (mqConnectionFactory != null) {
            mqConnectionFactory.setStringProperty(WMQConstants.USERID, jndiMqConfigurationProperties.getUser());
            mqConnectionFactory.setStringProperty(WMQConstants.PASSWORD, jndiMqConfigurationProperties.getPassword());
            mqConnectionFactory.setStringProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, String.valueOf(jndiMqConfigurationProperties.isUserAuthenticationMQCSP()));
        }

        return connectionFactory;
    }

    @Bean(destroyMethod = "")
    @ConditionalOnExpression("!'${app.mq.queue-manager.jndi-name:}'.isEmpty()")
    public ConnectionFactory jndiConnectionFactory(JndiMQConfigurationProperties jndiMQConfigurationProperties) {
        try {
            return createCachingConnectionFactory(new JndiLookup<>(ConnectionFactory.class).getResource(jndiMQConfigurationProperties.getJndiName()));
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    @ConditionalOnExpression("'${app.mq.queue-manager.jndi-name:}'.isEmpty()")
    public ConnectionFactory connectionFactory(JndiMQConfigurationProperties jndiMQConfigurationProperties) {
        return createCachingConnectionFactory(new MQConnectionFactoryFactory(jndiMQConfigurationProperties, null).createConnectionFactory(MQConnectionFactory.class));
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("4-8"); // core 4 threads and max 8 threads
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        factory.setAutoStartup(listenerAutoStart);
        factory.setErrorHandler(e -> {
            logger.info("jmsListenerContainerFactory error: " + e.getMessage(), e);
        });
        if (listenerCacheLevelName != null) {
            try {
                factory.setCacheLevelName((listenerCacheLevelName.toLowerCase().startsWith("cache_") ? listenerCacheLevelName : ("cache_" + listenerCacheLevelName)).toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                logger.info("jmsListenerContainerFactory setCacheLevelName error: " + e.getMessage());
            }
        }
        // https://community.ibm.com/community/user/integration/viewdocument/top-tip-when-using-ibm-mq-and-the-s?CommunityKey=183ec850-4947-49c8-9a2e-8e7c7fc46c64&tab=librarydocuments
        factory.setReceiveTimeout(TimeUnit.SECONDS.toMillis(jmsReceiveTimeout));
        return factory;
    }

    /**
     * properties for inbound queue
     * @return
     */
    @Bean
    @ConfigurationProperties("app.mq.inbound")
    @Qualifier("inboundQueueProperties")
    public QueueConfigurationProperties inboundQueueProperties() {
        return new QueueConfigurationProperties();
    }

    /**
     * properties for outbound queue
     * @return
     */
    @Bean
    @ConfigurationProperties("app.mq.outbound")
    @Qualifier("outboundQueueProperties")
    public QueueConfigurationProperties outboundQueueProperties() {
        return new QueueConfigurationProperties();
    }

    /**
     * inbound queue name
     * @return
     */
    @Bean
    public String inboundQueueName() {
        return getQueueName(inboundQueueProperties());
    }

    /**
     * outbound queue name
     * @return
     */
    @Bean
    public String outboundQueueName() {
        return getQueueName(outboundQueueProperties());
    }

    /**
     * get Queue name from JNDI resource or queue properties
     * @param queueConfigurationProperties
     * @return
     */
    private String getQueueName(QueueConfigurationProperties queueConfigurationProperties) {
        if (queueConfigurationProperties.getJndiName() != null) {
            try {
                return ((MQQueue) new JndiLookup<>(javax.jms.Queue.class).getResource(queueConfigurationProperties.getJndiName())).getBaseQueueName();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        } else {
            return queueConfigurationProperties.getName();
        }
        return null;
    }

    /**
     * create jms listener for inbound queue
     * @param jmsListenerContainerFactory
     * @return
     */
    @Bean
    public DefaultMessageListenerContainer inboundQueueListenerContainer(MQListener mqListener, DefaultJmsListenerContainerFactory jmsListenerContainerFactory) {
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setMessageListener(mqListener);
        endpoint.setDestination(inboundQueueName());
        return jmsListenerContainerFactory.createListenerContainer(endpoint);
    }

    /**
     * create JmsTemplate
     * @param connectionFactory
     * @return
     */
    @Bean
    public JmsTemplate jmsQueueTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName(outboundQueueName());
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setTimeToLive(TimeUnit.MINUTES.toMillis(60));
        return jmsTemplate;
    }

    /**
     * create message sender
     * @param jmsTemplate
     * @param connectionFactory
     * @return
     */
    @Bean
    public MQMessageSender mqMessageSender(JmsTemplate jmsTemplate, ConnectionFactory connectionFactory) {
        if (useJmsTemplate) {
            return new MQMessageSenderJmsTemplate(jmsTemplate);
        } else {
            return new MQMessageSenderConnectionFactory(connectionFactory);
        }
    }

}
