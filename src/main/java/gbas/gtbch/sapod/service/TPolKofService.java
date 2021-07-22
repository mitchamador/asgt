package gbas.gtbch.sapod.service;

import gbas.tvk.tpol3.TvkKof;

import java.util.List;

public interface TPolKofService {
    /**
     *
     * @param id
     * @return
     */
    TvkKof getKof(int id);

    /**
     *
     * @return
     */
    List<TvkKof> getKofList();

    /**
     *
     * @param idTPol
     * @return
     */
    List<TvkKof> getKofList(int idTPol);

    /**
     *
     * @param idTPol
     * @return
     */
    List<TvkKof> getKofBsList(int idTPol);

    /**
     *
     * @param kof
     * @return
     */
    int saveKof(TvkKof kof);

    /**
     *
     * @param id
     * @return
     */
    boolean deleteKof(int id);
}
