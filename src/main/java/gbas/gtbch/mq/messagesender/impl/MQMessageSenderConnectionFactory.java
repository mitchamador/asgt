package gbas.gtbch.mq.messagesender.impl;

import gbas.gtbch.mq.messagesender.MQMessageSender;

import javax.jms.*;

/**
 * message sender based on direct conection/session/queue
 */
public class MQMessageSenderConnectionFactory implements MQMessageSender {

    private final ConnectionFactory connectionFactory;

    public MQMessageSenderConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void sendMessage(String queue, String message, String correlationId) throws JMSException {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE)) {
            Queue qDestionation = session.createQueue(queue);
            try (MessageProducer dstQ = session.createProducer(qDestionation)) {
                Message msg = session.createTextMessage(message);
                msg.setJMSCorrelationID(correlationId);
                dstQ.send(msg);
            }
            session.commit();
        }

    }
}
