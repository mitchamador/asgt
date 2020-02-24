package gbas.gtbch.util;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import gbas.gtbch.mq.properties.JndiMQConfigurationProperties;
import gbas.gtbch.mq.properties.QueueConfigurationProperties;
import gbas.gtbch.web.request.KeyValue;
import gbas.tvk.util.UtilDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SystemInfo {

    private final static Logger logger = LoggerFactory.getLogger(SystemInfo.class);

    @Bean
    public List<KeyValue> systemProperties(
            ServletContext servletContext,
            @Qualifier("sapodDataSource") DataSource sapodDataSource,
            @Qualifier("pensiDataSource") DataSource pensiDataSource,
            ConnectionFactory connectionFactory,
            JndiMQConfigurationProperties jndiMQConfigurationProperties,
            @Qualifier("inboundQueueProperties") QueueConfigurationProperties inboundConfigurationProperties,
            @Qualifier("outboundQueueProperties") QueueConfigurationProperties outboundConfigurationProperties,
            @Qualifier("inboundQueueName") String inboundQueueName,
            @Qualifier("outboundQueueName") String outboundQueueName,
            @Autowired(required = false) BuildProperties buildProperties) {
        List<KeyValue> info = new ArrayList<>();

        info.add(new KeyValue("Сервер приложений", servletContext.getServerInfo()));

        info.addAll(getDbInfo(sapodDataSource, ""));
        info.addAll(getDbInfo(pensiDataSource, " ПЭНСИ"));

        info.addAll(getMqInfo(connectionFactory, jndiMQConfigurationProperties, inboundConfigurationProperties, outboundConfigurationProperties, inboundQueueName, outboundQueueName));

        info.add(new KeyValue("Сборка", buildProperties == null ? "local" : ("версия: " + buildProperties.getVersion() + "; время сборки: " + UtilDate.getStringDateTime(new java.util.Date(buildProperties.getTime().toEpochMilli())))));

        return info;
    }

    private List<KeyValue> getMqInfo(
            ConnectionFactory connectionFactory,
            JndiMQConfigurationProperties jndiMQConfigurationProperties,
            QueueConfigurationProperties inboundConfigurationProperties,
            QueueConfigurationProperties outboundConfigurationProperties,
            String inboundQueueName,
            String outboundQueueName) {
        List<KeyValue> info = new ArrayList<>();

        MQConnectionFactory mqConnectionFactory = null;

        if (connectionFactory instanceof MQConnectionFactory) {
            mqConnectionFactory = (MQConnectionFactory) connectionFactory;
        } else if (connectionFactory instanceof CachingConnectionFactory) {
            ConnectionFactory targetConnectionFactory = ((CachingConnectionFactory) connectionFactory).getTargetConnectionFactory();
            if (targetConnectionFactory instanceof MQConnectionFactory) {
                mqConnectionFactory = (MQConnectionFactory) targetConnectionFactory;
            }
        }

        if (mqConnectionFactory != null) {
            // MQConnectionFactory (for embedded tomcat)
            info.add(new KeyValue("Соединение MQ", "host: " + getInfoString(mqConnectionFactory.getHostName()) + "(" + getInfoString(String.valueOf(mqConnectionFactory.getPort())) + ")" +
                    ", queue manager: " + getInfoString(mqConnectionFactory.getQueueManager()) +
                    ", channel: " + getInfoString(mqConnectionFactory.getChannel()) +
                    ", user: " + getInfoString(mqConnectionFactory.get(WMQConstants.USERID).toString())));
        } else {
            // websphere Connectionfactory - com.ibm.ejs.jms.JMSQueueConnectionFactoryHandle
            info.add(new KeyValue("Соединение MQ", "connection factory jndi name: " + jndiMQConfigurationProperties.getJndiName() +
                    ", class: " + connectionFactory.getClass().getName()));
        }

        info.add(new KeyValue("Очереди MQ",
                "inbound queue jndi name: " + getInfoString(inboundConfigurationProperties.getJndiName()) +
                        ", name: " + getInfoString(inboundQueueName) + "; " +
                        "outbound queue jndi name: " + getInfoString(outboundConfigurationProperties.getJndiName()) +
                        ", name: " + getInfoString(outboundQueueName) + "; "));

        return info;
    }

    private String getInfoString(String s) {
        if (s == null || s.trim().isEmpty()) {
            return "n/a";
        } else {
            return s;
        }
    }

    private List<KeyValue> getDbInfo(DataSource dataSource, String name) {
        List<KeyValue> info = new ArrayList<>();
        try {
            try (Connection c = dataSource.getConnection()) {
                DatabaseMetaData databaseMetaData = c.getMetaData();
                info.add(new KeyValue("БД" + name,
                        databaseMetaData.getDatabaseProductName() + " v" + databaseMetaData.getDatabaseMajorVersion() + "." + databaseMetaData.getDatabaseMinorVersion() +
                        ", " + databaseMetaData.getDriverName() + " " + databaseMetaData.getDriverVersion() +
                        ", " + databaseMetaData.getURL() + ", user=" + databaseMetaData.getUserName()
                ));
            }
        } catch (SQLException e) {
            info.add(new KeyValue("БД " + name, "n/a"));
            e.printStackTrace();
        }
        return info;
    }

}
