package gbas.gtbch.jobs;

import gbas.gtbch.jobs.annotations.JobAlias;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * class for matching all {@link ServerJob}s annotated with {@link JobAlias}
 */
@Component
public class JobAliasMatcher {

    private final Map<String, ServerJob> serverJobMap = new HashMap<>();

    public ServerJob getServerJob(String serverJobName) {
        return serverJobMap.get(serverJobName);
    }

    public void addServerJob(String serverJobName, ServerJob serverJob) {
        serverJobMap.put(serverJobName, serverJob);
    }
}
