package gbas.gtbch.mq.messagesender;

import javax.jms.JMSException;

/**
 * mq message sender interface
 */
public interface MQMessageSender {
    void sendMessage(String queue, String message, String correlationId) throws JMSException;
}
