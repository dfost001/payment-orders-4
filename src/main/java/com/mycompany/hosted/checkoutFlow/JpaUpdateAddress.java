package com.mycompany.hosted.checkoutFlow;

import java.io.Serializable;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.webflow.execution.RequestContext;

import com.mycompany.hosted.checkoutFlow.jpa.CustomerJpa;
import com.mycompany.hosted.checkoutFlow.jpa.CustomerNotFoundException;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.model.Customer;
import com.mycompany.hosted.model.PostalAddress;
import com.mycompany.hosted.model.ShipAddress;

@SuppressWarnings("serial")
@Component
public class JpaUpdateAddress implements Serializable{
	
	@Autowired
	private CustomerJpa jpa;
	
	public Customer findCustomer(RequestContext ctx) throws CustomerNotFoundException {
		
		Customer customer = (Customer)ctx.getExternalContext().getSessionMap().get(WebFlowConstants.CUSTOMER_KEY);
		
		if(customer == null) return null;
		
		//System.out.println("JpaUpdateAddress#findCustomer: session=" + customer.toString());
		
		Customer found = jpa.findCustomer(customer.getId());
		
		//System.out.println("JpaUpdateAddress#findCustomer: found=" + found.toString());
		
		return found;
	}
	
	/*
	 * To do: replace evalBinding with validation
	 */
	public Customer update(PostalAddress addr, RequestContext ctx) {	
		
		Customer customer = null;
		
		if(addr == null)
			this.doException("update", "PostalAddress is null");
		
		evalBinding(addr); //stub for ValidationUtil#validate
		
		if(Customer.class.isAssignableFrom(addr.getClass())) {		
			
			customer = processCustomerUpdate(addr, ctx);
		}
		else if(ShipAddress.class.isAssignableFrom(addr.getClass())) {
			
			customer = processShipAddressUpdate(addr, ctx);
		}
		
		
		return customer;
	}
	
	private Customer processCustomerUpdate(PostalAddress addrValue, RequestContext ctx) {
		
		Customer customer = (Customer) addrValue;
		
		jpa.updateCustomer(customer);		
		
		return customer;

	}
	
	private Customer processShipAddressUpdate(PostalAddress addrValue, RequestContext ctx) {		
		
		Customer customer = (Customer) ctx.getExternalContext()
				.getSessionMap().get(WebFlowConstants.CUSTOMER_KEY);
		
		if(customer == null)
			doException("processShipAddressUpdate", "Customer in external session is null");
		
		ShipAddress shipAddr = (ShipAddress)addrValue;		
		
		Customer updated = jpa.updateShipAddress(customer, shipAddr);		
		
		//debugPrintShipUpdate(updated);
		
		return updated;
	}
	
	private void evalBinding(PostalAddress addr) {
		
		String line = addr.getFirstName() + ": " + addr.getCity() + ": " + addr.getState();
		
		if(line.trim().isEmpty())
			this.doException("debugPrint", "PostalAddress has empty values.");
		
		//System.out.println("JpaUpdateAddress#update: " + line);
		
	}
	
	/*private void debugPrintShipUpdate(Customer customer) {
		
		List<ShipAddress> related = customer.getShipAddressList();
		
		System.out.println("JpaUpdateAddress#printShipUpdate: size=" + related.size());
		
		for(ShipAddress ship : related) {
			System.out.println("id=" + ship.getId());
		}
	}*/
	
	private void doException(String method, String message) {
		
		throw new IllegalArgumentException (
				
				EhrLogger.doMessage(this.getClass(), method, message)
				
				);
	}

}
