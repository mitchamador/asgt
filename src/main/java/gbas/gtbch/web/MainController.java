package gbas.gtbch.web;

import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.gtbch.util.UtilDate8;
import gbas.gtbch.web.request.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class MainController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * {@link gbas.gtbch.util.SystemInfo}
     */
    private final List<KeyValue> systemPropertiesList;

    /**
     * {@link BeanFactory}
     */
    private final ApplicationContext context;

    public MainController(List<KeyValue> systemProperties, ApplicationContext context) {
        this.systemPropertiesList = systemProperties;
        this.context = context;
    }

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

    @GetMapping("/user")
    public ModelAndView userIndex() {
        return new ModelAndView("user/index");
    }

    @GetMapping("/admin")
    public ModelAndView adminIndex() {
        return new ModelAndView("admin/index");
    }

    @Autowired
    private TpImportDateService tpImportDateService;

    @GetMapping("/admin/sync")
    public ModelAndView adminSync() {

        TpImportDate tpImportDate = tpImportDateService.getTpImportDate();

        return new ModelAndView("admin/sync", "tpdate", tpImportDate != null ? tpImportDate.getTpDateString() : "");
    }

    @GetMapping("/admin/nbrb")
    public ModelAndView adminNbrb() {
        return new ModelAndView("admin/nbrb");
    }

    @GetMapping("/admin/mq")
    public ModelAndView adminMq() {
        return new ModelAndView("admin/mq");
    }

    @GetMapping("/admin/calclog")
    public ModelAndView adminCalcLog() {
        ModelAndView model = new ModelAndView("admin/calclog");

        List<KeyValue> sources = new ArrayList<>();
        sources.add(new KeyValue(null, "Все"));
        for (CalculationLog.Source source : CalculationLog.Source.values()) {
            sources.add(new KeyValue(source.name(), source.getName()));
        }
        model.addObject("sources", sources);

        List<KeyValue> types = new ArrayList<>();
        types.add(new KeyValue(null, "Все"));
        for (CalculationLog.Type type : CalculationLog.Type.values()) {
            types.add(new KeyValue(type.name(), type.getName()));
        }
        model.addObject("types", types);

        return model;
    }

    @GetMapping("/index")
    public String index() {
        return "index";
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
        List<KeyValue> systemProperties = new ArrayList<>(systemPropertiesList);

        try {
            Instant startTime = context.getBean("startTime", Instant.class);

            Duration upTime = Duration.between(startTime, Instant.now());

            systemProperties.add(new KeyValue("Время",
                    String.format("start time: %s, startup duration: %s, uptime: %s",
                            getStringTime(startTime),
                            getStringTime(context.getBean("startupDuration", Duration.class), 180),
                            getStringTime(upTime))));
        } catch (BeansException e) {
            e.printStackTrace();
        }

        return new ModelAndView("user/info", "info", systemProperties);
    }

    private String getStringTime(Object time) {
        return getStringTime(time, 10);
    }

    private String getStringTime(Object time, int diff) {
        if (time instanceof Instant) {
            return UtilDate8.getStringDate(new java.util.Date(((Instant) time).toEpochMilli()), "dd.MM.yyyy HH:mm:ss");
        } else if (time instanceof Duration) {
            Duration duration = (Duration) time;
            if (duration.getSeconds() > diff) {
                return String.format("%s days %s hours %s minutes", duration.toDays(), duration.toHours() % 24, duration.toMinutes() % 60);
            } else {
                return  (double) duration.toMillis() / TimeUnit.SECONDS.toMillis(1) + " s.";
            }
        }
        return "n/a";
    }



}
