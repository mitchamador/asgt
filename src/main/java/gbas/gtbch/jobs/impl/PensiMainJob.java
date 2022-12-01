package gbas.gtbch.jobs.impl;

import gbas.gtbch.jobs.ServerJob;
import gbas.gtbch.jobs.annotations.JobAlias;
import org.springframework.stereotype.Component;

@Component
@JobAlias("pensimain")
public class PensiMainJob extends ServerJob {

    @Override
    public String getJobName() {
        return null;
    }

    @Override
    public void run() {
    }
}
