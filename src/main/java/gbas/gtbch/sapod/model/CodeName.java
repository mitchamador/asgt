package gbas.gtbch.sapod.model;

import java.io.Serializable;

/**
 * Simple code-name class
 */
public class CodeName implements Serializable {

    /**
     * code
     */
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

    public CodeName(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
