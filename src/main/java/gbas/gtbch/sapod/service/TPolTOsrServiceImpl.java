package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.tpol.TpTvkTOsr;
import gbas.gtbch.sapod.repository.TPolTOsrRepository;
import gbas.gtbch.web.request.KeyValue;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TPolTOsrServiceImpl implements TPolTOsrService {

    private final TPolTOsrRepository tPolTOsrRepository;

    public TPolTOsrServiceImpl(TPolTOsrRepository tPolTOsrRepository) {
        this.tPolTOsrRepository = tPolTOsrRepository;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public TpTvkTOsr getCont(int id) {
        return tPolTOsrRepository.getCont(id);
    }

    /**
     * @return
     */
    @Override
    public List<TpTvkTOsr> getContList() {
        return tPolTOsrRepository.getContList();
    }

    /**
     * @param idTPol
     * @return
     */
    @Override
    public List<TpTvkTOsr> getContList(int idTPol) {
        return tPolTOsrRepository.getContList(idTPol);
    }

    /**
     * @param osr
     * @return
     */
    @Override
    public int saveCont(TpTvkTOsr osr) {
        return tPolTOsrRepository.saveCont(osr);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public boolean deleteCont(int id) {
        return tPolTOsrRepository.deleteCont(id);
    }

    @Override
    public List<KeyValue> getNstrValues() {
        return tPolTOsrRepository.getNstrValues();
    }

    @Override
    public List<KeyValue> getGrpkValues() {
        return tPolTOsrRepository.getGrpkValues();
    }

}
