package com.mycompany.hosted.exception_handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.mycompany.hosted.exception_handler.EhrLogger;


@ControllerAdvice(basePackages= {"com.mycompany.hosted.controller"})
public class ControllerExceptionAdvice {
	
	@ExceptionHandler(value=RuntimeException.class)	
	public ModelAndView handleException(Exception ex, HttpServletRequest req) {

		String url = req.getRequestURL().toString();

		String viewName = "jsp_error/applicationException";

		ModelAndView mav = EhrLogger.initModelAndView(url, viewName, ex, this.getClass().getCanonicalName(),
				"@ControllerAdvice(basePackages={com.mycompany.hosted.controller})");

		return mav;

	}

} //end class
