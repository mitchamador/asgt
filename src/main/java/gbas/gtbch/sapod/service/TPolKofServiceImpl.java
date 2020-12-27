package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.repository.TPolKofRepository;
import gbas.tvk.tpol3.TvkKof;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
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
    public TvkKof getKof(int id) {
        return tPolKofRepository.getKof(id);
    }


    /**
     * @return
     */
    @Override
    public List<TvkKof> getKofList() {
        return tPolKofRepository.getKofList();
    }

    /**
     * @param idTPol
     * @return
     */
    @Override
    public List<TvkKof> getKofList(int idTPol) {
        return tPolKofRepository.getKofList(idTPol);
    }

    /**
     * @param kof
     * @return
     */
    @Override
    @Transactional(transactionManager = "sapodTransactionManager")
    public int saveKof(TvkKof kof) {
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

}
