package gbas.gtbch.sapod.model;

import java.util.List;

public class TPolItemData {
    /**
     * {@link TpolItem#set}
     */
    int set;

    /**
     * data
     */
    String[] data;

    /**
     * array of data
     */
    List<String[]> array;


    public int getSet() {
        return set;
    }

    public void setSet(int set) {
        this.set = set;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public List<String[]> getArray() {
        return array;
    }

    public void setArray(List<String[]> array) {
        this.array = array;
    }
}
