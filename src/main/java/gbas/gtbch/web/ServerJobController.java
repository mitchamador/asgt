package gbas.gtbch.web;

import gbas.gtbch.jobs.JobAliasMatcher;
import gbas.gtbch.jobs.ServerJob;
import gbas.gtbch.model.ServerJobResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping(value = "/api/job")
public class ServerJobController {

    private final static Logger logger = LoggerFactory.getLogger(ServerJobController.class.getName());

    private final JobAliasMatcher jobAliasMatcher;

    public ServerJobController(JobAliasMatcher jobAliasMatcher) {
        this.jobAliasMatcher = jobAliasMatcher;
    }

    @RequestMapping(value = "/start/{job}", method = RequestMethod.GET)
    public ServerJobResponse syncStart(@PathVariable("job") String job) {

        ServerJob serverJob = jobAliasMatcher.getServerJob(job);

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

    @RequestMapping(value = "/status/{job}", method = RequestMethod.GET)
    public DeferredResult<ServerJobResponse> syncStatus(@PathVariable("job") String job, @RequestParam(value = "jobStep", required = false) Long timeUpdate) {

        DeferredResult<ServerJobResponse> deferredResult = new DeferredResult<>(defferedTimeout * 1000L);

        ServerJob serverJob = jobAliasMatcher.getServerJob(job);
        if (serverJob == null) {
            deferredResult.setResult(ServerJobResponse.undefinedJob());
        } else {
            serverJob.addDeferredResult(deferredResult, timeUpdate);
        }

        return deferredResult;
    }

}
