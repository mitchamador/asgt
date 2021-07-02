package gbas.gtbch.util;

import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.tvk.nsi.cash.Func;
import gbas.tvk.util.synchronizator.Syncronizator;
import gbas.tvk.util.synchronizator.entity.InsertMode;
import gbas.tvk.util.synchronizator.synchroObjects.SynDist;
import gbas.tvk.util.synchronizator.synchroObjects.SynStyck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Component
public class Syncronizer extends ServerJob {

    public static final String SYNCRONIZER_UPLOAD_PARAM = "syncronizer_data";
    private static final int SEMAPHORE_MAX = 64;

    private Logger logger = LoggerFactory.getLogger(Syncronizer.class);

    private byte[] bytes;

    private String filename;

    @Autowired
    private TpImportDateService tpImportDateService;

    @Autowired
    @Qualifier("sapodDataSource")
    private DataSource sapodDataSource;

    @Value("${app.jobs.syncronizer.fullmerge:false}")
    private boolean fullInsertMerge;

    @Value("${app.jobs.syncronizer.cacheable:false}")
    private boolean cacheable;

    @Value("${app.jobs.syncronizer.insertmode:normal}")
    private String insertMode;

    @Bean
    public Syncronizer init() {
        return new Syncronizer();
    }

    @Override
    public String getJobName() {
        return "Syncronizer";
    }

    @Override
    public void log(String s) {
        super.log(s);
        logger.info(s);
    }

    private final Semaphore semaphore;

    public Syncronizer() {
        semaphore = new Semaphore(SEMAPHORE_MAX, true);
    }

    @Async
    public void run() {

        synchronized (this) {
            if (isRunning()) {
                return;
            } else {
                setRunning(true);
            }
        }

        boolean syncronizerAcquired = false;

        try (Connection con = sapodDataSource.getConnection()) {
            // waiting for calculation threads to finish and block new calculation threads until syncronization done
            if (!semaphore.tryAcquire(SEMAPHORE_MAX, 0, TimeUnit.SECONDS)) {
                log("Waiting for completion of calculation jobs...");
                semaphore.acquire(SEMAPHORE_MAX);
            }
            syncronizerAcquired = true;

            clearJob();

            Syncronizator s = new Syncronizator(con, bytes) {
                @Override
                public void process(String[] logs) {
                    super.process(logs);

                    if (!logs[0].isEmpty()) {
                        for (String s : logs[0].split("\n")) {
                            log(s.isEmpty() ? " " : s);
                        }
                    }

                    try {
                        setProgress(Integer.parseInt(logs[3]));
                    } catch (NumberFormatException ignored) {
                    }
                }

                @Override
                public void finish() {
                    super.finish();

                    setProgress(100);

                    if (filename != null) {
                        String dateStr = (filename.substring(12, 14) + "") + '.' + (filename.substring(9, 11) + "") + '.' +
                                (filename.substring(4, 8) + "") + ' ' + (filename.substring(15, 17) + "") + ':' + (filename.substring(18, 20) + "");
                        Date dateCreate = UtilDate8.getDate(dateStr);
                        if (dateCreate != null) {
                            TpImportDate tpImportDate = new TpImportDate();
                            tpImportDate.setDateCreate(dateCreate);
                            tpImportDate.setDateImport(new Date());
                            tpImportDateService.save(tpImportDate);
                        }
                    }
                }
            };
            s.setFullInsertMerge(fullInsertMerge);
            s.setCacheable(cacheable);
            try {
                s.setInsertMode(InsertMode.valueOf(insertMode.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }

            s.setExcludeGroups(SynStyck.class, SynDist.class/*, SynMatherial.class, SynSpec.class*/);
            s.setExcludeNsiTables("tvk_stan", "tvk_algng", "tvk_etsng", "tvk_gng_etsng", "tvk_algng_etsng", "tvk_algng_dan", "tvk_algng_dang");

            s.run(true);

            String errors = s.getErr();
            if (!Func.isEmpty(s.getErr())) {
                System.err.println(errors);
            }

        } catch (InterruptedException e) {
            logger.error("Suncronizer.acquire(MAX) interrupted exception");
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Suncronizer exception: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            if (syncronizerAcquired) {
                // unblock calculation threads
                semaphore.release(SEMAPHORE_MAX);
            }
            setRunning(false);
        }
    }

    public void setData(String filename, byte[] bytes) {
        this.filename = filename;
        this.bytes = bytes;
    }

    /**
     * acquires one permit from syncronizator's semaphore
     * @return true, if acuqire is done, false, if timeout or interrupt
     */
    public boolean acquire() throws InterruptedException {
        return semaphore.tryAcquire(10, TimeUnit.MINUTES); // waiting 10 min for syncronization job
    }

    /**
     * releases one permit from syncronizator's semaphore
     */
    public void release() {
        semaphore.release();
    }

}
