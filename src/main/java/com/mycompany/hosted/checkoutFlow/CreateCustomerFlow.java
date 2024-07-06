package com.mycompany.hosted.checkoutFlow;

import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;


import com.mycompany.hosted.model.Customer;
import com.mycompany.hosted.model.PostalAddress;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.checkoutFlow.jpa.CustomerJpa;
import com.mycompany.hosted.checkoutFlow.jpa.CustomerNotFoundException;

import com.mycompany.hosted.model.ShipAddress;


@Component
public class CreateCustomerFlow {		
	
	private  final String NUMBER_FMT_BUNDLE =	"loginNumberFormat";
	
	private  final String ID_ERR_BUNDLE = "invalidCustomerId";
			
	@Autowired
	private CustomerJpa customerJpa;
	
	@Autowired
	private ValidationUtil vUtil;
	
	
	public boolean customerInSession(RequestContext ctx){	
		
		
		SharedAttributeMap<Object> sharedSession = ctx.getExternalContext().getSessionMap();
		
		Customer customer = (Customer)sharedSession.get(WebFlowConstants.CUSTOMER_KEY);	
		
		
		if(customer == null)
			return false; 
		
		debugPrintCustomer(customer);
		
		MessageContext utilCtx = vUtil.validate(customer);
		
		if(utilCtx.hasErrorMessages())
			this.throwIllegalArg("Errors for Customer " + customer.getId(), 
					"customerInSession", utilCtx);
			
		return true;		
			
	}
	
	private void validateRelatedShipAddress (Customer customer) {
		
		List<ShipAddress> list = customer.getShipAddressList();
		
		for(ShipAddress address : list) {
			
			MessageContext mctx = vUtil.validate(address);
			
			if(mctx.hasErrorMessages())
				this.throwIllegalArg("Errors for ShipAddress " + address.getId(), 
						"customerInSession", mctx);
		}
	}
	
	
	/*
	 * To do: Are we retrieving or inserting.
	 * If inserting, do not validate. DONE.
	 *
	 */
	public void customerIntoSession(Customer customer,
			RequestContext ctx) {
		
		if(customer == null) 
			this.throwIllegalArg("Customer parameter is null: Cannot add to the session.",					
					 "customerIntoSession",
					 null) ;
			
		
		if(customer.getId() == null || customer.getId().equals(0)) {
			
			this.throwIllegalArg("Customer parameter has an empty Id at "
					 + ctx.getCurrentState().getId(),
					 "customerIntoSession",
					 null) ;
		}		
		
       MessageContext mctx = vUtil.validate((PostalAddress)customer); //Validate the Customer
		
		if(mctx.hasErrorMessages())
			 this.throwIllegalArg("", "customerIntoSession", mctx); 
		
		validateRelatedShipAddress(customer); //Validate List<ShipAddress>
		
		SharedAttributeMap<Object> sharedSession = ctx.getExternalContext()
				.getSessionMap(); 
		
		sharedSession.put(WebFlowConstants.CUSTOMER_KEY, customer);
		
	}
	
	/*
	 * Exit on a cancelled customer insertion
	 */
	public void customerIntoSessionOnCancel(Customer customer, RequestContext ctx,
			MyFlowAttributes myFlowAttrs) {
		
		if(myFlowAttrs.isCustomerInsertion()) {
			myFlowAttrs.setCustomerInsertion(false);
			return;
		}
		
		customerIntoSession(customer,ctx);
		
	}
	
	public Customer processLogin(String id, RequestContext ctx) 
			throws CustomerNotFoundException {
		
        Customer customer = null;
        
        MessageContext messageContext = ctx.getMessageContext();
        
        Integer customerId = null;
        
        try {
        	customerId = new Integer(id);
        }
        catch(NumberFormatException ex) {
        	
        	initMessageContext(messageContext,id, this.NUMBER_FMT_BUNDLE);        	
        	
			throw new CustomerNotFoundException("processLogin: " + ex.getMessage());
        }
		
		try {
			
			 customer = customerJpa.findCustomer(customerId);
			
		}
		catch(CustomerNotFoundException ex){
			
			initMessageContext(messageContext, id, this.ID_ERR_BUNDLE);
			
			throw ex;
			
		}
		return customer;
	}
	
	
	
	
	public Customer newCustomer() {
		
		Customer customer = new Customer();
		
		customer.setDtCreated(new Date());
		
		return customer;
	}
	
	
	
/*	public void createUserCookie(RequestContext ctx, Customer customer) {		
		
		 HttpServletResponse response = (HttpServletResponse)ctx.getExternalContext().getNativeResponse();
		
		 String name = customer.getFirstName() + " " + customer.getLastName();
	        
	        Cookie cookie = new Cookie(MyFlowHandler.CHECKOUT_NAME_COOKIE, name );   	        
	        
	        String path =  ctx.getExternalContext().getContextPath();
	        
	        cookie.setPath(path);
	        
	        cookie.setMaxAge(-1);
	        
	        response.addCookie(cookie);  
	        
	        System.out.println("CreateCustomerFlow#createUserCookie: " + MyFlowHandler.CHECKOUT_NAME_COOKIE);
	     
	}	*/
	
	private void throwIllegalArg(String message, String method, MessageContext ctx) {
		
	     String err = "";
	     
	     if(ctx == null)
	    	 err = message;
	     else {
	    	 
	        for(Message m : ctx.getAllMessages())
	    	    err += m.getText() + "; " ;
	     }   
	     
	     err = message + ": " + err;
		
		 throw new IllegalArgumentException(EhrLogger.doMessage(this.getClass(), method, err));
				 	
		 
	}	
	/*
	 * Note: The parameter passed to MessageContext#addMessage(MessageResolver)
	 * Note: args are template variables present in the bundle message in order
	 * MVC MessageSource is used to locate bundle
	 */
	private void initMessageContext(MessageContext messageCtx, String id, String code){
		messageCtx.addMessage(new MessageBuilder()
		.code(code)
		.args("Customer Id", id)
		.error()		
		.source("Customer Id")
		.build());
	}
	
	private void debugPrintCustomer(Customer customer) {
		
		System.out.println("CreateCustomerFlow#debugPrint: for Customer in session");
		
		String out = customer.getFirstName() + " " + customer.getLastName() 
				+ " " + customer.getAddress() + " " + customer.getCity()
				+ " " + customer.getPostalCode();
		
		System.out.println(out);
	}

}
