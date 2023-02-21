package com.mycompany.hosted.exception_handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;



public class MyDefaultExceptionResolver extends DefaultHandlerExceptionResolver {

	 @Override
	    public ModelAndView doResolveException(HttpServletRequest req,
	        HttpServletResponse resp, Object handler, Exception ex) {  
		 
		 String url = req.getRequestURL().toString();
		 
		 String view = "jsp_error/applicationException" ;
		 
		 ModelAndView mav = EhrLogger.initModelAndView(url, view, ex, 
				 this.getClass().getCanonicalName(), handler.toString());
		 
		 
		 
		 return mav;
		 
	 }

}
