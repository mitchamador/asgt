package gbas.gtbch.sapod.model.matherials;

public class Descriptor {
    /**
     * {@link gbas.tvk.objects.service.SborDescriptor##code}
     */
    private int code;

    /**
     * {@link gbas.tvk.objects.service.SborDescriptor##name}
     */
    private String name;

    /**
     * title for static {@link gbas.tvk.otpravka.object.SborOsob}
     */
    private String osobTitle;

    /**
     * {@link gbas.tvk.otpravka.object.SborOsobVal} value
     */
    private int osobVal;

    /**
     * {@link gbas.tvk.otpravka.object.SborOsobVal} name
     */
    private String osobName;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOsobTitle() {
        return osobTitle;
    }

    public void setOsobTitle(String osobTitle) {
        this.osobTitle = osobTitle;
    }

    public int getOsobVal() {
        return osobVal;
    }

    public void setOsobVal(int osobVal) {
        this.osobVal = osobVal;
    }

    public String getOsobName() {
        return osobName;
    }

    public void setOsobName(String osobName) {
        this.osobName = osobName;
    }
}
