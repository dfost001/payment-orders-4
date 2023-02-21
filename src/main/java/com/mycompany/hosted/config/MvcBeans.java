package com.mycompany.hosted.config;

import org.springframework.context.MessageSource;
//import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
//import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MvcBeans {
	
	    @Bean
	    public CommonAnnotationBeanPostProcessor commonAnnotationPostProcessor() {
	        
	        return new CommonAnnotationBeanPostProcessor();
	    }
	    
	   @Bean
	    public MessageSource messageSource() {
	        
	        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
	        
	        source.setBasenames("classpath:validationMessages/flowAddressValid");
	        
	        return source;
	    } 

}
