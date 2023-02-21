package com.mycompany.hosted.checkoutFlow.exceptions;

@SuppressWarnings("serial")
public class CheckoutHttpException extends Exception {	
	
	private Throwable cause;
	
	private String method;
	
	private boolean testException;
	
	public CheckoutHttpException(Throwable cause, String method) {
		
		super(cause.getMessage());		
		
		this.cause = cause;
		
		this.method = method;
		
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public String getMethod() {
		return method;
	}

	public boolean isTestException() {
		return testException;
	}

	public void setTestException(boolean testException) {
		this.testException = testException;
	}
	
	

}
