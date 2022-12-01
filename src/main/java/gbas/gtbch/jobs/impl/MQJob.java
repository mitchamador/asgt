package gbas.gtbch.jobs.impl;

import gbas.gtbch.jobs.ServerJob;
import gbas.gtbch.jobs.annotations.JobAlias;
import gbas.gtbch.util.UtilDate8;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@JobAlias("mqlogger")
public class MQJob extends ServerJob {

    @Override
    public String getJobName() {
        return "MQ";
    }

    @Override
    public void log(String s) {
        super.log(UtilDate8.getStringFullDate(new Date()) + " " + s);
    }

    @Override
    public void run() {
    }
}
