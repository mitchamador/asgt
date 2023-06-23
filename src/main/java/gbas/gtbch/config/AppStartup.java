package gbas.gtbch.config;

import gbas.gtbch.mailer.MailService;
import gbas.gtbch.util.SystemInfo;
import gbas.gtbch.util.SystemInfoProperties;
import gbas.gtbch.util.calc.handler.Handler;
import gbas.gtbch.web.request.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import static gbas.gtbch.mailer.MailerConstants.MAILER_CONFIG_EVENT_STARTUP;

@Component
public class AppStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final MailService mailService;
    private final SystemInfoProperties systemInfoProperties;
    private final String host;
    private final MessageListenerContainer inboundQueueListenerContainer;
    private final Handler handler;

    public AppStartup(MailService mailService,
                      SystemInfo systemInfo,
                      SystemInfoProperties systemInfoProperties,
                      @Autowired(required = false) MessageListenerContainer inboundQueueListenerContainer,
                      Handler handler) {
        this.mailService = mailService;
        this.host = systemInfo.getHost();
        this.systemInfoProperties = systemInfoProperties;
        this.inboundQueueListenerContainer = inboundQueueListenerContainer;
        this.handler = handler;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        // send mail message
        sendMailStartupMessage();
        // start jms listener
        startInboundJmsListener();
        // init calc handler
        initCalcHandler(applicationReadyEvent.getApplicationContext());
    }

    private void initCalcHandler(ConfigurableApplicationContext applicationContext) {
        handler.init(applicationContext);
    }

    private void startInboundJmsListener() {
        if (inboundQueueListenerContainer != null && !inboundQueueListenerContainer.isAutoStartup()) {
            inboundQueueListenerContainer.start();
        }
    }

    private void sendMailStartupMessage() {
        if (mailService.getMailProperties().isEventEnabled(MAILER_CONFIG_EVENT_STARTUP)) {
            StringBuilder htmlMessage = new StringBuilder();

            htmlMessage.append("<html>");
            htmlMessage.append("<body>");
            htmlMessage.append("<p><b>AS GT is started on ").append(host).append("</b></p>");
            htmlMessage.append("<table border=\"1\">");
            htmlMessage.append("<caption>Информация о системе</caption>");
            htmlMessage.append("<tr>");
            htmlMessage.append("<th>Параметр</th>");
            htmlMessage.append("<th>Значение</th>");
            htmlMessage.append("</tr>");

            for (KeyValue kv : systemInfoProperties.getSystemProperties()) {
                htmlMessage
                        .append("<tr>")
                        .append("<td>").append(kv.getKey()).append("</td>")
                        .append("<td>").append(kv.getValue()).append("</td>")
                        .append("</tr>");
            }

            htmlMessage.append("</table>");

            htmlMessage.append("</body>");
            htmlMessage.append("</html>");

            mailService.sendHtmlMessage(null, "startup", htmlMessage.toString());
        }
    }
}