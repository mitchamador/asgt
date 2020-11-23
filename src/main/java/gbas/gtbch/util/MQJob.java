package gbas.gtbch.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MQJob extends ServerJob {

    private final static Logger logger = LoggerFactory.getLogger(MQJob.class.getName());

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
