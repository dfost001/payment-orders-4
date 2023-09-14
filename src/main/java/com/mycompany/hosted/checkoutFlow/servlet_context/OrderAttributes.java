package com.mycompany.hosted.checkoutFlow.servlet_context;

import com.mycompany.hosted.model.PostalAddress;

public class OrderAttributes {
	
	private PostalAddress billTo;
	private PostalAddress shipTo;
	private CartAttrs cartAttrs;
	public PostalAddress getBillTo() {
		return billTo;
	}
	public void setBillTo(PostalAddress billTo) {
		this.billTo = billTo;
	}
	public PostalAddress getShipTo() {
		return shipTo;
	}
	public void setShipTo(PostalAddress shipTo) {
		this.shipTo = shipTo;
	}
	public CartAttrs getCartAttrs() {
		return cartAttrs;
	}
	public void setCartAttrs(CartAttrs cartAttrs) {
		this.cartAttrs = cartAttrs;
	}
	
	
	

}
