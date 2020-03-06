package gbas.gtbch.pensi;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "pensiEntityManager",
        transactionManagerRef = "pensiTransactionManager",
        basePackageClasses = PensiDataSourceConfig.class)
public class PensiDataSourceConfig {

    private final PersistenceUnitManager persistenceUnitManager;

    public PensiDataSourceConfig(ObjectProvider<PersistenceUnitManager> persistenceUnitManager) {
        this.persistenceUnitManager = persistenceUnitManager.getIfAvailable();
    }

    @Bean
    @ConfigurationProperties("app.pensi.jpa")
    public JpaProperties pensiJpaProperties() {
        return new JpaProperties();
    }

    @Bean
    @ConfigurationProperties("app.pensi.datasource")
    public DataSourceProperties pensiDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * имя в JNDI
     */
    @Value("${app.pensi.datasource.jndi-name:#{null}}")
    private String jndiName;

    /**
     * DataSource из JNDI
     * @return
     */
    @Bean(destroyMethod = "")
    @ConditionalOnExpression("!'${app.pensi.datasource.jndi-name:}'.isEmpty()")
    @Qualifier("pensiDataSource")
    public DataSource jndiPensiDataSource() {
        return new JndiDataSourceLookup().getDataSource(jndiName);
    }

    /**
     * DataSource из application.properties
     * @return
     */
    @Bean
    @Qualifier("pensiDataSource")
    @ConditionalOnExpression("'${app.pensi.datasource.jndi-name:}'.isEmpty()")
    public DataSource propPensiDataSource() {
        return pensiDataSourceProperties().initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean pensiEntityManager(
            JpaProperties pensiJpaProperties, @Qualifier("pensiDataSource") DataSource pensiDataSource) {
        EntityManagerFactoryBuilder builder = createEntityManagerFactoryBuilder(
                pensiJpaProperties);
        return builder
                .dataSource(pensiDataSource)
                .packages(PensiDataSourceConfig.class)
                .persistenceUnit("pensiDs")
                .build();
    }

    @Bean
    public JpaTransactionManager pensiTransactionManager(
            EntityManagerFactory pensiEntityManager) {
        return new JpaTransactionManager(pensiEntityManager);
    }

    private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(
            JpaProperties pensiJpaProperties) {
        JpaVendorAdapter jpaVendorAdapter = createJpaVendorAdapter(pensiJpaProperties);
        return new EntityManagerFactoryBuilder(jpaVendorAdapter,
                pensiJpaProperties.getProperties(), this.persistenceUnitManager);
    }

    private JpaVendorAdapter createJpaVendorAdapter(JpaProperties jpaProperties) {
        AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(jpaProperties.isShowSql());
        adapter.setDatabase(jpaProperties.getDatabase());
        adapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
        adapter.setGenerateDdl(jpaProperties.isGenerateDdl());
        return adapter;
    }

}