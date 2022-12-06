package gbas.gtbch.jobs.schedule;

import gbas.gtbch.jobs.AbstractServerJob;
import gbas.gtbch.jobs.TimeLimitedTask;
import gbas.gtbch.jobs.annotations.ServerJob;
import gbas.gtbch.sapod.model.CalculationLogConfig;
import gbas.gtbch.sapod.service.CalculationLogService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Component
@ServerJob(alias = "calclogremover", name = "Calculation log remover")
public class CalculationLogDeleteJob extends AbstractServerJob {

    private final CalculationLogService calculationLogService;

    private final CalculationLogConfig calculationLogConfig;

    public CalculationLogDeleteJob(CalculationLogService calculationLogService, CalculationLogConfig calculationLogConfig) {
        this.calculationLogService = calculationLogService;
        this.calculationLogConfig = calculationLogConfig;
    }

    private LocalDate keepDateOther;

    private LocalDate keepDateNakl;

    @Scheduled(cron = "${app.jobs.calclog-remover.cron:-}")
    public void run() {
        runTask(new TimeLimitedTask() {
            @Override
            public void prepare() {
                keepDateOther = LocalDate.now().minusDays(calculationLogConfig.getKeepCalculationLogDaysOther());
                keepDateNakl = LocalDate.now().minusDays(calculationLogConfig.getKeepCalculationLogDaysNakl());
            }

            @Override
            public int step() throws Exception {
                return calculationLogService.deleteRows(keepDateNakl, keepDateOther, calculationLogConfig.getDeleteCalculationLogBatchSize());
            }

            @Override
            public void finish(int total) {
                if (total > 0) {
                    log(String.format("%s: %s rows deleted from calculation log.", getJobName(), total));
                }
            }
        }, TimeUnit.MINUTES.toMillis(calculationLogConfig.getJobDuration()));
    }

}
