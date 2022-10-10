package gbas.gtbch.web;

import gbas.gtbch.sapod.model.tpol.TpDocument;
import gbas.gtbch.sapod.model.tpol.TpGroup;
import gbas.gtbch.sapod.model.tpol.TpItem;
import gbas.gtbch.sapod.service.TPolItemsService;
import gbas.gtbch.sapod.service.TPolRowService;
import gbas.gtbch.sapod.service.TPolService;
import gbas.tvk.tpol3.service.TPItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user/nsi/tpol")
public class NsiTpolController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final TPolService tPolService;
    private final TPolItemsService tPolItemsService;
    private final TPolRowService tPolRowService;

    public NsiTpolController(TPolService tPolService, TPolRowService tPolRowService, TPolItemsService tPolItemsService) {
        this.tPolService = tPolService;
        this.tPolRowService = tPolRowService;
        this.tPolItemsService = tPolItemsService;
    }

    /**
     * tp page
     * @return
     */
    @GetMapping("")
    public ModelAndView adminTp() {
        ModelAndView model = new ModelAndView("user/nsi/tpol");

        List<TpGroup> tpGroups = tPolService.getGroups();
        TpGroup tpGroup = new TpGroup();
        tpGroup.setName("Все");
        tpGroups.add(0, tpGroup);
        model.addObject("tpGroups", tpGroups);

        return model;
    }

    /**
     * tp rows framgent
     * @param id
     * @return
     */
    @GetMapping(value = "document/{id:[\\d]+}/rows")
    public ModelAndView getRows(@PathVariable int id, @RequestBody(required = false) Map<String, String> filterMap) {
        ModelAndView model = new ModelAndView("fragments/tpol :: rows");

        model.addObject("tpolRows", tPolRowService.getRows(id, filterMap));

        return model;
    }

    /**
     * get {@link TpDocument}
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/document/{id:[\\d]+}/editor", method = RequestMethod.GET)
    public ModelAndView getDocumentEditor(@PathVariable int id) {
        ModelAndView model = new ModelAndView("fragments/tpol :: documentEditor");

        TpDocument tpDocument;
        if (id == 0) {
            tpDocument = new TpDocument();

            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.clear(Calendar.MINUTE);
            c.clear(Calendar.SECOND);
            c.clear(Calendar.MILLISECOND);
            tpDocument.date_begin = c.getTime();

            c.add(Calendar.YEAR, 1);
            c.add(Calendar.DAY_OF_YEAR, -1);
            tpDocument.date_end = c.getTime();

            tpDocument.sobstList = tPolService.getSobstList(0, true);
        } else {
            tpDocument = tPolService.getDocument(id, true);
        }

        model.addObject("document", tpDocument);
        model.addObject("tarif", tPolService.getBaseTarifList());
        model.addObject("groups", tPolService.getGroups());

        return model;
    }



    /**
     * tp items fragment
     * @param id
     * @return
     */
    @GetMapping(value = "row/{id:[\\d]+}/items")
    public ModelAndView getItems(@PathVariable int id) {
        ModelAndView model = new ModelAndView("fragments/tpol :: items");

        List<TpItem> items = new ArrayList<>();
        for (TPItems enumItems : TPItems.values()) {
            TpItem item = new TpItem(enumItems.getItem());

            List<String[]> data = tPolItemsService.getData(item, id);

//            if ((data != null && !data.isEmpty())) {
//                item.setItemData(data);
//                items.add(item);
//            }

            item.setItemDataSize(data != null && !data.isEmpty() ? data.size() : 0);
            items.add(item);
        }
        model.addObject("items", items);
        model.addObject("row", tPolRowService.getRow(id, null));

        return model;
    }

    /**
     * tp item fragment
     * @param id
     * @param name
     * @return
     */
    @GetMapping(value = "row/{id:[\\d]+}/item/{name}")
    public ModelAndView getItems(
            @PathVariable int id,
            @PathVariable String name,
            @RequestParam(name = "set", required = false) Integer setParam) {

        int set = setParam == null ? 0 : setParam;

        ModelAndView model = new ModelAndView("fragments/tpol :: item");

        TpItem item = new TpItem(TPItems.getTpItem(name), set);
        if (item.getItem() != null) {
            item.setItemData(tPolItemsService.getData(item, id));
            item.setItemDataSize(item.getItemData() != null && !item.getItemData().isEmpty() ? item.getItemData().size() : 0);
            //item.setNsiData(tPolItemsRepository.getNsi(item));
        }
        model.addObject("item", item);
        model.addObject("row", tPolRowService.getRow(id, null));

        return model;
    }


}
