package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.TpolItem;

import java.util.List;

public interface TPolItemsService {
    List<String[]> getNsi(TpolItem item);

    List<String[]> getData(TpolItem item, int id_tpol);

    Boolean checkData(TpolItem item, int id, String[] data);

    boolean addData(TpolItem item, int id, String[] data);

    Boolean deleteData(TpolItem item, int id, String data);
}
