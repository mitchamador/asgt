package gbas.gtbch.mq;

import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.util.CalcData;
import gbas.gtbch.util.CalcHandler;
import gbas.gtbch.util.MQJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import static gbas.gtbch.util.CropString.getCroppedString;

@Component
@ConditionalOnExpression("'${app.mq.enable:}' == 'true'")
public class MQListener implements MessageListener {

    private final static Logger logger = LoggerFactory.getLogger(MQListener.class);

    /**
     *
     */
    private final JmsTemplate jmsTemplate;

    /**
     *
     */
    private final String outboundQueueName;

    /**
     *
     */
    private final MQJob mqJob;

    public MQListener(JmsTemplate jmsTemplate, String outboundQueueName, MQJob mqJob, CalcHandler calcHandler) {
        this.jmsTemplate = jmsTemplate;
        this.outboundQueueName = outboundQueueName;
        this.mqJob = mqJob;
        this.calcHandler = calcHandler;
    }

    private void log(String s) {
        logger.info(s);
        mqJob.log(s);
    }

    @Override
    //@JmsListener(destination = "${app.mq.inbound-queue:Q.IN}") // remove listener for more flexible jndi configuration
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                processMessage((TextMessage) message);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        } else {
            try {
                log(String.format("message type must be a TextMessage, messageId: %s", message.getJMSMessageID()));
            } catch (JMSException e) {
                e.printStackTrace();
            }
            //throw new IllegalArgumentException("Message type must be a TextMessage");
        }
    }

    private final CalcHandler calcHandler;

    private void processMessage(final TextMessage message) throws JMSException {
        log(String.format("message received: \"%s\", messageId: %s, correlationId: %s", getCroppedString(message.getText()), message.getJMSMessageID(), message.getJMSCorrelationID()));

        String response = calcHandler.calc(new CalcData(message.getText(), new CalculationLog(message.getJMSCorrelationID()))).getOutputXml();

        if (message.getJMSCorrelationID() != null && !message.getJMSCorrelationID().isEmpty()) {
            jmsTemplate.send(outboundQueueName, session -> {

                TextMessage answer = session.createTextMessage();
                answer.setJMSCorrelationID(message.getJMSCorrelationID());
                answer.setText(response);

                log(String.format("send reply: \"%s\" for correlationId: %s", getCroppedString(response), message.getJMSCorrelationID()));

                return answer;
            });
        }

    }

}