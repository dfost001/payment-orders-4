package com.mycompany.hosted.checkoutFlow.exceptions;

@SuppressWarnings("serial")
public class RefundIdException extends Exception {
	
	public RefundIdException() {
		
		super( "Payment probably refunded, but Refund Id is null in the service's Response");
	}
	
	public RefundIdException(String message) {
		super(message);
	}

}
