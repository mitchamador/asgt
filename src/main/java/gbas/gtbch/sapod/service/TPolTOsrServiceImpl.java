package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.repository.TPolTOsrRepository;
import gbas.tvk.tpol3.TvkTOsr;
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
    public TvkTOsr getCont(int id) {
        return tPolTOsrRepository.getCont(id);
    }

    /**
     * @return
     */
    @Override
    public List<TvkTOsr> getContList() {
        return tPolTOsrRepository.getContList();
    }

    /**
     * @param idTPol
     * @return
     */
    @Override
    public List<TvkTOsr> getContList(int idTPol) {
        return tPolTOsrRepository.getContList(idTPol);
    }

    /**
     * @param osr
     * @return
     */
    @Override
    public int saveCont(TvkTOsr osr) {
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
