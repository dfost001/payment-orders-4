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
import com.mycompany.hosted.checkoutFlow.exceptions.EndpointRuntimeReason;
import com.mycompany.hosted.checkoutFlow.exceptions.ProcessorResponseNullException;
import com.mycompany.hosted.checkoutFlow.mvc.controller.paypal.FailedPaymentStatusController;
//import com.mycompany.hosted.checkoutFlow.mvc.controller.paypal.FailedPaymentStatusController;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails.CaptureStatusEnum;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails.FailedReasonEnum;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails.GetDetailsStatus;

import com.mycompany.hosted.exception_handler.EhrLogger;
//import com.paypal.orders.Payer;
import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;


/*
 * Note: Not able to test FailedReasonEnum
 */

@Component
public class CaptureOrder {
	
	@Autowired
	private PayPalClient payClient;
	
	boolean testRecoverableException = false;
	//boolean testCaptureId = false;
	boolean testProcessorResponse = false; //see debugPrintProcessorOrThrow
	boolean testFailedCvv = false; //see isFailedProcessorCode
	
	private String capturedPaymentId;
	
	private Capture capture;
	
	private EndpointRuntimeReason reason;
	
	private String integrationType;
	
	private void resetProperties(RequestContext request) {
		
		capturedPaymentId = "";
		capture = null;
		integrationType = request.getExternalContext()
				.getSessionMap().getString(WebFlowConstants.PAYPAL_INTEGRATION_TYPE);
	}
	
	public String capture(RequestContext ctx) throws CheckoutHttpException  {		
		
		 resetProperties(ctx);	//Reset module-level captureId
		
		 PaymentDetails details = (PaymentDetails)ctx.getExternalContext()
			       .getSessionMap()
			       .get(WebFlowConstants.PAYMENT_DETAILS);		 
		 
		 
		String statusResult = "";
		
		HttpResponse<Order> response = null;
		 
		 try {
		 
		    evalDetails(details);	//throws to catch-block for null or empty resource id	
		
		    if(testRecoverableException) {		 
			 
			  doTestException(ctx, response, details.getPayPalResourceId());				
		    }		
		
	        OrdersCaptureRequest request = new OrdersCaptureRequest(details.getPayPalResourceId());
	    
	        request.prefer("return=representation");	   
	    
	        request.requestBody(buildRequestBody());
	   
	        response = payClient.client().execute(request); //throws IOException	    
	       
	        EhrLogger.consolePrint(this.getClass(), "capture", "Status Code: " + response.statusCode());		   
	   
	        Order order = response.result();	   
	    
	        debugPrintOrder(order); //throws Runtime for uninitialized fields to catch block
	    
	        String json = GetOrderDetails.debugPrintJson(response);	 //Json re-serialized   
	    
	        statusResult = initCaptureId(order, details,json); //"success" or "failed"
	    
		} catch(IOException | RuntimeException | ProcessorResponseNullException e) {			
			
			if(e instanceof HttpException)
	    		this.reason = EndpointRuntimeReason.CAPTURE_FAILED_HTTP_STATUS;
	    	else if(e instanceof IOException)
	    		this.reason = EndpointRuntimeReason.CAPTURE_EXECUTE_IO;
			
			CheckoutHttpException httpEx = EhrLogger.initCheckoutException(e,
					"capture", response, reason);
			
			String payPalId = details == null ? null : details.getPayPalResourceId(); //Error on evalDetails()
			httpEx.setPayPalId(payPalId);
			
			httpEx.setCapturedPaymentId(this.capturedPaymentId);
		    	
		    	ctx.getExternalContext()
		    	   .getSessionMap()
		    	   .put(WebFlowConstants.CHECKOUT_HTTP_EXCEPTION, httpEx);
		    	
		    	throw httpEx;
		}
	    
	    return statusResult;	   
	   
	  }

	  
	  private void doTestException(RequestContext ctx,
			  HttpResponse<Order> response, String resourceId) throws CheckoutHttpException {
		  
		    testRecoverableException = false; 				  
		    
		    CheckoutHttpException ex = EhrLogger.initCheckoutException(new Exception("Testing Recoverable 503 Status"),
					"capture", response, EndpointRuntimeReason.CAPTURE_FAILED_HTTP_STATUS); 			
		    
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
		  
		  if(!err.isEmpty()) {	
			  
			 this.reason = EndpointRuntimeReason.CAPTURE_VALIDATE_DETAILS;
			 EhrLogger.throwIllegalArg(this.getClass(), "evalDetails", err);
		  }
	  }	
	  
