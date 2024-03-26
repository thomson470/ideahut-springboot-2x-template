package net.ideahut.springboot.template.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.audit.AuditInfo;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.Application;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.template.service.MessageService;

@Component
@ComponentScan
public class RequestInterceptor implements HandlerInterceptor {
	
	@Autowired
	private AppProperties appProperties;
	@Autowired
	private MessageService messageService;
	@Autowired
	private AdminHandler adminHandler;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)	throws Exception {
		if (!Application.isReady()) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			return false;
		}
		AuditInfo.context().setAuditor(AppConstants.Profile.SYSTEM);
		messageService.setRequestLanguage();
		
		if (handler instanceof HandlerMethod) {
			Method method = ((HandlerMethod) handler).getMethod();
			if (!appProperties.getIgnoredHandlerClasses().contains(method.getDeclaringClass())) {
				AuditInfo.context().setInfo(method.getDeclaringClass().getSimpleName() + ":" + method.getName());				
			}
		}
		else if (handler instanceof ResourceHttpRequestHandler) {
			String redirect = adminHandler.getRedirect(request);
			if (redirect != null) {
				response.sendRedirect(redirect);
				return false;
			}
		}
		return true;
	}	
	
}
