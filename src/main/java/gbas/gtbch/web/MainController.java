package gbas.gtbch.web;

import gbas.gtbch.web.request.KeyValue;
import gbas.tvk.util.UtilDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
     * {@link ApplicationContext}
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
        List<KeyValue> systemProperties = new ArrayList<>(systemPropertiesList);

        Instant startTime = (Instant) context.getBean("startTime");
        Duration upTime = Duration.between(startTime, Instant.now());

        systemProperties.add(new KeyValue("Время",
                String.format("start time: %s, startup duration: %s, uptime: %s",
                        getStringTime(startTime),
                        getStringTime(context.getBean("startupDuration")),
                        getStringTime(upTime))));

        return new ModelAndView("user/info", "info", systemProperties);
    }

    private String getStringTime(Object time) {
        if (time instanceof Instant) {
            return UtilDate.getStringDate(new java.util.Date(((Instant) time).toEpochMilli()), "dd.MM.yyyy HH:mm:ss");
        } else if (time instanceof Duration) {
            Duration duration = (Duration) time;
            if (duration.getSeconds() > 10) {
                return String.format("%s days %s hours %s minutes", duration.toDays(), duration.toHours() % 24, duration.toMinutes() % 60);
            } else {
                return  (double) duration.toMillis() / TimeUnit.SECONDS.toMillis(1) + " s.";
            }
        }
        return "n/a";
    }



}
