package com.mycompany.hosted.model.order;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;


/**
 * The persistent class for the service_details database table.
 * 
 */
@Entity
@Table(name="service_details")
@NamedQuery(name="ServiceDetail.findAll", query="SELECT s FROM ServiceDetail s")
public class ServiceDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)	
	private int id;

	@Column(name="captureId", nullable=true, length=40)
	private String captureId;

	@Column(name="captureStatus", nullable=true, length=20)
	private String captureStatus;
	
	@Column(name="failedCaptureReason", nullable=true, length=30)
	private String failedCaptureReason;

	@Column(name="cardDigits", nullable=true, length=10)
	private String cardDigits;

	@Column(name="cardExpiry", nullable=true, length=10)
	private String cardExpiry;

	@Column(name="cardType", nullable=true, length=10)
	private String cardType;

	@Column(name="createTime", nullable=true, length=10)
	private String createTime;
	
	@Column(name="serviceId", nullable=true, length=30)
	private String serviceId;
	
	@Column(name="captureJson")
	@Lob
	private String captureJson;
	
	@Column(name="refundJson")
	@Lob
	private String refundJson;
	
	@Column(name="refundId", nullable=true, length=40)
	private String refundId;
	
	@Column(name="refundAmount", nullable=true, precision=10, scale=2)
	private BigDecimal refundAmount;
	
	@Column(name="refundTime", nullable=true, length=30)
	private String refundTime;
	
	@Column(name="billingName", nullable=true, length=30)
	private String billingName;
	
	@Column(name="billingEmail", nullable=true, length=50)
	private String billingEmail;
	
	@Column(name="billingAddress", nullable=true, length=70)
	private String billingAddress;

	public ServiceDetail() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCaptureId() {
		return this.captureId;
	}

	public void setCaptureId(String captureId) {
		this.captureId = captureId;
	}

	public String getCaptureStatus() {
		return this.captureStatus;
	}

	public void setCaptureStatus(String captureStatus) {
		this.captureStatus = captureStatus;
	}

	public String getCardDigits() {
		return this.cardDigits;
	}

	public void setCardDigits(String cardDigits) {
		this.cardDigits = cardDigits;
	}

	public String getCardExpiry() {
		return this.cardExpiry;
	}

	public void setCardExpiry(String cardExpiry) {
		this.cardExpiry = cardExpiry;
	}

	public String getCardType() {
		return this.cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getRefundId() {
		return this.refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public String getFailedCaptureReason() {
		return failedCaptureReason;
	}

	public void setFailedCaptureReason(String failedCaptureReason) {
		this.failedCaptureReason = failedCaptureReason;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getCaptureJson() {
		return captureJson;
	}

	public void setCaptureJson(String captureJson) {
		this.captureJson = captureJson;
	}

	public String getRefundJson() {
		return refundJson;
	}

	public void setRefundJson(String refundJson) {
		this.refundJson = refundJson;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(String refundTime) {
		this.refundTime = refundTime;
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

	public String getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
	}
	

}