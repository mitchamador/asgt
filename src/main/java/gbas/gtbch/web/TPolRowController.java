package gbas.gtbch.web;

import gbas.gtbch.sapod.model.tpol.TpItem;
import gbas.gtbch.sapod.model.tpol.TpItemData;
import gbas.gtbch.sapod.model.tpol.TpRow;
import gbas.gtbch.sapod.service.TPolItemsService;
import gbas.gtbch.sapod.service.TPolRowService;
import gbas.gtbch.web.controlleradvice.annotations.DuplicateKeyExceptionHandler;
import gbas.tvk.tpol3.service.TPItem;
import gbas.tvk.tpol3.service.TPItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@DuplicateKeyExceptionHandler
@RequestMapping("/api/tpol/row")
public class TPolRowController {

    private static final Logger logger = LoggerFactory.getLogger(TPolRowController.class);

    private final TPolRowService tpolRowService;
    private final TPolItemsService tPolItemsService;


    public TPolRowController(TPolRowService tPolRowService, TPolItemsService tPolItemsService) {
        this.tpolRowService = tPolRowService;
        this.tPolItemsService = tPolItemsService;
    }

    /**
     * get TpRow
     * @param id tvk_t_pol.id
     */
    @RequestMapping(value = "/{id:[\\d]+}", method = RequestMethod.GET)
    public ResponseEntity<TpRow> getRow(@PathVariable int id, @RequestBody(required = false) Map<String, String> filterMap) {
        return new ResponseEntity<>(tpolRowService.getRow(id, filterMap), HttpStatus.OK);
    }

    /**
     * copy TpRow
     * @param id tvk_t_pol.id
     * @param documentId - destinatation documentId
     */
    @RequestMapping(value = "/{id:[\\d]+}/copy", method = RequestMethod.POST)
    public ResponseEntity<TpRow> copyRow(@PathVariable int id, @RequestParam(required = false, name = "doc_id") Integer documentId) {
        logger.info("copy source tp row with id = {} to tp document with id {}", id, documentId);
        return new ResponseEntity<>(tpolRowService.copyRow(id, documentId != null ? documentId : 0), HttpStatus.OK);
    }

    /**
     * create new TpRow
     * @param row - {@link TpRow}
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity saveRow(@RequestBody TpRow row) {
        int id = tpolRowService.saveRow(row);
        return id != 0 ? ResponseEntity.created(URI.create("/api/tpol/row/" + id)).build() : ResponseEntity.noContent().build();
    }

    /**
     * update TpRow
     * @param id - tvk_t_pol.id
     */
    @RequestMapping(value = "/{id:[\\d]+}", method = RequestMethod.PUT)
    public ResponseEntity updateRow(@PathVariable int id, @RequestBody TpRow row) {
        row.id = id;
        return tpolRowService.saveRow(row) != 0 ? ResponseEntity.ok().build() : ResponseEntity.noContent().build();
    }

    /**
     * delete TPRow
     * @param id - tvk_t_pol.id
     */
    @RequestMapping(value = "/{id:[\\d]+}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteRow(@PathVariable int id) {
        logger.info("delete tp row with id = {}", id);
        return new ResponseEntity<>(tpolRowService.deleteRow(id), HttpStatus.OK);
    }

    /**
     * get all items for {@link TpRow}
     * @param id - TPRow.id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/items", method = RequestMethod.GET)
    public ResponseEntity<List<TpItem>> getItems(
            @PathVariable int id,
            @RequestParam(required = false) boolean all) {
        List<TpItem> items = new ArrayList<>();
        for (TPItems enumItems : TPItems.values()) {
            TpItem item = new TpItem(enumItems.getItem());

            List<String[]> data = tPolItemsService.getData(item, id);

            if ((data != null && !data.isEmpty()) || all) {
                item.setItemData(data);
                items.add(item);
            }

        }
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    /**
     * get or check data for {@link TpItem}
     * @param id {@link TpRow} id
     * @param name {@link TpItem} name
     * @param data data
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/item/{name}", method = RequestMethod.GET)
    public ResponseEntity<List<String[]>> getItemData(
            @PathVariable int id,
            @PathVariable String name,
            @RequestParam(required = false) String[] data,
            @RequestParam(name = "set", required = false) Integer setParam) {
        int set = setParam == null ? 0 : setParam;

        TPItem tpItem = TPItems.getTpItem(name);
        if (tpItem != null) {
            if (data == null) {
                return new ResponseEntity<>(tPolItemsService.getData(new TpItem(tpItem, set), id), HttpStatus.OK);
            } else {
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * save data for {@link TpItem}
     * @param id {@link TpRow} id
     * @param name {@link TpItem} name
     * @param tpItemData {@link TpItemData}
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/item/{name}", method = RequestMethod.POST)
    public ResponseEntity<Boolean> saveItemData(
            @PathVariable int id,
            @PathVariable String name,
            @RequestBody TpItemData tpItemData) {

        List<String[]> dataArray = tpItemData.getData() != null ? Collections.singletonList(tpItemData.getData()) : tpItemData.getArray();

        TPItem tpItem = TPItems.getTpItem(name);

        boolean result = false;
        if (dataArray != null && tpItem != null) {
            TpItem tpolItem = new TpItem(tpItem, tpItemData.getSet());
            for (String[] d : dataArray) {
                if (!tPolItemsService.checkData(tpolItem, id, d)) {
                    tPolItemsService.addData(tpolItem, id, d);
                    logger.info("save item data: rowId = {}, item = {}, set = {}, data = {}", id, tpItem.getName(), tpItemData.getSet(), d);
                    result = true;
                } else {
                    logger.info("item data exists: rowId = {}, item = {}, set = {}, data = {}", id, tpItem.getName(), tpItemData.getSet(), d);
                }
            }
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * delete data for {@link TpItem}
     * @param id {@link TpRow} id
     * @param name {@link TpItem} name
     * @param tpItemData {@link TpItemData}
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/item/{name}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteItemData(
            @PathVariable int id,
            @PathVariable String name,
            @RequestBody TpItemData tpItemData) {

        List<String[]> dataArray = tpItemData.getData() != null ? Collections.singletonList(tpItemData.getData()) : tpItemData.getArray();

        TPItem tpItem = TPItems.getTpItem(name);

        boolean result = false;
        if (dataArray != null && tpItem != null) {
            TpItem tpolItem = new TpItem(tpItem, tpItemData.getSet());
            for (String[] d : dataArray) {
                if (Arrays.asList("ClassItem", "Distance", "StationTargetItem", "StationSourceItem").contains(name)) {
                    d[0] = d[1];
                }
                result |= tPolItemsService.deleteData(tpolItem, id, d[0]);
                logger.info("delete item data: rowId = {}, item = {}, set = {}, data = {}", id, tpItem.getName(), tpItemData.getSet(), d);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
