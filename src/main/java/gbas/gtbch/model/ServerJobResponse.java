package gbas.gtbch.model;

public class ServerJobResponse extends ServerResponse {

    private static final String UNDEFINED_JOB = "Undefined job";

    /**
     *
     */
    private boolean running;

    /**
     *
     */
    private int progress;

    /**
     *
     */
    private long jobStep;

    /**
     *
     */
    private boolean timeout;

    /**
     *
     * @return
     */
    public boolean isRunning() {
        return running;
    }

    /**
     *
     * @param running
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     *
     * @return
     */
    public int getProgress() {
        return progress;
    }

    /**
     *
     * @param progress
     */
    public void setProgress(int progress) {
        this.progress = progress;
    }

    /**
     *
     * @return
     */
    public long getJobStep() {
        return jobStep;
    }

    /**
     *
     * @param jobStep
     */
    public void setJobStep(long jobStep) {
        this.jobStep = jobStep;
    }

    /**
     *
     * @return
     */
    public boolean isTimeout() {
        return timeout;
    }

    /**
     *
     * @param timeout
     */
    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    /**
     *
     * @return
     */
    public static ServerJobResponse undefinedJob() {
        ServerJobResponse job = new ServerJobResponse();
        job.setMessage(UNDEFINED_JOB);
        job.setRunning(false);
        job.setProgress(0);
        return job;
    }


}
