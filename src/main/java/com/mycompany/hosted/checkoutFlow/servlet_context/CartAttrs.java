package com.mycompany.hosted.checkoutFlow.servlet_context;

import java.util.List;

import com.mycompany.hosted.cart.CartItem;

public class CartAttrs {
	
	private List<CartItem> cartList;
	private String subtotal;
	private String taxAmount;
	private String shippingFee;
	private String grandTotal;
	
	public List<CartItem> getCartList() {
		return cartList;
	}	
	public void setCartList(List<CartItem> cartList) {
		this.cartList = cartList;
	}
	public String getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(String subtotal) {
		this.subtotal = subtotal;
	}
	public String getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(String tax) {
		this.taxAmount = tax;
	}
	public String getShippingFee() {
		return shippingFee;
	}
	public void setShippingFee(String shippingFee) {
		this.shippingFee = shippingFee;
	}
	public String getGrandTotal() {
		return grandTotal;
	}
	public void setGrandTotal(String grandTotal) {
		this.grandTotal = grandTotal;
	}
	
	

}
