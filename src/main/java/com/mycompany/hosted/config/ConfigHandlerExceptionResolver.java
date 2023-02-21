package com.mycompany.hosted.config;
//import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import com.mycompany.hosted.exception_handler.MyDefaultExceptionResolver;

//@Configuration
//Not used: see extendExceptionResolvers
public class ConfigHandlerExceptionResolver {
	
	 public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
	        
	        return new ExceptionHandlerExceptionResolver();
	 }
	 
	 public MyDefaultExceptionResolver myDefaultExceptionResolver() {
		 
		 return new MyDefaultExceptionResolver();
	 }

}
