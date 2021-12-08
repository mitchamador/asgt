package gbas.gtbch.web;

import gbas.gtbch.sapod.model.tpol.TpItem;
import gbas.gtbch.sapod.service.TPolItemsService;
import gbas.tvk.tpol3.service.TPItem;
import gbas.tvk.tpol3.service.TPItems;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tpol/items")
public class TPolItemsController {

    private final TPolItemsService tPolItemsService;

    public TPolItemsController(TPolItemsService tPolItemsService) {
        this.tPolItemsService = tPolItemsService;
    }

    /**
     * get {@link TpItem} list
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<TpItem>> getItems() {
        List<TpItem> items = new ArrayList<>();
        for (TPItems enumItems : TPItems.values()) {
            items.add(new TpItem(enumItems.getItem()));
        }
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    /**
     * get NSI for {@link TpItem}
     *
     * @param name - {@link TpItem} name
     * @return
     */
    @RequestMapping(value = "/nsi/{name}", method = RequestMethod.GET)
    public ResponseEntity<List<String[]>> getItemNsi(
            @PathVariable String name,
            @RequestParam(name = "set", required = false) Integer setParam) {
        List<String[]> data = null;

        int set = setParam == null ? 0 : setParam;

        TPItem tpItem = TPItems.getTpItem(name);
        if (tpItem != null) {
            data = tPolItemsService.getNsi(new TpItem(tpItem, set));
        }
        return new ResponseEntity<>(data, HttpStatus.OK);
    }


}
