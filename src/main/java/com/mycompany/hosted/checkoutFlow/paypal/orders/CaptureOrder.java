package com.mycompany.hosted.checkoutFlow.paypal.orders;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.execution.RequestContext;

import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.PaymentCollection;

import com.paypal.orders.ProcessorResponse;
import com.paypal.orders.PurchaseUnit;
//import com.paypal.orders.AddressPortable;
//import com.paypal.orders.LinkDescription;
import com.paypal.orders.Capture;

import com.mycompany.hosted.checkoutFlow.PaymentObjectsValidator;
import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutHttpException;
import com.mycompany.hosted.checkoutFlow.exceptions.ProcessorResponseNullException;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails.CaptureStatusEnum;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails.FailedReasonEnum;
import com.mycompany.hosted.exception_handler.EhrLogger;
//import com.paypal.orders.Payer;
import com.paypal.http.HttpResponse;


/*
 * Note: Not able to test FailedReasonEnum
 */

@Component
public class CaptureOrder {
	
	@Autowired
	private PayPalClient payClient;
	
	boolean testException = false;
	
	public String capture(RequestContext ctx) throws CheckoutHttpException  {		
		 
		 if(testException) {		 
		 
			  doTestException(ctx);				
		 }		
		
		 PaymentDetails details = (PaymentDetails)ctx.getExternalContext()
			       .getSessionMap()
			       .get(WebFlowConstants.PAYMENT_DETAILS);
		 
		String statusResult = "";
		 
		 try {
		 
		evalDetails(details);	//throws to catch-block for null object or empty resource id	
		
	    OrdersCaptureRequest request = new OrdersCaptureRequest(details.getPayPalResourceId());
	    
	    request.prefer("return=representation");	   
	    
	    request.requestBody(buildRequestBody());
	   
	    HttpResponse<Order> response = payClient.client().execute(request); //throws IOException	    
	       
	    System.out.println("CaptureOrder#Status Code: " + response.statusCode());		   
	   
	    Order order = response.result();	   
	    
	    debugPrintOrder(order); //throws Runtime for uninitialized fields to catch block
	    
	    String json = GetOrderDetails.debugPrintJson(response);	    
	    
	    statusResult = initCaptureId(order, details,json); //"success" or "failed"
	    
		} catch(IOException | RuntimeException | ProcessorResponseNullException e) {
			
			 CheckoutHttpException httpEx = new CheckoutHttpException(e, "capture");
		    	
		    	ctx.getExternalContext()
		    	   .getSessionMap()
		    	   .put("checkoutHttpException", httpEx);
		    	
		    	throw httpEx;
		}
	    
	    return statusResult;	   
	   
	  }

	  
	  private void doTestException(RequestContext ctx) throws CheckoutHttpException {
		  
		  testException = false; 
		    
		    CheckoutHttpException ex = new CheckoutHttpException(new Exception("Testing Exception"),
		    		"capture");
		    
		    ex.setTestException(true);
		    
		    ctx.getExternalContext()
	    	   .getSessionMap()
	    	   .put("checkoutHttpException", ex);
		    
			throw ex;
		  
	  }
	
	  private OrderRequest buildRequestBody() {
	    return new OrderRequest();
	  }
	  
	  private void evalDetails(PaymentDetails details) {
		  
		  String err = PaymentObjectsValidator.validateDetailsBeforeCapture(details);
		  
		  if(!err.isEmpty())	
			 EhrLogger.throwIllegalArg(this.getClass(), "evalDetails", err);
		
	  }	
	  
	  private void debugPrintOrder(Order order) {
		  
		  if(order == null)
		    	this.throwIllegalArg("Order returned in HttpResponse is null.");	
		  
		  String err = "Order contains uninitialized fields: ";
		  
		   System.out.println("Printing captured order: ");
		  
		   System.out.println("Status: " + order.status()); //May not be initialized
		      
		   System.out.println("Order ID: " + order.id());
		   
		   if(order.paymentSource() == null)
	        	System.out.println("paymentSource is null"); 		      
		 
		   
		   List<PurchaseUnit> units = order.purchaseUnits();
		   
		   if(units == null) {
			   err += " Order#List<PurchaseUnit> ";
			   System.out.println(err);
			   throwIllegalArg(err);
		   }   
		      
		   PurchaseUnit purchaseUnit = order.purchaseUnits().get(0);
		   
		   if(purchaseUnit == null) {
			   err += " Order#PurchaseUnit ";
			   System.out.println(err);
			   throwIllegalArg(err);
		   }
		      
		   PaymentCollection payments = purchaseUnit.payments();
		   
		   if(payments == null) {
			   err += " Order#PaymentCollection ";
			   System.out.println(err);
			   throwIllegalArg(err);
		   }
		      
		   List<Capture> captures = payments.captures();
		      
		   if(captures == null || captures.isEmpty()) {
               err += " List<Capture> "	;	
               System.out.println(err);
			   throwIllegalArg(err);
		   }
		   
		 
		     
	  }
	  
