package gbas.gtbch.jobs.schedule;

import gbas.gtbch.jobs.AbstractServerJob;
import gbas.gtbch.jobs.PensiCommonJob;
import gbas.gtbch.jobs.annotations.ServerJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ServerJob(alias = "pensiupdater", name = "PENSI updater")
public class PensiUpdaterJob extends AbstractServerJob {

    private final PensiCommonJob pensiCommonJob;

    public PensiUpdaterJob(PensiCommonJob pensiCommonJob) {
        this.pensiCommonJob = pensiCommonJob;
    }


    @Scheduled(cron = "${app.jobs.pensi-updater.cron:-}")
    public void run() {
        runTask(() -> pensiCommonJob.getPensiManager().update(pensiCommonJob.createPensiJobStatus(this), false));
    }

}
