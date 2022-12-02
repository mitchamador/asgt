package gbas.gtbch.mq;

import gbas.gtbch.mq.properties.JndiMQConfigurationProperties;
import gbas.gtbch.mq.properties.QueueConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;

@Component
public class MQProperties {

    private ConnectionFactory connectionFactory;
    private boolean useCaching;
    private JndiMQConfigurationProperties jndiMQConfigurationProperties;
    private QueueConfigurationProperties inboundConfigurationProperties;
    private QueueConfigurationProperties outboundConfigurationProperties;
    private String inboundQueueName;
    private String outboundQueueName;

    @Value("${app.mq.enable:false}")
    private boolean isEnabled;

    public boolean isEnabled() {
        return isEnabled;
    }

    public MQProperties() {
    }

    @Bean
    public MQProperties mqProperties(
            @Autowired(required = false) ConnectionFactory connectionFactory,
            @Autowired(required = false) JndiMQConfigurationProperties jndiMQConfigurationProperties,
            @Autowired(required = false) @Qualifier("inboundQueueProperties") QueueConfigurationProperties inboundConfigurationProperties,
            @Autowired(required = false) @Qualifier("outboundQueueProperties") QueueConfigurationProperties outboundConfigurationProperties,
            @Autowired(required = false) @Qualifier("inboundQueueName") String inboundQueueName,
            @Autowired(required = false) @Qualifier("outboundQueueName") String outboundQueueName) {

        MQProperties mqProperties = new MQProperties();
        if (isEnabled) {
            if (connectionFactory instanceof CachingConnectionFactory) {
                mqProperties.useCaching = true;
                mqProperties.connectionFactory = ((CachingConnectionFactory) connectionFactory).getTargetConnectionFactory();
            } else {
                mqProperties.connectionFactory = connectionFactory;
            }
            mqProperties.jndiMQConfigurationProperties = jndiMQConfigurationProperties;
            mqProperties.inboundConfigurationProperties = inboundConfigurationProperties;
            mqProperties.outboundConfigurationProperties = outboundConfigurationProperties;
            mqProperties.inboundQueueName = inboundQueueName;
            mqProperties.outboundQueueName = outboundQueueName;
        }

        return mqProperties;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public boolean isUseCaching() {
        return useCaching;
    }

    public JndiMQConfigurationProperties getJndiMQConfigurationProperties() {
        return jndiMQConfigurationProperties;
    }

    public QueueConfigurationProperties getInboundConfigurationProperties() {
        return inboundConfigurationProperties;
    }

    public QueueConfigurationProperties getOutboundConfigurationProperties() {
        return outboundConfigurationProperties;
    }

    public String getInboundQueueName() {
        return inboundQueueName;
    }

    public String getOutboundQueueName() {
        return outboundQueueName;
    }

}
