package gbas.gtbch.web.request;

import java.io.Serializable;

/**
 * Simple key-value class
 */
public class KeyValue implements Serializable {

    /**
     * key
     */
    private String key;

    /**
     * value
     */
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
