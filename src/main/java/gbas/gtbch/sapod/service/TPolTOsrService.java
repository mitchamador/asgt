package gbas.gtbch.sapod.service;

import gbas.tvk.tpol3.TvkTOsr;

import java.util.List;

public interface TPolTOsrService {
    /**
     *
     * @param id
     * @return
     */
    TvkTOsr getCont(int id);

    /**
     *
     * @return
     */
    List<TvkTOsr> getContList();

    /**
     *
     * @param idTPol
     * @return
     */
    List<TvkTOsr> getContList(int idTPol);

    /**
     *
     * @param osr
     * @return
     */
    int saveCont(TvkTOsr osr);

    /**
     *
     * @param id
     * @return
     */
    boolean deleteCont(int id);
}
