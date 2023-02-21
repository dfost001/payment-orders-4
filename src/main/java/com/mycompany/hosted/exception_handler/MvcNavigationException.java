package com.mycompany.hosted.exception_handler;

@SuppressWarnings("serial")
public class MvcNavigationException extends Exception {
	
	private static String friendly = "A view may not be accessible if "
			+ "you are using the browser to navigate";

	public MvcNavigationException(String message) {
		
		super(message);
	}
	
	public MvcNavigationException() {
		super(friendly);
	}

	public String getFriendly() {
		return friendly;
	}

	public void setFriendly(String display) {
		friendly = display;
	}
	
	
	
}
