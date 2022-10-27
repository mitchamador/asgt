package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.tpol.TpItem;

import java.util.List;

public interface TPolItemsService {
    /**
     *
     * @param item
     * @return
     */
    List<String[]> getNsi(TpItem item);

    /**
     *
     * @param item
     * @param id_tpol
     * @return
     */
    List<String[]> getData(TpItem item, int id_tpol);

    /**
     *
     * @param item
     * @param id
     * @param data
     * @return
     */
    Boolean checkData(TpItem item, int id, String[] data);

    /**
     *
     * @param item
     * @param id
     * @param data
     * @return
     */
    boolean addData(TpItem item, int id, String[] data);

    /**
     *
     * @param item
     * @param id
     * @param data
     * @return
     */
    Boolean deleteData(TpItem item, int id, String[] data);
}
