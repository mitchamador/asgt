package gbas.gtbch.sapod.service;

import gbas.tvk.tpol3.service.TPRow;

import java.util.List;

public interface TPolRowService {
    /**
     *
     * @param id_tarif
     * @return
     */
    List<TPRow> getRows(int id_tarif);

    /**
     *
     * @param idRow
     * @return
     */
    TPRow getRow(int idRow);

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
    int saveRow(TPRow row);

    /**
     *
     * @param sourceRowId
     * @param destinationDocumentId
     * @return
     */
    TPRow copyRow(int sourceRowId, int destinationDocumentId);
}
