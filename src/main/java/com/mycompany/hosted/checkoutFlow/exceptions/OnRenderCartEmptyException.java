package com.mycompany.hosted.checkoutFlow.exceptions;

@SuppressWarnings("serial")
public class OnRenderCartEmptyException extends Exception {
	
	private final static String message = "Webflow entered with an empty cart";
	
	private  final static String friendly = "You may have emptied your cart " +
	                                        "and used the browser to navigate";
	
	
	
	public OnRenderCartEmptyException() {
		
		super(message);		
	}

	public String getFriendly() {
		return friendly;
	}	

}
