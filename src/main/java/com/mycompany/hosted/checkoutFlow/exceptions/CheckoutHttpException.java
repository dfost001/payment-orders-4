package com.mycompany.hosted.checkoutFlow.exceptions;

@SuppressWarnings("serial")
public class CheckoutHttpException extends Exception {	
	
	private Throwable cause;
	
	private String method;
	
	private boolean testException;
	
	private boolean isExpired = false;
	
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

	public boolean isExpired() {
		return isExpired;
	}

	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}
	
	

}
