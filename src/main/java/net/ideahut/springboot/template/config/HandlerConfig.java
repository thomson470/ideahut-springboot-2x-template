package net.ideahut.springboot.template.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.audit.AuditHandler;
import net.ideahut.springboot.audit.DatabaseMultiAuditHandler;
import net.ideahut.springboot.cache.CacheGroupHandler;
import net.ideahut.springboot.cache.CacheHandler;
import net.ideahut.springboot.cache.RedisCacheGroupHandler;
import net.ideahut.springboot.cache.RedisCacheHandler;
import net.ideahut.springboot.crud.CrudAction;
import net.ideahut.springboot.crud.CrudHandler;
import net.ideahut.springboot.crud.CrudHandlerImpl;
import net.ideahut.springboot.crud.CrudPermission;
import net.ideahut.springboot.crud.CrudProps;
import net.ideahut.springboot.crud.CrudRequest;
import net.ideahut.springboot.crud.CrudResource;
import net.ideahut.springboot.entity.EntityInfo;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.TrxManagerInfo;
import net.ideahut.springboot.grid.GridHandler;
import net.ideahut.springboot.grid.GridHandlerImpl;
import net.ideahut.springboot.init.InitHandler;
import net.ideahut.springboot.init.InitHandlerImpl;
import net.ideahut.springboot.mail.MailHandler;
import net.ideahut.springboot.mail.MailHandlerImpl;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.report.ReportHandler;
import net.ideahut.springboot.report.ReportHandlerImpl;
import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.AppProperties;
import net.ideahut.springboot.template.AppProperties.Audit;
import net.ideahut.springboot.template.entity.EntityFill;
import net.ideahut.springboot.template.support.GridSupport;
import net.ideahut.springboot.util.BeanUtil;

/*
 * Konfigurasi handler
 * - AuditHandler
 * - CrudHandler
 */
@Configuration
class HandlerConfig {
	
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private AppProperties appProperties;
	@Autowired
	private ServletWebServerApplicationContext servletWebServerApplicationContext;
	
	@Bean
	protected InitHandler initHandler() {
		InitHandler.Endpoint endpoint = new InitHandler.Endpoint() {
			@Override
			public String getUrl() {
				return "http://localhost:" + servletWebServerApplicationContext.getWebServer().getPort() + "/warmup";
			}
		};
		return new InitHandlerImpl()
		.setEndpoint(endpoint);
	}

	@Bean
	protected AuditHandler auditHandler(
		@Qualifier(AppConstants.Bean.ENTITY_TRX_MANAGER) EntityTrxManager entityTrxManager,
		@Qualifier(AppConstants.Bean.Async.AUDIT) TaskHandler auditAsync,
		@Qualifier(AppConstants.Bean.Audit.SESSION_FACTORY) SessionFactory auditSessionFactory
	) {
		Audit audit = appProperties.getAudit();
		return new DatabaseMultiAuditHandler()
		.setEntityTrxManager(entityTrxManager)
		.setProperties(audit.getProperties())
		.setTaskHandler(auditAsync)
		.setRejectNonAuditEntity(true);
		/*
		return new DatabaseSingleAuditHandler()
		.setEntityTrxManager(entityTrxManager)
		.setProperties(audit.getProperties())
		.setTaskHandler(auditAsync);
		*/
	}
	
	@Bean
	protected CacheGroupHandler cacheGroupHandler(
		DataMapper dataMapper,
		RedisTemplate<String, byte[]> redisTemplate,
		@Qualifier(AppConstants.Bean.Async.COMMON) TaskHandler cacheAsync
	) throws Exception {
		return new RedisCacheGroupHandler()
		.setDataMapper(dataMapper)
		.setGroups(appProperties.getCache().getGroups())
		.setRedisTemplate(redisTemplate)
		.setTaskHandler(cacheAsync);
	}
	
	@Bean
	protected CacheHandler cacheHandler(
		DataMapper dataMapper,
		RedisTemplate<String, byte[]> redisTemplate,
		@Qualifier(AppConstants.Bean.Async.COMMON) TaskHandler cacheAsync
	) throws Exception {
		return new RedisCacheHandler()
		.setDataMapper(dataMapper)
		.setRedisTemplate(redisTemplate)
		.setTaskHandler(cacheAsync)
		.setLimit(100)
		.setNullable(true)
		.setPrefix("_test");
	}
	
	@Bean
	protected ReportHandler reportHandler() {
		return new ReportHandlerImpl();
	}
	
	@Bean
	protected MailHandler mailHandler(
    	@Qualifier(AppConstants.Bean.Async.COMMON) TaskHandler taskHandler
    ) {
		return new MailHandlerImpl()
		.setTaskHandler(taskHandler)
		.setMailProperties(appProperties.getMail());
	}
	
	@Bean
	protected GridHandler gridHandler(
		DataMapper dataMapper,
		RedisTemplate<String, byte[]> redisTemplate
	) {
		return new GridHandlerImpl()
		.setApplicationContext(applicationContext)
		.setDataMapper(dataMapper)
		.setLocation(appProperties.getGridLocation())
		.setRedisTemplate(redisTemplate)
		.setAdditionals(GridSupport.getAdditionals())
		.setOptions(GridSupport.getOptions());
	}
	
	@Bean
	protected CrudHandler crudHandler(
		@Qualifier(AppConstants.Bean.ENTITY_TRX_MANAGER) EntityTrxManager entityTrxManager,
		@Qualifier(AppConstants.Bean.DATA_MAPPER) DataMapper dataMapper,
		CrudResource resource,
		CrudPermission permission
	) {
		return new CrudHandlerImpl()
		.setEntityTrxManager(entityTrxManager)
		.setResource(resource);
		//.setPermission(permission);
	}
	
	// Contoh ambil Crud Resource
	// Bisa disimpan di database untuk Entity yang boleh di-crud
	// Ditambah dengan Permission untuk membatasi akses
	@Bean
	protected CrudResource crudResource(
		@Qualifier(AppConstants.Bean.ENTITY_TRX_MANAGER) EntityTrxManager entityTrxManager
	) {
		return new CrudResource() {
			@Override
			public CrudProps getCrudProps(String manager, String name) {
				try {
					Class<?> clazz = BeanUtil.classOf(EntityFill.class.getPackageName() + "." + name);
					TrxManagerInfo trxManagerInfo = entityTrxManager.getDefaultTrxManagerInfo();
					if (manager != null && !manager.isEmpty()) {
						trxManagerInfo = entityTrxManager.getTrxManagerInfo(manager);
					}
					EntityInfo entityInfo = trxManagerInfo.getEntityInfo(clazz);
					CrudProps resource = new CrudProps();
					resource.setEntityInfo(entityInfo);
					resource.setMaxLimit(200);
					resource.setUseNative(false);
					return resource;
				} catch (Exception e) {
					throw BeanUtil.exception(e);
				}
			}
		};
	}
	
	// Contoh cek akses crud
	// Bisa disimpan di database dengan memasangkan Resource & Role
	// Termasuk bisa diset Specific Filter di bagian ini
	@Bean
	protected CrudPermission crudPermission() {
		return new CrudPermission() {
			
			@Override
			public boolean isCrudAllowed(CrudAction action, CrudRequest request) {
				//request.setSpesifics(null);
				return true;
			}
		};
	}
	
}
