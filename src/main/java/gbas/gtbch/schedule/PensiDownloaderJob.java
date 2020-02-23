package gbas.gtbch.schedule;

import gbas.gtbch.util.PensiMainJob;
import gbas.gtbch.util.ServerJob;
import gbas.tvk.interaction.pensi.PensiManager;
import gbas.tvk.interaction.pensi.jobs.PensiJobStatus;
import gbas.tvk.util.UtilDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
public class PensiDownloaderJob extends ServerJob {

    @Override
    public void log(String s) {
        super.log(UtilDate.getStringFullDate(new Date()) + " - " + s);
        pensiMainJob.log("[downloader] " + s);
    }

    private PensiMainJob pensiMainJob;
    private PensiManager pensiManager;

    public PensiDownloaderJob(final PensiManager pensiManager, PensiMainJob pensiMainJob) {
        this.pensiManager = pensiManager;
        this.pensiMainJob = pensiMainJob;
    }

    @Value("${app.jobs.pensi-downloader.cron:-}")
    protected String cronString;

    @Scheduled(cron = "${app.jobs.pensi-downloader.cron:-}")
    public void run() {
        if (!isRunning()) {
            try {
                setRunning(true);
                log("PENSI downloader task started");

                pensiManager.download(new PensiJobStatus() {
                    @Override
                    public void updateProgress(int i) {
                        setProgress(i);
                    }

                    @Override
                    public void updateLog(String s) {
                        log(s);
                    }
                }, false);

            } catch (Error | Exception e) {
                log(e.getMessage());
                //e.printStackTrace();
            } finally {
                log("PENSI downloader task finished");
                setRunning(false);
            }
        }
    }


    @PostConstruct
    public void runPostConstruct() {
        if (getJobEnabled(cronString)) {
            //run();
        }
    }

}
