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
public class PensiSyncronizerJob extends ServerJob {

    @Override
    public void log(String s) {
        super.log(UtilDate.getStringFullDate(new Date()) + " - " + s);
        pensiMainJob.log("[syncronizer] " + s);
    }

    private PensiMainJob pensiMainJob;
    private PensiManager pensiManager;

    public PensiSyncronizerJob(final PensiManager pensiManager, PensiMainJob pensiMainJob) {
        this.pensiManager = pensiManager;
        this.pensiMainJob = pensiMainJob;
    }

    @Value("${app.jobs.pensi-syncronizer.cron:-}")
    protected String cronString;

    @Scheduled(cron = "${app.jobs.pensi-syncronizer.cron:-}")
    public void run() {
        if (!isRunning()) {
            try {
                setRunning(true);
                log("PENSI syncronizer task started");
                pensiManager.sync(new PensiJobStatus() {
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
                log("PENSI syncronizer task finished");
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
