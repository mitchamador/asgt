package gbas.gtbch.schedule;

import gbas.gtbch.util.PensiServerJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PensiMailerJob extends PensiServerJob {

    @Override
    protected String getPensiTaskName() {
        return "mailer";
    }

    @Value("${app.jobs.pensi-mailer.cron:-}")
    private String cronString;

    @Scheduled(cron = "${app.jobs.pensi-mailer.cron:-}")
    public void run() {
        run(() -> pensiManager.mailer(getPensiJobStatus()));
    }

    @PostConstruct
    public void runPostConstruct() {
        if (getJobEnabled(cronString)) {
            //run();
        }
    }


}
