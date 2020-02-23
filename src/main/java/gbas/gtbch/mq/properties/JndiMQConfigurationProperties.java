package gbas.gtbch.mq.properties;

import com.ibm.mq.spring.boot.MQConfigurationProperties;

public class JndiMQConfigurationProperties extends MQConfigurationProperties {

    /**
     * jndi name
     */
    private String jndiName;

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

}