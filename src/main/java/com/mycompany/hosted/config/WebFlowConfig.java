package com.mycompany.hosted.config;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import org.springframework.webflow.mvc.servlet.FlowHandlerMapping;
import org.springframework.webflow.mvc.servlet.FlowHandler;
import org.springframework.webflow.mvc.servlet.FlowHandlerAdapter;
import org.springframework.webflow.config.AbstractFlowConfiguration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.executor.FlowExecutor;

import com.mycompany.hosted.checkoutFlow.MyFlowHandler;
import com.mycompany.hosted.formatter.DateFormatter;
import com.mycompany.hosted.formatter.TextFormatAnnotationFormatterFactory;

@Configuration
public class WebFlowConfig extends AbstractFlowConfiguration {	
	
	
	@Bean
	public FlowExecutor flowExecutor() {
		
	    return getFlowExecutorBuilder(flowRegistry())	    		
	    		.setMaxFlowExecutionSnapshots(0)
	    		.build(); 
		
		// return getFlowExecutorBuilder(flowRegistry()).build();
	}
	
	@Bean
	public FlowDefinitionRegistry flowRegistry() {
		
		String pattern = "/WEB-INF/flows/**/*-flow.xml";
	
	    return getFlowDefinitionRegistryBuilder(flowBuilderServices())
	    		
	        .addFlowLocationPattern(pattern)
	        
	        .build();
	}  
	
	@Bean
	public FlowBuilderServices flowBuilderServices() {
	    return getFlowBuilderServicesBuilder() 
	    		
	    		.setConversionService(conversionService())
	            
	            .setValidator(validator())
	           
	            .build();
	}
	
	@Bean
	public ConversionService conversionService() {
		
		FormattingConversionService formattingSvc = new DefaultFormattingConversionService(); //MVC
		
		addFormatters(formattingSvc);
		
		ConversionService conversionSvc = new DefaultConversionService(formattingSvc); //WebFlow
		
		return conversionSvc;
	    
	}
	
   private void addFormatters(FormatterRegistry registry) {
		
		registry.addFormatterForFieldAnnotation(new TextFormatAnnotationFormatterFactory());
		
		registry.addFormatter(new DateFormatter());
		
	}
   
   @Bean
   public LocalValidatorFactoryBean validator() {
	   
	  return new LocalValidatorFactoryBean() ;
   }
	
	  @Bean
	   public FlowHandlerMapping flowHandlerMapping() {
		   
		   FlowHandlerMapping handlerMapping = new FlowHandlerMapping();
		   
		   handlerMapping.setOrder(0); //WebFlow request will take precedence
		   
		   handlerMapping.setFlowRegistry(flowRegistry());
		   
		   return handlerMapping;
	   }
	   
	   @Bean
	   public FlowHandlerAdapter flowHandlerAdaptor() {
		   
		   FlowHandlerAdapter adapter = new FlowHandlerAdapter();
		   
		   adapter.setFlowExecutor(flowExecutor());
		   
		   return adapter;
		   
	   }
	   
	   @Bean(name= {"checkout-flow"})
	   
	   public FlowHandler flowHandler() {
		   
		   return new MyFlowHandler();
	   }
	
	
	
   
 

} //end config
