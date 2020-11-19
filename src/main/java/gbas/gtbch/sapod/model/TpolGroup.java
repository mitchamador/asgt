package gbas.gtbch.sapod.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * tpol groups
 */
public class TpolGroup {
    /**
     * code
     */
    @JsonProperty("type_code")
    private String code;

    /**
     * name
     */
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
