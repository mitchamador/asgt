package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.TpImportDate;

public interface TpImportDateService {

    TpImportDate getTpImportDate();

    TpImportDate save(TpImportDate tpImportDate);

    void delete(TpImportDate tpImportDate);
}