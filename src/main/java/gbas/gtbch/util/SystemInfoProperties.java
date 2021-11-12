package gbas.gtbch.util;

import gbas.gtbch.web.request.KeyValue;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static gbas.gtbch.util.UtilDate8.getStringTime;

@Component
public class SystemInfoProperties {

    private final ApplicationContext context;

    private final List<KeyValue> systemProperties;

    public SystemInfoProperties(ApplicationContext context, List<KeyValue> systemProperties) {
        this.context = context;
        this.systemProperties = systemProperties;
    }

    public List<KeyValue> getSystemProperties() {
        Instant startTime = context.getBean("startTime", Instant.class);
        Duration startupDuration = context.getBean("startupDuration", Duration.class);

        List<KeyValue> systemInformation = new ArrayList<>(systemProperties);

        Duration upTime = Duration.between(startTime, Instant.now());

        systemInformation.add(new KeyValue("Время",
                String.format("start time: %s, startup duration: %s, uptime: %s",
                        getStringTime(startTime),
                        getStringTime(startupDuration, 180),
                        getStringTime(upTime))));

        return systemInformation;
    }
}
