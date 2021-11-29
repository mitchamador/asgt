package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.TpRow;

import java.util.List;

public interface TPolRowService {
    /**
     *
     * @param id_tarif
     * @return
     */
    List<TpRow> getRows(int id_tarif);

    /**
     *
     * @param idRow
     * @return
     */
    TpRow getRow(int idRow);

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
}
