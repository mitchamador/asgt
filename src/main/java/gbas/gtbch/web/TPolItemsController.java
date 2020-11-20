package gbas.gtbch.web;

import gbas.gtbch.sapod.model.TpolItem;
import gbas.gtbch.sapod.repository.TPolItemsRepository;
import gbas.tvk.tpol3.service.TPItem;
import gbas.tvk.tpol3.service.TPItems;
import gbas.tvk.tpol3.service.TPRow;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tpol/items")
public class    TPolItemsController {

    private final TPolItemsRepository tPolItemsRepository;

    public TPolItemsController(TPolItemsRepository tPolItemsRepository) {
        this.tPolItemsRepository = tPolItemsRepository;
    }

    /**
     * get {@link TpolItem} list
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<TpolItem>> getItems() {
        List<TpolItem> items = new ArrayList<>();
        for (TPItems enumItems : TPItems.values()) {
            items.add(new TpolItem(enumItems.getItem()));
        }
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    /**
     * get NSI for {@link TpolItem}
     * @param name - {@link TpolItem} name
     * @return
     */
    @RequestMapping(value = "/nsi/{name}", method = RequestMethod.GET)
    public ResponseEntity<List<String[]>> getItemNsi(@PathVariable String name) {
        List<String[]> data = null;
        TPItem tpItem = TPItems.getTpItem(name);
        if (tpItem != null) {
            data = tPolItemsRepository.getNsi(new TpolItem(tpItem));
        }
        return new ResponseEntity<>(data, HttpStatus.OK);
    }


}
