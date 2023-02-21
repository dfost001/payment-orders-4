package com.mycompany.hosted.checkoutFlow.jpa;

@SuppressWarnings("serial")
public class OrderNotFoundException extends RuntimeException {
	
	public OrderNotFoundException(String message) {
		
		super(message);
	}

}
