package gbas.gtbch.web;

import gbas.gtbch.sapod.model.TpolItem;
import gbas.gtbch.sapod.repository.TpolRepositoryImpl;
import gbas.tvk.tpol3.TpolDocument;
import gbas.tvk.tpol3.service.ColumnInfo;
import gbas.tvk.tpol3.service.TPItem;
import gbas.tvk.tpol3.service.TPItems;
import gbas.tvk.tpol3.service.TPRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class TpolController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    final TpolRepositoryImpl tpolRepository;

    public TpolController(TpolRepositoryImpl tpolRepository) {
        this.tpolRepository = tpolRepository;
    }

    /**
     * get TpolItems
     * @return
     */
    @RequestMapping(value = "/api/tpol/items", method = RequestMethod.GET)
    public ResponseEntity<List<TpolItem>> getItems() {
        List<TpolItem> items = new ArrayList<>();
        for (TPItems enumItems : TPItems.values()) {
            items.add(new TpolItem(enumItems.getItem()));
        }
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    /**
     * get NSI for TpolItem
     * @param name - TpolItem name
     * @return
     */
    @RequestMapping(value = "/api/tpol/items/nsi/{name}", method = RequestMethod.GET)
    public ResponseEntity<List<String[]>> getItemNsi(@PathVariable String name) {
        List<String[]> data = null;
        TPItem tpItem = TPItems.getTpItem(name);
        if (tpItem != null) {
            data = tpolRepository.getNsi(new TpolItem(tpItem));
        }
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /**
     * get all items for TPRow
     * @param id - TPRow.id
     * @return
     */
    @RequestMapping(value = "/api/tpol/items/{id:[\\d]+}", method = RequestMethod.GET)
    public ResponseEntity<List<TpolItem>> getItems(@PathVariable int id, @RequestParam(required = false) boolean all) {
        List<TpolItem> items = new ArrayList<>();
        for (TPItems enumItems : TPItems.values()) {
            TpolItem item = new TpolItem(enumItems.getItem());

            List<String[]> data = tpolRepository.getData(item, id);

            if ((data != null && !data.isEmpty()) || all) {
                item.setItemData(data);
                items.add(item);
            }

        }
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    /**
     * get data for TpolItem
     * @param name - TpolItem name
     * @return
     */
    @RequestMapping(value = "/api/tpol/items/{id:[\\d]+}/{name}", method = RequestMethod.GET)
    public ResponseEntity<List<String[]>> getItemData(@PathVariable int id, @PathVariable String name) {
        List<String[]> data = null;
        TPItem tpItem = TPItems.getTpItem(name);
        if (tpItem != null) {
            data = tpolRepository.getData(new TpolItem(tpItem), id);
        }
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /**
     * get TpolDocument list
     */
    @RequestMapping(value = "/api/tpol/documents", method = RequestMethod.GET)
    public ResponseEntity<List<TpolDocument>> getDocuments(
            @RequestParam(value = "date_begin", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateBegin,
            @RequestParam(value = "date_end", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateEnd) {
        return new ResponseEntity<>(tpolRepository.getDocuments(dateBegin, dateEnd), HttpStatus.OK);
    }

    /**
     * get TPRow list
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/api/tpol/rows/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<TPRow>> getRows(@PathVariable int id) {
        return new ResponseEntity<>(tpolRepository.getRows(id), HttpStatus.OK);
    }

}