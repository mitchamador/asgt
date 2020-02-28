package gbas.gtbch.schedule;

import gbas.gtbch.util.PensiServerJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PensiDownloaderJob extends PensiServerJob {

    @Override
    protected String getPensiTaskName() {
        return "downloader";
    }

    @Value("${app.jobs.pensi-downloader.cron:-}")
    protected String cronString;

    @Scheduled(cron = "${app.jobs.pensi-downloader.cron:-}")
    public void run() {
        run(() -> pensiManager.download(getPensiJobStatus(), false));
    }

    @PostConstruct
    public void runPostConstruct() {
        if (getJobEnabled(cronString)) {
            //run();
        }
    }

}
