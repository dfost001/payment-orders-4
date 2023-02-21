package com.mycompany.hosted.model.order;

import java.io.Serializable;
import javax.persistence.*;

import com.mycompany.hosted.model.Customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the order_payment database table.
 * 
 */
@Entity
@Table(name="order_payment")
@NamedQuery(name="OrderPayment.findAll", query="SELECT o FROM OrderPayment o")
public class OrderPayment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)	
	@Column(name="order_id", unique=true, nullable=false)
	private int orderId;

	@Column(name="order_amount_grand", nullable=false, precision=10, scale=2)
	private BigDecimal orderAmountGrand;

	@Column(name="order_date", nullable=false)
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date orderDate;

	@Column(name="order_shipping", nullable=false, precision=10, scale=2)
	private BigDecimal orderShippingFee;

	@Column(name="order_subtotal", nullable=false, precision=10, scale=2)
	private BigDecimal orderSubtotal;

	@Column(name="order_tax", nullable=false, precision=10, scale=2)
	private BigDecimal orderTax;	

	@Column(name="payment_status", length=20)
	private String paymentStatus;
	
	@Column(name="capture_id", length=40)
	private String captureId;
	
	@JoinColumn(name="customer_id", nullable=false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Customer customerId;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="shipTo")
	private OrderShipTo orderShipTo;
	
	@JoinColumn(name="payment_details", nullable=true)
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private ServiceDetail serviceDetail;

	//bi-directional many-to-one association to LineItemPayment
	@OneToMany(mappedBy="orderPayment",
			fetch=FetchType.LAZY,
			cascade=CascadeType.PERSIST)	 
	private List<LineItemPayment> lineItemPayments;

	public OrderPayment() {
	}

	public int getOrderId() {
		return this.orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public Customer getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(Customer customerId) {
		this.customerId = customerId;
	}

	public BigDecimal getOrderAmountGrand() {
		return this.orderAmountGrand;
	}

	public void setOrderAmountGrand(BigDecimal orderAmountGrand) {
		this.orderAmountGrand = orderAmountGrand;
	}

	public Date getOrderDate() {
		return this.orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public BigDecimal getOrderShippingFee() {
		return this.orderShippingFee;
	}

	public OrderShipTo getOrderShipTo() {
		return orderShipTo;
	}

	public void setOrderShipTo(OrderShipTo orderShipTo) {
		this.orderShipTo = orderShipTo;
	}

	public void setOrderShippingFee(BigDecimal orderShipping) {
		this.orderShippingFee = orderShipping;
	}

	public BigDecimal getOrderSubtotal() {
		return this.orderSubtotal;
	}

	public void setOrderSubtotal(BigDecimal orderSubtotal) {
		this.orderSubtotal = orderSubtotal;
	}

	public BigDecimal getOrderTax() {
		return this.orderTax;
	}

	public void setOrderTax(BigDecimal orderTax) {
		this.orderTax = orderTax;
	}

	public ServiceDetail getServiceDetail() {
		return this.serviceDetail;
	}

	public void setServiceDetail(ServiceDetail serviceDetail) {
		this.serviceDetail = serviceDetail;
	}
	public String getCaptureId() {
		return captureId;
	}

	public void setCaptureId(String captureId) {
		this.captureId = captureId;
	}

	public String getPaymentStatus() {
		return this.paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public List<LineItemPayment> getLineItemPayments() {
		if(this.lineItemPayments == null)
			lineItemPayments = new ArrayList<>();
		return this.lineItemPayments;
	}

	public void setLineItemPayments(List<LineItemPayment> lineItemPayments) {
		this.lineItemPayments = lineItemPayments;
	}

	public LineItemPayment addLineItemPayment(LineItemPayment lineItemPayment) {
		
		lineItemPayment.setOrderPayment(this);
		
		getLineItemPayments().add(lineItemPayment);		

		return lineItemPayment;
	}

	public LineItemPayment removeLineItemPayment(LineItemPayment lineItemPayment) {
		
		getLineItemPayments().remove(lineItemPayment);
		
		lineItemPayment.setOrderPayment(null);

		return lineItemPayment;
	}
	
	
	
	

}