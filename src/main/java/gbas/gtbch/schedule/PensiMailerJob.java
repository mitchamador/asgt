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
public class PensiMailerJob extends ServerJob {

    @Override
    public void log(String s) {
        super.log(UtilDate.getStringFullDate(new Date()) + " - " + s);
        pensiMainJob.log("[mailer] " + s);
    }

    private final PensiMainJob pensiMainJob;
    private PensiManager pensiManager;

    public PensiMailerJob(final PensiManager pensiManager, PensiMainJob pensiMainJob) {
        this.pensiManager = pensiManager;
        this.pensiMainJob = pensiMainJob;
    }

    @Value("${app.jobs.pensi-mailer.cron:-}")
    private String cronString;

    @Scheduled(cron = "${app.jobs.pensi-mailer.cron:-}")
    public void run() {
        if (!isRunning()) {
            try {
                setRunning(true);
                log("PENSI mailer task started");

                pensiManager.mailer(new PensiJobStatus() {
                    @Override
                    public void updateProgress(int i) {
                        setProgress(i);
                    }

                    @Override
                    public void updateLog(String s) {
                        log(s);
                    }
                });

            } catch (Error | Exception e) {
                log(e.getMessage());
                //e.printStackTrace();
            } finally {
                log("PENSI mailer task finished");
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
