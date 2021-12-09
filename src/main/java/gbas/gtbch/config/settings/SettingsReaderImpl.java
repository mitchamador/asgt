package gbas.gtbch.config.settings;

import gbas.eds.gtbch.settings.reader.SettingsReader;

public class SettingsReaderImpl implements SettingsReader {

    private final SettingsProperties settingsProperties;

    public SettingsReaderImpl(SettingsProperties settingsProperties) {
        this.settingsProperties = settingsProperties;
    }

    @Override
    public boolean isNewServiceCodes() {
        return settingsProperties.getNewServiceCodes();
    }
}
