package net.ideahut.springboot.template.config;

import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import net.ideahut.springboot.entity.EntityAnnotationIntrospector;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.EntityTrxManagerImpl;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.mapper.DataMapperImpl;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.AppProperties;
import net.ideahut.springboot.template.AppProperties.Audit;
import net.ideahut.springboot.template.entity.EntityFill;
import net.ideahut.springboot.util.FrameworkUtil;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
(
	entityManagerFactoryRef = AppConstants.Bean.ENTITY_MANAGER_FACTORY,
	transactionManagerRef = AppConstants.Bean.TRANSACTION_MANAGER,
	basePackages = {
		"net.ideahut.springboot.template.repo"
	}
)
class TrxManagerConfig {
	
	@Autowired
	private Environment environment;	
	@Autowired
	private AppProperties appProperties;
	

	@Primary
	@Bean(name = AppConstants.Bean.DATA_SOURCE)
	@ConfigurationProperties(prefix = "spring.datasource")
	protected DataSource dataSource() {
		String jndi = environment.getProperty("spring.datasource.jndi-name", "").trim();
		if (!jndi.isEmpty()) {
			JndiDataSourceLookup lookup = new JndiDataSourceLookup();
			return lookup.getDataSource(jndi);
		} else {
			return DataSourceBuilder.create().build();
		}
    }
	
	@Primary
	@Bean(name = AppConstants.Bean.ENTITY_MANAGER_FACTORY)
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
		EntityManagerFactoryBuilder builder,
		@Qualifier(AppConstants.Bean.DATA_SOURCE) DataSource dataSource,
		@Qualifier(AppConstants.Bean.Audit.SESSION_FACTORY) SessionFactory auditSessionFactory
	) {
		Map<String, Object> properties = FrameworkUtil.getHibernateProperties(environment, "spring.jpa.properties");
		/*
		 * Session Factory audit dapat di-set disini
		 * atau bisa juga di application.properties / application.yml di property:
		 * - "spring.jpa.properties.hibernate.audit_identifier": audit id yang digunakan agar terhubung dengan AuditHandler, contoh: spring_sample
		 * - "spring.jpa.properties.hibernate.audit_bean_name": nama bean audit session factory, contoh: auditSessionFactory
		 */
		//EntityIntegrator.setAuditSessionFactory("spring_sample", properties, auditSessionFactory);
		return builder
			.dataSource(dataSource)
			.packages(
				EntityFill.class.getPackageName()
			)			
			.persistenceUnit("default")
			.properties(properties)			
			.build();
	}

	@Primary
	@Bean(name = AppConstants.Bean.TRANSACTION_MANAGER)
	public PlatformTransactionManager transactionManager(
		@Qualifier(AppConstants.Bean.ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory
	) {
		return new JpaTransactionManager(entityManagerFactory);
	}
	
	
	
	/*
	 * CONTOH TRX MANAGER / DATABASE LAIN
	 */
	/*
	@Bean("otherrEmf")
	public LocalContainerEntityManagerFactoryBean otherDbEMF(
		EntityManagerFactoryBuilder builder, 
		@Qualifier("otherDb") DataSource dataSource,
		@Qualifier(AppConstants.Bean.Audit.SESSION_FACTORY) SessionFactory auditSessionFactory
	) {
		Map<String, Object> properties = BeanUtil.getHibernateProperties(environment, "app.other-db.jpa.properties");
		EntityIntegrator.setAuditSessionFactory("spring_sample_2", properties, auditSessionFactory);
		return builder
			.dataSource(dataSource)
			.packages(
				"net.ideahut.springboot.sample.otherdb.entity"
			)			
			.persistenceUnit("default")
			.properties(properties)			
			.build();
	}
	@Bean("otherDb")
	public DataSource otherDb() {
		Database other = appProperties.getOtherDb();
		String jndi = other.getDatasource().getJndiName();
		jndi = jndi != null ? jndi.trim() : "";
		if (jndi.length() != 0) {
			JndiDataSourceLookup lookup = new JndiDataSourceLookup();
			return lookup.getDataSource(jndi);
		} else {
			return DataSourceBuilder.create()
			.driverClassName(other.getDatasource().getDriverClassName())
			.url(other.getDatasource().getJdbcUrl())
			.username(other.getDatasource().getUsername())
			.password(other.getDatasource().getPassword())
			.build();
		}
    }
	@Bean(name = "otherManager")
	public PlatformTransactionManager othrTransactionManager(
		@Qualifier("otherrEmf") EntityManagerFactory entityManagerFactory
	) {
		return new JpaTransactionManager(entityManagerFactory);
	}
	*/
	
	
	@Bean(name = AppConstants.Bean.Audit.DATA_SOURCE)
	protected DataSource auditDatasource() {
		Audit audit = appProperties.getAudit();
		String jndi = audit.getDatasource().getJndiName();
		jndi = jndi != null ? jndi.trim() : "";
		if (jndi.length() != 0) {
			JndiDataSourceLookup lookup = new JndiDataSourceLookup();
			return lookup.getDataSource(jndi);
		} else {
			return DataSourceBuilder.create()
			.driverClassName(audit.getDatasource().getDriverClassName())
			.url(audit.getDatasource().getJdbcUrl())
			.username(audit.getDatasource().getUsername())
			.password(audit.getDatasource().getPassword())
			.build();
		}
    }	
	
	@Bean(name = AppConstants.Bean.Audit.SESSION_FACTORY)
	protected LocalSessionFactoryBean auditSessionFactory(
		@Qualifier(AppConstants.Bean.Audit.DATA_SOURCE) DataSource datasource
	) {
		Audit audit = appProperties.getAudit();
		Map<String, String> jpaProps = audit.getJpa().getProperties();
		Properties properties = new Properties();
		for (Map.Entry<String, String> entry : jpaProps.entrySet()) {
			if (entry.getKey().startsWith("hibernate.")) {
				properties.put(entry.getKey(), entry.getValue());
			}
		}
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(datasource);
        sessionFactory.setHibernateProperties(properties);
        sessionFactory.setEntityInterceptor(null);
        return sessionFactory;
	}
	
	@Bean(name = AppConstants.Bean.DATA_MAPPER)
	protected DataMapper dataMapper() {
		return new DataMapperImpl()
		.setIntrospector(new EntityAnnotationIntrospector());
	}
	
	@Bean(name = AppConstants.Bean.ENTITY_TRX_MANAGER)
	protected EntityTrxManager entityTrxManager() {
		return new EntityTrxManagerImpl();
	}
	
	
}
