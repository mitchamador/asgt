package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.tpol.TpRow;

import java.util.List;
import java.util.Map;

public interface TPolRowService {

    /**
     *
     * @param idRow
     * @param filterMap
     * @return
     */
    TpRow getRow(int idRow, Map<String, String> filterMap);

    /**
     *
     * @param id
     * @return
     */
    boolean deleteRow(int id);

    /**
     *
     * @param row
     * @return
     */
    int saveRow(TpRow row);

    /**
     *
     * @param sourceRowId
     * @param destinationDocumentId
     * @return
     */
    TpRow copyRow(int sourceRowId, int destinationDocumentId);

    /**
     *
     * @param id_tarif
     * @return
     */
    List<TpRow> getRows(int id_tarif, Map<String, String> filterMap);
}
