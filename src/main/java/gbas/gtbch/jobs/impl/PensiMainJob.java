package gbas.gtbch.jobs.impl;

import gbas.gtbch.jobs.ServerJob;
import org.springframework.stereotype.Component;

@Component
public class PensiMainJob extends ServerJob {

    @Override
    public String getJobName() {
        return null;
    }

    @Override
    public void run() {
    }
}
