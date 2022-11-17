package gbas.gtbch.jobs.schedule;

import gbas.gtbch.jobs.impl.PensiServerJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PensiUpdaterJob extends PensiServerJob {

    @Override
    protected String getPensiTaskName() {
        return "updater";
    }

    @Scheduled(cron = "${app.jobs.pensi-updater.cron:-}")
    public void run() {
        runTask(() -> pensiManager.update(getPensiJobStatus(), false));
    }

}
