package gbas.gtbch.util;

import gbas.gtbch.mailer.MailService;
import gbas.tvk.interaction.pensi.PensiManager;
import gbas.tvk.interaction.pensi.jobs.PensiJobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
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

    private MailService mailService;

    @Autowired
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    private SystemInfo systemInfo;

    @Autowired
    public void setSystemInfo(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
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
        String dateString = UtilDate8.getStringFullDate(new Date());
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

            @Override
            public void notifyError(String error) {
                String htmlMessage = "<html>" +
                        "<body>" +
                        "<p><b>PENSI download error on " + systemInfo.getHost() + "</b></p>" +
                        "<span style=\"white-space: pre-line\">" + error + "</span>" +
                        "</body>" +
                        "</html>";
                mailService.sendHtmlMessage(null, "pensi download error", htmlMessage);
            }
        };
    }

}
