package com.mycompany.hosted.config;



import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.mycompany.hosted.filter.CacheControlFilter;

public class MyAnnotationConfigDispatcherInitializer
      extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		
		Class<?>[] rootClasses = {MyConfigurer.class,
				MvcBeans.class, WebFlowConfig.class, JpaConfig.class};
		
		return rootClasses;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		
		return new String[] {"/spring/*"} ;
	}
	
	@Override
	protected FrameworkServlet createDispatcherServlet(WebApplicationContext servletAppContext) {
		
		DispatcherServlet dispatcher =new DispatcherServlet(servletAppContext);
		
		dispatcher.setThrowExceptionIfNoHandlerFound(true);
		
		return dispatcher;
		
	}
	
 	@Override
	protected Filter[] getServletFilters() {
		
		Filter cacheControl = new CacheControlFilter();
		
		return new Filter [] {cacheControl};
		
	}
	
	@Override
	protected FilterRegistration.Dynamic registerServletFilter(ServletContext servletContext,
			Filter filter) {
		
		
		FilterRegistration.Dynamic reg = servletContext.addFilter("cacheControlFilter", 
				filter);
		
		EnumSet<DispatcherType> set = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
		
		String[] patterns = new String[] {"/spring/paymentException/initErrorModel",
				"/spring/viewCart/request"};
		
		reg.addMappingForUrlPatterns(set, true, patterns);
		
		return reg;
		
	} 
	
	
} //end config