	  private void debugPrintOrder(Order order) {
		  
		  if(order == null)
		    	this.throwIllegalArg("debugPrintOrder", "Order returned in HttpResponse is null.");	
		  
		  String err = "Order contains uninitialized fields: ";
		  
		   System.out.println("Printing captured order: ");
		  
		   System.out.println("Status: " + order.status()); 
		      
		   System.out.println("Order ID: " + order.id());		 	   
		  
		   List<PurchaseUnit> units = order.purchaseUnits();
		   
		   if(units == null) {
			   err += " Order#List<PurchaseUnit> ";
			   
			   this.reason = EndpointRuntimeReason.CAPTURE_FIELDS_EMPTY;
			  
			   this.throwIllegalArg("debugPrintOrder", err);
			  
		   }   
		      
		   PurchaseUnit purchaseUnit = order.purchaseUnits().get(0);
		   
		   if(purchaseUnit == null) {
			   err += " Order#PurchaseUnit ";			   
			   this.reason = EndpointRuntimeReason.CAPTURE_FIELDS_EMPTY;			  
			   this.throwIllegalArg("debugPrintOrder", err);			  
		   }
		      
		   PaymentCollection payments = purchaseUnit.payments();
		   
		   if(payments == null) {
			   err += " Order#PaymentCollection ";		
			   this.reason = EndpointRuntimeReason.CAPTURE_FIELDS_EMPTY;
			   this.throwIllegalArg("debugPrintOrder", err);	
		   }
		      
		   List<Capture> captures = payments.captures();		  
		      
		   if(captures == null || captures.isEmpty()) {
               err += "Order#PaymentCollection#List<Capture> "	;     
               this.reason = EndpointRuntimeReason.CAPTURE_FIELDS_EMPTY;
               this.throwIllegalArg("debugPrintOrder", err);	
		   }
		   
		   this.capture = captures.get(0);
		   
		   this.capturedPaymentId = captures.get(0).id(); //Will throw for empty at initCaptureId()	
			 
		   err = new String();
		   
		   if(isNullOrEmpty(order.status())) {
			   err += " Order#status " ;			   
		   }
		
		   if(isNullOrEmpty(captures.get(0).status())) {
			   err += "Capture#status ";
		   }
		   
		   if(!err.isEmpty()) {   		  
			   err = "Order contains uninitialized fields: " + err;	
			   this.reason = EndpointRuntimeReason.CAPTURE_FIELDS_EMPTY;
			   this.throwIllegalArg("debugPrintOrder", err);
		   }
	  }
	  
	  private String initCaptureId(Order order, PaymentDetails details, String json) 
		         throws ProcessorResponseNullException {			      
	     
	      if(order.status().equals(CaptureStatusEnum.COMPLETED.name()) 
	    		  && isNullOrEmpty(this.capture.id())) {
	    		  
	    	      this.reason = EndpointRuntimeReason.CAPTURE_EMPTY_CAPTURE_ID;
	    		  this.throwIllegalArg("initCaptureId",
	    				  "Capture Status is COMPLETED and captureId is null");
	      }	 
		      
		  debugPrintProcessorResponseOrThrow(capture); 
		      
		  String debug = MessageFormat.format("capture.status={0} transId={1} order.status={2}", 
		    		   capture.status(), capture.id(), order.status());	    
		  
		  EhrLogger.consolePrint(this.getClass(), "initCaptureId", debug);
		  
		  initPaymentDetails(order, details, json);	
		      
		  boolean isFailedStatusDetails = initCaptureStatusDetails(details) ;			       
		     
		  boolean isFailedProcessorResponse = isFailedProcessorCode(capture.processorResponse());		    	
		      
		  boolean isFailedCaptureStatus = !isValidCaptureStatus(details);      
		      
		  String status = "";
		     
		  if(!isFailedStatusDetails && !isFailedProcessorResponse)
			   
		   	  if(!isFailedCaptureStatus)
		    		  status = "success";
		   	  else status = "failed"; //Failed status with no reason is evaluated at Failed Controller
		   
		   else if(!isFailedCaptureStatus)  //else one or both failure reasons
			   status = "failed"; //Successful Capture status with failed reason, evaluated at Controller
		   
		   else if(isFailedCaptureStatus)
		       status = "failed"; //Failed Capture with failed reason
		   
		   return status;		
	  }
	  
