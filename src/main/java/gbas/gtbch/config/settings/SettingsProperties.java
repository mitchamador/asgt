package gbas.gtbch.config.settings;

public class SettingsProperties {

    private String jndiName;
    private boolean newServiceCodes;

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
