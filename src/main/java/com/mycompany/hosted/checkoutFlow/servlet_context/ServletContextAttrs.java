package com.mycompany.hosted.checkoutFlow.servlet_context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.springframework.webflow.execution.RequestContext;

import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutHttpException;
import com.mycompany.hosted.exception_handler.EhrLogger;

@Component
public class ServletContextAttrs implements ServletContextAware {
	
	private static ServletContext sc;

	@Override
	public void setServletContext(ServletContext servletContext) {
		
		sc = servletContext;
		
	}
	
	@SuppressWarnings("unchecked")
	public static String setException(CheckoutHttpException ex) {
		
		
		Map<String, CheckoutHttpException> map = (Map<String, CheckoutHttpException>)
				 sc.getAttribute(WebFlowConstants.CHECKOUT_HTTP_EXCEPTION);
		 
		 if(map == null) {
			 map = new LinkedHashMap<>();
			 sc.setAttribute(WebFlowConstants.CHECKOUT_HTTP_EXCEPTION, map);
		 }		 
		 
		 String id = UUID.randomUUID().toString();
		 
		 try {
			id =  URLEncoder.encode(id, "UTF-8");
		 } catch (UnsupportedEncodingException e) {}
		 
	     map.put(id, ex) ;
		 
		 return id;
		
	}
	
	@SuppressWarnings("unchecked")
	public static CheckoutHttpException getException(String key) {
		
		Map<String, CheckoutHttpException> map = 
				(Map<String, CheckoutHttpException>) sc.getAttribute(WebFlowConstants.CHECKOUT_HTTP_EXCEPTION);
		
		if(map == null)
			EhrLogger.throwIllegalArg(ServletContextAttrs.class, "getException", 
					"Map<String, CheckoutHttpException> is not found in ServletContext");
		
		CheckoutHttpException ex = map.get(key) ;
		
		if(ex == null)
			EhrLogger.throwIllegalArg(ServletContextAttrs.class, "getException", 
					"CheckoutHttpException keyed by id " + key + " is not the map.");		
		
		return ex;			
		
	}
	
	@SuppressWarnings("unchecked")
	public void setOrderAttributes(RequestContext request, Integer orderId) {
		
		OrderAttributes orderAttrs = OrderAttributesUtil.initOrderAttrs(request);
		
		Map<String, OrderAttributes> map = (Map<String, OrderAttributes>)
				 sc.getAttribute(WebFlowConstants.ORDER_ATTRIBUTES);
		 
		 if(map == null) {
			 map = new LinkedHashMap<>();
			 sc.setAttribute(WebFlowConstants.ORDER_ATTRIBUTES, map);
		 }		 
		 
		 map.put(orderId.toString(), orderAttrs) ;
				
		
	}
	
	@SuppressWarnings("unchecked")
	public OrderAttributes getOrderAttributes(Integer orderId) {
		
		Map<String,OrderAttributes> map = (Map<String, OrderAttributes>)
				sc.getAttribute(WebFlowConstants.ORDER_ATTRIBUTES);
		
		OrderAttributes orderAttrs = map.get(orderId.toString());
		
		if(orderAttrs == null)
			EhrLogger.throwIllegalArg(ServletContextAttrs.class, "getOrderAttributes", 
					"OrderAttributes keyed by orderId " + orderId + " is not the map.");	
		
		return orderAttrs;
	}

}
