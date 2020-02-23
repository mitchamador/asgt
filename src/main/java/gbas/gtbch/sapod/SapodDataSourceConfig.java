package gbas.gtbch.sapod;

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
import org.springframework.context.annotation.Primary;
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
        entityManagerFactoryRef = "sapodEntityManager",
        transactionManagerRef = "sapodTransactionManager",
        basePackageClasses = SapodDataSourceConfig.class)
public class SapodDataSourceConfig {

    private final PersistenceUnitManager persistenceUnitManager;

    public SapodDataSourceConfig(ObjectProvider<PersistenceUnitManager> persistenceUnitManager) {
        this.persistenceUnitManager = persistenceUnitManager.getIfAvailable();
    }

    @Bean
    @ConfigurationProperties("app.sapod.jpa")
    public JpaProperties sapodJpaProperties() {
        return new JpaProperties();
    }

    @Bean
    @ConfigurationProperties("app.sapod.datasource")
    public DataSourceProperties sapodDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * имя в JNDI
     */
    @Value("${app.sapod.datasource.jndi-name:#{null}}")
    private String jndiName;

    /**
     * DataSource из JNDI (если заполнено в properties app.sapod.datasource.jndi-name)
     * @return
     */
    @Bean(destroyMethod = "")
    @Primary
    @ConditionalOnExpression("!'${app.sapod.datasource.jndi-name:}'.isEmpty()")
    @Qualifier("sapodDataSource")
    public DataSource jndiSapodDataSource() {
        return new JndiDataSourceLookup().getDataSource(jndiName);
    }

    /**
     * DataSource из application.properties
     * @return
     */
    @Bean
    @Primary
    @Qualifier("sapodDataSource")
    @ConditionalOnExpression("'${app.sapod.datasource.jndi-name:}'.isEmpty()")
    public DataSource propSapodDataSource() {
        return sapodDataSourceProperties().initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean sapodEntityManager(
            JpaProperties sapodJpaProperties, @Qualifier("sapodDataSource") DataSource sapodDataSource) {
        EntityManagerFactoryBuilder builder = createEntityManagerFactoryBuilder(
                sapodJpaProperties);
        return builder
                .dataSource(sapodDataSource)
                .packages(SapodDataSourceConfig.class)
                .persistenceUnit("sapodDs")
                .build();
    }

    @Bean
    public JpaTransactionManager sapodTransactionManager(
            EntityManagerFactory sapodEntityManager) {
        return new JpaTransactionManager(sapodEntityManager);
    }

    private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(
            JpaProperties sapodJpaProperties) {
        JpaVendorAdapter jpaVendorAdapter = createJpaVendorAdapter(sapodJpaProperties);
        return new EntityManagerFactoryBuilder(jpaVendorAdapter,
                sapodJpaProperties.getProperties(), this.persistenceUnitManager);
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