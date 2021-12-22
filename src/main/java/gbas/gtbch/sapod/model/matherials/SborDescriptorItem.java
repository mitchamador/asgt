package gbas.gtbch.sapod.model.matherials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gbas.tvk.objects.service.SborDescriptor;
import gbas.tvk.otpravka.object.SborOsob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(value = {"osob", "osobName"})
public class SborDescriptorItem {

    private int code;

    private String name;

    private SborOsob osob;

    private List<SborDescriptorOsobItem> osobItems = new ArrayList<>();

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getOsobName() {
        return osob != null ? osob.name() : "";
    }

    public String getOsobTitle() {
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

    private SborDescriptorItem(int code, String name, SborOsob sborOsob) {
        this(code, name);
        if (sborOsob != null) {
            osob = sborOsob;
            Arrays.asList(sborOsob.getVal()).forEach(sborOsobVal -> osobItems.add(new SborDescriptorOsobItem(sborOsobVal.getCodV(), sborOsobVal.getNameV())));
        }
    }

    public static List<SborDescriptorItem> getList() {
        return Arrays.stream(SborDescriptor.values())
                .map(sd -> new SborDescriptorItem(sd.getCode(), sd.getName(), sd.getStaticSborOsob()))
                .sorted(Comparator.comparing(SborDescriptorItem::getName)) //.sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(Collectors.toList());
    }

}
