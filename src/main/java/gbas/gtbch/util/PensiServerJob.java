package gbas.gtbch.util;

import gbas.tvk.interaction.pensi.PensiManager;
import gbas.tvk.interaction.pensi.jobs.PensiJobStatus;
import gbas.tvk.util.UtilDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class PensiServerJob extends ServerJob {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected PensiManager pensiManager;

    @Autowired
    public void setPensiManager(PensiManager pensiManager) {
        this.pensiManager = pensiManager;
    }

    private PensiMainJob pensiMainJob;

    @Autowired
    public void setPensiMainJob(PensiMainJob pensiMainJob) {
        this.pensiMainJob = pensiMainJob;
    }

    @Override
    public void run() {
    }

    @Override
    public String getJobName() {
        return "PENSI " + getPensiTaskName();
    }

    @Override
    public void log(String s) {
        String dateString = UtilDate.getStringFullDate(new Date());
        //super.log(dateString + " " + s);

        pensiMainJob.log(dateString + " [" + getPensiTaskName() + "] " + s);

        logger.info(s);
    }

    protected String getPensiTaskName() {
        return "main";
    }

    protected PensiJobStatus getPensiJobStatus() {
        return new PensiJobStatus() {
            @Override
            public void updateProgress(int i) {
                setProgress(i);
            }

            @Override
            public void updateLog(String s) {
                log(s);
            }
        };
    }

}
