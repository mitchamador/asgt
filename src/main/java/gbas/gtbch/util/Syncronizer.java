package gbas.gtbch.util;

import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.gtbch.util.cache.AppCacheManager;
import gbas.tvk.nsi.cash.Func;
import gbas.tvk.util.synchronizator.Syncronizator;
import gbas.tvk.util.synchronizator.entity.ExcludeTables;
import gbas.tvk.util.synchronizator.entity.InsertMode;
import gbas.tvk.util.synchronizator.entity.SyncFileData;
import gbas.tvk.util.synchronizator.entity.SyncGroupsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;
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

    private TpImportDateService tpImportDateService;

    private DataSource sapodDataSource;

    private final Semaphore semaphore;

    private final AppCacheManager cacheManager;

    public Syncronizer(TpImportDateService tpImportDateService, @Qualifier("sapodDataSource") DataSource sapodDataSource, AppCacheManager cacheManager) {
        this.tpImportDateService = tpImportDateService;
        this.sapodDataSource = sapodDataSource;
        this.cacheManager = cacheManager;
        semaphore = new Semaphore(SEMAPHORE_MAX, true);
    }


    @Value("${app.jobs.syncronizer.fullmerge:false}")
    private boolean fullInsertMerge;

    @Value("${app.jobs.syncronizer.cacheable:false}")
    private boolean cacheable;

    @Value("${app.jobs.syncronizer.insertmode:normal}")
    private String insertMode;

    private InsertMode getInsertMode() {
        try {
            return InsertMode.valueOf(insertMode.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        return InsertMode.NORMAL;
    }

//    @Bean
//    public Syncronizer init() {
//        return new Syncronizer(payTransportationQueryCache);
//    }

    @Override
    public String getJobName() {
        return "Syncronizer";
    }

    @Override
    public void log(String s) {
        super.log(s);
        logger.info(s);
    }

    @Async
    public void run() {

        boolean syncronizerAcquired = false;

        if (setRunning(true)) {
            try (Connection con = sapodDataSource.getConnection()) {
                // waiting for calculation threads to finish and block new calculation threads until syncronization done
                if (!semaphore.tryAcquire(SEMAPHORE_MAX, 0, TimeUnit.SECONDS)) {
                    log("Waiting for completion of calculation jobs...");
                    semaphore.acquire(SEMAPHORE_MAX);
                }
                syncronizerAcquired = true;

                clearJob();

                Syncronizator s = new Syncronizator(con) {
                    @Override
                    public void process(String[] logs) {
                        super.process(logs);
                        Syncronizer.this.process(logs);
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
                s.setInsertMode(getInsertMode());
                s.setExcludeTables(ExcludeTables.GT);

                s.sync(new SyncFileData(null, bytes), SyncGroupsSet.GT, true);

                String errors = s.getErr();
                if (!Func.isEmpty(s.getErr())) {
                    System.err.println(errors);
                }

                cacheManager.clearTpCaches();

            } catch (InterruptedException e) {
                logger.error("Syncronizer.acquire(MAX) interrupted exception");
                e.printStackTrace();
            } catch (Exception e) {
                logger.error("Syncronizer exception: {}", e.getMessage());
                e.printStackTrace();
            } finally {
                if (syncronizerAcquired) {
                    // unblock calculation threads
                    semaphore.release(SEMAPHORE_MAX);
                }
                setRunning(false);
            }
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

    public SyncFileData create(SyncGroupsSet set) throws Exception {

        SyncFileData syncFileData = null;

        if (setRunning(true)) {
            try (Connection connection = sapodDataSource.getConnection()) {

                clearJob();

                Syncronizator s = new Syncronizator(connection) {
                    @Override
                    public void process(String[] logs) {
                        super.process(logs);
                        Syncronizer.this.process(logs);
                    }

                    @Override
                    public void finish() {
                        super.finish();
                        setProgress(100);
                    }
                };

                s.setExcludeTables(ExcludeTables.GT);

                syncFileData = s.create(set, true);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                setRunning(false);
            }
        } else {
            throw new Exception("Syncronizer is already running.");
        }

        return syncFileData;
    }

    private void process(String[] logs) {
        if (!logs[0].isEmpty()) {
            Arrays.stream(logs[0].split("\n")).forEach(s -> log(s.isEmpty() ? " " : s));
        }
        try {
            setProgress(Integer.parseInt(logs[3]));
        } catch (NumberFormatException ignored) {
        }
    }

    /**
     * comment this out to default behaviour (clear logs for every new job)
     */
    @Override
    public synchronized void clearJob() {
        setProgress(0);
    }
}
