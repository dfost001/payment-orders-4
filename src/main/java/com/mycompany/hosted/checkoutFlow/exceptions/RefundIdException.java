package com.mycompany.hosted.checkoutFlow.exceptions;

@SuppressWarnings("serial")
public class RefundIdException extends Exception {
	
	private static String issue = 
			"Payment probably refunded, but Refund Id field is null in the service's Response";
	
	public RefundIdException() {
		
		super(issue) ;
	}
	
	public RefundIdException(String method) {
		super(method + ": " + issue);
	}

}