	  private String initCaptureId(Order order, PaymentDetails details, String json) 
	         throws ProcessorResponseNullException {				  
		  
		  PurchaseUnit purchaseUnit = order.purchaseUnits().get(0);
	      
	      PaymentCollection payments = purchaseUnit.payments();
		  
	      List<Capture> captures = payments.captures();	      
	      
	      Capture capture = captures.get(0);	
	      
	      details.setCaptureStatus(CaptureStatusEnum.valueOf(capture.status()));
    	  
    	  details.setTransactionId(capture.id());
	      
	      details.setCaptureTime(capture.createTime());	
	      
	      details.setJson(json);
	      
	      details.setProcessorResponse(capture.processorResponse());
	      
	      debugPrintProcessorResponseOrThrow(capture); 
	      
	      System.out.println(MessageFormat.format("{0}: capture.status={1} transId={2}", 
	    		  "CaptureOrder#initCaptureId", capture.status(), capture.id()));	      
	      
	      if(capture.captureStatusDetails() != null) {	    	  
	    	 
	    	  String reason = capture.captureStatusDetails().reason();
	    	 
	    	  details.setStatusReason(FailedReasonEnum.valueOf(reason));	 
	    	  
	    	  System.out.println(MessageFormat.format("{0}: captureStatusDetails={1}", 
		    		  "CaptureOrder#initCaptureId", reason));
	    	  
	    	  return "failed";
	    	  
	      } else if(details.getCaptureStatus().equals(CaptureStatusEnum.DECLINED)
	    		  || details.getCaptureStatus().equals(CaptureStatusEnum.FAILED)) {       	  
	    	  	    	  
	    	  return "failed";
	    	  
	      } else if(isFailedProcessorCode(capture.processorResponse())) {
	    	  return "failed"; //Probably not necessary since capture status will be DECLINED
	    	  
	      } else if(order.status().contentEquals("COMPLETED")
	    		  && isNullOrEmpty(details.getTransactionId())){
	    		  
	    		  this.throwIllegalArg("Status is COMPLETED and captureId is null");
	      }	  
	        return "success";		  
	  }
	  
	
	  
	  private void debugPrintProcessorResponseOrThrow(Capture capture)
			  throws ProcessorResponseNullException {
		  
		  ProcessorResponse processor = capture.processorResponse();
		  
		  if(processor == null) {
			 EhrLogger.consolePrint(this.getClass(), "debugPrintProcessorReponse",
					 "Processor is null");
			 throw new ProcessorResponseNullException();
			 
		  
		  }
		  else EhrLogger.consolePrint(this.getClass(), "debugPrintProcessorReponse",
					 "Printing fields of ProcessorResponse: ");
		  
		  String line = MessageFormat.format("avsCode={0} cvvCode={1} responseCode={2}", 
				  processor.avsCode(), processor.cvvCode(), processor.responseCode());
		  
		  System.out.println(line);
		  
	  }
	  
	 private boolean isFailedProcessorCode(ProcessorResponse response) {
		 
		 if((response.responseCode() != null && !response.responseCode().equals("0000"))
				 || response.getAvsCode() != null
				 || response.cvvCode().contentEquals("N") 
				 || response.cvvCode().contentEquals("I"))
            return true;
		
		 return false;
	 }
	  
	 private boolean isNullOrEmpty(String value) {
		 
		 if(value == null || value.isEmpty())
			 return true;
		 return false;
	 }
	 
     private void throwIllegalArg(String message) {
		  
		  throw new IllegalArgumentException(this.getClass().getCanonicalName()
				  + "#captureOrder: " + message);
	  }
}
