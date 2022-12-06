package gbas.gtbch.jobs.schedule;

import gbas.gtbch.jobs.AbstractServerJob;
import gbas.gtbch.jobs.PensiCommonJob;
import gbas.gtbch.jobs.annotations.ServerJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ServerJob(alias = "pensimailer", name = "PENSI mailer")
public class PensiMailerJob extends AbstractServerJob {

    private final PensiCommonJob pensiCommonJob;

    public PensiMailerJob(PensiCommonJob pensiCommonJob) {
        this.pensiCommonJob = pensiCommonJob;
    }

    @Scheduled(cron = "${app.jobs.pensi-mailer.cron:-}")
    public void run() {
        runTask(() -> pensiCommonJob.getPensiManager().mailer(pensiCommonJob.createPensiJobStatus(this)));
    }

}
