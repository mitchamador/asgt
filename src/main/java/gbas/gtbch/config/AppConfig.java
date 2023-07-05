package gbas.gtbch.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gbas.eds.gtbch.settings.GtSettings;
import gbas.gtbch.config.settings.SettingsProperties;
import gbas.gtbch.config.settings.SettingsReaderGtBch;
import gbas.tvk.interaction.pensi.ConnectionManager;
import gbas.tvk.interaction.pensi.PensiManager;
import gbas.tvk.interaction.pensi.sync.Sync;
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
import org.springframework.context.annotation.*;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.TimeZone;

@Configuration
@EnableScheduling
@EnableAsync
@EnableRetry
@EnableAspectJAutoProxy
public class AppConfig {

    private static Logger logger = LoggerFactory.getLogger(AppConfig.class);

    /**
     * Waiting for datasource
     * @param sapodDataSource
     * @return
     * @throws SQLException
     */
    @Bean
    @Autowired
    @Retryable(maxAttempts=30, backoff=@Backoff(multiplier=2, maxDelay=10000))
    @DependsOn("servletContext")
    public boolean waitSapodDataSource(@Qualifier("sapodDataSource") DataSource sapodDataSource) throws SQLException {

        try (Connection cSapod = sapodDataSource.getConnection();
             Statement stSapod = cSapod.createStatement()) {
            stSapod.executeQuery(cSapod.getMetaData().getURL().startsWith("jdbc:db2") ? "SELECT 1 FROM SYSIBM.SYSDUMMY1" : "SELECT 1");
            logger.info("connection with sapodDataSource establised");
        }

        return true;
    }

    /**
     * Инициализация {@link DbHelper}
     * @return
     */
    @Bean
    @Autowired
    @Lazy
    public DbHelper dbHelper(@Qualifier("sapodDataSource") DataSource sapodDataSource) throws SQLException {
        DbHelper dbHelper = DbHelper.INSTANCE;
        try (Connection c = sapodDataSource.getConnection()) {
            dbHelper.init(c.getMetaData().getDatabaseProductName().contains("DB2") ? new Db2Worker() : new SqlServerWorker());
        }
        return dbHelper;
    }

    @Value("${app.jobs.pensimanager.fullmergepensi:true}")
    private boolean fullInsertMergePensi;

    @Value("${app.jobs.pensimanager.fullmergesapod:true}")
    private boolean fullInsertMergeSapod;

    private PensiManager pm;

    /**
     * Create {@link PensiManager}
     */
    @Bean
    @Autowired
    @DependsOn("dbHelper")
    public PensiManager pensiManager(
            @Qualifier("sapodDataSource") DataSource sapodDataSource,
            @Qualifier("pensiDataSource") DataSource pensiDataSource) {
        pm = PensiManager.INSTANCE;

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

        pm.setMode(Sync.ASGT);

        pm.setFullMergePensi(fullInsertMergePensi);
        pm.setFullMergeSapod(fullInsertMergeSapod);

        return pm;
    }

    private Instant startTime, endTime;

    @EventListener(ApplicationReadyEvent.class)
    public void startApp() {
        endTime = Instant.now();
    }

    private ApplicationContext context;

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

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

    /**
     * jackson timezone configuration
     * @param objectMapper
     */
    @Autowired
    public void configureJackson(ObjectMapper objectMapper) {
        objectMapper.setTimeZone(TimeZone.getDefault());
    }

    /**
     * init {@link GtSettings}
     * @return
     */
    @Bean
    public GtSettings gtSettings(SettingsProperties settingsProperties) {
        GtSettings gtSettings = GtSettings.INSTANCE;
        gtSettings.init(new SettingsReaderGtBch(settingsProperties));
        return gtSettings;
    }

}