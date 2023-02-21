package com.mycompany.hosted.checkoutFlow.mvc.controller.paypal;

import javax.servlet.http.HttpSession;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutErrModel;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutHttpException;
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
				
		private final String errFatal = "An error occurred. Please contact support to complete your order.";		
		
		@GetMapping(value="paymentException/initErrorModel")
		public String  initModel(HttpSession session, ModelMap model) throws MvcNavigationException {					
			
			CheckoutHttpException ex = (CheckoutHttpException)session.getAttribute("checkoutHttpException");
			
			if(ex == null) //View is under cache-control, so static view with link to checkout cannot be requested.
				this.throwMvcNavigationException();
			
			CheckoutErrModel errModel = initErrorModel(ex);	
			
			if(errModel.isRecoverable()) {		
			
				  session.removeAttribute("checkoutHttpException");
			
			      errModel.setRetUrl(this.evalRecoverableUrl(errModel));
			}
			
			removeDetailsOnNotRecoverable(session,errModel);	
			
			model.addAttribute("checkoutErrModel", errModel);					
			
			return "jsp/checkoutErrSupport";
			
		}
		
		private void throwMvcNavigationException() throws MvcNavigationException {
			
            String err = EhrLogger.doMessage(this.getClass(), "initModel", 
            		"CheckoutHttpException is not in the session.");
			
			err += "Assuming browser navigation after exception removed";
			
			throw new MvcNavigationException(err);
			
		}
		
		private CheckoutErrModel initErrorModel(CheckoutHttpException ex) {
			
	       if(ex.isTestException()) {				
				
				return initTestRecoverable(ex);
			}
			CheckoutErrModel err = new CheckoutErrModel();
			
			Throwable cause = EhrLogger.getRootCause(ex.getCause());
			
			if(cause != null)	        
		           err.setCause(cause.getClass().getCanonicalName());			
		
			
			Integer status = null;	
			
			String contentType = "";
			
	        if(cause != null && cause.getClass() == HttpException.class) {
				
				status = ((HttpException)cause).statusCode();
				
				err.setResponseCode(status);
				
				contentType = this.doContentType((HttpException)cause);
				
				err.setContentType (contentType);
		    }  
	        else {
	        	err.setContentType("Content-Type only applicable for HttpException");
	        	err.setResponseCode(-1);
	        }
	        
	        if(contentType.toLowerCase().contains("html"))
	        	err.setMessageTrace(contentType); //For HTML, message will be the body
	        else
	        	err.setMessageTrace(EhrLogger.getMessages(ex, "<br />")); //Message trace
	        
	        
	        err.setMessage(ex.getMessage());
	       
	        //To do: eval status codes: 422
	        if(status != null && status == 503) {
	        	
	        	err.setRecoverable(true);
	        	
	        	err.setFriendly(errRecoverable);
	        } else {
	        	
	        	err.setRecoverable(false);
	        	
	        	err.setFriendly(errFatal);
	        }          
	        
	        err.setErrMethod(ex.getMethod());  	  	       
	        
	        if(err.getErrMethod().contains("refund"))
	        	err.setRecoverable(false); // Currently, there is no code to configure a link back to PaymentStatusController
	        
	      
	        
	        return err;
	   }
	 /*
	  * Note: If error occurs at getDetails there will be no PAYMENT_DETAILS to remove	
	  */
	 private void removeDetailsOnNotRecoverable(HttpSession session, CheckoutErrModel errModel)	{
		 
		 if(!errModel.isRecoverable() && errModel.getErrMethod().toLowerCase().contains("capture"))
			 
				session.removeAttribute(WebFlowConstants.PAYMENT_DETAILS);		 
	 }
		
	  private String doContentType(HttpException ex) {
		  
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
		
		private CheckoutErrModel initTestRecoverable(CheckoutHttpException ex) {
			
	        CheckoutErrModel err = new CheckoutErrModel();	
	        
	        Throwable cause = EhrLogger.getRootCause(ex);
	        
	        err.setCause(cause.getClass().getCanonicalName());
	        
	        err.setRecoverable(true);
	                	
	        err.setFriendly(errRecoverable); 
	        
	        err.setResponseCode(503);
	        
	        err.setContentType("NA (Only applicable for HttpException)");
	        
	        err.setMessage(ex.getMessage());
	        
	        err.setMessageTrace(EhrLogger.getMessages(ex, "<br />"));
	        
	        err.setErrMethod(ex.getMethod());
	        
	        return err;
			
		}	


}