	  private void initPaymentDetails(Order order, PaymentDetails details, String json) {		 	
	      
	      details.setCaptureStatus(CaptureStatusEnum.valueOf(capture.status()));
    	  
    	  details.setTransactionId(capture.id());
	      
	      details.setCaptureTime(capture.createTime());	
	      
	      details.setJson(json);
	      
	      details.setProcessorResponse(capture.processorResponse());
	      
	      details.setCompletionStatus(GetDetailsStatus.valueOf(order.status()));
		  
	  }
	  
	  private boolean initCaptureStatusDetails(PaymentDetails details) {
		  
		  if(capture.captureStatusDetails() != null) {	    	  
		    	 
	    	  String reason = capture.captureStatusDetails().reason();
	    	 
	    	  details.setStatusReason(FailedReasonEnum.valueOf(reason));	
	    	  
	    	  EhrLogger.consolePrint(this.getClass(), "initCaptureId","captureStatusDetails=" + reason);		    	 
	    	  
	    	  return true;
	      }	
		  else return false;
	  }
	  
	  private void debugPrintProcessorResponseOrThrow(Capture capture)
			  throws ProcessorResponseNullException {
		  
		  if(this.integrationType.equals(WebFlowConstants.IntegrationValue.StandardCheckout.name()))
			  return;
		  
		  ProcessorResponse processor = capture.processorResponse();
		  
		  if(processor == null) {
			 EhrLogger.consolePrint(this.getClass(), "debugPrintProcessorReponse",
					 "Processor is null");
			 
			 this.reason = EndpointRuntimeReason.CAPTURE_NULL_PROCESSOR_RESPONSE;
			 
			 throw new ProcessorResponseNullException(EhrLogger.doMessage(this.getClass(),
					 "debugPrintProcessorResponseOrThrow", "ProcessorResponse is null")
					 );		  
		  }
		 
		  
		  String line = MessageFormat.format("avsCode={0} cvvCode={1} responseCode={2}", 
				  processor.avsCode(), processor.cvvCode(), processor.responseCode());
		  
		  EhrLogger.consolePrint(this.getClass(), "debugPrintProcessorResponseOrThrow", line);
		  
		  if(this.testProcessorResponse) {
			  testProcessorResponse = false;
			  throw new ProcessorResponseNullException(EhrLogger.doMessage(this.getClass(),
						 "debugPrintProcessorResponseOrThrow", "Test: ProcessorResponse is null"));		  
		  }		  
	  } //end debug
	  
	
	 private boolean isFailedProcessorCode(ProcessorResponse response) {
		 
		 if(this.testFailedCvv) {
			 response.cvvCode("N");
			 testFailedCvv = false;
		 }
		 if( (response.responseCode() != null && !response.responseCode().equals("0000"))
				 || response.getAvsCode() != null
				 || isCvvError(response.cvvCode()))
            return true;
		
		 return false;
	 }
	 
	 public static boolean isCvvError(String cvvCode) {
		 
		 if(cvvCode == null)
			    return false;
			
			boolean isError = true;
			
			switch(cvvCode) {
			case "M" :
				isError = false;
				break;
			case "E" :			
			case "P" :
			case "S" :
			case "X" :
			case "U" :	
			  //  Transaction-State uncertain
			  isError = true;	
			  break;
			case "I" :
			case "N" :
				isError = true;
				break ;
			default: //For all others
			   isError=true;
			}
			
			return isError;
	 }
	 
	 public static boolean isValidCaptureStatus(PaymentDetails details) {
			
			boolean valid = false;
			
			CaptureStatusEnum status = details.getCaptureStatus();
			
			switch (status) {
			case COMPLETED :
			case PARTIALLY_REFUNDED:
			case PENDING:
			case REFUNDED:
				valid = true;
				break;
			case FAILED:
			case DECLINED:	
				valid = false;
				break;
			default:
				EhrLogger.throwIllegalArg(FailedPaymentStatusController.class, "isValidCaptureStatus", 
						"Unknown Capture Status value.");
				
			}
			
			return valid;
			
		}
	  
	 private boolean isNullOrEmpty(String value) {
		 
		 if(value == null || value.trim().isEmpty())
			 return true;
		 return false;
	 }
	 
     private void throwIllegalArg(String method, String message) {
		  
		  String err = this.getClass().getCanonicalName()
				  + "#" + method + ": "  + message;
		  
		  System.out.println(err);
		  
		  throw new IllegalArgumentException(err);
	  }
}
