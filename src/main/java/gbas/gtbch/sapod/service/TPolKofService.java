package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.tpol.TpTvkKof;

import java.util.List;

public interface TPolKofService {
    /**
     *
     * @param id
     * @return
     */
    TpTvkKof getKof(int id);

    /**
     *
     * @return
     */
    List<TpTvkKof> getKofList();

    /**
     *
     * @param idTPol
     * @return
     */
    List<TpTvkKof> getKofList(int idTPol);

    /**
     *
     * @param idTPol
     * @return
     */
    List<TpTvkKof> getKofBsList(int idTPol);

    /**
     *
     * @param kof
     * @return
     */
    int saveKof(TpTvkKof kof);

    /**
     *
     * @param id
     * @return
     */
    boolean deleteKof(int id);

    /**
     * copy table
     * @param tab
     * @return
     */
    int copyKofTable(int tab);

    /**
     * delete table
     * @param tab
     * @return
     */
    boolean deleteKofTable(int tab);
}
