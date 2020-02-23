package gbas.gtbch.util;

import gbas.tvk.util.UtilDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PensiMainJob extends ServerJob {

    private final static Logger logger = LoggerFactory.getLogger(PensiMainJob.class.getName());

    @Override
    public void log(String s) {
        super.log(UtilDate.getStringFullDate(new Date()) + " " + s);
        logger.info(s);
    }

    @Override
    public void run() {

    }
}
