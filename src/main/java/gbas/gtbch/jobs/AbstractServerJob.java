package gbas.gtbch.jobs;

import gbas.gtbch.jobs.annotations.RunAtStartup;
import gbas.gtbch.model.ServerJobResponse;
import gbas.gtbch.util.ServerLog;
import gbas.gtbch.util.UtilDate8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringValueResolver;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Класс, описывающий асинхронную задачу на стороне сервера
 *
 * @see RunAtStartup
 * @see gbas.gtbch.jobs.annotations.ServerJob
 * @see SimpleTask
 * @see TimeLimitedTask
 */
public abstract class AbstractServerJob implements EmbeddedValueResolverAware {

    private StringValueResolver embeddedValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Async
    @EventListener(ApplicationStartedEvent.class)
    public void runAtStartup() {
        RunAtStartup runAtStartupAnnotation = AnnotationUtils.findAnnotation(this.getClass(), RunAtStartup.class);
        if (runAtStartupAnnotation != null && (runAtStartupAnnotation.force() || getJobEnabled())) {
            run();
        }
    }

    private ServerJobAliasHandler serverJobAliasHandler;

    @Autowired
    private void setJobAliasMatcher(ServerJobAliasHandler serverJobAliasHandler) {
        this.serverJobAliasHandler = serverJobAliasHandler;
    }

    @PostConstruct
    public void handleServerJobAnnotation() {
        gbas.gtbch.jobs.annotations.ServerJob serverJob = AnnotationUtils.findAnnotation(this.getClass(), gbas.gtbch.jobs.annotations.ServerJob.class);
        if (serverJob != null) {
            if (!serverJob.alias().isEmpty()) {
                jobAlias = serverJob.alias();
                serverJobAliasHandler.addServerJob(jobAlias, this);
            }
            if (!serverJob.name().isEmpty()) {
                jobName = serverJob.name();
            }
        }
    }

    private String jobName;

    public String getJobName() {
        return jobName;
    }

    private String jobAlias;

    public String getJobAlias() {
        return jobAlias;
    }

    /**
     * check, if job is scheduled
     * @return
     */
    protected boolean getJobEnabled() {
        return !"-".equals(getCronString());
    }

