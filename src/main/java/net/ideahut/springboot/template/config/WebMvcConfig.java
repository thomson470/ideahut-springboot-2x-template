package net.ideahut.springboot.template.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.config.BasicWebMvcConfig;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.template.interceptor.RequestInterceptor;

/*
 * Konfigurasi Web MVC
 * 
 */
@Configuration
@EnableWebMvc
class WebMvcConfig extends BasicWebMvcConfig {
	
	@Autowired
	private DataMapper dataMapper;
	@Autowired
	private RequestInterceptor requestInterceptor;
	@Autowired
	private AdminHandler adminHandler;
	
	
	@Override
	protected String parameterName() {
		return "_fmt";
	}

	@Override
	protected boolean enableAcceptHeader() {
		return true;
	}

	@Override
	protected DataMapper dataMapper() {
		return dataMapper;
	}

	@Override
	protected HandlerInterceptor[] handlerInterceptors() {
		return new HandlerInterceptor[] {
			requestInterceptor
		};
	}

	@Override
	protected Map<String, String> resourceHandlers() {
		Map<String, String> map = new HashMap<>();
		String resourceLocations = adminHandler.getProperties().getResource().getLocations();
		resourceLocations = resourceLocations != null ? resourceLocations.trim() : "";
		if (!resourceLocations.isEmpty()) {
			map.put(adminHandler.getProperties().getResource().getRequestPath() + "/**", resourceLocations);
		}
		return map;
	}

	/*
	 * Untuk stream download
	 */
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
	    taskExecutor.setCorePoolSize(10);
	    taskExecutor.setMaxPoolSize(10);
	    taskExecutor.afterPropertiesSet();
	    configurer.setTaskExecutor(taskExecutor);
		super.configureAsyncSupport(configurer);
	}

	@Override
	protected boolean enableExtension() {
		return true;
	}
	
	@Override
	protected Map<String, MediaType> mediaTypes() {
		return null;
	}
	
}
