package gbas.gtbch.web;

import gbas.gtbch.sapod.model.*;
import gbas.gtbch.sapod.service.TPolItemsService;
import gbas.gtbch.sapod.service.TPolService;
import gbas.gtbch.sapod.service.TPolRowService;
import gbas.tvk.tpol3.service.TPItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    @GetMapping("index")
    public ModelAndView adminTp() {
        ModelAndView model = new ModelAndView("user/nsi/tpol/index");

        List<TpolGroup> tpolGroups = tPolService.getGroups();
        TpolGroup tpolGroup = new TpolGroup();
        tpolGroup.setName("Все");
        tpolGroups.add(0, tpolGroup);
        model.addObject("tpGroups", tpolGroups);

        return model;
    }

    /**
     * tp documents fragment
     * @return
     */
    @GetMapping("documents")
    public ModelAndView adminTpDocuments() {
        return new ModelAndView("fragments/tpol :: documents");
    }

    /**
     * tp rows framgent
     * @param id
     * @return
     */
    @GetMapping(value = "document/{id:[\\d]+}/rows")
    public ModelAndView getRows(@PathVariable int id) {
        ModelAndView model = new ModelAndView("fragments/tpol :: rows");

        model.addObject("tpolRows", tPolRowService.getRows(id).stream().map(TpRow::new).collect(Collectors.toList()));

        return model;
    }

    /**
     * get {@link TPolDocument}
     * @param id - tvk_tarif.id
     */
    @RequestMapping(value = "/document/{id:[\\d]+}/editor", method = RequestMethod.GET)
    public ModelAndView getDocumentEditor(@PathVariable int id) {
        ModelAndView model = new ModelAndView("fragments/tpol :: documentEditor");

        model.addObject("document", tPolService.getDocument(id, true));
        model.addObject("tarif", tPolService.getBaseTarifList());

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

        List<TpolItem> items = new ArrayList<>();
        for (TPItems enumItems : TPItems.values()) {
            TpolItem item = new TpolItem(enumItems.getItem());

            List<String[]> data = tPolItemsService.getData(item, id);

//            if ((data != null && !data.isEmpty())) {
//                item.setItemData(data);
//                items.add(item);
//            }

            item.setItemDataSize(data != null && !data.isEmpty() ? data.size() : 0);
            items.add(item);
        }
        model.addObject("items", items);
        model.addObject("rowId", id);

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

        TpolItem item = new TpolItem(TPItems.getTpItem(name), set);
        if (item.getItem() != null) {
            item.setItemData(tPolItemsService.getData(item, id));
            item.setItemDataSize(item.getItemData() != null && !item.getItemData().isEmpty() ? item.getItemData().size() : 0);
            //item.setNsiData(tPolItemsRepository.getNsi(item));
        }
        model.addObject("item", item);
        model.addObject("rowId", id);

        return model;
    }


}
