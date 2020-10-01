package gbas.gtbch.sapod.model;

import gbas.tvk.tpol3.service.ColumnInfo;
import gbas.tvk.tpol3.service.TPItem;

import java.util.List;

public class TpolItem {

    /**
     * TarifPolicy item object {@link TPItem}
     */
    private TPItem item;

    /**
     * set (bullshit, see S in SOLID)
     */
    private int set;

    /**
     * item name
     */
    private String name;

    /**
     * item UI name
     */
    private String buttonName;

    /**
     * item UI columns
     */
    private ColumnInfo[] columns;

    /**
     * item data
     */
    private List<String[]> itemData;

    /**
     * item NSI UI header
     */
    private String nsiHeader;

    /**
     * item NSI UI columns
     */
    private ColumnInfo[] nsiColumns;

    /**
     * item NSI itemData
     */
    private List<String[]> nsiData;

    public TpolItem(TPItem item) {
        this(item, 0);
    }

    public TpolItem(TPItem item, int set) {
        this.item = item;
        if (item != null) {
            this.name = item.getName();
            this.set = set;
            this.buttonName = item.getButtonName();
            this.columns = item.getItemColumnInfo();

            this.nsiHeader = item.getNSIDialogTitle();
            this.nsiColumns = item.getNSIColumnInfo(set);
        }
    }

    public TPItem getItem() {
        return item;
    }

    public int getSet() {
        return set;
    }

    public ColumnInfo[] getNsiColumns() {
        return nsiColumns;
    }

    public List<String[]> getItemData() {
        return itemData;
    }

    public void setItemData(List<String[]> itemData) {
        this.itemData = itemData;
    }
}

