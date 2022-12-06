package gbas.gtbch.jobs;

import gbas.gtbch.jobs.impl.PensiMainJob;
import gbas.gtbch.mailer.MailService;
import gbas.gtbch.util.SystemInfo;
import gbas.tvk.interaction.pensi.PensiManager;
import gbas.tvk.interaction.pensi.jobs.PensiJobStatus;
import org.springframework.stereotype.Component;

import static gbas.gtbch.mailer.MailerConstants.MAILER_CONFIG_EVENT_ERRORS;

@Component
public class PensiCommonJob {

    protected final PensiManager pensiManager;
    private final PensiMainJob pensiMainJob;
    private final MailService mailService;
    private final SystemInfo systemInfo;

    public PensiCommonJob(PensiManager pensiManager, PensiMainJob pensiMainJob, MailService mailService, SystemInfo systemInfo) {
        this.pensiManager = pensiManager;
        this.pensiMainJob = pensiMainJob;
        this.mailService = mailService;
        this.systemInfo = systemInfo;
    }

    public PensiManager getPensiManager() {
        return pensiManager;
    }

    public PensiJobStatus createPensiJobStatus(final AbstractServerJob abstractServerJob) {
        return new PensiJobStatus() {
            @Override
            public void updateProgress(int i) {
                abstractServerJob.setProgress(i);
            }

            @Override
            public void updateLog(String s) {
                // logs for PensiMainJob
                pensiMainJob.log("[" + abstractServerJob.getJobName().replace("PENSI ", "") + "] " + s, AbstractServerJob.LOG_EVENT_DATE);
                // logs for current job
                abstractServerJob.log(s);
            }

            @Override
            public void notifyError(String error) {
                if (mailService.getMailProperties().isEventEnabled(MAILER_CONFIG_EVENT_ERRORS)) {
                    String htmlMessage = "<html>" +
                            "<body>" +
                            "<p><b>PENSI download error on " + systemInfo.getHost() + "</b></p>" +
                            "<span style=\"white-space: pre-line\">" + error + "</span>" +
                            "</body>" +
                            "</html>";
                    mailService.sendHtmlMessage(null, "pensi download error", htmlMessage);
                }
            }
        };
    }

}
