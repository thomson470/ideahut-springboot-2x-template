package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.AdminHandlerImpl;
import net.ideahut.springboot.admin.AdminSecurity;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.security.RedisMemoryCredential;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.AppProperties;
import net.ideahut.springboot.template.support.GridSupport;

@Configuration
class AdminConfig {
	
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private AppProperties appProperties;
	
	
	@Bean(name = AppConstants.Bean.Admin.HANDLER)
	protected AdminHandler adminHandler(
		DataMapper dataMapper,
		RedisTemplate<String, byte[]> redisTemplate
	) {
		AppProperties.Admin admin = appProperties.getAdmin();
		return new AdminHandlerImpl()
		.setApplicationContext(applicationContext)
		.setConfigFile(admin.getConfigFile())
		.setDataMapper(dataMapper)
		.setGridAdditionals(GridSupport.getAdditionals())
		.setGridOptions(GridSupport.getOptions())
		.setProperties(admin)
		.setRedisTemplate(redisTemplate);
	}
	
	@Bean(name = AppConstants.Bean.Admin.CREDENTIAL)
	protected RedisMemoryCredential adminCredential(
		DataMapper dataMapper,
		RedisTemplate<String, byte[]> redisTemplate
	) {
		AppProperties.Admin admin = appProperties.getAdmin();
		return new RedisMemoryCredential()
		.setConfigFile(admin.getCredentialFile())
		.setDataMapper(dataMapper)
		.setRedisPrefix("ADMIN-CREDENTIAL")
		.setRedisTemplate(redisTemplate);
	}
	
	
	@Bean(name = AppConstants.Bean.Admin.SECURITY)
	protected AdminSecurity adminSecurity(
		DataMapper dataMapper,
		@Qualifier(AppConstants.Bean.Admin.CREDENTIAL) RedisMemoryCredential adminCredential,
		@Qualifier(AppConstants.Bean.Admin.HANDLER) AdminHandler adminHandler
	) {
		return new AdminSecurity()
		.setCredential(adminCredential)
		.setDataMapper(dataMapper)
		.setProperties(adminHandler.getProperties());
		
	}
	
	
	/*
	@Bean(name = AppConstants.Bean.Admin.SECURITY)
	protected BasicAuthSecurity adminSecurity(
		@Qualifier(AppConstants.Bean.Admin.CREDENTIAL) RedisMemoryCredential adminCredential
	) {
		return new BasicAuthSecurity()
		.setCredential(adminCredential)
		.setRealm("Admin");
	}
	*/
	
}
