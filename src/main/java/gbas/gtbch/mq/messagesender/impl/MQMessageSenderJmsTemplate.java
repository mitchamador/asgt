package gbas.gtbch.mq.messagesender.impl;

import gbas.gtbch.mq.messagesender.MQMessageSender;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * message sender based on {@link JmsTemplate} of spring jms
 */
public class MQMessageSenderJmsTemplate implements MQMessageSender {

    private final JmsTemplate jmsTemplate;

    public MQMessageSenderJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void sendMessage(String queue, String message, String correlationId) throws JMSException {

        jmsTemplate.send(queue, session -> {

            TextMessage answer = session.createTextMessage();
            answer.setJMSCorrelationID(correlationId);
            answer.setText(message);

            return answer;
        });

    }
}
