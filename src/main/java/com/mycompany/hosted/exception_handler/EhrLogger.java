package com.mycompany.hosted.exception_handler;

import java.text.MessageFormat;

import org.springframework.web.servlet.ModelAndView;

import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutHttpException;

import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;

public class EhrLogger {
	
    public static CheckoutHttpException initCheckoutException(Exception ex, String method, 
    		HttpResponse<?> response, String payPalId, Integer persistOrderId)	{
    	
    	CheckoutHttpException checkoutEx = new CheckoutHttpException(ex, method);
    	
    	checkoutEx.setPersistOrderId(persistOrderId);    	
    	
    	checkoutEx.setPayPalId(payPalId);
    	
    	if(response != null)
    		checkoutEx.setResponseStatus(response.statusCode());
    	else if(HttpException.class.isAssignableFrom(ex.getClass())) {
    		Integer status = ((HttpException)ex).statusCode();
    		checkoutEx.setResponseStatus(status);
    	}   	
    	
    	return checkoutEx;
    	
    }
	
	 public static void throwNullValues(Class<?> handler, String method, String[] titles,
			 Object...objects) {
		 
		 String err = "";
		 
		 for(int i=0; i < objects.length; i++) {
			 
			 if(objects[i] == null)
				 err += titles[i ]+ " is null. ";
				 
		 }
		 
		 if(!err.isEmpty())
			 EhrLogger.throwIllegalArg(handler, method, err);
		 
	 }
	
	 public static String getStackTrace(Throwable t, String description, String delim){
	        
	        String trace = description + delim;
	        
	        for(StackTraceElement el : t.getStackTrace()){
	            String line = MessageFormat.format("{0}.{1} ({2}:{3})", 
	                    el.getClassName(),el.getMethodName(),el.getFileName(),el.getLineNumber());
	            trace += line + delim;
	        }
	        
	        return trace;
	        
	    }
	    
	    public static String getMessages(Throwable throwable, String delim) {
	        
	        Throwable t = throwable;
	        
	        String msg = "";
	        
	        while(t != null) {
	           
	            String line = MessageFormat.format("{0} -> {1}", 
	                    t.getClass().getSimpleName(), t.getMessage());
	            
	            msg += line + delim;
	            t = t.getCause();
	        }
	        
	        return msg;
	        
	    }
	    
	    public static Throwable getRootCause(Throwable t) {
	    	
	    	Throwable hold = t;
	    	Throwable cause = t;
	    	while(cause != null) {
	    		hold = cause;
	    		cause = cause.getCause();
	    	}
	    	    
	    	return hold;
	    }
	    
	    public static Throwable getCause(Throwable t, Class<?> searchType) {	    	    	
	    	
	    	Throwable cause = t;
	    	
	    	while(cause != null) {	   
	    		
	    		System.out.println("EhrLogger#getCause: " + cause.getClass().getSimpleName());
	    		
	    		if(cause.getClass().equals(searchType))
	    			return cause;
	    		
	    		cause = cause.getCause();
	    	}
	    	
	    	return null;
	    }
	    
	    public static String formatError(String controller, String method, String message) {
	    	
	    	return controller + "#" + method + ": " + message;
	    }
	    
	    public static ModelAndView initModelAndView(String url, String viewName,
	    		Throwable ex, String resolver, String handler) {
	    	
	    	ModelAndView mav = new ModelAndView(viewName);
	    	
	    	 String trace = EhrLogger.getStackTrace(ex, url, "<br/>");
	 	    
		        String messages = EhrLogger.getMessages(ex, "<br/>");
		        
		        Throwable rootCause = EhrLogger.getRootCause(ex);  
		        
		        System.out.println("EhrLogger#initModelAndView: rootCause=" + rootCause);	
		        
		        System.out.println("EhrLogger#initModelAndView: resolver=" + resolver);
		        
		        System.out.println("EhrLogger#initModelAndView: handler=" + handler);
		        
		        System.out.println("EhrLogger#initModelAndView: message=" + ex.getMessage());
		       
		         mav.addObject("exception", ex);
		         mav.addObject("url", url);
		         mav.addObject("trace",trace);
		         mav.addObject("messages", messages);
		         mav.addObject("handler", handler);
		         mav.addObject("rootCause", rootCause);
		         mav.addObject("exceptionResolver", resolver);
		         mav.addObject("exceptionType", ex.getClass().getCanonicalName());
	    	
	    	return mav;
	    	
	    }
	    
	    public static String doMessage(Class<?> handler, String method, String message) {
	    	
	    	return handler.getCanonicalName() + "#" + method + ": " + message;
	    }
	    
	    public static void consolePrint(Class<?> handler, String method, String message) {
	    	
	    	String line = handler.getCanonicalName() + "#" + method + ": " + message;
	    	
	    	System.out.println(line);
	    	
	    }
	    
	    public static void throwIllegalArg(Class<?> handler, String method, String message) {
	    	
	    	String err = handler.getCanonicalName() + "#" + method + ": " + message;
	    	
	    	throw new IllegalArgumentException(err);
	    }
	    
       public static void throwIllegalArg(Class<?> handler, String method, String message, Throwable cause) {
	    	
	    	String err = handler.getCanonicalName() + "#" + method + ": " + message;
	    	
	    	throw new IllegalArgumentException(err, cause);
	    }
	    
	}

