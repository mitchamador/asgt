package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.tpol.TpTvkTVes;
import gbas.gtbch.sapod.repository.TPolTVesRepository;
import gbas.gtbch.util.cache.annotation.CacheClear;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TPolTVesServiceImpl implements TPolTVesService {

    private final TPolTVesRepository tPolTVesRepository;

    public TPolTVesServiceImpl(TPolTVesRepository tPolTVesRepository) {
        this.tPolTVesRepository = tPolTVesRepository;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public TpTvkTVes getVO(int id) {
        return tPolTVesRepository.getVO(id);
    }

    /**
     * @return
     */
    @Override
    public List<TpTvkTVes> getVOList() {
        return tPolTVesRepository.getVOList();
    }

    /**
     * @param idTPol
     * @return
     */
    @Override
    public List<TpTvkTVes> getVOList(int idTPol) {
        return tPolTVesRepository.getVOList(idTPol);
    }

    /**
     * @param tVes
     * @return
     */
    @Override
    @CacheClear({"tp"})
    public int saveVO(TpTvkTVes tVes) {
        return tPolTVesRepository.saveVO(tVes);
    }

    /**
     * @param id
     * @return
     */
    @Override
    @CacheClear({"tp"})
    public boolean deleteVO(int id) {
        return tPolTVesRepository.deleteVO(id);
    }

}
