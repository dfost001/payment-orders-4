package com.mycompany.hosted.checkoutFlow;

import java.io.Serializable;


import com.mycompany.hosted.model.Customer;
import com.mycompany.hosted.model.PostalAddress;
import com.mycompany.hosted.model.ShipAddress;
import com.mycompany.hosted.cart.Cart;
import com.mycompany.hosted.cart.CartItem;
import com.mycompany.hosted.exception_handler.EhrLogger;

import java.util.List;



import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.Message;
import org.springframework.webflow.execution.RequestContext;


@SuppressWarnings("serial")
public class MyFlowAttributes implements Serializable {
	
	
	
	private final String SHIP_CAPTION = "Ship To Information";
	
	private final String CUSTOMER_CAPTION = "Customer Billing Information";	
	
	private String formTitle ;
	
	private List<CartItem> flowCartItems;
	
	private final String ALL_MESSAGES = "allMessages" ;
	
	private boolean isCustomerInsertion = false;
	
	private boolean isErrorGetDetails = false;
	
	
	public List<CartItem> getFlowCartItems() {
		if(flowCartItems == null)
			EhrLogger.throwIllegalArg(this.getClass(), "getFlowCartItems", 
					"Property has not been set. ");
		return flowCartItems;
	}

	public void setFlowCartItems(Cart cart) {
		if(cart == null)
			EhrLogger.throwIllegalArg(this.getClass(), "setFlowCartItems", 
					"Passed in Cart param is null. ");
		this.flowCartItems = cart.getCartList();
	}

	public void evalFormTitle(PostalAddress addr) {
		
		if(Customer.class.isAssignableFrom(addr.getClass()))
			formTitle = CUSTOMER_CAPTION;
		else if(ShipAddress.class.isAssignableFrom(addr.getClass()))
			formTitle = SHIP_CAPTION;
	}
	
	public String getFormTitle() {
		
		return formTitle;
	}
	
	
	
	public boolean isCustomerInsertion() {
		return isCustomerInsertion;
	}

	public void setCustomerInsertion(boolean isCustomerInsertion) {
		this.isCustomerInsertion = isCustomerInsertion;
	}
	
	

	public void preserveMessagesIntoViewScope(RequestContext request, MessageContext msgCtx) {
		
		Message[] messages = null;
		
		if(msgCtx != null)
		    messages =  msgCtx.getAllMessages();
		else return;
		
		/*if(messages.length > 0) {
			request.getExternalContext()
			   .getSessionMap()
			   .put(ALL_MESSAGES, messages);
			return;
		}*/
		if(messages.length > 0) {
		    request.getViewScope().put(ALL_MESSAGES, messages);		
	    }
	}
	
	public void removeViewScopeMessages(RequestContext request) {
	
		/*request.getExternalContext()
		   .getSessionMap()
		   .remove(ALL_MESSAGES);*/
		
		request.getViewScope().remove(ALL_MESSAGES);
	
    }

	public boolean isErrorGetDetails() {
		return isErrorGetDetails;
	}

	public void setErrorGetDetails(boolean isErrorGetDetails) {
		this.isErrorGetDetails = isErrorGetDetails;
	}
	
	

} //end class
