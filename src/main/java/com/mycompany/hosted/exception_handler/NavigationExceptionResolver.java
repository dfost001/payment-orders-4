package com.mycompany.hosted.exception_handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

public class NavigationExceptionResolver extends AbstractHandlerExceptionResolver {	

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, 
			HttpServletResponse response, Object handler, Exception ex) {
		
		if(!MvcNavigationException.class.isAssignableFrom(ex.getClass()))
			return null;
		
		String url = request.getRequestURL().toString();
		
		String viewName = "jsp_error/errMvcNavigation";		
		
		ModelAndView mav = EhrLogger.initModelAndView(url, viewName, ex, 
				this.getClass().getCanonicalName(), 
				handler.toString());		
		
		mav.addObject("message", customizeMessage(url));
		
		
		return mav;
	}
	
	private String customizeMessage(String url) {
		
		String refundMsg = "A refund cannot be issued while a transaction is in progress. "
				+ "Cancel the current transaction and contact support.";
		
		String expiredMsg = "Navigation to a view that is no longer current." ;
		
		if(url.toLowerCase().contains("refund")) {
			
			return refundMsg;
			
		} else return expiredMsg;
		
		
		
	}

}
