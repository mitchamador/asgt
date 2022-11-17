package gbas.gtbch.jobs;

/**
 * time limited server job
 */
public interface TimeLimitedTask {
    /**
     * prepare job, run once at start of job
     */
    void prepare();

    /**
     * job step
     * @return 0, if job is finished
     * @throws Exception
     */
    int step() throws Exception;

    /**
     * finish job, run once after all steps is done
     * @param total
     */
    void finish(int total);
}
