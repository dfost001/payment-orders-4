package com.mycompany.hosted.checkoutFlow.paypal.rest;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;
import com.mycompany.hosted.formatter.StringUtil;
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
	
	public static String applicationUrl;
	
	
	@PostMapping(value = "/paypal/order/create/{loginType}", consumes = "application/json", produces = { "application/json" })
	public OrderId createOrder(HttpServletRequest request, @PathVariable("loginType") String loginType) throws CheckoutHttpException {

		applicationUrl = request.getScheme() + "://"
				+ request.getServerName() + ":"
				+ request.getLocalPort()
				+ request.getContextPath() + "/spring/checkout-flow";
		
		HttpSession session = request.getSession();		
		
		PostalAddress postalSelected = (PostalAddress) session.getAttribute(WebFlowConstants.SELECTED_POSTAL_ADDR);
		
		Customer customer = (Customer)session.getAttribute(WebFlowConstants.CUSTOMER_KEY);

		OrderId orderId = null;

		try {

			if (cart == null || cart.getCount() == 0) {
                this.reason = EndpointRuntimeReason.CREATE_NULL_SESSION_ATTRS;
				this.throwIllegalArgument("createOrder", Cart.class.getCanonicalName() + " is null or empty");
			}
			if (postalSelected == null || customer == null) {
				this.reason = EndpointRuntimeReason.CREATE_NULL_SESSION_ATTRS;
				this.throwIllegalArgument("createOrder",
						"Customer or Selected_Postal_Address or both are null in the session");
			}
			
			PostalAddress payPalAddress = this.getLoginType(loginType, postalSelected, customer);			
			
			Order order = createOrder.create(cart, payPalAddress, loginType); 			

			String id = order.id();

			orderId = new OrderId(id);

			session.setAttribute(WebFlowConstants.PAYPAL_SERVER_ID, orderId);

		} catch (IllegalArgumentException ex) { //Thrown from this block
			
			throw EhrLogger.initCheckoutException(ex, "create", null, this.reason); // Response -> null
		} 

		return orderId;

	}	
	
	private PostalAddress getLoginType(String loginType, PostalAddress selected, Customer customer) {
		
		PostalAddress payPalAddress = null;
		
		if(StringUtil.isNullOrEmpty(loginType)) {
			this.reason = EndpointRuntimeReason.CREATE_MISSING_LOGINTYPE;
			this.throwIllegalArgument("createOrder",
					"@PathVariable('loginType') does not have a value");
		}
		else if(loginType.contentEquals("payPal"))
		    payPalAddress = selected; //PayPal with a buyer account fills in shipping details
		else if(loginType.contentEquals("advanced") || loginType.contentEquals("notEligible"))
			payPalAddress = customer; //PayPal fills credit-card form with billing info
		else {
			this.reason = EndpointRuntimeReason.CREATE_MISSING_LOGINTYPE;
			this.throwIllegalArgument("createOrder",
					"@PathVariable('loginType') has an unknown value");
		}
		return payPalAddress;
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
