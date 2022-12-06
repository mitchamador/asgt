package gbas.gtbch.jobs.impl;

import gbas.gtbch.jobs.AbstractServerJob;
import gbas.gtbch.jobs.annotations.ServerJob;
import org.springframework.stereotype.Component;

/**
 * {@link AbstractServerJob} for PENSI's tasks logs
 */
@Component
@ServerJob(alias = "pensimain", name = "PENSI main job")
public class PensiMainJob extends AbstractServerJob {

    @Override
    public void run() {
    }
}
