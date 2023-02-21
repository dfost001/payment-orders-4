package com.mycompany.hosted.config;

import java.util.Arrays;
import java.util.HashSet;
//import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.ComponentScan;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.util.UrlPathHelper;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mycompany.hosted.exception_handler.MyDefaultExceptionResolver;
import com.mycompany.hosted.exception_handler.NavigationExceptionResolver;
//import com.mycompany.hosted.exception_handler.CheckoutHttpExceptionResolver;
import com.mycompany.hosted.formatter.DateFormatter;
import com.mycompany.hosted.formatter.TextFormatAnnotationFormatterFactory;
import com.mycompany.hosted.jackson2Json.MyObjectMapper;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {		
		"com.mycompany.hosted.mock.service",		
		"com.mycompany.hosted.controller",
		"com.mycompany.hosted.exception_handler",
		"com.mycompany.hosted.checkoutFlow",
		"com.mycompany.hosted.checkoutFlow.mvc.controller",
		"com.mycompany.hosted.checkoutFlow.jpa",
		"com.mycompany.hosted.checkoutFlow.paypal.orders",
		"com.mycompany.hosted.checkoutFlow.paypal.rest",
		"com.mycompany.hosted.http",
		"com.mycompany.hosted.cart",
		"com.mycompany.hosted.utility",
		"com.mycompany.hosted.validation"})

public class MyConfigurer implements WebMvcConfigurer {	

	@Override
	public void configurePathMatch(PathMatchConfigurer config) {
		
		  config.setPathMatcher(new AntPathMatcher());
		    
            UrlPathHelper helper = new UrlPathHelper() ;
	        
	        helper.setAlwaysUseFullPath(false);
	        
	        helper.setUrlDecode(true);
		    
	        config.setUrlPathHelper(helper);
	        
	        config.setUseRegisteredSuffixPatternMatch(Boolean.TRUE); //only match against registered suffixes
	        
	        config.setUseSuffixPatternMatch(Boolean.FALSE); //normally set to false, will match any request
	        
	        config.setUseTrailingSlashMatch(Boolean.TRUE);
		
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer config) {
		
		    config.ignoreAcceptHeader(false);
	        
	        config.favorParameter(true);
	        
	        config.favorPathExtension(false); 	      
	        
	        config.parameterName("media");
		
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry reg) {
		
		reg.addResourceHandler("/resources/**")
        .addResourceLocations("/resources/", "/WEB-INF/flows/checkout/resources/")
        .setCachePeriod(0);
		
	}
	
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		
		InternalResourceViewResolver jspResolver = new InternalResourceViewResolver();
		jspResolver.setPrefix("/WEB-INF/");
		jspResolver.setSuffix(".jsp");
		
		registry.beanName();
		registry.viewResolver(jspResolver);
		
		MappingJackson2JsonView defaultView =  jsonView();
		
		registry.enableContentNegotiation(true, defaultView);	
	}
	
	private MappingJackson2JsonView jsonView() {
		
		 String[] keys = {"exceptionType", "messages", "handler"};
		        
		 MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		        
		 Set<String> modelKeys = new HashSet<>();
		        
		 modelKeys.addAll(Arrays.asList(keys));
		        
		jsonView.setModelKeys(modelKeys);
		
		return jsonView;
		
	}
	
	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		
         for(int i=0; i < exceptionResolvers.size(); i++) {
			
			if(exceptionResolvers.get(i).getClass().equals(DefaultHandlerExceptionResolver.class))
				exceptionResolvers.set(i, new MyDefaultExceptionResolver());
         }
         
         HandlerExceptionResolver[] custom = {new NavigationExceptionResolver() };
         
         for(int i = 0; i < custom.length; i++)
        	 exceptionResolvers.add(0, custom[i]);
         
       //  System.out.println("Printing exceptionResolvers: ") ;
         
       /*  for(int i = 0; i < exceptionResolvers.size(); i++)
        	 System.out.println(exceptionResolvers.get(i).getClass().getSimpleName()); */
		
	}
	
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		
		for(int i = 0; i < converters.size(); i++) {
			
			if(converters.get(i).getClass().equals(MappingJackson2HttpMessageConverter.class))
				 
				converters.set(i, new MappingJackson2HttpMessageConverter(new MyObjectMapper()));
		}
		
		for(int i = 0; i < converters.size(); i++) {
			
			Class<?> cls = converters.get(i).getClass();  
			
			System.out.println("Converter=" + cls.getCanonicalName());
       	 
       	    if(cls.equals(MappingJackson2HttpMessageConverter.class)) {
       	    	
       	    	MappingJackson2HttpMessageConverter conv = 
    					(MappingJackson2HttpMessageConverter)converters.get(i); 
       	    	
       	    	ObjectMapper mapper = conv.getObjectMapper();
       	    	 
       	    	 System.out.println("Mapper: " + mapper.getClass().getCanonicalName());
       	    	 
       	    }
		}
		
		
	}
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
		
		registry.addFormatterForFieldAnnotation(new TextFormatAnnotationFormatterFactory());
		
		registry.addFormatter(new DateFormatter());
		
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		
		//registry.addViewController("support");
		
	}

	

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		
	  /*	HandlerExceptionResolver[] resolvers = { exceptionConfig.exceptionHandlerExceptionResolver(),
				exceptionConfig.myDefaultExceptionResolver()};
		
		exceptionResolvers.addAll(Arrays.asList(resolvers)); */
				
	}
	

	@Override
	public Validator getValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageCodesResolver getMessageCodesResolver() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
