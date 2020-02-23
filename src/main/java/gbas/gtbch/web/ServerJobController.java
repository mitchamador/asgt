package gbas.gtbch.web;

import gbas.gtbch.model.ServerJobResponse;
import gbas.gtbch.schedule.NbrbCurrencyDownloaderJob;
import gbas.gtbch.schedule.PensiDownloaderJob;
import gbas.gtbch.schedule.PensiMailerJob;
import gbas.gtbch.util.MQJob;
import gbas.gtbch.util.PensiMainJob;
import gbas.gtbch.util.ServerJob;
import gbas.gtbch.util.Syncronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class ServerJobController {

    private final static Logger logger = LoggerFactory.getLogger(ServerJobController.class.getName());

    private Syncronizer syncronizer;

    @Autowired
    public void setSyncronizer(Syncronizer syncronizer) {
        this.syncronizer = syncronizer;
    }

    private NbrbCurrencyDownloaderJob nbrbCurrencyDownloaderJob;

    @Autowired
    public void setNbrbCurrencyDownloaderJob(NbrbCurrencyDownloaderJob nbrbCurrencyDownloaderJob) {
        this.nbrbCurrencyDownloaderJob = nbrbCurrencyDownloaderJob;
    }

    private PensiDownloaderJob pensiDownloaderJob;

    @Autowired
    private void setPensiDownloaderJob(PensiDownloaderJob pensiDownloaderJob) {
        this.pensiDownloaderJob = pensiDownloaderJob;
    }

    private PensiMailerJob pensiMailerJob;

    @Autowired
    private void setPensiMailerJob(PensiMailerJob pensiMailerJob) {
        this.pensiMailerJob = pensiMailerJob;
    }

    private PensiMainJob pensiMainJob;

    @Autowired
    public void setPensiMainJob(PensiMainJob pensiMainJob) {
        this.pensiMainJob = pensiMainJob;
    }

    private MQJob mqJob;

    @Autowired
    public void setMQJob(MQJob mqJob) {
        this.mqJob = mqJob;
    }

    private ServerJob getServerJob(String job) {
        ServerJob serverJob = null;

        switch (job) {
            case "syncronizer":
                serverJob = syncronizer;
                break;
            case "nbrbdownloader":
                serverJob = nbrbCurrencyDownloaderJob;
                break;
            case "pensidownloader":
                serverJob = pensiDownloaderJob;
                break;
            case "pensimailer":
                serverJob = pensiMailerJob;
                break;
            case "pensisyncronizer":
                //serverJob = nbrbCurrencyDownloadJob;
                break;
            case "pensimain":
                serverJob = pensiMainJob;
                break;
            case "mqlogger":
                serverJob = mqJob;
                break;
        }
        return serverJob;
    }

    @RequestMapping(value = "api/jobstart", method = RequestMethod.GET)
    public ServerJobResponse syncStart(@RequestParam("job") String job) {

        ServerJob serverJob = getServerJob(job);

        if (serverJob != null) {
            ServerJobResponse response = new ServerJobResponse();

            if (!serverJob.isRunning()) {
                try {
                    response.setRunning(true);
                    serverJob.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                response.setRunning(false);
            }

            return response;
        } else {
            return ServerJobResponse.undefinedJob();
        }
    }

    @Value("${app.defferedtimeout:60}")
    int defferedTimeout;

    @RequestMapping(value = "api/jobstatus", method = RequestMethod.GET)
    public DeferredResult<ServerJobResponse> syncStatus(@RequestParam("job") String job, @RequestParam("jobStep") Long timeUpdate) {

        DeferredResult<ServerJobResponse> deferredResult = new DeferredResult<>(defferedTimeout * 1000L);

        ServerJob serverJob = getServerJob(job);
        if (serverJob == null) {
            deferredResult.setResult(ServerJobResponse.undefinedJob());
        } else {
            serverJob.addDeferredResult(deferredResult, timeUpdate);
        }

        return deferredResult;
    }

}
