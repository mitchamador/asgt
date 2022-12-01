package gbas.gtbch.jobs.schedule;

import gbas.gtbch.jobs.annotations.JobAlias;
import gbas.gtbch.jobs.impl.PensiServerJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@JobAlias("pensimailer")
public class PensiMailerJob extends PensiServerJob {

    @Override
    protected String getPensiTaskName() {
        return "mailer";
    }

    @Scheduled(cron = "${app.jobs.pensi-mailer.cron:-}")
    public void run() {
        runTask(() -> pensiManager.mailer(getPensiJobStatus()));
    }

}
