package gbas.gtbch.web;

import gbas.gtbch.jobs.AbstractServerJob;
import gbas.gtbch.jobs.ServerJobAliasHandler;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.gtbch.util.SystemInfoProperties;
import gbas.gtbch.web.request.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * {@link BeanFactory}
     */
    private final ApplicationContext context;

    public MainController(ApplicationContext context) {
        this.context = context;
    }

    /**
     * index.html
     * @param
     * @return
     */
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    /**
     * login page
     * @return
     */
    @GetMapping("/login")
    public String login() {
        return "index";
    }

    /**
     * access-denied page
     * @return
     */
    @GetMapping("/error/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }

    /**
     * index.html
     * @param authentication
     * @return
     */
    @GetMapping("/")
    public ModelAndView root(Authentication authentication) {
        if (authentication != null) {
            if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
                return new ModelAndView("admin/index");
            } else if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER"))) {
                return new ModelAndView("user/index");
            } else if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_GUEST"))) {
                return new ModelAndView("guest/index");
            }
        }
        return new ModelAndView("index");
    }

    /**
     * main user page
     * @return
     */
    @GetMapping("/user")
    public ModelAndView userIndex() {
        return new ModelAndView("user/index");
    }

    /**
     * main admin page
     * @return
     */
    @GetMapping("/admin")
    public ModelAndView adminIndex() {
        return new ModelAndView("admin/index");
    }

    @Autowired
    private TpImportDateService tpImportDateService;

    /**
     * sync page
     * @return
     */
    @GetMapping("/admin/sync")
    public ModelAndView adminSync() {

        TpImportDate tpImportDate = tpImportDateService.getTpImportDate();

        return new ModelAndView("admin/sync", "tpdate", tpImportDate != null ? tpImportDate.getTpDateString() : "");
    }

    /**
     * nbrb page
     * @return
     */
    @GetMapping("/admin/nbrb")
    public ModelAndView adminNbrb() {
        return new ModelAndView("admin/nbrb");
    }

    private ServerJobAliasHandler serverJobAliasHandler;

    @Autowired
    public void setServerJobController(ServerJobAliasHandler serverJobAliasHandler) {
        this.serverJobAliasHandler = serverJobAliasHandler;
    }
    /**
     * unified server job page
     * @return
     */
    @GetMapping("/admin/job/{job}")
    public ModelAndView adminMq(@PathVariable(value = "job") String jobName) {
        Map<String, Object> modelMap = null;
        AbstractServerJob serverJob = serverJobAliasHandler.getServerJob(jobName);
        if (serverJob != null) {
            modelMap = serverJob.getWebPageData();
        }
        return new ModelAndView("admin/job", modelMap);
    }

    /**
     * calulation log page
     * @return
     */
    @GetMapping("/admin/calclog")
    public ModelAndView adminCalcLog(Authentication authentication) {
        ModelAndView model = new ModelAndView("admin/calclog");

        List<KeyValue> sources = new ArrayList<>();
        sources.add(new KeyValue(null, "Все"));
        for (CalculationLog.Source source : CalculationLog.Source.values()) {
            sources.add(new KeyValue(source.name(), source.getName()));
        }
        model.addObject("sources", sources);

        List<KeyValue> types = new ArrayList<>();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // add all document types for 'su'
            if ("su".equals(((UserDetails) authentication.getPrincipal()).getUsername())) {
                types.add(new KeyValue(null, "Все"));
            }
        }
        for (CalculationLog.Type type : CalculationLog.Type.values()) {
            types.add(new KeyValue(type.name(), type.getName()));
        }
        model.addObject("types", types);

        return model;
    }

    /**
     * calculation page
     * @return
     */
    @GetMapping("/user/calc")
    public ModelAndView calc() {
        return new ModelAndView("user/calc");
    }

    @Autowired
    private SystemInfoProperties systemInfoProperties;

    /**
     * system info page
     * @return
     */
    @GetMapping("/user/info")
    public ModelAndView info() {
        return new ModelAndView("user/info", "info", systemInfoProperties.getSystemProperties());
    }


}
