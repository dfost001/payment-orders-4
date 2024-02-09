package com.mycompany.hosted.checkoutFlow.mvc.controller.paypal;



import java.io.IOException;
import java.net.ConnectException;

import java.net.UnknownHostException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutErrModel;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutHttpException;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PayPalErrorDetail;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PayPalErrorResponse;
import com.mycompany.hosted.checkoutFlow.servlet_context.ServletContextAttrs;
import com.mycompany.hosted.checkoutFlow.servlet_context.OrderAttributes;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.exception_handler.MvcNavigationException;
import com.paypal.http.Headers;
import com.paypal.http.exceptions.HttpException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/*
 * Fixed: If CheckoutException is not in session, throw UserNavigation
 * To do: Give HttpCheckoutException a property for the Response status.
 * Then, if IllegalArg or some other type we will have the status.
 */

@Controller
public class PaymentExceptionController {	  
	
	  @Autowired
	  private ServletContextAttrs servletContextAttrs;
	
	  public static final String ERR_GET_DETAIL = "ERR_GET_DETAIL";	
	  
	  public static final String ERR_ON_CAPTURE = "ERR_ON_CAPTURE";
	  
	  /*
	   * Strings used by backing-beans to create the HttpCheckoutException
	   * Used to assignFriendly
	   * To do: Change to a public enum and revise backing-beans
	   */
	  private final String CREATE = "create";
	  private final String GET_DETAILS = "getOrder";
	  private final String CAPTURE = "capture" ;
	  private final String REFUND = "refund" ; 
	  
	  private final String FULLY_REFUNDED = "CAPTURE_FULLY_REFUNDED";
		
		private final String errRecoverable = "The payment service is temporarily unavailable. ";
				
		private final String errFatal = "A non-recoverable error occurred.";		
		
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
			
			if(errModel.getErrMethod().contentEquals(REFUND))
				this.prepareModelOrderAttributes(model, ex.getPersistOrderId());
			
			return "jsp/checkoutErrSupport";
			
		}
		
		
		
		private void throwMvcNavigationException() throws MvcNavigationException {
			
            String err = EhrLogger.doMessage(this.getClass(), "initModel", 
            		"CheckoutHttpException is marked as expired after a recoverable was processed.");
			
			err += "Most likely cause is browser navigation or a book-marked URL.";
			
			throw new MvcNavigationException(err);
			
		}
		
		private CheckoutErrModel initErrorModel(CheckoutHttpException ex, String id) {
			
	      
			CheckoutErrModel err = new CheckoutErrModel();
			
			err.setException(ex); //Make Id fields accessible to EL
			
			err.setUuid(id);
			
			err.setErrMethod(ex.getMethod()); 
			
			err.setMessage(ex.getMessage());
			
            Throwable cause = EhrLogger.getRootCause(ex.getCause());
			
			 String exceptionName = cause != null ? cause.getClass().getCanonicalName()
					: "No inner cause"; // Should always have a cause since exception is a wrapper
			
			err.setCause(exceptionName);	
			
			if(ex.isTestException())
				
				err.setResponseCode(503);
			
			else err.setResponseCode(ex.getResponseStatus());  
			
			setRecoverable(err,cause, err.getResponseCode()) ;
			
			if(err.getErrMethod().contains("refund"))
		        	err.setRecoverable(false); // Currently, a link back to PaymentStatusController is not configured
	        
	        initContentType(err, cause);
	        
	        initMessageTraceByContent(err, ex, err.getErrContentType()); //Contains content-type or the trace	       
	        
	        assignFriendly(err) ;	      
		
			return err;
	        
	       
	   }
		
	private void initContentType(CheckoutErrModel err,  Throwable cause)	{
		
		String contentType = "";
		
		//Content-Type is labeled as Error Content-Type
		
        if(cause != null && cause.getClass() == HttpException.class) {			
			
			contentType = this.getContentType((HttpException)cause);				
	    } 
        else contentType = "Only available for HttpException failed status" ;
      
        err.setErrContentType(contentType);         
		
	}
	
	private void initMessageTraceByContent(CheckoutErrModel model, Exception ex, String contentType) {
		
		 if(contentType.toLowerCase().contains("html"))
	        	model.setMessageTrace(contentType); //For HTML, message will be the body
	      else
	        	model.setMessageTrace(EhrLogger.getMessages(ex, "<br />")); //Message trace  
	}
	
	private void setRecoverable(CheckoutErrModel model, Throwable cause, int httpStatus) {
		
		switch (httpStatus) {
		
		case 503 :
			
		    model.setRecoverable(true);
		    break;
		    
		case -1: //Can be more specific such as ConnectException, NoRouteToHost
			
			if(cause != null && (ConnectException.class.isAssignableFrom(cause.getClass())
					|| UnknownHostException.class.isAssignableFrom(cause.getClass())))
				model.setRecoverable(true);			
			
			else model.setRecoverable(false) ; 
			break;
			
		default :
			
			model.setRecoverable(false); //IllegalArgument; status may be 200
		
		}
		
	}
	
	private void assignFriendly(CheckoutErrModel model) {
		
		String friendly = model.isRecoverable() ? this.errRecoverable : this.errFatal;
		String label = "";
		
		switch (model.getErrMethod()) {
		
		case CREATE :
			label = "Submit Card: " ;
			break; 
		case GET_DETAILS:
			label = "Review Details: ";
			break;
		case CAPTURE:
			label = "Finalize Payment: ";
			break;
		case REFUND:			
			
			boolean refunded = false;
			
			label = "Refund Payment: ";
			
			if(model.getResponseCode().equals(422)) {
				
				refunded = deserializeError(model.getException().getCause());
				
				if(refunded)
				  friendly = "Your refund has already been issued. This is a duplicate request. ";
			} 
		}
		
		model.setFriendly(label + friendly);
	}
	
	private boolean deserializeError(Throwable ex) {
		
		if(!HttpException.class.isAssignableFrom(ex.getClass()))
			return false;
		
		ObjectMapper mapper = new ObjectMapper();
		
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); //All fields not defined by Java class 
		
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE); //Json field to Java
		
		PayPalErrorResponse err = null;
		
		try {
			 err = mapper.readValue(ex.getMessage(), 
					PayPalErrorResponse.class);
		} catch (IOException e) {
			
			System.out.println("ExceptionController#deserializeError: IOException: " + e.getMessage());
			
			return false;
		}
		List<PayPalErrorDetail> details = err.getDetails();
		
		if(details == null || details.isEmpty()) {
			System.out.println("PaymentExceptionController#deserializeError: details are null: returning false");
			return false;	
		} else {
			System.out.println("PaymentExceptionController#deserializeError: details are NOT null");
			System.out.println("Issue: " + details.get(0).getIssue() );
		}	
		
		if(details.get(0).getIssue().contentEquals(FULLY_REFUNDED))
		     return true;
		
		return false;
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
		
	    private void prepareModelOrderAttributes(ModelMap model, Integer orderId) {
	    	
	    	OrderAttributes orderAttrs = servletContextAttrs.getOrderAttributes(orderId);
	    	
	    	model.addAttribute("selectedAddress", orderAttrs.getShipTo());
	    	
	    	model.addAttribute("customer", orderAttrs.getBillTo());
	    	
	    	model.addAttribute("cart", orderAttrs.getCartAttrs());
	    }


}
