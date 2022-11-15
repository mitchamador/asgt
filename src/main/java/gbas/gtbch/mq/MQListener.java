package gbas.gtbch.mq;

import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.util.MQJob;
import gbas.gtbch.util.calc.CalcHandler;
import gbas.gtbch.util.calc.GtCalcData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
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
    private final MQJob mqJob;

    public MQListener(JmsTemplate jmsTemplate, MQJob mqJob, CalcHandler calcHandler) {
        this.jmsTemplate = jmsTemplate;
        this.mqJob = mqJob;
        this.calcHandler = calcHandler;
    }

    private void log(String s) {
        logger.info(s);
        mqJob.log(s);
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                processMessage((TextMessage) message);
            } else {
                log(String.format("message type must be a TextMessage, messageId: %s", message.getJMSMessageID()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final CalcHandler calcHandler;

    private void processMessage(final TextMessage message) throws Exception {

        log(String.format("message received: \"%s\", messageId: %s, correlationId: %s", getCroppedString(message.getText()), message.getJMSMessageID(), message.getJMSCorrelationID()));

        long startTime = System.currentTimeMillis();

        CalculationLog calculationLog = new CalculationLog(CalculationLog.Source.MQ);
        if (message.getJMSCorrelationID() != null) {
            calculationLog.setJmsCorrelationId(message.getJMSCorrelationID().replaceAll("ID:", ""));
        }

        String response = calcHandler.calc(
                new GtCalcData(message.getText(), calculationLog)
        ).getOutputXml();

        if (message.getJMSCorrelationID() != null && !message.getJMSCorrelationID().isEmpty()) {

            String outboundQueueName = message.getJMSReplyTo() instanceof Queue ? ((Queue) message.getJMSReplyTo()).getQueueName() : jmsTemplate.getDefaultDestinationName();

            if (outboundQueueName != null) {
                jmsTemplate.send(outboundQueueName, session -> {

                    TextMessage answer = session.createTextMessage();
                    answer.setJMSCorrelationID(message.getJMSCorrelationID());
                    answer.setText(response);

                    return answer;
                });

                startTime = System.currentTimeMillis() - startTime;

                String replyTo = "";

                if (message.getJMSReplyTo() instanceof Queue) {
                    replyTo = "to " + (outboundQueueName.lastIndexOf('/') != -1 ? outboundQueueName.substring(outboundQueueName.lastIndexOf('/') + 1) : outboundQueueName) + " ";
                }

                String elapsedString = ("in " + (startTime / 1000) + "." + ((startTime % 1000) / 10) + " s");

                log(String.format("send reply %s%s: \"%s\" for correlationId: %s", replyTo, elapsedString, getCroppedString(response), message.getJMSCorrelationID()));
            }
        }
    }
}