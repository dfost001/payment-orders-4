package com.mycompany.hosted.checkoutFlow;

import org.springframework.stereotype.Component;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;

import com.mycompany.hosted.cart.Cart;
import com.mycompany.hosted.model.order.OrderPayment;

@Component
public class PaymentStateAttrs {
	
	public enum PaymentState {
		LOGGED_IN,
		SHIP_SELECTED,
		DETAILS_COMPLETED,
		ERR_GET_DETAIL,
		ERR_ON_CAPTURE,		
		NONE
	}


 public String evalPaymentState(RequestContext request,
		 boolean errOnGetDetail, boolean errOnCapture, MyFlowAttributes attrs)	{ 
	
	 
	 SharedAttributeMap<Object> map =  request
			 .getExternalContext()
			 .getSessionMap();	 
	 
	if(errOnGetDetail) {
		request.getFlowScope().put(WebFlowConstants.ERR_GET_DETAIL,false);
		attrs.setErrorGetDetails(true);
		return PaymentState.ERR_GET_DETAIL.name(); //HttpException on GetDetails, transparently re-execute	
	}
	else if(errOnCapture) {
		request.getFlowScope().put(WebFlowConstants.ERR_ON_CAPTURE, false);
		return PaymentState.ERR_ON_CAPTURE.name(); //HttpException on Capture, re-execute	
	}
	else if(map.get(WebFlowConstants.PAYMENT_DETAILS) != null) //end-user exited at details view
		 return PaymentState.DETAILS_COMPLETED.name(); //Also recoverable at Capture, if not re-entered thru support 	
	
	else if(map.get(WebFlowConstants.SELECTED_POSTAL_ADDR) != null) //Set into session on transition to card-entry 
		//remains in session for non-recoverable error on getDetails()
		return PaymentState.SHIP_SELECTED.name();
	
	else if(map.get(WebFlowConstants.CUSTOMER_KEY) != null)//Set into session on transition to selectShipAddress view
		return PaymentState.LOGGED_IN.name();
	
	return PaymentState.NONE.name(); //return customerLogin
 }
	
 public void cancelPaymentDetails (RequestContext request) {
		 
		 SharedAttributeMap<Object> map =  request
				 .getExternalContext()
				 .getSessionMap();		 
		
		
		map.remove(WebFlowConstants.PAYPAL_SCRIPT_ID);		
		
		map.remove(WebFlowConstants.PAYPAL_SERVER_ID);
		
		map.remove(WebFlowConstants.PAYMENT_DETAILS);
		
		map.remove(WebFlowConstants.SELECTED_POSTAL_ADDR);
		 
	 }
 
 
 public void removeAttrsOnOrderPersisted(RequestContext request) {
	 
	 SharedAttributeMap<Object> map =  request
			 .getExternalContext()
			 .getSessionMap();
	 
     Cart cart = (Cart)map.get(WebFlowConstants.CART);
	 
	 cart.clearCart();
	 
	 map.remove(WebFlowConstants.PAYMENT_DETAILS);
	 
	 map.remove(WebFlowConstants.SELECTED_POSTAL_ADDR);
	 
	 map.remove(WebFlowConstants.PAYPAL_SCRIPT_ID);		
		
	 map.remove(WebFlowConstants.PAYPAL_SERVER_ID);	 
	 
 }
 
 public void idValSubmittedIntoSession(RequestContext request, String paramId) {
	 
	 SharedAttributeMap<Object> map =  request
			 .getExternalContext()
			 .getSessionMap();
	 
	 map.put(WebFlowConstants.PAYPAL_SCRIPT_ID,paramId);
 }
 
    public void setOrderIntoSession(RequestContext request, OrderPayment order) {
    	
    	 SharedAttributeMap<Object> map =  request
    			 .getExternalContext()
    			 .getSessionMap();
    	 
    	 map.put(WebFlowConstants.ORDER_ENTITY_VALUE, order);
	
     }
    
  
}//end class
