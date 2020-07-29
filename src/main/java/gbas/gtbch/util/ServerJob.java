package gbas.gtbch.util;

import gbas.gtbch.model.ServerJobResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Класс, описывающий асинхронную задачу на стороне сервера
 */
public abstract class ServerJob {

    public interface Runnable {
        void run() throws Exception;
    }

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private AtomicBoolean running = new AtomicBoolean();

    /**
     *
     */
    private final List<String> logs = new ArrayList<>();

    /**
     *
     */
    private AtomicInteger progress = new AtomicInteger();

    /**
     *
     */
    private AtomicLong jobStep = new AtomicLong();

    /**
     *
     * @return
     */
    public abstract String getJobName();

    /**
     * @param s
     */
    public void log(String s) {
        if (s != null && !s.isEmpty()) {
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

    protected void setRunning(boolean running) {
        logger.trace("set job running to {}", running);
        this.running.set(running);
        updateJob();
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

    protected void run(Runnable task) {
        if (!isRunning()) {
            try {
                setRunning(true);
                logger.info(getJobName() + " task started", false);

                task.run();

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
     * @param cronString
     * @return
     */
    protected boolean getJobEnabled(String cronString) {
        return !"-".equals(cronString);
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

    class TimedDeferredResult<T> {
        long time;

        long getTime() {
            return time;
        }

        DeferredResult<T> getDeferredResult() {
            return deferredResult;
        }

        DeferredResult<T> deferredResult;

        TimedDeferredResult(long time, DeferredResult<T> deferredResult) {
            this.time = time;
            this.deferredResult = deferredResult;
        }
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
}
