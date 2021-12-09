package gbas.gtbch.mq;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.spring.boot.MQConnectionFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import gbas.gtbch.mq.properties.JndiMQConfigurationProperties;
import gbas.gtbch.mq.properties.QueueConfigurationProperties;
import gbas.gtbch.util.jndi.JndiLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
import javax.naming.NamingException;
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
     * set user credentials on {@link MQConnectionFactory} and use {@link CachingConnectionFactory} for embedded tomcat
     * @param connectionFactory
     * @param jndiMqConfigurationProperties
     * @return
     * @throws JMSException
     */

    @Bean
    @Profile("embedded")
    @Primary
    public ConnectionFactory embeddedConnectionFactory(ConnectionFactory connectionFactory,
                                                              JndiMQConfigurationProperties jndiMqConfigurationProperties) throws JMSException {
        ((MQConnectionFactory) connectionFactory).setStringProperty(WMQConstants.USERID, jndiMqConfigurationProperties.getUser());
        ((MQConnectionFactory) connectionFactory).setStringProperty(WMQConstants.PASSWORD, jndiMqConfigurationProperties.getPassword());
        ((MQConnectionFactory) connectionFactory).setStringProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, String.valueOf(jndiMqConfigurationProperties.isUserAuthenticationMQCSP()));

        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);
        cachingConnectionFactory.setSessionCacheSize(500);
        cachingConnectionFactory.setReconnectOnException(true);

        return cachingConnectionFactory;
    }

    @Bean(destroyMethod = "")
    @ConditionalOnExpression("!'${app.mq.queue-manager.jndi-name:}'.isEmpty()")
    public ConnectionFactory jndiConnectionFactory(JndiMQConfigurationProperties jndiMQConfigurationProperties) {
        try {
            return new JndiLookup<>(ConnectionFactory.class).getResource(jndiMQConfigurationProperties.getJndiName());
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    @ConditionalOnExpression("'${app.mq.queue-manager.jndi-name:}'.isEmpty()")
    public ConnectionFactory connectionFactory(JndiMQConfigurationProperties jndiMQConfigurationProperties) {
        return new MQConnectionFactoryFactory(jndiMQConfigurationProperties, null).createConnectionFactory(MQConnectionFactory.class);
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("4-8"); // core 4 threads and max 8 threads
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
                //return ((MQQueue) new JndiQueueLookup().getQueue(queueConfigurationProperties.getJndiName())).getBaseQueueName();
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
    //@Profile("production")
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
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setDefaultDestinationName(outboundQueueName());
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setTimeToLive(TimeUnit.MINUTES.toMillis(60));
        return jmsTemplate;
    }

}
