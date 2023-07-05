package gbas.gtbch.config.settings;

import gbas.eds.gtbch.settings.reader.SettingsReaderDefault;

public class SettingsReaderGtBch extends SettingsReaderDefault {

    private final SettingsProperties settingsProperties;

    public SettingsReaderGtBch(SettingsProperties settingsProperties) {
        this.settingsProperties = settingsProperties;
    }

    @Override
    public boolean isNewServiceCodes() {
        return settingsProperties.getNewServiceCodes();
    }
}
