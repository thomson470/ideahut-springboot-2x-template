package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import net.ideahut.springboot.task.TaskHandler;
import net.ideahut.springboot.task.TaskHandlerImpl;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.AppProperties;

/*
 * Konfigurasi TaskHandler
 * Untuk proses asynchronous
 */
@Configuration
class AsyncConfig {
	
	@Autowired
	private AppProperties appProperties;
	
	@Primary
	@Bean(name = AppConstants.Bean.Async.COMMON, destroyMethod = "shutdown")
    protected TaskHandler commonAsync() {
		return new TaskHandlerImpl()
		.setTaskProperties(appProperties.getAsync().getCommon());
    }
	
	@Bean(name = AppConstants.Bean.Async.AUDIT, destroyMethod = "shutdown")
	protected TaskHandler auditAsync() {
		return new TaskHandlerImpl()
		.setTaskProperties(appProperties.getAsync().getAudit());
    }
	
}
