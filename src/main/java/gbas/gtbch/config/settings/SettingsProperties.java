package gbas.gtbch.config.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Settings from app.settings
 */
@Component
@ConfigurationProperties("app.settings")
public class SettingsProperties {

    /**
     * use new services' codes
     */
    private boolean newServiceCodes;

    public boolean getNewServiceCodes() {
        return newServiceCodes;
    }

    public void setNewServiceCodes(boolean newServiceCodes) {
        this.newServiceCodes = newServiceCodes;
    }
}
