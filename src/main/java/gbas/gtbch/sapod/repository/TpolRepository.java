package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.TpolItem;
import gbas.tvk.tpol3.TpolDocument;
import gbas.tvk.tpol3.service.TPRow;

import java.util.List;

public interface TpolRepository {

    /**
     * get NSI
     * @param item
     * @return
     */
    List<String[]> getNsi(TpolItem item);

    /**
     * get data
     * @param item
     * @param id_tpol
     * @return
     */
    List<String[]> getData(TpolItem item, int id_tpol);

    /**
     *
     * @return
     */
    List<TpolDocument> getDocuments();

    /**
     * @param id_tarif
     * @return
     */
    List<TPRow> getRows(int id_tarif);
}
