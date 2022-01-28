package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.tpol.TpClient;
import gbas.gtbch.sapod.model.tpol.TpLinkedClient;
import gbas.gtbch.sapod.model.tpol.TpTvkKof;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
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

    @Autowired
    TPolRepository tPolRepository;

    @Test
    public void testTpClient() {

        List<TpClient> tpClients = tPolRepository.getTpClients();
        if (!tpClients.isEmpty()) {
            System.out.println(tpClients.get(0).toString());
        }


        {
            List<TpLinkedClient> tpLinkedClients = tPolRepository.getLinkedTpClients(18581);
            tpLinkedClients.forEach(tpLinkedClient -> System.out.println(tpLinkedClient.toString()));
        }

        {
            List<TpLinkedClient> tpLinkedClients = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                TpLinkedClient tpLinkedClient = new TpLinkedClient();
                tpLinkedClient.setCode(String.valueOf(100500 + i));
                tpLinkedClient.setName("test" + (i + 1));
                tpLinkedClient.setNumNod(i + 4);
                tpLinkedClients.add(tpLinkedClient);
            }
            tPolRepository.saveLinkedTpClients(18486, tpLinkedClients);

            tpLinkedClients = tPolRepository.getLinkedTpClients(18486);
            tpLinkedClients.forEach(tpLinkedClient -> System.out.println(tpLinkedClient.toString()));
        }

        {
            List<TpLinkedClient> tpLinkedClients = new ArrayList<>();
            for (int i = 5; i < 7; i++) {
                TpLinkedClient tpLinkedClient = new TpLinkedClient();
                tpLinkedClient.setCode(String.valueOf(100500 + i));
                tpLinkedClient.setName("test" + (i + 1));
                tpLinkedClient.setNumNod(i);
                tpLinkedClients.add(tpLinkedClient);
            }

            tPolRepository.saveLinkedTpClients(18486, tpLinkedClients);

            tpLinkedClients = tPolRepository.getLinkedTpClients(18486);
            tpLinkedClients.forEach(tpLinkedClient -> System.out.println(tpLinkedClient.toString()));
        }

        {
            tPolRepository.deleteLinkedTpClients(18486);
            List<TpLinkedClient> tpLinkedClients = tPolRepository.getLinkedTpClients(18486);
            tpLinkedClients.forEach(tpLinkedClient -> System.out.println(tpLinkedClient.toString()));
        }

    }
}