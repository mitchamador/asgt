package gbas.gtbch.sapod.service;

import gbas.tvk.tpol3.TvkTOsr;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TPolTOsrService {
    TvkTOsr getCont(int id);

    List<TvkTOsr> getContList();

    List<TvkTOsr> getContList(int idTPol);

    int saveCont(TvkTOsr osr);

    boolean deleteCont(int id);
}
