package com.mycompany.hosted.checkoutFlow.paypal.orders;

import java.io.Serializable;

import com.paypal.orders.ProcessorResponse;


//import com.mycompany.hosted.model.order.PaymentStatusEnum;

@SuppressWarnings("serial")
public class PaymentDetails implements Serializable{
	
	public enum CaptureStatusEnum {
		COMPLETED, 
		DECLINED, 
		PARTIALLY_REFUNDED, 
		PENDING, 
		REFUNDED, 
		FAILED 
	}
	
	public enum GetDetailsStatus {
		CREATED, 
		SAVED, 
		APPROVED, 
		VOIDED, 
		COMPLETED, 
		PAYER_ACTION_REQUIRED
	}
	
	public enum FailedReasonEnum {
		BUYER_COMPLAINT, 
		CHARGEBACK, 
		ECHECK, 
		INTERNATIONAL_WITHDRAWAL, 
		OTHER, 
		PENDING_REVIEW, 
		RECEIVING_PREFERENCE_MANDATES_MANUAL_ACTION, 
		REFUNDED, 
		TRANSACTION_APPROVED_AWAITING_FUNDING, 
		UNILATERAL, 
		VERIFICATION_REQUIRED 
	}
	
	private String lastDigits;
	
	private String expiry;
	
	private String cardType;
	
	private String payPalResourceId;
	
    private String payerId;
	
	private String transactionId;
	
	private String billingName;
	
	private String billingEmail;
	
	private String billingAddressLine;
	
	private String json;	
	
	private String createTime;
	
	private String captureTime;		
	
	private GetDetailsStatus createdStatus; //Status at GetDetails, when order created
	
	private CaptureStatusEnum captureStatus; //Status at Capture, same as created
	
	private GetDetailsStatus completionStatus; //Status contained by Capture transaction object
	
	private FailedReasonEnum statusReason;
	
	private ProcessorResponse processorResponse;
	
	    // Read from refund field of Order in session or retrieved
	    //private String refundTime;	
		//private String refundId;		
		//private String refundAmount;
		//private String refundJson;
	
	public PaymentDetails() {}
	
	public PaymentDetails(String id) {
		
		this.payPalResourceId = id;
	}	

	public String getPayPalResourceId() {
		return payPalResourceId;
	}

	public void setPayPalResourceId(String payPalResourceId) {
		this.payPalResourceId = payPalResourceId;
	}

	public String getBillingName() {
		return billingName;
	}

	public void setBillingName(String billingName) {
		this.billingName = billingName;
	}

	public String getBillingEmail() {
		return billingEmail;
	}

	public void setBillingEmail(String billingEmail) {
		this.billingEmail = billingEmail;
	}
	public String getBillingAddressLine() {
		return billingAddressLine;
	}

	public void setBillingAddressLine(String billingAddressLine) {
		this.billingAddressLine = billingAddressLine;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		
		this.json = json;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCaptureTime() {
		return captureTime;
	}

	public void setCaptureTime(String captureTime) {
		this.captureTime = captureTime;
	}

	public CaptureStatusEnum getCaptureStatus() {
		return captureStatus;
	}

	public void setCaptureStatus(CaptureStatusEnum captureStatus) {
		this.captureStatus = captureStatus;
	}

	public FailedReasonEnum getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(FailedReasonEnum statusReason) {
		this.statusReason = statusReason;
	}	

	public GetDetailsStatus getCreatedStatus() {
		return createdStatus;
	}

	public void setCreatedStatus(GetDetailsStatus createdStatus) {
		this.createdStatus = createdStatus;
	}

    
	public GetDetailsStatus getCompletionStatus() {
		return completionStatus;
	}

	public void setCompletionStatus(GetDetailsStatus completionStatus) {
		this.completionStatus = completionStatus;
	}

	public String getLastDigits() {
		return lastDigits;
	}

	public void setLastDigits(String lastDigits) {
		this.lastDigits = lastDigits;
	}

	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public ProcessorResponse getProcessorResponse() {
		return processorResponse;
	}

	public void setProcessorResponse(ProcessorResponse processorResponse) {
		this.processorResponse = processorResponse;
	}
	
	

} //end details
