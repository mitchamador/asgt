package gbas.gtbch.sapod.model.tpol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gbas.tvk.tpol3.service.CargoItem;
import gbas.tvk.tpol3.service.ColumnInfo;
import gbas.tvk.tpol3.service.TPItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TpItem {

    /**
     * TarifPolicy item object {@link TPItem}
     */
    @JsonIgnore
    private TPItem item;

    /**
     * set (bullshit, see S in SOLID)
     */
    private int set;

    /**
     * item's name
     */
    private String name;

    /**
     * item's UI name
     */
    private String buttonName;

    /**
     * item's UI columns
     */
    private ColumnInfo[] columns;

    /**
     * item's data
     */
    private List<String[]> itemData;

    /**
     * item's data size
     */
    private int itemDataSize;

    /**
     * item's NSI UI header
     */
    private String nsiHeader;

    /**
     * item's NSI UI columns
     */
    private List<ColumnInfo[]> nsiColumns;

    /**
     * item's NSI itemData
     */
    private List<String[]> nsiData;

    public TpItem(TPItem item) {
        this(item, 0);
    }

    public TpItem(TPItem item, int set) {
        this.item = item;
        if (item != null) {
            this.name = item.getName();
            this.set = set;
            this.buttonName = item.getButtonName();
            this.columns = item.getItemColumnInfo();

            this.nsiHeader = item.getNSIDialogTitle();

            this.nsiColumns = new ArrayList<>(Collections.singletonList(item.getNSIColumnInfo(0)));
            if (item instanceof CargoItem) {
                this.nsiColumns.add(item.getNSIColumnInfo(1));
            }
        }
    }

    public TPItem getItem() {
        return item;
    }

    public int getSet() {
        return set;
    }

    public List<ColumnInfo[]> getNsiColumns() {
        return nsiColumns;
    }

    public List<String[]> getItemData() {
        return itemData;
    }

    public void setItemData(List<String[]> itemData) {
        this.itemData = itemData;
    }

    public void setItem(TPItem item) {
        this.item = item;
    }

    public void setSet(int set) {
        this.set = set;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public ColumnInfo[] getColumns() {
        return columns;
    }

    public void setColumns(ColumnInfo[] columns) {
        this.columns = columns;
    }

    public String getNsiHeader() {
        return nsiHeader;
    }

    public void setNsiHeader(String nsiHeader) {
        this.nsiHeader = nsiHeader;
    }

    public void setNsiColumns(List<ColumnInfo[]> nsiColumns) {
        this.nsiColumns = nsiColumns;
    }

    public List<String[]> getNsiData() {
        return nsiData;
    }

    public void setNsiData(List<String[]> nsiData) {
        this.nsiData = nsiData;
    }

    public int getItemDataSize() {
        return itemDataSize;
    }

    public void setItemDataSize(int itemDataSize) {
        this.itemDataSize = itemDataSize;
    }

}

