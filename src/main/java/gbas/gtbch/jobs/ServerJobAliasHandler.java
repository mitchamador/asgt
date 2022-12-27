package gbas.gtbch.jobs;

import gbas.gtbch.jobs.annotations.ServerJob;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * class for matching all {@link AbstractServerJob}s annotated with {@link ServerJob}
 */
@Component
public class ServerJobAliasHandler {

    private final Map<String, AbstractServerJob> serverJobMap = new HashMap<>();

    private final ApplicationContext context;

    public ServerJobAliasHandler(ApplicationContext context) {
        this.context = context;
    }

    public AbstractServerJob getServerJob(String serverJobName) {
        return context.getBean(serverJobMap.get(serverJobName).getClass());
    }

    public void addServerJob(String serverJobName, AbstractServerJob serverJob) {
        serverJobMap.put(serverJobName, serverJob);
    }
}
