package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.tpol.TpRow;
import gbas.gtbch.sapod.repository.TPolRowRepository;
import gbas.gtbch.util.cache.annotation.CacheClear;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
     * @param idRow
     * @return
     */
    @Override
    public TpRow getRow(int idRow, Map<String, String> filterMap) {
        return tPolRowRepository.getRow(idRow, filterMap);
    }

    /**
     * delete {@link TpRow}
     *
     * @param id
     * @return
     */
    @Override
    @CacheClear({"tp"})
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
    @CacheClear({"tp"})
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
    @CacheClear({"tp"})
    public TpRow copyRow(int sourceRowId, int destinationDocumentId) {
        return tPolRowRepository.copyRow(sourceRowId, destinationDocumentId);
    }

    /**
     *
     * @param id_tarif
     * @return
     */
    @Override
    public List<TpRow> getRows(int id_tarif, Map<String, String> filterMap) {
        return tPolRowRepository.getRows(id_tarif, filterMap);
    }
}
