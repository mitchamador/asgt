package gbas.gtbch.sapod.service;

import gbas.tvk.tpol3.service.TPRow;

import java.util.List;

public interface TPolRowService {
    List<TPRow> getRows(int id_tarif);

    TPRow getRow(int idRow);

    boolean deleteRow(int id);

    int saveRow(TPRow row);

    TPRow copyRow(int sourceRowId, int destinationDocumentId);
}
