package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.TpolItem;

import java.util.List;

public interface TPolItemsService {
    /**
     *
     * @param item
     * @return
     */
    List<String[]> getNsi(TpolItem item);

    /**
     *
     * @param item
     * @param id_tpol
     * @return
     */
    List<String[]> getData(TpolItem item, int id_tpol);

    /**
     *
     * @param item
     * @param id
     * @param data
     * @return
     */
    Boolean checkData(TpolItem item, int id, String[] data);

    /**
     *
     * @param item
     * @param id
     * @param data
     * @return
     */
    boolean addData(TpolItem item, int id, String[] data);

    /**
     *
     * @param item
     * @param id
     * @param data
     * @return
     */
    Boolean deleteData(TpolItem item, int id, String data);
}
