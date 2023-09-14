package com.mycompany.hosted.checkoutFlow.servlet_context;

import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;

import com.mycompany.hosted.cart.Cart;
import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.model.Customer;
import com.mycompany.hosted.model.PostalAddress;

/*
 * To do: Null Pointer Evaluation
 */

public class OrderAttributesUtil {
	
	public static OrderAttributes initOrderAttrs(RequestContext request) {
		
		SharedAttributeMap<Object> session = request.getExternalContext().getSessionMap();
		
		Customer billTo = (Customer) session.get(WebFlowConstants.CUSTOMER_KEY);
		
		PostalAddress shipTo = (PostalAddress) session.get(WebFlowConstants.SELECTED_POSTAL_ADDR);
		
		Cart cart = (Cart) session.get(WebFlowConstants.CART);
		
		OrderAttributes attrs = new OrderAttributes();
		
		attrs.setBillTo(billTo);
		
		attrs.setShipTo(shipTo);
		
		initCartAttrs(attrs, cart);
		
		return attrs;
		
	}
	
	private static void initCartAttrs(OrderAttributes attrs, Cart cart) {
		
		CartAttrs cartAttrs = new CartAttrs();
		
		cartAttrs.setCartList(cart.getCartList());
		
		cartAttrs.setGrandTotal(cart.getFormattedGrand());
		
		cartAttrs.setSubtotal(cart.getFormattedSubtotal());
		
		cartAttrs.setShippingFee(cart.getFormattedShipping());
		
		cartAttrs.setTaxAmount(cart.getFormattedTax());
		
		attrs.setCartAttrs(cartAttrs);
	}

}
