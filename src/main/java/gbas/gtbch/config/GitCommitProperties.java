package gbas.gtbch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:git.properties", ignoreResourceNotFound=true)
public class GitCommitProperties {
    @Value("${git.commit.id.abbrev:-}")
    private String abbrev;

    @Value("${git.commit.time:-}")
    private String time;

    @Value("${git.build.time:-}")
    private String buildTime;

    @Value("${git.build.version:-}")
    private String buildVersion;

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }
}