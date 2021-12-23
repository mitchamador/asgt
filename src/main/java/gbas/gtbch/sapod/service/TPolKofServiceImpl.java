package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.tpol.TpTvkKof;
import gbas.gtbch.sapod.repository.TPolKofRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TPolKofServiceImpl implements TPolKofService {

    private final TPolKofRepository tPolKofRepository;

    public TPolKofServiceImpl(TPolKofRepository tPolKofRepository) {
        this.tPolKofRepository = tPolKofRepository;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public TpTvkKof getKof(int id) {
        return tPolKofRepository.getKof(id);
    }


    /**
     * @return
     */
    @Override
    public List<TpTvkKof> getKofList() {
        return tPolKofRepository.getKofList();
    }

    /**
     * @param idTPol
     * @return
     */
    @Override
    public List<TpTvkKof> getKofList(int idTPol) {
        return tPolKofRepository.getKofList(idTPol);
    }

    /**
     *
     * @param idTPol
     * @return
     */
    @Override
    public List<TpTvkKof> getKofBsList(int idTPol) {
        return tPolKofRepository.getKofBsList(idTPol);
    }

    /**
     * @param kof
     * @return
     */
    @Override
    public int saveKof(TpTvkKof kof) {
        return tPolKofRepository.saveKof(kof);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public boolean deleteKof(int id) {
        return tPolKofRepository.deleteKof(id);
    }

    @Override
    public int copyKofTable(int tab) {
        return tPolKofRepository.copyKofTab(tab);
    }

    @Override
    public boolean deleteKofTable(int tab) {
        return tPolKofRepository.deleteKofTab(tab);
    }

}
