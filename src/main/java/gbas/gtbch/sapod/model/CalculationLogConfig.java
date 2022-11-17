package gbas.gtbch.sapod.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration values for CalculationLog delete job
 */
@Component
public class CalculationLogConfig {

    /**
     * default value of batch size for calculation log's delete job
     */
    private static final int DEFAULT_CALCULATION_LOG_BATCH_SIZE = 2500;

    /**
     * default value keep days for NAKL type of documents for calculation log's delete job
     */
    private static final int DEFAULT_CALCULATION_LOG_KEEP_DAYS_NAKL = 7;

    /**
     * default value of keep days for other type of documents for calculation log's delete job
     */
    private static final int DEFAULT_CALCULATION_LOG_KEEP_DAYS_OTHER = 60;

    /**
     * default value for max duration of calculation log's delete job (in minutes)
     */
    private final static int DEFAULT_CALCULATION_LOG_JOB_DURATION = 15;

    @Value("${app.jobs.calclog-remover.batch-size:" + DEFAULT_CALCULATION_LOG_BATCH_SIZE + "}")
    private int deleteCalculationLogBatchSize;

    @Value("${app.jobs.calclog-remover.keep-days.other:" + DEFAULT_CALCULATION_LOG_KEEP_DAYS_OTHER + "}")
    private int keepCalculationLogDaysOther;

    @Value("${app.jobs.calclog-remover.keep-days.nakl:" + DEFAULT_CALCULATION_LOG_KEEP_DAYS_NAKL + "}")
    private int keepCalculationLogDaysNakl;

    @Value("${app.jobs.calclog-remover.duration:" + DEFAULT_CALCULATION_LOG_JOB_DURATION + "}")
    private int jobDuration;

    public int getDeleteCalculationLogBatchSize() {
        return deleteCalculationLogBatchSize;
    }

    public void setDeleteCalculationLogBatchSize(int deleteCalculationLogBatchSize) {
        this.deleteCalculationLogBatchSize = deleteCalculationLogBatchSize;
    }

    public int getKeepCalculationLogDaysOther() {
        return keepCalculationLogDaysOther;
    }

    public void setKeepCalculationLogDaysOther(int keepCalculationLogDaysOther) {
        this.keepCalculationLogDaysOther = keepCalculationLogDaysOther;
    }

    public int getKeepCalculationLogDaysNakl() {
        return keepCalculationLogDaysNakl;
    }

    public void setKeepCalculationLogDaysNakl(int keepCalculationLogDaysNakl) {
        this.keepCalculationLogDaysNakl = keepCalculationLogDaysNakl;
    }

    public int getJobDuration() {
        return jobDuration;
    }

    public void setJobDuration(int jobDuration) {
        this.jobDuration = jobDuration;
    }
}
