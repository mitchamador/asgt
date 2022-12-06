package gbas.gtbch.jobs.schedule;

import gbas.gtbch.jobs.AbstractServerJob;
import gbas.gtbch.jobs.PensiCommonJob;
import gbas.gtbch.jobs.annotations.ServerJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ServerJob(alias = "pensidownloader", name = "PENSI downloader")
public class PensiDownloaderJob extends AbstractServerJob {

    private final PensiCommonJob pensiCommonJob;

    public PensiDownloaderJob(PensiCommonJob pensiCommonJob) {
        this.pensiCommonJob = pensiCommonJob;
    }

    @Scheduled(cron = "${app.jobs.pensi-downloader.cron:-}")
    public void run() {
        runTask(() -> pensiCommonJob.getPensiManager().download(pensiCommonJob.createPensiJobStatus(this), false));
    }

}
