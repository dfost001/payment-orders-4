package com.mycompany.hosted.checkoutFlow;

import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails;
import com.mycompany.hosted.exception_handler.EhrLogger;

/*
 * validateDetailsAfterCapture is invoked from Jpa Component before persisting the order.
 */
public class PaymentObjectsValidator {	
	
	public static String validateDetailsBeforeCapture(PaymentDetails details) { 			
		
		return validateDetails(details, false);		
	}
	
	public static String validateDetailsAfterCapture(PaymentDetails details) {
		
		return validateDetails(details, true);		
	}
	
	private static String validateDetails(PaymentDetails details, boolean captured) {
		
	 EhrLogger.consolePrint(PaymentObjectsValidator.class, "validateDetails", "Entering procedure");	
		
	 String err = "";	
		
	 if(details == null) {
			err = "PaymentDetails is null";
	        return err;
	 }
	 if (!captured) {	   	 
		 
	   if(details.getTransactionId() != null && !details.getTransactionId().isEmpty())
		   
		   err = "PaymentDetails has an initialized CaptureId before payment captured. ";
	   
	 } else	 if(details.getTransactionId() == null || details.getTransactionId().isEmpty()) {
		 
		   err =  "PaymentDetails has an empty CaptureId after payment captured. ";
	 }
		 
	 if(details.getPayPalResourceId() == null || details.getPayPalResourceId().isEmpty()) {
		 
		   err += "PaymentDetails has an uninitialized OrderId";
		
	}
	
	  return err;
   }
	
} //end class
