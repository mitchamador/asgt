package gbas.gtbch.sapod.service;

import gbas.tvk.tpol3.TvkTVes;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TPolTVesService {
    TvkTVes getVO(int id);

    List<TvkTVes> getVOList();

    List<TvkTVes> getVOList(int idTPol);

    int saveVO(TvkTVes tVes);

    boolean deleteVO(int id);
}