    /**
     * get cron screen for {@link Scheduled} annotation
     * @return
     */
    protected String getCronString() {
        try {
            Scheduled annotation = AnnotationUtils.findAnnotation(this.getClass().getMethod("run"), Scheduled.class);
            if (annotation != null) {
                return embeddedValueResolver.resolveStringValue(annotation.cron());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-";
    }

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private AtomicBoolean running = new AtomicBoolean();

    /**
     * max log list size
     */
    private final static int MAX_LOG_SIZE = 512;
    /**
     *
     */
    private final ServerLog logs = new ServerLog(MAX_LOG_SIZE);

    /**
     *
     */
    private AtomicInteger progress = new AtomicInteger();

    /**
     *
     */
    private AtomicLong jobStep = new AtomicLong();

    /**
     * no additional log
     */
    public final static int LOG_EVENT_NONE = 0;
    /**
     * add date to {@link AbstractServerJob}'s log
     */
    public final static int LOG_EVENT_DATE = 1 << 0;
    /**
     * print info level to system log
     */
    public final static int LOG_LOGGER_INFO = 1 << 1;

    /**
     * default log
     * @param s
     */
    public void log(String s) {
        log(s, LOG_EVENT_DATE | LOG_LOGGER_INFO);
    }

    /**
     * log
     * @param s
     * @param logSettings
     */
    public void log(String s, int logSettings) {
        if (s != null && !s.isEmpty()) {
            if ((logSettings & LOG_LOGGER_INFO) != 0) {
                logger.info(s);
            }
            if ((logSettings & LOG_EVENT_DATE) != 0) {
                s = UtilDate8.getStringFullDate(new Date()) + " " + s;
            }
            synchronized (logs) {
                logs.add(s);
            }
            updateJob();
        }
    }

    /**
     * @param progress
     */
    public void setProgress(int progress) {
        if (this.progress.getAndSet(progress) != progress) {
            updateJob();
        }
    }

    /**
     *
     */
    public synchronized void clearJob() {
        synchronized (logs) {
            logs.clear();
        }
        progress.set(0);
        updateJob();
    }

    /**
     * @return
     */
    private String getLogs() {
        synchronized (logs) {
            return String.join("\n", logs);
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    private int getProgress() {
        return progress.get();
    }

    protected boolean setRunning(boolean running) {
        if (running) {
            if (this.running.compareAndSet(false, true)) {
                logger.trace("set job running to {}", true);
            } else {
                logger.trace("cannot set job running to {}", true);
                running = false;
            }
        } else {
            this.running.set(false);
            logger.trace("set job running to {}", false);
        }
        updateJob();
        return running;
    }

    private long getJobStep() {
        return jobStep.longValue();
    }

    private void setJobStep() {
        logger.trace("set job step to {}", jobStep);
        jobStep.incrementAndGet();
    }

    /**
     *
     */
    private void updateJob() {
        setJobStep();
        updateDeferredResults();
    }

    /**
     *
     */
    @Async
    public abstract void run();

    /**
     * run {@link SimpleTask}
     * @param simpleTask
     */
    protected void runTask(final SimpleTask simpleTask) {
        if (setRunning(true)) {
            try {
                logger.info(getJobName() + " task started", false);

                // run task
                simpleTask.run();

            } catch (Error | Exception e) {
                log(e.getMessage());
                //e.printStackTrace();
            } finally {
                logger.info(getJobName() + " task finished", false);
                setRunning(false);
            }
        }
    }

    /**
     * run {@link TimeLimitedTask}
     * @param timeLimitedTask
     * @param duration
     */
    protected void runTask(TimeLimitedTask timeLimitedTask, long duration) {
        if (setRunning(true)) {
            try {
                logger.info(getJobName() + " task started", false);

                // prepare task
                timeLimitedTask.prepare();

                long startTime = System.currentTimeMillis();

                // run task's step
                int rows = 0;
                int stepResult = -1;
                while (stepResult != 0 && (System.currentTimeMillis() - startTime) < duration) {
                    try {
                        stepResult = timeLimitedTask.step();
                    } catch (Exception e) {
                        logger.error(getJobName() + " task step error", e);
                        stepResult = 0;
                    }
                    rows += stepResult;
                }

                // finish task
                timeLimitedTask.finish(rows);

            } catch (Error | Exception e) {
                log(e.getMessage());
                //e.printStackTrace();
            } finally {
                logger.info(getJobName() + " task finished", false);
                setRunning(false);
            }
        }
    }

    /**
     * @param timeout
     * @return
     */
    private ServerJobResponse getServerJobResponse(boolean timeout) {
        ServerJobResponse response = new ServerJobResponse();
        if (!timeout) {
            response.setMessage(getLogs());
            response.setProgress(getProgress());
        }
        response.setRunning(isRunning());
        response.setJobStep(getJobStep());
        response.setTimeout(timeout);
        return response;
    }

    private ConcurrentLinkedQueue<TimedDeferredResult<ServerJobResponse>> deferredResultQueue = new ConcurrentLinkedQueue<>();

    public void addDeferredResult(DeferredResult<ServerJobResponse> deferredResult, Long clientJobStep) {

        logger.trace("deffered result started, client job step: {}, job step: {}", clientJobStep, getJobStep());

        TimedDeferredResult<ServerJobResponse> timedDeferredResult = new TimedDeferredResult<>(System.currentTimeMillis(), deferredResult);

        if (clientJobStep == null) {
            deferredResult.setResult(getServerJobResponse(false));
        } else if (clientJobStep < getJobStep()) {

            logger.trace("set delayed deferred result with timeout {} ms", MIN_POLLING_INTERVAL);

            setDelayedDeferredResult(MIN_POLLING_INTERVAL, deferredResult);
        } else {
            deferredResult.onTimeout(() -> {
                logger.trace("deffered result timeout");
                deferredResult.setResult(getServerJobResponse(true));
                deferredResultQueue.remove(timedDeferredResult);
            });

            deferredResult.onCompletion(() -> {
                if (!deferredResult.hasResult()) {
                    logger.trace("deffered result has no result on complete");
                }
                logger.trace("deferred result completed");
            });

            deferredResultQueue.add(timedDeferredResult);
        }
    }

    private final static int MIN_POLLING_INTERVAL = 250;

    private void updateDeferredResults() {
        TimedDeferredResult<ServerJobResponse> timedDeferredResult;

        while ((timedDeferredResult = deferredResultQueue.poll()) != null) {
            if (!timedDeferredResult.getDeferredResult().hasResult()) {
                long interval = System.currentTimeMillis() - timedDeferredResult.getTime();
                if (interval > MIN_POLLING_INTERVAL) {
                    logger.trace("set deferred result on job status update");
                    timedDeferredResult.getDeferredResult().setResult(getServerJobResponse(false));
                } else {
                    logger.trace("set delayed deferred result on job status update with timeout {} ms", MIN_POLLING_INTERVAL - interval);
                    setDelayedDeferredResult(MIN_POLLING_INTERVAL - interval, timedDeferredResult.getDeferredResult());
                }
            }
        }

    }

    private void setDelayedDeferredResult(long delay, DeferredResult<ServerJobResponse> deferredResult) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            deferredResult.setResult(getServerJobResponse(false));
        });
    }

    public final static String SERVERJOB_MVC_ALIAS = "jobAlias";
    public final static String SERVERJOB_MVC_PAGE_HEADER = "pageHeader";
    public final static String SERVERJOB_MVC_LOG_TITLE = "logTitle";
    public final static String SERVERJOB_MVC_START_BUTTON_TEXT = "buttonText";

    /**
     * get default data for MVC
     * @return
     */
    public Map<String, Object> getWebPageData() {
        Map<String, Object> map = new HashMap<>();
        map.put(SERVERJOB_MVC_ALIAS, getJobAlias());
        map.put(SERVERJOB_MVC_PAGE_HEADER, getJobName());
        map.put(SERVERJOB_MVC_LOG_TITLE, "Лог " + getJobName());
        return map;
    }
}
