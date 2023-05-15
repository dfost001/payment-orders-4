package com.mycompany.hosted.checkoutFlow.mvc.controller.paypal;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutErrModel;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutHttpException;
import com.mycompany.hosted.checkoutFlow.servlet_context.ServletContextAttrs;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.exception_handler.MvcNavigationException;
import com.paypal.http.Headers;
import com.paypal.http.exceptions.HttpException;

/*
 * Fixed: If CheckoutException is not in session, throw UserNavigation
 * To do: Give HttpCheckoutException a property for the Response status.
 * Then, if IllegalArg or some other type we will have the status.
 */

@Controller
public class PaymentExceptionController {	  
	
	  public static final String ERR_GET_DETAIL = "ERR_GET_DETAIL";	
	  
	  public static final String ERR_ON_CAPTURE = "ERR_ON_CAPTURE";
		
		private final String errRecoverable = "The payment service is temporarily unavailable. ";
				
		private final String errFatal = "A non-recoverable error occurred. Please contact support to complete your order.";		
		
		@GetMapping(value="paymentException/initErrorModel")
		public String  initModel(@RequestParam(WebFlowConstants.CHECKOUT_EXCEPTION_REQUEST_PARAM)
		   String id, HttpServletRequest request, ModelMap model) throws MvcNavigationException {					
			
			CheckoutHttpException ex = ServletContextAttrs.getException(id);
			
			if(ex.isExpired())
				this.throwMvcNavigationException();
			
			CheckoutErrModel errModel = initErrorModel(ex, id);				
			
			if(errModel.isRecoverable()) {		
			
				  ex.setExpired(true);
			
			      errModel.setRetUrl(this.evalRecoverableUrl(errModel));
			}
			
			removeDetailsOnNotRecoverable(request.getSession(),errModel);	
			
			model.addAttribute("checkoutErrModel", errModel);					
			
			return "jsp/checkoutErrSupport";
			
		}
		
		
		
		private void throwMvcNavigationException() throws MvcNavigationException {
			
            String err = EhrLogger.doMessage(this.getClass(), "initModel", 
            		"CheckoutHttpException is marked as expired after a recoverable was processed.");
			
			err += "Most likely cause is browser navigation or a book-marked URL.";
			
			throw new MvcNavigationException(err);
			
		}
		
		private CheckoutErrModel initErrorModel(CheckoutHttpException ex, String id) {
			
	       if(ex.isTestException()) {				
				
				return initTestRecoverable(ex, id);
			}
			CheckoutErrModel err = new CheckoutErrModel();
			
			err.setException(ex);
			
			err.setUuid(id);
			
			err.setErrMethod(ex.getMethod()); 
			
            Throwable cause = EhrLogger.getRootCause(ex.getCause());
			
			/* String exceptionName = cause != null ? cause.getClass().getSimpleName()
					: "No inner cause"; -- Will always have a cause*/
			
			err.setCause(cause.getClass().getCanonicalName());	
			
			err.setResponseCode(ex.getResponseStatus());  		
	        
	        err.setMessage(ex.getMessage());
	        
	        initMessageTraceByContent(err, ex, cause);
	       
	        //To do: eval status other codes: 422
	        if(ex.getResponseStatus().equals(503)) {
	        	
	        	err.setRecoverable(true);
	        	
	        	err.setFriendly(errRecoverable);
	        } else {
	        	
	        	err.setRecoverable(false);
	        	
	        	err.setFriendly(errFatal);
	        }         	        	  	       
	        
	        if(err.getErrMethod().contains("refund"))
	        	err.setRecoverable(false); // Currently, there is no code to configure a link back to PaymentStatusController
		
			return err;
	        
	       
	   }
		
	private void initMessageTraceByContent(CheckoutErrModel err, Exception ex, Throwable cause)	{
		
		String contentType = "";
		
		//Content-Type is labeled as Error Content-Type
		
        if(cause != null && cause.getClass() == HttpException.class) {			
			
			contentType = this.getContentType((HttpException)cause);				
	    } 
        else contentType = "Only available for HttpException failed status" ;
      
        err.setErrContentType(contentType);
        
        if(contentType.toLowerCase().contains("html"))
        	err.setMessageTrace(contentType); //For HTML, message will be the body
        else
        	err.setMessageTrace(EhrLogger.getMessages(ex, "<br />")); //Message trace
        
		
	}
	 /*
	  * Note: If error occurs at getDetails there will be no PAYMENT_DETAILS to remove	
	  */
	 private void removeDetailsOnNotRecoverable(HttpSession session, CheckoutErrModel errModel)	{
		 
		 if(!errModel.isRecoverable() && errModel.getErrMethod().toLowerCase().contains("capture"))
			 
				session.removeAttribute(WebFlowConstants.PAYMENT_DETAILS);		 
	 }
		
	  private String getContentType(HttpException ex) {
		  
		  Headers headers = ex.headers();
		  
		  String value = headers.header(Headers.CONTENT_TYPE);
		  
		  if(value == null)
			 
			 return "Empty Content-Type header";
		  
		  return value;	 
		  
	  }
		
	   private String evalRecoverableUrl(CheckoutErrModel errModel) {		   
			
			String url = "checkout-flow";
			
			if (errModel.isRecoverable()) {
				
				if( errModel.getErrMethod().contains("getOrder"))
					url += "?" + ERR_GET_DETAIL + "=true" ;
				
				else if(errModel.getErrMethod().contains("capture"))
					url += "?" + ERR_ON_CAPTURE + "=true" ;
			}			
			
			return url;
		}
		
		private CheckoutErrModel initTestRecoverable(CheckoutHttpException ex, String id) {
			
	        CheckoutErrModel err = new CheckoutErrModel();	
	        
	        Throwable cause = EhrLogger.getRootCause(ex);
	        
	        err.setException(ex);
	        
	        err.setCause(cause.getClass().getCanonicalName());
	        
	        err.setRecoverable(true);
	                	
	        err.setFriendly(errRecoverable); 
	        
	        err.setResponseCode(new Integer(503));
	        
	        err.setErrContentType("Only applicable for non-testing HttpException");
	        
	        err.setMessage(ex.getMessage());
	        
	        err.setMessageTrace(EhrLogger.getMessages(ex, "<br />"));
	        
	        err.setErrMethod(ex.getMethod());
	        
	        err.setUuid(id);
	        
	        return err;
			
		}	


}
