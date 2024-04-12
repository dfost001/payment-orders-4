package com.mycompany.hosted.checkoutFlow;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;

//import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;

import com.mycompany.hosted.errordetail.ErrorDetailBean;

public class WebFlowConstants {
	
	public static final String CART = "cart";
	
	public static final String CUSTOMER_KEY = "customer";
	
	public static final String SELECTED_POSTAL_ADDR = "selectedAddress";
	
	public static final String ORDER_ENTITY_VALUE = "order";
	
	public static final String CUSTOMER_UNIT = "springMvcSample"; 
	
	public static final String SUPPORT_UNIT = "supportedValidation";
	
	public static final String PAYMENT_DETAILS = "details";	
	
	public static final String PAYPAL_SERVER_ID = "serverId";
	
	public static final String PAYPAL_SCRIPT_ID = "scriptId";
	
	
	//Exception stored in ServletContext using this key
	public static final String CHECKOUT_HTTP_EXCEPTION = "checkoutHttpException";
	
	//Written by MyFlowHandler, Read by PaymentExceptionController
	public static final String CHECKOUT_EXCEPTION_REQUEST_PARAM = "id";
	
	public static final String ERROR_DETAIL_BEAN = "errorDetailBean";
	
	public static final String ERROR_DETAIL_MAP = "errorDetailMap";
	
	public static final String ERR_GET_DETAIL = "ERR_GET_DETAIL";
	
	public static final String ERR_ON_CAPTURE = "ERR_ON_CAPTURE";
	
	//Written into the ServletContext. Read by PaymentExceptionController.
	public static final String ORDER_ATTRIBUTES = "orderAttributes";
	
 
    /*
     * Invoked from Webflow component
     */
    public static ErrorDetailBean errorBeanFromServletContext(RequestContext requestCtx) {
    	
    	ServletContext sc = (ServletContext) requestCtx.getExternalContext().getNativeContext();
    	
	    ErrorDetailBean errorDetailBean = (ErrorDetailBean) sc.getAttribute("errorDetailBean");
		
		if(errorDetailBean == null) {			
		
			errorDetailBean = new ErrorDetailBean();
		
		    sc.setAttribute(ERROR_DETAIL_BEAN, errorDetailBean);
		}
		return errorDetailBean;
    }
	
    /*
     * Overloaded for MVC Controller
     */
    public static ErrorDetailBean errorBeanFromServletContext(HttpServletRequest request) {
 
    	ServletContext sc = (ServletContext) request.getServletContext();
    	
	    ErrorDetailBean errorDetailBean = (ErrorDetailBean) sc.getAttribute("errorDetailBean");
		
		if(errorDetailBean == null) {			
		
			errorDetailBean = new ErrorDetailBean();
		
		    sc.setAttribute(ERROR_DETAIL_BEAN, errorDetailBean);
		}
		return errorDetailBean;
    }
}
