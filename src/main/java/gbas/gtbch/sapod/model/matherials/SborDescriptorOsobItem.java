package gbas.gtbch.sapod.model.matherials;

public class SborDescriptorOsobItem {
    /**
     *
     */
    private int code;

    /**
     *
     */
    private String name;

    public SborDescriptorOsobItem(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
