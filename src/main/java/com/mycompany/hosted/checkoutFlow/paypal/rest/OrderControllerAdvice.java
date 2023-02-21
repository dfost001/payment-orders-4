package com.mycompany.hosted.checkoutFlow.paypal.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutHttpException;
import com.mycompany.hosted.exception_handler.EhrLogger;

@ControllerAdvice(basePackageClasses = {OrderController.class})
public class OrderControllerAdvice {
	
	@ExceptionHandler(value=CheckoutHttpException.class)
	public ModelAndView handleCheckoutException(Exception ex, 
			HttpServletRequest request,
			HttpServletResponse response) {
		
		request.getSession().setAttribute("checkoutHttpException", ex);
		
		String view = "";
		
		ModelAndView mav = EhrLogger.initModelAndView(request.getRequestURL().toString(),
				view, ex, 
				this.getClass().getCanonicalName(),
				OrderController.class.getCanonicalName());
		
		response.setStatus(500);		
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		
		return mav;
		
	}

}
