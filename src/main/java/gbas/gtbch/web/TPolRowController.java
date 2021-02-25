package gbas.gtbch.web;

import gbas.gtbch.sapod.model.TpolItem;
import gbas.gtbch.sapod.service.TPolItemsService;
import gbas.gtbch.sapod.service.TPolRowService;
import gbas.tvk.tpol3.service.TPItem;
import gbas.tvk.tpol3.service.TPItems;
import gbas.tvk.tpol3.service.TPRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
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
     * get TPRow
     * @param id tvk_t_pol.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TPRow> getRow(@PathVariable int id) {
        return new ResponseEntity<>(tpolRowService.getRow(id), HttpStatus.OK);
    }

    /**
     * copy TPRow
     * @param id tvk_t_pol.id
     * @param documentId - destinatation documentId
     */
    @RequestMapping(value = "/{id}/copy", method = RequestMethod.GET)
    public ResponseEntity<TPRow> copyRow(@PathVariable int id, @RequestParam(required = false, name = "doc_id") Integer documentId) {
        logger.info("copy source tp row with id = {} to tp document with id {}", id, documentId);
        return new ResponseEntity<>(tpolRowService.copyRow(id, documentId != null ? documentId : 0), HttpStatus.OK);
    }

    /**
     * create new TPRow
     * @param row - {@link TPRow}
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity saveRow(@RequestBody TPRow row) {
        int id = tpolRowService.saveRow(row);
        return id != 0 ? ResponseEntity.created(URI.create("/api/tpol/row/" + id)).build() : ResponseEntity.notFound().build();
    }

    /**
     * update TPRow
     * @param id - tvk_t_pol.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateRow(@PathVariable int id, @RequestBody TPRow row) {
        row.id = id;
        return tpolRowService.saveRow(row) != 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * delete TPRow
     * @param id - tvk_t_pol.id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteRow(@PathVariable int id) {
        logger.info("delete tp row with id = {}", id);
        return new ResponseEntity<>(tpolRowService.deleteRow(id), HttpStatus.OK);
    }

    /**
     * get all items for {@link TPRow}
     * @param id - TPRow.id
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/items", method = RequestMethod.GET)
    public ResponseEntity<List<TpolItem>> getItems(
            @PathVariable int id,
            @RequestParam(required = false) boolean all) {
        List<TpolItem> items = new ArrayList<>();
        for (TPItems enumItems : TPItems.values()) {
            TpolItem item = new TpolItem(enumItems.getItem());

            List<String[]> data = tPolItemsService.getData(item, id);

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
                return new ResponseEntity<>(tPolItemsService.getData(new TpolItem(tpItem, set), id), HttpStatus.OK);
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
    @RequestMapping(value = "/{id:[\\d]+}/item/{name}", method = RequestMethod.POST)
    public ResponseEntity<Boolean> saveItemData(
            @PathVariable int id,
            @PathVariable String name,
            @RequestParam(required = false) String[] data,
            @RequestParam(required = false) List<String[]> array,
            @RequestParam(name = "set", required = false) Integer setParam) {

        List<String[]> dataArray = data != null ? Collections.singletonList(data) : array;

        int set = setParam == null ? 0 : setParam;

        TPItem tpItem = TPItems.getTpItem(name);

        boolean result = false;
        if (dataArray != null && tpItem != null) {
            TpolItem tpolItem = new TpolItem(tpItem, set);
            for (String[] d : dataArray) {
                if (!tPolItemsService.checkData(tpolItem, id, d)) {
                    tPolItemsService.addData(tpolItem, id, d);
                    logger.info("save item data: rowId = {}, item = {}, set = {}, data = {}", id, tpItem.getName(), set, d);
                    result = true;
                } else {
                    logger.info("item data exists: rowId = {}, item = {}, set = {}, data = {}", id, tpItem.getName(), set, d);
                }
            }
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * delete data for {@link TpolItem}
     * @param id {@link TPRow} id
     * @param name {@link TpolItem} name
     * @param data data
     * @return
     */
    @RequestMapping(value = "/{id:[\\d]+}/item/{name}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteItemData(
            @PathVariable int id,
            @PathVariable String name,
            @RequestParam(required = false) String[] data,
            @RequestParam(required = false) List<String[]> array,
            @RequestParam(name = "set", required = false) Integer setParam) {

        List<String[]> dataArray = data != null ? Collections.singletonList(data) : array;

        int set = setParam == null ? 0 : setParam;

        TPItem tpItem = TPItems.getTpItem(name);

        boolean result = false;
        if (dataArray != null && tpItem != null) {
            TpolItem tpolItem = new TpolItem(tpItem, set);
            for (String[] d : dataArray) {
                if (Arrays.asList("ClassItem", "Distance", "StationTargetItem", "StationSourceItem").contains(name)) {
                    d[0] = d[1];
                }
                result |= tPolItemsService.deleteData(tpolItem, id, d[0]);
                logger.info("delete item data: rowId = {}, item = {}, set = {}, data = {}", id, tpItem.getName(), set, d);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
