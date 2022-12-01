package gbas.gtbch.jobs.schedule;

import gbas.gtbch.jobs.annotations.JobAlias;
import gbas.gtbch.jobs.impl.PensiServerJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@JobAlias("pensidownloader")
public class PensiDownloaderJob extends PensiServerJob {

    @Override
    protected String getPensiTaskName() {
        return "downloader";
    }

    @Scheduled(cron = "${app.jobs.pensi-downloader.cron:-}")
    public void run() {
        runTask(() -> pensiManager.download(getPensiJobStatus(), false));
    }

}
