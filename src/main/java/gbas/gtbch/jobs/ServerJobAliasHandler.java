package gbas.gtbch.jobs;

import gbas.gtbch.jobs.annotations.ServerJob;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * class for matching all {@link AbstractServerJob}s annotated with {@link ServerJob}
 */
@Component
public class ServerJobAliasHandler {

    private final Map<String, AbstractServerJob> serverJobMap = new HashMap<>();

    public AbstractServerJob getServerJob(String serverJobName) {
        return serverJobMap.get(serverJobName);
    }

    public void addServerJob(String serverJobName, AbstractServerJob serverJob) {
        serverJobMap.put(serverJobName, serverJob);
    }
}
