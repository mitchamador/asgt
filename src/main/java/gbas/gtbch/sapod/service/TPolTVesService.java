package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.tpol.TpTvkTVes;

import java.util.List;

public interface TPolTVesService {
    /**
     *
     * @param id
     * @return
     */
    TpTvkTVes getVO(int id);

    /**
     *
     * @return
     */
    List<TpTvkTVes> getVOList();

    /**
     *
     * @param idTPol
     * @return
     */
    List<TpTvkTVes> getVOList(int idTPol);

    /**
     *
     * @param tVes
     * @return
     */
    int saveVO(TpTvkTVes tVes);

    /**
     *
     * @param id
     * @return
     */
    boolean deleteVO(int id);
}
