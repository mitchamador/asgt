package gbas.gtbch.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gbas.gtbch.util.jndi.JndiLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import javax.naming.NamingException;
import java.util.Map;
import java.util.Properties;

/**
 * Override app properties from jndi resource config/GT.APP.PROPERTIES
 */
public class AppProperties implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private final static String JNDI_APP_PROPERTIES = "config/GT.APP.PROPERTIES";

    private static final Logger logger = LoggerFactory.getLogger(AppProperties.class);

    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        Properties props = new Properties();

        try {
            String json = new JndiLookup<>(String.class).getResource(JNDI_APP_PROPERTIES);
            Map<String, String> map = new ObjectMapper().readValue(json, Map.class);
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (entry.getKey().startsWith("app.")) {
                        logger.info("override app property {}={}", entry.getKey(), entry.getValue());
                        props.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        } catch (NamingException e) {
            logger.info("cannot find resource {}", JNDI_APP_PROPERTIES);
        } catch (JsonProcessingException e) {
            logger.info("error parsing json settings from {}", JNDI_APP_PROPERTIES);
        } catch (Exception ignored) {
        }

        if (!props.isEmpty()) {
            ConfigurableEnvironment environment = event.getEnvironment();
            environment.getPropertySources().addFirst(new PropertiesPropertySource("app.jndi.properties", props));
        }
    }
}
