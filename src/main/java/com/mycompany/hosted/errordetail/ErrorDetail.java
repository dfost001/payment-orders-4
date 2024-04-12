package com.mycompany.hosted.errordetail;

//import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails;
import com.mycompany.hosted.model.order.OrderPayment;

public class ErrorDetail {
	
	public enum ErrorDetailReason  {
			PERSIST_ORDER_ERR,
			REFUNDED_ONPERSIST_ERR,
			NOT_RETRIEVABLE_FOR_REFUND,
			REFUND_UPDATE_ERR,
			REFUND_ID_MISSING
	}
	
	private ErrorDetailReason errorDetailReason;
	private OrderPayment order;
	private Integer localOrderId; //Identity for the OrderPayment entity
	private String svcTransactionId; //CaptureId
	private String errMethod;
	private Class<?> exceptionClass;
	private String errMessage;
	private Exception exception;
	private String errTime;
	
	
	public ErrorDetailReason getErrorDetailReason() {
		return errorDetailReason;
	}
	public void setErrorDetailReason(ErrorDetailReason errorDetailReason) {
		this.errorDetailReason = errorDetailReason;
	}
	public String getErrTime() {
		return errTime;
	}
	public void setErrTime(String errTime) {
		this.errTime = errTime;
	}
	public String getSvcTransactionId() {
		return svcTransactionId;
	}
	public void setSvcTransactionId(String svcPaymentTransactionId) {
		this.svcTransactionId = svcPaymentTransactionId;
	}
	
	public Integer getLocalOrderId() {
		return localOrderId;
	}
	public void setLocalOrderId(Integer localOrderId) {
		this.localOrderId = localOrderId;
	}
	public String getErrMethod() {
		return errMethod;
	}
	public void setErrMethod(String errMethod) {
		this.errMethod = errMethod;
	}
	public Class<?> getExceptionClass() {
		return exceptionClass;
	}
	public void setExceptionClass(Class<?> exception) {
		this.exceptionClass = exception;
	}
	public String getErrMessage() {
		return errMessage;
	}
	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}
	
	public OrderPayment getOrder() {
		return order;
	}
	public void setOrder(OrderPayment order) {
		this.order = order;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	/*public PaymentDetails getPaymentDetails() {
		return paymentDetails;
	}
	public void setPaymentDetails(PaymentDetails paymentDetails) {
		this.paymentDetails = paymentDetails;
	}*/
	

}
