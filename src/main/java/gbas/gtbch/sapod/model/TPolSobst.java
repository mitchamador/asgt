package gbas.gtbch.sapod.model;

/**
 *
 */
public class TPolSobst {

    /**
     * administration code
     */
    public int kAdm;

    /**
     * administration small name
     */
    public String sName;

    /**
     * administration name
     */
    public String name;

    /**
     * use administration in TPolDocument
     */
    public boolean checked;

    public int getkAdm() {
        return kAdm;
    }

    public void setkAdm(int kAdm) {
        this.kAdm = kAdm;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
