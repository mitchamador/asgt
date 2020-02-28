package gbas.gtbch.schedule;

import gbas.gtbch.util.PensiServerJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PensiSyncronizerJob extends PensiServerJob {

    @Override
    protected String getPensiTaskName() {
        return "syncronizer";
    }

    @Value("${app.jobs.pensi-syncronizer.cron:-}")
    protected String cronString;

    @Scheduled(cron = "${app.jobs.pensi-syncronizer.cron:-}")
    public void run() {
        run(() -> pensiManager.sync(getPensiJobStatus()));
    }

    @PostConstruct
    public void runPostConstruct() {
        if (getJobEnabled(cronString)) {
            //run();
        }
    }

}
