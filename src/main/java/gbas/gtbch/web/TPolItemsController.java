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
public class TPolItemsController {

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

    /**
     * get all items for {@link TPRow}
     * @param id - TPRow.id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}", method = RequestMethod.GET)
    public ResponseEntity<List<TpolItem>> getItems(@PathVariable int id, @RequestParam(required = false) boolean all) {
        List<TpolItem> items = new ArrayList<>();
        for (TPItems enumItems : TPItems.values()) {
            TpolItem item = new TpolItem(enumItems.getItem());

            List<String[]> data = tPolItemsRepository.getData(item, id);

            if ((data != null && !data.isEmpty()) || all) {
                item.setItemData(data);
                items.add(item);
            }

        }
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    /**
     * get or check data for {@link TpolItem}
     * @param id {@link TPRow} id
     * @param name {@link TpolItem} name
     * @param data data
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/{name}", method = RequestMethod.GET)
    public ResponseEntity<List<String[]>> getItemData(@PathVariable int id, @PathVariable String name, @RequestParam(value = "data", required = false) String[] data) {
        TPItem tpItem = TPItems.getTpItem(name);
        if (tpItem != null) {
            if (data == null) {
                return new ResponseEntity<>(tPolItemsRepository.getData(new TpolItem(tpItem), id), HttpStatus.OK);
            } else {
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * save data for {@link TpolItem}
     * @param id {@link TPRow} id
     * @param name {@link TpolItem} name
     * @param data data
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/{name}", method = RequestMethod.POST)
    public ResponseEntity<Integer> saveItemData(@PathVariable int id, @PathVariable String name, @RequestParam(value = "data") String[] data) {
        TPItem tpItem = TPItems.getTpItem(name);
        if (tpItem != null) {
            return new ResponseEntity<>(tPolItemsRepository.addData(new TpolItem(tpItem), id, data), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * delete data for {@link TpolItem}
     * @param id {@link TPRow} id
     * @param name {@link TpolItem} name
     * @param data data
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/{name}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteItemData(@PathVariable int id, @PathVariable String name, @RequestParam(value = "data") String data) {
        TPItem tpItem = TPItems.getTpItem(name);
        if (tpItem != null) {
            return new ResponseEntity<>(tPolItemsRepository.deleteData(new TpolItem(tpItem), id, data), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

}
