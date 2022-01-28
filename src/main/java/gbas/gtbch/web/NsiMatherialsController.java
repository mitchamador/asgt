package gbas.gtbch.web;

import gbas.gtbch.sapod.model.matherials.Matherial;
import gbas.gtbch.sapod.model.matherials.Measure;
import gbas.gtbch.sapod.model.matherials.SborDescriptorItem;
import gbas.gtbch.sapod.service.MatherialService;
import gbas.gtbch.sapod.service.MeasureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/user/nsi/matherials")
public class NsiMatherialsController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final MatherialService matherialService;
    private final MeasureService measureService;

    public NsiMatherialsController(MatherialService matherialService, MeasureService measureService) {
        this.matherialService = matherialService;
        this.measureService = measureService;
    }

    /**
     * matherials page
     * @return
     */
    @GetMapping("")
    public ModelAndView adminMatherials() {
        ModelAndView model = new ModelAndView("user/nsi/matherials");

        return model;
    }

    /**
     * get {@link gbas.gtbch.sapod.model.matherials.Matherial}
     * @param id - matherial.matherial
     */
    @RequestMapping(value = "/{id:[\\d]+}/editor", method = RequestMethod.GET)
    public ModelAndView getDocumentEditor(@PathVariable int id) {
        ModelAndView model = new ModelAndView("fragments/matherials :: matherialEditor");

        Matherial matherial;
        if (id == 0) {
            matherial = new Matherial();

            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.clear(Calendar.MINUTE);
            c.clear(Calendar.SECOND);
            c.clear(Calendar.MILLISECOND);
            matherial.setDateBegin(c.getTime());

            c.add(Calendar.YEAR, 1);
            c.add(Calendar.DAY_OF_YEAR, -1);
            matherial.setDateEnd(c.getTime());

        } else {
            matherial = matherialService.getMatherial(id);
        }

        model.addObject("matherial", matherial);

        model.addObject("descriptors", SborDescriptorItem.getList());

        List<Measure> measures = new ArrayList<>(Collections.singletonList(new Measure(0, "")));
        measures.addAll(measureService.getMeasureList());
        model.addObject("measures", measures);

        return model;
    }

}
