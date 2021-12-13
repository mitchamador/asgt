package gbas.gtbch.sapod.model.matherials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gbas.tvk.otpravka.object.SborOsob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(value = {"osob", "osobMnemo", "osobName", "osobItems"})
public class SborDescriptorItem {

    private int code;

    private String name;

    private SborOsob osob;

    private List<SborDescriptorOsobItem> osobItems = new ArrayList<>();

    public SborDescriptorItem(int code, String name, SborOsob sborOsob) {
        this(code, name);
        if (sborOsob != null) {
            osob = sborOsob;
            Arrays.asList(sborOsob.getVal()).forEach(sborOsobVal -> osobItems.add(new SborDescriptorOsobItem(sborOsobVal.getCodV(), sborOsobVal.getNameV())));
        }
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getOsobName() {
        return osob != null ? osob.name() : "";
    }

    public String getOsobMnemo() {
        return osob != null ? osob.getNameSO() : "";
    }

    public List<SborDescriptorOsobItem> getOsobItems() {
        return osobItems;
    }

    public void setOsobItems(List<SborDescriptorOsobItem> osobItems) {
        this.osobItems = osobItems;
    }

    public SborDescriptorItem(int code, String name) {
        this.code = code;
        this.name = name;
    }

}
