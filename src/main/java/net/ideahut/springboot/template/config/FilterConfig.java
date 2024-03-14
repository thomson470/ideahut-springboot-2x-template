package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.filter.DefaultRequestFilter;
import net.ideahut.springboot.filter.SecurityAuthorizationFilter;
import net.ideahut.springboot.security.SecurityAuthorization;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.AppProperties;
import net.ideahut.springboot.util.BeanUtil;

/*
 * Konfigurasi Filter
 */
@Configuration
class FilterConfig {
	
	@Autowired
	private Environment environment;
	@Autowired
	private AppProperties appProperties;
	
	@Bean
	protected FilterRegistrationBean<DefaultRequestFilter> defaultRequestFilter() {		
		return BeanUtil.createFilterBean(
			environment,
			new DefaultRequestFilter()
				.setCORSHeaders(appProperties.getCors())
				.setRequestWrapperEnable(true)
				.setTraceEnable(true)
				.initialize(), 
			1, 
			"/*"
		);
	}
	
	@Bean
	protected FilterRegistrationBean<SecurityAuthorizationFilter> adminFilter(
		@Qualifier(AppConstants.Bean.Admin.HANDLER) AdminHandler adminHandler,
		@Qualifier(AppConstants.Bean.Admin.SECURITY) SecurityAuthorization adminSecurity
	) {
		return BeanUtil.createFilterBean(
			environment,
			new SecurityAuthorizationFilter()
				.setSecurityAuthorization(adminSecurity),
			2,
			adminHandler.getProperties().getResource().getRequestPath() + "/*",
			adminHandler.getProperties().getApi().getRequestPath() + "/*"
		);
	}
	
}