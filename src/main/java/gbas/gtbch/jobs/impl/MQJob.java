package gbas.gtbch.jobs.impl;

import gbas.gtbch.jobs.AbstractServerJob;
import gbas.gtbch.jobs.annotations.ServerJob;
import org.springframework.stereotype.Component;

/**
 * {@link AbstractServerJob} for mq logs
 */
@Component
@ServerJob(alias = "mqlogger", name = "MQ")
public class MQJob extends AbstractServerJob {

    @Override
    public void run() {
    }
}
