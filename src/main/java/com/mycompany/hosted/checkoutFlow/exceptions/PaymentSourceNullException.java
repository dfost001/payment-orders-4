package com.mycompany.hosted.checkoutFlow.exceptions;

@SuppressWarnings("serial")
public class PaymentSourceNullException extends Exception {
	
	private static String issue = "There was a problem processing your card. Please enter another or try again. " ;
	
	public PaymentSourceNullException() {
		
		super(issue);
	}
	
	public PaymentSourceNullException(String message) {
		
		super(message);
	}

}
