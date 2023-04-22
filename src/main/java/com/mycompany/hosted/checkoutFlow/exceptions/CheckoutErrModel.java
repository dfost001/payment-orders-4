package com.mycompany.hosted.checkoutFlow.exceptions;

public class CheckoutErrModel {
	
	private String uuid;
	private String message;
	private String messageTrace;
	private boolean recoverable;
	private Integer responseCode;
	private String errContentType;
	private String errMethod;
	private String friendly;
	private String retUrl;
	private String cause;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isRecoverable() {
		return recoverable;
	}
	public void setRecoverable(boolean recoverable) {
		this.recoverable = recoverable;
	}
	public Integer getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}
	
	public String getErrContentType() {
		return errContentType;
	}
	public void setErrContentType(String contentType) {
		this.errContentType = contentType;
	}
	public String getFriendly() {
		return friendly;
	}
	public void setFriendly(String friendly) {
		this.friendly = friendly;
	}
	public String getErrMethod() {
		return errMethod;
	}
	public void setErrMethod(String errMethod) {
		this.errMethod = errMethod;
	}
	public String getRetUrl() {
		return retUrl;
	}
	public void setRetUrl(String retUrl) {
		this.retUrl = retUrl;
	}
	public String getMessageTrace() {
		return messageTrace;
	}
	public void setMessageTrace(String messageTrace) {
		this.messageTrace = messageTrace;
	}
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	
	

}
