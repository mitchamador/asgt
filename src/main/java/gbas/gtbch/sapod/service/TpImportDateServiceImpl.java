package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.repository.TpImportDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("tpImportDateService")
public class TpImportDateServiceImpl implements TpImportDateService {

    @Autowired
    TpImportDateRepository tpImportDateRepository;

    @Override
    public TpImportDate getTpImportDate() {
        return tpImportDateRepository.findFirstByOrderByDateImportDesc();
    }

    @Override
    public TpImportDate save(TpImportDate tpImportDate) {
        return tpImportDateRepository.save(tpImportDate);
    }

    @Override
    public void delete(TpImportDate tpImportDate) {
        tpImportDateRepository.delete(tpImportDate);
    }
}
