package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.repository.TPolTVesRepository;
import gbas.tvk.tpol3.TvkTVes;
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
    public TvkTVes getVO(int id) {
        return tPolTVesRepository.getVO(id);
    }

    /**
     * @return
     */
    @Override
    public List<TvkTVes> getVOList() {
        return tPolTVesRepository.getVOList();
    }

    /**
     * @param idTPol
     * @return
     */
    @Override
    public List<TvkTVes> getVOList(int idTPol) {
        return tPolTVesRepository.getVOList(idTPol);
    }

    /**
     * @param tVes
     * @return
     */
    @Override
    public int saveVO(TvkTVes tVes) {
        try {
            return tPolTVesRepository.saveVO(tVes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public boolean deleteVO(int id) {
        return tPolTVesRepository.deleteVO(id);
    }

}
