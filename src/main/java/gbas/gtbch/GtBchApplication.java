package gbas.gtbch;

import gbas.tvk.util.UtilDate;
import org.aspectj.lang.annotation.After;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;

import java.time.Duration;
import java.time.Instant;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
		JmsAutoConfiguration.class})
public class GtBchApplication extends SpringBootServletInitializer {

	private static Instant startTime;
	private static Instant endTime;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(GtBchApplication.class);
	}

    public static void main(String[] args) throws Exception {
		startTime = Instant.now();
        SpringApplication.run(GtBchApplication.class, args);
    }

	@EventListener(ApplicationReadyEvent.class)
	public void startApp() {
		endTime = Instant.now();
	}

	/**
	 * Время запуска приложения
	 * @return
	 */
	@Bean
	@Lazy
	public Instant startTime() {
		return startTime;
	}

	/**
	 * Продолжительность запуска приложения
	 * @return
	 */
	@Bean
	@Lazy
	public Duration startupDuration() {
		return Duration.between(startTime, endTime);
	}

}

