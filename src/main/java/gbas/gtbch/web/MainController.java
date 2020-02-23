package gbas.gtbch.web;

import gbas.gtbch.web.request.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class MainController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private final List<KeyValue> systemPropertiesList;

    public MainController(List<KeyValue> systemProperties) {
        this.systemPropertiesList = systemProperties;
    }

    @GetMapping("/")
    public ModelAndView root(Authentication authentication) {
        if (authentication != null) {
            if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
                return new ModelAndView("admin/index");
            } else if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER"))) {
                return new ModelAndView("user/index");
            }
        }
        return new ModelAndView("index");
    }

    @GetMapping("/user")
    public ModelAndView userIndex() {
        return new ModelAndView("user/index");
    }

    @GetMapping("/admin")
    public ModelAndView adminIndex() {
        return new ModelAndView("admin/index");
    }

    @GetMapping("/admin/sync")
    public ModelAndView adminSync() {
        return new ModelAndView("admin/sync");
    }

    @GetMapping("/admin/nbrb")
    public ModelAndView adminNbrb() {
        return new ModelAndView("admin/nbrb");
    }

    @GetMapping("/admin/mq")
    public ModelAndView adminMq() {
        return new ModelAndView("admin/mq");
    }

    @GetMapping("/login")
    public String login() {
        return "index";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }

    @GetMapping("/user/calc")
    public ModelAndView calc() {
        return new ModelAndView("user/calc");
    }

    @GetMapping("/user/info")
    public ModelAndView info() {
        return new ModelAndView("user/info", "info", systemPropertiesList);
    }

}
