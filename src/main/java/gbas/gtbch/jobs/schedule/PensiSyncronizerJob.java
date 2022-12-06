package gbas.gtbch.jobs.schedule;

import gbas.gtbch.jobs.AbstractServerJob;
import gbas.gtbch.jobs.PensiCommonJob;
import gbas.gtbch.jobs.annotations.ServerJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ServerJob(alias = "pensisyncronizer", name = "PENSI syncronizer")
public class PensiSyncronizerJob extends AbstractServerJob {

    private final PensiCommonJob pensiCommonJob;

    public PensiSyncronizerJob(PensiCommonJob pensiCommonJob) {
        this.pensiCommonJob = pensiCommonJob;
    }

    @Scheduled(cron = "${app.jobs.pensi-syncronizer.cron:-}")
    public void run() {
        runTask(() -> pensiCommonJob.getPensiManager().sync(pensiCommonJob.createPensiJobStatus(this)));
    }

}
