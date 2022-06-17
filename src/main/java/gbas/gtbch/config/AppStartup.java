package gbas.gtbch.config;

import gbas.gtbch.mailer.MailService;
import gbas.gtbch.util.SystemInfo;
import gbas.gtbch.util.SystemInfoProperties;
import gbas.gtbch.web.request.KeyValue;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AppStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final MailService mailService;
    private final SystemInfoProperties systemInfoProperties;
    private final String host;

    public AppStartup(MailService mailService, SystemInfo systemInfo, SystemInfoProperties systemInfoProperties) {
        this.mailService = mailService;
        this.host = systemInfo.getHost();
        this.systemInfoProperties = systemInfoProperties;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
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