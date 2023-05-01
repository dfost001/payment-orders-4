package com.mycompany.hosted.checkoutFlow.exceptions;

@SuppressWarnings("serial")
public class OrderNotRetrievableRefundException extends Exception {
	
	public OrderNotRetrievableRefundException(Throwable cause, Integer orderId) {
		
		super("Order '" + orderId + "' is not retrievable from storage for refund", cause);
		
	}	
		
		
}


