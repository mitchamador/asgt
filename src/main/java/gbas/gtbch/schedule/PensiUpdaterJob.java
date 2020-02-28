package gbas.gtbch.schedule;

import gbas.gtbch.util.PensiServerJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PensiUpdaterJob extends PensiServerJob {

    @Override
    protected String getPensiTaskName() {
        return "updater";
    }

    @Value("${app.jobs.pensi-updater.cron:-}")
    protected String cronString;

    @Scheduled(cron = "${app.jobs.pensi-updater.cron:-}")
    public void run() {
        run(() -> pensiManager.update(getPensiJobStatus(), false));
    }

    @PostConstruct
    public void runPostConstruct() {
        if (getJobEnabled(cronString)) {
            //run();
        }
    }

}
