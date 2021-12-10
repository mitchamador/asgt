package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.tpol.TpTvkTOsr;
import gbas.gtbch.sapod.repository.TPolTOsrRepository;
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

}
