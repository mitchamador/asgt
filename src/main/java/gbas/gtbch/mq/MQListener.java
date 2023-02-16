package gbas.gtbch.mq;

import gbas.gtbch.jobs.AbstractServerJob;
import gbas.gtbch.jobs.impl.MQJob;
import gbas.gtbch.mq.messagesender.MQMessageSender;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.util.calc.CalcHandler;
import gbas.gtbch.util.calc.GtCalcData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.jms.JmsException;
import org.springframework.stereotype.Component;

import javax.jms.*;

import static gbas.gtbch.util.CropString.getCroppedString;

@Component
@ConditionalOnExpression("'${app.mq.enable:}' == 'true'")
public class MQListener implements MessageListener {

    private final static Logger logger = LoggerFactory.getLogger(MQListener.class);

    /**
     * mq job
     */
    private final MQJob mqJob;

    /**
     * {@link CalcHandler}
     */
    private final CalcHandler calcHandler;

    /**
     * default outbound queue name
     */
    private final String defaultOutboundQueueName;

    private final MQMessageSender messageSender;

    public MQListener(MQMessageSender messageSender, MQJob mqJob, CalcHandler calcHandler, String outboundQueueName) {
        this.mqJob = mqJob;
        this.calcHandler = calcHandler;
        this.defaultOutboundQueueName = outboundQueueName;
        this.messageSender = messageSender;
    }

    private void log(String s) {
        logger.info(s);
        mqJob.log(s, AbstractServerJob.LOG_EVENT_DATE);
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

            String outboundQueueName = message.getJMSReplyTo() instanceof Queue ? ((Queue) message.getJMSReplyTo()).getQueueName() : this.defaultOutboundQueueName;

            if (outboundQueueName != null) {
                try {
                    messageSender.sendMessage(outboundQueueName, response, message.getJMSCorrelationID());

                    long duration = System.currentTimeMillis() - startTime;
                    String elapsedString = ("in " + (duration / 1000) + "." + ((duration % 1000) / 10) + " s");

                    String replyTo = "";
                    if (message.getJMSReplyTo() instanceof Queue) {
                        replyTo = "to " + (outboundQueueName.lastIndexOf('/') != -1 ? outboundQueueName.substring(outboundQueueName.lastIndexOf('/') + 1) : outboundQueueName) + " ";
                    }

                    log(String.format("send reply %s%s: \"%s\" for correlationId: %s", replyTo, elapsedString, getCroppedString(response), message.getJMSCorrelationID()));
                } catch (JmsException | JMSException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}