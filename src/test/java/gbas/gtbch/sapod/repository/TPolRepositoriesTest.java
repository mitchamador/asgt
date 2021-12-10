package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.tpol.TpTvkKof;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class TPolRepositoriesTest {

    @Autowired
    TPolKofRepository tPolKofRepository;

    @Test
    public void getKofList() {
        List<TpTvkKof> list = tPolKofRepository.getKofList();
        if (list != null && !list.isEmpty()) {

            TpTvkKof tvkKof = tPolKofRepository.getKof(list.get(0).id);
            if (tvkKof == null) return;

            tvkKof.id = 0;
            //tvkKof.maxRast = 12345.67;
            int id = tPolKofRepository.saveKof(tvkKof);

            if (id != 0) {
                tPolKofRepository.deleteKof(id);
            }
        }
    }
}