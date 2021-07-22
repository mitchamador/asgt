package gbas.gtbch.sapod.service;

import gbas.tvk.tpol3.TvkTVes;

import java.util.List;

public interface TPolTVesService {
    /**
     *
     * @param id
     * @return
     */
    TvkTVes getVO(int id);

    /**
     *
     * @return
     */
    List<TvkTVes> getVOList();

    /**
     *
     * @param idTPol
     * @return
     */
    List<TvkTVes> getVOList(int idTPol);

    /**
     *
     * @param tVes
     * @return
     */
    int saveVO(TvkTVes tVes);

    /**
     *
     * @param id
     * @return
     */
    boolean deleteVO(int id);
}
