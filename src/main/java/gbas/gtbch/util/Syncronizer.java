package gbas.gtbch.util;

import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.tvk.nsi.cash.Func;
import gbas.tvk.util.UtilDate;
import gbas.tvk.util.synchronizator.Syncronizator;
import gbas.tvk.util.synchronizator.synchroObjects.NsiOperations;
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

@Component
public class Syncronizer extends ServerJob {

    public static final String SYNCRONIZER_UPLOAD_PARAM = "syncronizer_data";

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

    @Async
    public void run() {
        if (isRunning()) return;

        clearJob();

        try (Connection con = sapodDataSource.getConnection()) {
            setRunning(true);

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
                        Date dateCreate = UtilDate.getDate(dateStr, "dd.MM.yyyy HH:mm");
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
                s.setInsertMode(NsiOperations.InsertMode.valueOf(insertMode.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
            s.run(true);

            String errors = s.getErr();
            if (!Func.isEmpty(s.getErr())) {
                System.err.println(errors);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setRunning(false);
        }
    }

    public void setData(String filename, byte[] bytes) {
        this.filename = filename;
        this.bytes = bytes;
    }


}
