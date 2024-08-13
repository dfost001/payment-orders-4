package com.mycompany.hosted.checkoutFlow.paypal.rest;



import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import com.mycompany.hosted.cart.Cart;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.model.Customer;
import com.mycompany.hosted.model.PostalAddress;
import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.checkoutFlow.paypal.orders.CreateOrder2;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutHttpException;
import com.mycompany.hosted.checkoutFlow.exceptions.EndpointRuntimeReason;
import com.paypal.orders.Order;

@RequestScope
@RestController
public class OrderController {			
	
	@Autowired
	private CreateOrder2 createOrder;	
	
	@Autowired
	private Cart cart;
	
	private EndpointRuntimeReason reason;
	
	
	@PostMapping(value = "/paypal/order/create", consumes = "application/json", produces = { "application/json" })
	public OrderId createOrder(HttpSession session) throws CheckoutHttpException {

		PostalAddress postal = (PostalAddress) session.getAttribute(WebFlowConstants.SELECTED_POSTAL_ADDR);
		
		Customer customer = (Customer)session.getAttribute(WebFlowConstants.CUSTOMER_KEY);

		OrderId orderId = null;

		try {

			if (cart == null || cart.getCount() == 0) {
                this.reason = EndpointRuntimeReason.CREATE_NULL_SESSION_ATTRS;
				this.throwIllegalArgument("createOrder", Cart.class.getCanonicalName() + " is null or empty");
			}
			if (postal == null || customer == null) {
				this.reason = EndpointRuntimeReason.CREATE_NULL_SESSION_ATTRS;
				this.throwIllegalArgument("createOrder",
						"Customer or Selected_Postal_Address or both are null in the session");
			}
			Order order = createOrder.create(cart, postal); //PayPal with a buyer account fills in shipping details
			
			//Order order = createOrder.create(cart, customer); //PayPal fills credit-card billing with shipping

			String id = order.id();

			orderId = new OrderId(id);

			session.setAttribute(WebFlowConstants.PAYPAL_SERVER_ID, orderId);

		} catch (IllegalArgumentException ex) { 
			
			throw EhrLogger.initCheckoutException(ex, "create", null, this.reason); // Response -> null
		} 

		return orderId;

	}	
	
	private void throwIllegalArgument(String method, String message) {
		
		throw new IllegalArgumentException(
				
				EhrLogger.doMessage(this.getClass(), method, message)
						
	     );		
	}
	
	
  /* private void debugPrint(String method, String message) {
		
		String line = "OrderController#" + method + ": " + message;
		
		System.out.println(line);
	} */

} //end class
