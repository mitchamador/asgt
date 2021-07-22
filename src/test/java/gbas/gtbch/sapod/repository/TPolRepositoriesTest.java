package gbas.gtbch.sapod.repository;

import gbas.tvk.tpol3.TvkKof;
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
        List<TvkKof> list = tPolKofRepository.getKofList();
        if (list != null && !list.isEmpty()) {

            TvkKof tvkKof = tPolKofRepository.getKof(list.get(0).id);
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