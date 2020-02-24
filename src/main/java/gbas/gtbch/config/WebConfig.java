package gbas.gtbch.config;

import gbas.tvk.interaction.pensi.ConnectionManager;
import gbas.tvk.interaction.pensi.PensiManager;
import gbas.tvk.paymentng.PayTransportation;
import gbas.tvk.rwmap.dist.DistCalc;
import gbas.tvk.rwmap.dist.db.DistCalcDbSapod;
import gbas.tvk.service.db.DbHelper;
import gbas.tvk.service.db.workers.Db2Worker;
import gbas.tvk.service.db.workers.SqlServerWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;

@Configuration
@EnableScheduling
@EnableAsync
public class WebConfig implements WebMvcConfigurer {

    private static Logger logger = LoggerFactory.getLogger(WebConfig.class);

//    /**
//     * класс для работы с Db2
//     * @return
//     */
//    @Bean
//    @Lazy
//    Db2Worker db2Worker() {
//        return new Db2Worker();
//    }
//
//    /**
//     * класс для работы с SqlServer
//     * @return
//     */
//    @Bean
//    @Lazy
//    SqlServerWorker sqlServerWorker() {
//        return new SqlServerWorker();
//    }
//
//    /**
//     * имя бина DbWorker
//     */
//    @Value("${app.dbworker}")
//    String dbWorkerName;
//
//    /**
//     * Инициализация {@link DbHelper}
//     * @return
//     */
//    @Bean
//    @Autowired
//    public DbHelper dbHelper(ApplicationContext context) {
//
//        DbWorker worker = (DbWorker) context.getBean(dbWorkerName);
//
//        DbHelper dbHelper = DbHelper.INSTANCE;
//        dbHelper.init(worker);
//
//        logger.info("DbHelper initialized with {}", worker.getClass().getSimpleName());
//
//        return dbHelper;
//    }

    /**
     * Инициализация {@link DbHelper}
     * @return
     */
    @Bean
    @Autowired
    public DbHelper dbHelper(@Qualifier("sapodDataSource") DataSource sapodDataSource) throws SQLException {
        DbHelper dbHelper = DbHelper.INSTANCE;
        try (Connection c = sapodDataSource.getConnection()) {
            dbHelper.init(c.getMetaData().getDatabaseProductName().contains("DB2") ? new Db2Worker() : new SqlServerWorker());
        }
        return dbHelper;
    }

    /**
     * класс расчета расстояний
     * * @Scope("prototype") - используются множественные экземпляры класса
     * * connection должен закрываться в конце жизненного цикла бина public методом close()
     * @return
     * @throws SQLException
     */
    @Bean
    @Scope("prototype")
    @Autowired
    DistCalc distCalc(@Qualifier("sapodDataSource") DataSource sapodDataSource) throws SQLException {
        return new DistCalc(sapodDataSource.getConnection(), new DistCalcDbSapod());
    }

    /**
     * класс расчета провозной платы
     * * @Scope("prototype") - используются множественные экземпляры класса
     * * connection должен закрываться в конце жизненного цикла бина public методом close()
     * @return
     * @throws Exception
     */
    @Bean
    @Scope("prototype")
    @Autowired
    PayTransportation payTransportation(@Qualifier("sapodDataSource") DataSource sapodDataSource) throws Exception {
        return new PayTransportation(sapodDataSource.getConnection());
    }


    @Value("${app.jobs.pensimanager.fullmergepensi:true}")
    private boolean fullInsertMergePensi;

    @Value("${app.jobs.pensimanager.fullmergesapod:true}")
    private boolean fullInsertMergeSapod;

    /**
     * Инициализация {@link PensiManager}
     * @return
     */
    @Bean
    //@Lazy
    @Autowired
    @DependsOn("dbHelper")
    public PensiManager pensiManager(
            @Qualifier("sapodDataSource") DataSource sapodDataSource,
            @Qualifier("pensiDataSource") DataSource pensiDataSource) {
        PensiManager pm = PensiManager.INSTANCE;

        pm.init(new ConnectionManager() {
            @Override
            public Connection getSapodCon() throws SQLException {
                return sapodDataSource.getConnection();
            }

            @Override
            public Connection getPensiCon() throws SQLException {
                return pensiDataSource.getConnection();
            }
        });

        pm.setFullMergePensi(fullInsertMergePensi);
        pm.setFullMergeSapod(fullInsertMergeSapod);

        return pm;
    }

    private Instant startTime, endTime;

    @EventListener(ApplicationReadyEvent.class)
    public void startApp() {
        endTime = Instant.now();
    }

    @Autowired
    ApplicationContext context;

    @Bean
    @Lazy
    public Instant startTime() {
        return startTime = Instant.ofEpochMilli(context.getStartupDate());
    }

    /**
     * Продолжительность запуска приложения
     * @return
     */
    @Bean
    @Lazy
    public Duration startupDuration() {
        return startTime != null && endTime != null ? Duration.between(startTime, endTime) : null;
    }



}