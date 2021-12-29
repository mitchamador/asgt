package gbas.gtbch.config.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {"jndiName"})
public class SettingsProperties {

    /**
     * jndi name for configuration
     */
    private String jndiName;

    /**
     * use new services' codes
     * set default to true
     */
    private boolean newServiceCodes = true;

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public boolean getNewServiceCodes() {
        return newServiceCodes;
    }

    public void setNewServiceCodes(boolean newServiceCodes) {
        this.newServiceCodes = newServiceCodes;
    }
}
