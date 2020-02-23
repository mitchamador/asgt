package gbas.gtbch.mq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "app.mq.queue"
)
public class QueueConfigurationProperties {

    /**
     * queue name
     */
    private String name;

    /**
     * jndi queue name
     */
    private String jndiName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }
}
