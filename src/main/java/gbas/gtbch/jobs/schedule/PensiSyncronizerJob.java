package gbas.gtbch.jobs.schedule;

import gbas.gtbch.jobs.impl.PensiServerJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PensiSyncronizerJob extends PensiServerJob {

    @Override
    protected String getPensiTaskName() {
        return "syncronizer";
    }

    @Scheduled(cron = "${app.jobs.pensi-syncronizer.cron:-}")
    public void run() {
        runTask(() -> pensiManager.sync(getPensiJobStatus()));
    }

}
