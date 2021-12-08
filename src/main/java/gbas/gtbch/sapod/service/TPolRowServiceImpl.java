package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.tpol.TpRow;
import gbas.gtbch.sapod.repository.TPolRowRepository;
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
    public List<TpRow> getRows(int id_tarif) {
        return tPolRowRepository.getRows(id_tarif);
    }

    /**
     * @param idRow
     * @return
     */
    @Override
    public TpRow getRow(int idRow) {
        return tPolRowRepository.getRow(idRow);
    }

    /**
     * delete {@link TpRow}
     *
     * @param id
     * @return
     */
    @Override
    public boolean deleteRow(int id) {
        return tPolRowRepository.deleteRow(id);
    }

    /**
     * create new or update existing {@link TpRow}
     *
     * @param row
     * @return
     */
    @Override
    public int saveRow(TpRow row) {
        return tPolRowRepository.saveRow(row);
    }

    /**
     *
     * @param sourceRowId
     * @param destinationDocumentId
     * @return
     */
    @Override
    public TpRow copyRow(int sourceRowId, int destinationDocumentId) {
        return tPolRowRepository.copyRow(sourceRowId, destinationDocumentId);
    }
}
