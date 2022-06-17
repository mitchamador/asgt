package gbas.gtbch.util;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Properties;

/**
 * class with defaults properties used for placeholder
 */
@Component
public class PlaceHolderProperties {

    private final static String PLACEHOLDER_PROPERTIES_SERVER_IP_NAME = "gt.server.ip";

    private final SystemInfo systemInfo;
    private Properties defaultPlaceHolderProperties;
    private PropertyPlaceholderHelper defaultPropertyPlaceholderHelper;

    public PlaceHolderProperties(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Bean
    public PropertyPlaceholderHelper defaultPropertyPlaceholderHelper() {
        this.defaultPropertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}", ":", false);
        return this.defaultPropertyPlaceholderHelper;
    }

    @Bean
    public Properties defaultPlaceholderProperties() {
        defaultPlaceHolderProperties = new Properties();
        if (systemInfo != null) {
            defaultPlaceHolderProperties.setProperty(PLACEHOLDER_PROPERTIES_SERVER_IP_NAME, systemInfo.getHost());
        }
        return defaultPlaceHolderProperties;
    }

    public String replacePlaceHoldersDefault(String value) {
        return defaultPropertyPlaceholderHelper.replacePlaceholders(value, defaultPlaceHolderProperties);
    }

    /**
     * see {@link org.springframework.util.PropertyPlaceholderHelper#replacePlaceholders(String, Properties)}
     * @param value the value containing the placeholders to be replaced
     * @param properties the Properties to use for replacement
     * @return the supplied value with placeholders replaced inline
     */
    public String replacePlaceHolders(String value, Properties properties) {
        return defaultPropertyPlaceholderHelper.replacePlaceholders(value, properties);
    }

    /**
     * see {@link org.springframework.util.PropertyPlaceholderHelper#replacePlaceholders(String, PropertyPlaceholderHelper.PlaceholderResolver)}
     * @param value the value containing the placeholders to be replaced
     * @param resolver the PlaceholderResolver to use for replacement
     * @return the supplied value with placeholders replaced inline
     */
    public String replacePlaceHolders(String value, PropertyPlaceholderHelper.PlaceholderResolver resolver) {
        return defaultPropertyPlaceholderHelper.replacePlaceholders(value, resolver);
    }
}
