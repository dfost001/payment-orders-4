package com.mycompany.hosted.checkoutFlow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;

import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.model.Customer;
import com.mycompany.hosted.model.PostalAddress;
import com.mycompany.hosted.model.ShipAddress;

@Component
public class SelectAddressFlow {
	
	
	public boolean isSelected(RequestContext ctx) {
		
		PostalAddress selected = (PostalAddress)ctx.getExternalContext()
				
				.getSessionMap().get(WebFlowConstants.SELECTED_POSTAL_ADDR);
		
		if(selected == null)
			return false;
		
		return true;
		
	}
	
	public List<? super PostalAddress> createAddressList (Customer customer) {
		
		throwIfNull(customer, "Customer", "getPostalAddressList");
		
		List<? super PostalAddress> postal = this.getPostalList(customer);	
		
		return postal;
		
	}
	
	
	
	private List<? super PostalAddress> getPostalList(Customer customer) {
		
        List<ShipAddress> shipAddressList = customer.getShipAddressList(); //Obtain from relation
        
        List<? super PostalAddress> postalList = new ArrayList<>();
		
		postalList.add(customer);
		
		postalList.addAll(shipAddressList);
		
		//debugPrintList(postalList);
		
		return postalList;
		
	}
	
	public void selectedIntoSession(String identifier, RequestContext ctx) {
		
		PostalAddress postal = findPostalAddress(identifier, ctx) ;
		
		ctx.getExternalContext().getSessionMap()
		
		  .put(WebFlowConstants.SELECTED_POSTAL_ADDR, postal);
	}
	
	
	
	public PostalAddress findPostalAddress(String identifier, RequestContext ctx) {
		
		SharedAttributeMap<Object> sessionMap = ctx.getExternalContext().getSessionMap();
		
		Customer customer = (Customer) sessionMap
				.get(WebFlowConstants.CUSTOMER_KEY);
		
		throwIfNull(customer,"Customer", "findPostalAddress");
		
		int pos = identifier.indexOf('_')	;
		
		Integer index = Integer.parseInt(identifier.substring(pos + 1));
		
		PostalAddress postal = null;
		
		if(index == 0) {			
		
			postal = customer;			
			
		} else {		  
		
		   Integer id = Integer.parseInt(identifier.substring(0, pos)  );
		
		   List<ShipAddress> shipAddresses = customer.getShipAddressList();
		
		   postal = find(shipAddresses, id);
		}	
		
		return postal;	
		
	}	
	
	public Integer setPreviousSelected (RequestContext ctx) {
		
         PostalAddress selected = (PostalAddress)ctx.getExternalContext()
				
				.getSessionMap().get(WebFlowConstants.SELECTED_POSTAL_ADDR);
         
         System.out.println("SelectAddressFlow#setPreviousSelected executing: selected=" + selected);
         
         if(selected == null) return 0;
         
         Customer customer = (Customer)ctx.getExternalContext()
        		 .getSessionMap().get(WebFlowConstants.CUSTOMER_KEY);
         
         this.throwIfNull(customer, "Customer", "setPreviousSelected");
         
         if(Customer.class.isAssignableFrom(selected.getClass()))
        	 return 0;
         
         List<? super PostalAddress> postal = this.createAddressList(customer);
         
         int i = 1;
         
         boolean found = false;
         
         ShipAddress sessionShip = (ShipAddress) selected;
         
         for( ; i < postal.size(); i++) {
        	ShipAddress ship = (ShipAddress)postal.get(i);
        	if(ship.getId().equals(sessionShip.getId())) {
        		
        		found = true;
        		
        		break;
        	}	
         }
         
         if(!found)
        	 this.throwException("setPreviousSelected", 
        			 "Selected ShipAddress not found in Collection");
         
         System.out.println("SelectAddressFlow#found=" + i);
         
         return i;
	}

	
	private ShipAddress find(List<ShipAddress> list, Integer id) {
		
		List<ShipAddress> found = list.stream().filter(
				
				s -> s.getId().equals(id)	)
				
				.collect(Collectors.toList()); 
			
		
		if(found == null)
			throwException("findShipAddress", "ShipAddress not found");		
		
		
		return found.get(0);
	}
	
	public void updateSessionSelectionIfEdited(RequestContext request, PostalAddress postalEdited) {
		
		SharedAttributeMap<Object> sessionMap = request.getExternalContext().getSessionMap();
		
		PostalAddress postalSelected = (PostalAddress)sessionMap
				.get(WebFlowConstants.SELECTED_POSTAL_ADDR);
				
		
		if(postalSelected == null)
		   return ;
		
		if(Customer.class.isAssignableFrom(postalEdited.getClass())) {
		    if(Customer.class.isAssignableFrom(postalSelected.getClass())) {			
		
			   sessionMap.put(WebFlowConstants.SELECTED_POSTAL_ADDR, postalEdited); 			   
			   return;
		    }		    
		}
		else if(Customer.class.isAssignableFrom(postalSelected.getClass()))
			 return;
		else if(((ShipAddress)postalEdited).getId() == null) //Inserting
			return;
		
		else if(((ShipAddress)postalEdited).getId().equals(
				
			((ShipAddress)postalSelected).getId()))
			
               sessionMap.put(WebFlowConstants.SELECTED_POSTAL_ADDR, postalEdited);
		
		     EhrLogger.consolePrint(this.getClass(), "updateSessionIfEdited", 
		    		 "postalEdited == postalSelected: " 
		              + postalEdited.equals(postalSelected));		
	}
	
	
	
	public PostalAddress newShipAddress() {
		
		ShipAddress ship = new ShipAddress() ;
			
		ship.setDtCreated(new Date());
		
		return ship;
		
	}
	
	private void throwIfNull(Object obj, String title, String method) {
		
		if(obj == null)
			throw new IllegalArgumentException(
					
					EhrLogger.doMessage(this.getClass(), method,
							 title + "is null"));
		
	}
	
	private void throwException(String method, String message) {
		throw new IllegalArgumentException(
				
				EhrLogger.doMessage(this.getClass(), method, message));
						 
	}
	
/*	private void debugPrintList(List<? super PostalAddress> postalList) {
		
		Customer p = (Customer) postalList.get(0);
		
		System.out.println("SelectAddressFlow: " + p.getFirstName() + " " + p.getLastName() + " " + p.getCity());
		
		for(PostalAddress postal : postalList) {
			
		}
	}*/
	
	public void debugCompareCustomer(Customer customerIn, RequestContext ctx) {
		
		Customer sessCustomer = (Customer) ctx.getExternalContext()
				.getSessionMap()
				.get("customer");
		
		Boolean equal = sessCustomer.equals(customerIn);
		
		System.out.println("SelectAddressFlow#debugCompareCustomer: isEqual = " + equal);
		 
	}

}
