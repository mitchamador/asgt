package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.repository.TPolRowRepository;
import gbas.tvk.tpol3.service.TPRow;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class TPolRowServiceImpl implements TPolRowService {

    private final TPolRowRepository tPolRowRepository;

    public TPolRowServiceImpl(TPolRowRepository tPolRowRepository) {
        this.tPolRowRepository = tPolRowRepository;
    }

    /**
     *
     * @param id_tarif
     * @return
     */
    @Override
    public List<TPRow> getRows(int id_tarif) {
        return tPolRowRepository.getRows(id_tarif);
    }

    /**
     * @param idRow
     * @return
     */
    @Override
    public TPRow getRow(int idRow) {
        return tPolRowRepository.getRow(idRow);
    }

    /**
     * delete {@link TPRow}
     *
     * @param id
     * @return
     */
    @Override
    public boolean deleteRow(int id) {
        return tPolRowRepository.deleteRow(id);
    }

    /**
     * create new or update existing {@link TPRow}
     *
     * @param row
     * @return
     */
    @Override
    public int saveRow(TPRow row) {
        return tPolRowRepository.saveRow(row);
    }

    /**
     *
     * @param sourceRowId
     * @param destinationDocumentId
     * @return
     */
    @Override
    public TPRow copyRow(int sourceRowId, int destinationDocumentId) {
        return tPolRowRepository.copyRow(sourceRowId, destinationDocumentId);
    }
}
