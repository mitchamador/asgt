package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.tpol.TpItem;
import gbas.gtbch.sapod.model.tpol.TpRow;
import gbas.gtbch.sapod.repository.TPolItemsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TPolItemsServiceImpl implements TPolItemsService {

    private final TPolItemsRepository tPolItemsRepository;

    public TPolItemsServiceImpl(TPolItemsRepository tPolItemsRepository) {
        this.tPolItemsRepository = tPolItemsRepository;
    }

    @Override
    public List<String[]> getNsi(TpItem item) {
        return tPolItemsRepository.getNsi(item);
    }

    @Override
    public List<String[]> getData(TpItem item, int id_tpol) {
        return tPolItemsRepository.getData(item, id_tpol);
    }

    /**
     * check data for existing
     *
     * @param item {@link TpItem}
     * @param id   {@link TpRow} id
     * @param data data
     * @return true if data exists
     */
    @Override
    public Boolean checkData(TpItem item, int id, String[] data) {
        return tPolItemsRepository.checkData(item, id, data);
    }

    /**
     * add data
     *
     * @param item {@link TpItem}
     * @param id   {@link TpRow} id
     * @param data data
     * @return
     */
    @Override
    public boolean addData(TpItem item, int id, String[] data) {
        return tPolItemsRepository.addData(item, id, data);
    }

    /**
     * delete data
     *
     * @param item
     * @param id
     * @param data
     * @return
     */
    @Override
    public Boolean deleteData(TpItem item, int id, String data) {
        return tPolItemsRepository.deleteData(item, id, data);
    }

}
