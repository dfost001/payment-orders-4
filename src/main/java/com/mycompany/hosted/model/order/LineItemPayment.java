package com.mycompany.hosted.model.order;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
/**
 * The persistent class for the line_item_payment database table.
 * 
 */
@Entity
@Table(name="line_item_payment")
@NamedQuery(name="LineItemPayment.findAll", query="SELECT l FROM LineItemPayment l")
public class LineItemPayment implements Serializable {
	private static final long serialVersionUID = 1L;

	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)	
	@Column(name="line_id", unique=true, nullable=false)
	private int lineId;

	@Column(nullable=false, length=200)
	private String description;

	@Column(name="ext_price", nullable=false, precision=10, scale=2)
	private BigDecimal extPrice;

	@Column(nullable=false, precision=10, scale=2)
	private BigDecimal price;

	@Column(name="product_id", nullable=false)	
	private Integer productId;

	@Column(nullable=false)
	private int quantity;

	//bi-directional many-to-one association to OrderPayment
	@ManyToOne
	@JoinColumn(name="order_id", nullable=false)
	private OrderPayment orderPayment;

	public LineItemPayment() {
	}

	public int getLineId() {
		return this.lineId;
	}

	public void setLineId(int lineId) {
		this.lineId = lineId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getExtPrice() {
		return this.extPrice;
	}

	public void setExtPrice(BigDecimal extPrice) {
		this.extPrice = extPrice;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getProductId() {
		return this.productId;
	}

	public void setProductId(Integer id) {
		this.productId = id;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public OrderPayment getOrderPayment() {
		return this.orderPayment;
	}

	public void setOrderPayment(OrderPayment orderPayment) {
		this.orderPayment = orderPayment;
	}

}