package gbas.gtbch.sapod.service;

import gbas.tvk.tpol3.TvkKof;

import java.util.List;

public interface TPolKofService {
    TvkKof getKof(int id);

    List<TvkKof> getKofList();

    List<TvkKof> getKofList(int idTPol);

    int saveKof(TvkKof kof);

    boolean deleteKof(int id);
}
