package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.tpol.TpTvkTOsr;
import gbas.gtbch.web.request.KeyValue;

import java.util.List;

public interface TPolTOsrService {
    /**
     *
     * @param id
     * @return
     */
    TpTvkTOsr getCont(int id);

    /**
     *
     * @return
     */
    List<TpTvkTOsr> getContList();

    /**
     *
     * @param idTPol
     * @return
     */
    List<TpTvkTOsr> getContList(int idTPol);

    /**
     *
     * @param osr
     * @return
     */
    int saveCont(TpTvkTOsr osr);

    /**
     *
     * @param id
     * @return
     */
    boolean deleteCont(int id);

    /**
     * get list of values for {@link TpTvkTOsr#nSt}
     * @return
     */
    List<KeyValue> getNstrValues();

    /**
     * get list of values for {@link TpTvkTOsr#grpk}
     * @return
     */
    List<KeyValue> getGrpkValues();

}
