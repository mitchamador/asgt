package gbas.gtbch.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gbas.gtbch.sapod.model.tpol.TpItem;
import gbas.gtbch.sapod.model.tpol.TpItemFilter;
import gbas.gtbch.sapod.service.TPolItemsService;
import gbas.tvk.tpol3.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tpol/items")
public class TPolItemsController {

    private final TPolItemsService tPolItemsService;

    private final ObjectMapper mapper;

    public TPolItemsController(TPolItemsService tPolItemsService, ObjectMapper mapper) {
        this.tPolItemsService = tPolItemsService;
        this.mapper = mapper;
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

    /**
     * get {@link TpItemFilter} list
     *
     * @return
     */
    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    public ResponseEntity<List<TpItemFilter>> getItemsFilter(@RequestParam(required = false) boolean nsi) {
        List<TpItemFilter> items = new ArrayList<>();
        for (TPItems enumItems : TPItems.values()) {
            TPItem item = enumItems.getItem();
            if (item instanceof CargoItem || item instanceof CargoDiapItem) {
                for (int set = 0; set < 2; set++) {
                    TpItemFilter filter = new TpItemFilter();
                    filter.setType(item.getName() + ":" + set);
                    filter.setName(item.getButtonName() + (set == 0 ? " ГНГ" : " ЕТСНГ"));
                    if (item instanceof CargoItem) {
                        filter.setNsiColumns(getFilterColumns(item, set, set == 0 ? 4 : 1));
                    } else if (item instanceof CargoDiapItem) {
                        filter.setNsiColumns(getFilterColumns(item, set, 1));
                    }
                    if (nsi) {
                        filter.setNsiData(_getFilterNsi(filter.getType()));
                    }
                    items.add(filter);
                }
            } else {
                TpItemFilter filter = new TpItemFilter();
                filter.setType(item.getName());
                filter.setName(item.getButtonName());
                filter.setNsiColumns(getFilterColumns(item, 0, 1));
                if (nsi) {
                    filter.setNsiData(_getFilterNsi(filter.getType()));
                }

                items.add(filter);
            }
        }
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    /**
     * get NSI for {@link TpItemFilter}
     *
     * @param name - {@link TpItemFilter} name
     * @return
     */
    @RequestMapping(value = "/filter/{name}/nsi", method = RequestMethod.GET)
    public ResponseEntity getFilterNsi(HttpServletRequest request, @PathVariable String name) throws JsonProcessingException {
        return GzippedResponseEntity.getGzippedResponseEntity(request, mapper.writeValueAsString(_getFilterNsi(name)));
    }

    private String[] getFilterColumns(TPItem item, int set, int secondColumnIndex) {
        ColumnInfo[] columnInfos = item.getNSIColumnInfo(set);
        List<String> nsiColumns = new ArrayList<>();
        if (columnInfos != null) {
            if (columnInfos.length > 0) {
                nsiColumns.add(columnInfos[0].text);
            }
            if (columnInfos.length > secondColumnIndex) {
                nsiColumns.add(columnInfos[secondColumnIndex].text);
            }
        }
        return nsiColumns.isEmpty() ? null : nsiColumns.toArray(new String[0]);
    }

    private List<String[]> _getFilterNsi(String name) {
        List<String[]> data = null;

        String[] filterName = name.split(":");
        int set = filterName.length > 1 ? Integer.parseInt(filterName[1]) : 0;

        TPItem tpItem = TPItems.getTpItem(filterName[0]);
        if (tpItem != null) {
            List<String[]> tData = tPolItemsService.getNsi(new TpItem(tpItem, set));
            int secondColumnIndex;
            if (tpItem instanceof CargoItem && set == 0) {
                secondColumnIndex = 5;
            } else {
                secondColumnIndex = 2;
            }
            data = new ArrayList<>();
            for (String[] tStrings : tData) {
                data.add(new String[] {tStrings[1], tStrings[secondColumnIndex]});
            }
        }
        return data;
    }

}
