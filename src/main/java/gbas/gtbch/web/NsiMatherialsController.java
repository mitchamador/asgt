package gbas.gtbch.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user/nsi/matherials")
public class NsiMatherialsController {

    private Logger logger = LoggerFactory.getLogger(getClass());

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
    @RequestMapping(value = "/matherials/{id:[\\d]+}/editor", method = RequestMethod.GET)
    public ModelAndView getDocumentEditor(@PathVariable int id) {
        ModelAndView model = new ModelAndView("fragments/matherials :: matherialEditor");

        return model;
    }

}
