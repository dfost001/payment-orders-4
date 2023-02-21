package com.mycompany.hosted.checkoutFlow.exceptions;

@SuppressWarnings("serial")
public class RefundPaymentException extends Exception {
	
	private Integer orderId;
	
	public RefundPaymentException(String message, Throwable cause, Integer orderId) {
		super(message, cause);
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
	

}
