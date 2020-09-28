package gbas.gtbch.config;

import gbas.eds.gtbch.ConvertXmlToVagonOtprTransit;
import gbas.tvk.interaction.pensi.ConnectionManager;
import gbas.tvk.interaction.pensi.PensiManager;
import gbas.tvk.interaction.pensi.sync.Sync;
import gbas.tvk.payment.PayTransportation;
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
import org.springframework.context.annotation.*;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.zip.GZIPOutputStream;

@Configuration
@EnableScheduling
@EnableAsync
@EnableRetry
public class WebConfig implements WebMvcConfigurer {

    private static Logger logger = LoggerFactory.getLogger(WebConfig.class);

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
    public boolean checkSapodDataSource(@Qualifier("sapodDataSource") DataSource sapodDataSource) throws SQLException {

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
     * <p>@Scope("prototype") - используются множественные экземпляры класса
     * <p>connection должен закрываться в конце жизненного цикла бина public методом close()
     * @return
     * @throws Exception
     */
    @Bean
    @Scope("prototype")
    @Autowired
    PayTransportation payTransportation(@Qualifier("sapodDataSource") DataSource sapodDataSource) throws Exception {
        return new PayTransportation(sapodDataSource.getConnection());
    }

    /**
     * класс конвертирования входящего XML в {@link gbas.tvk.otpravka.object.VagonOtprTransit}
     * <p>@Scope("prototype") - используются множественные экземпляры класса
     * <p>connection должен закрываться в конце жизненного цикла бина public методом close()
     * @param sapodDataSource
     * @return
     * @throws Exception
     */
    @Bean
    @Scope("prototype")
    @Autowired
    ConvertXmlToVagonOtprTransit convertXmlToVagonOtprTransit(@Qualifier("sapodDataSource") DataSource sapodDataSource) throws Exception {
        return new ConvertXmlToVagonOtprTransit(sapodDataSource.getConnection());
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
//    @Lazy
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


    /**
     * compress to gzip
     * @param body
     * @return
     * @throws IOException
     */
    private static byte[] compress(byte[] body) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos)) {
            gzipOutputStream.write(body);
        }
        return baos.toByteArray();
    }

    /**
     * check HttpServletRequest for HttpHeaders.ACCEPT_ENCODING header
     * @param httpRequest
     * @return
     */
    private static boolean acceptsGZipEncoding(HttpServletRequest httpRequest) {
        String acceptEncoding = httpRequest.getHeader(HttpHeaders.ACCEPT_ENCODING);
        return acceptEncoding != null && acceptEncoding.contains("gzip");
    }

    /**
     * gzip ResponseEntity
     * @param request
     * @param body
     * @return
     */
    public static ResponseEntity getGzippedResponseEntity(HttpServletRequest request, String body) {


        if (body != null && acceptsGZipEncoding(request)) {

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_ENCODING, "gzip");

            try {
                return new ResponseEntity<>(compress(body.getBytes(StandardCharsets.UTF_8)), headers, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}