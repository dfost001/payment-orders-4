package com.mycompany.hosted.checkoutFlow.paypal.orders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mycompany.hosted.cart.Cart;
import com.mycompany.hosted.cart.CartItem;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutHttpException;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.mock.service.Book;
import com.mycompany.hosted.model.PostalAddress;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AddressPortable;
//import com.paypal.orders.AddressPortable;
import com.paypal.orders.AmountBreakdown;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Item;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Money;
import com.paypal.orders.Name;
//import com.paypal.orders.Name;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;
import com.paypal.orders.ShippingDetail;
//import com.paypal.orders.ShippingDetail;

@Component
public class CreateOrder2 {
	
	private final String description = "Hartley Book-Sellers";
	
	private final String softDescriptor = "Hartley-Books";
	
	private String referenceId = "PUHF";	
	
	private String customId="Hartley";
	
	private boolean testRecoverableException = false;
	
	private boolean testIdNotAssigned = false;
	
	private boolean testFaultyRequest = true;
	
	@Autowired
	private PayPalClient payClient;	
	
	 /**
	   *Method to create order
	   *
	   *@param debug true = print response data
	   *@return HttpResponse<Order> response received from API
	   *@throws IOException Exceptions from API if any
	   */
	  public Order create(Cart cart, PostalAddress shipping) throws CheckoutHttpException {	
		  
		EhrLogger.consolePrint(this.getClass(), "create", "executing") ; 	
		
		HttpResponse<Order> response = null;
		
		if(testRecoverableException) {
			
			initTestException(response); //Throws exception
		}

	    OrdersCreateRequest request = new OrdersCreateRequest();

	    request.prefer("return=representation");   
	    

	    request.requestBody(buildRequestBody(cart, shipping));		    

	    try {
	    	
	      response = payClient.client().execute(request);
	      
	      if(response == null || response.result() == null) {
		    	
		    	this.throwIllegalArg("HttpResponse or Order is null");
		    }	  
	      
	      if(response.result().id() == null)
	    	  this.throwIllegalArg("PayPal Order result does not contain an Id");
	      
	      if(this.testIdNotAssigned) {
	    	  this.testIdNotAssigned = false;
	    	  throw new IllegalArgumentException(
	    			  "Testing CreateOrder: Response does not contain an Id");
	      }
	      
	      debugPrint(response);
	      
	    } catch (IOException | IllegalArgumentException io)  {	    	    	
	    	
	    	/* payPalId=null, persistOrderId=null */    	
	    	throw EhrLogger.initCheckoutException(io, "create", response, null, null); //ControllerAdvice
	    	
	    }	     
	   	    
	    return response.result();
	  }

   private void initTestException(HttpResponse<Order> response) throws CheckoutHttpException {
	   
	    this.testRecoverableException = false;
		
		CheckoutHttpException ex = EhrLogger.initCheckoutException(new Exception("Testing Recoverable 503 Status"),
				"create", response, null, null); 				
		
		ex.setTestException(true);
		
		throw ex; //Handled by ControllerAdvice
   }
	
	private OrderRequest buildRequestBody(Cart cart, PostalAddress shipping) {
			
			
		    OrderRequest orderRequest = new OrderRequest();
		    
		    orderRequest.checkoutPaymentIntent("CAPTURE");		  
		    
		    orderRequest.applicationContext(initApplicationContext()); 	  
		    
		    PurchaseUnitRequest purchaseUnit =  initPurchaseUnit(cart, shipping);
		    
		    List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<PurchaseUnitRequest>();
		    
		    purchaseUnitRequests.add(purchaseUnit);
		    
		    orderRequest.purchaseUnits(purchaseUnitRequests);		 
		    
		    return orderRequest;
	  }	

	  private ApplicationContext initApplicationContext() {
		  
		  ApplicationContext applicationContext = new ApplicationContext()
			    	.brandName("Hartley Book-Sellers")
			    	.landingPage("BILLING")
			        .shippingPreference("SET_PROVIDED_ADDRESS")	
			    //    .landingPage("LOGIN")
			    //	.shippingPreference("NO_SHIPPING")
			        .userAction("CONTINUE");
			        //.returnUrl(returnUrl)
			        //.cancelUrl(cancelUrl);
		  
		  return applicationContext;
		  
	  }
	  
	  
 private PurchaseUnitRequest initPurchaseUnit(Cart cart, PostalAddress shipping) {	 
	
	 PurchaseUnitRequest unit = new PurchaseUnitRequest();
	 
	 unit.referenceId(this.referenceId).description(description).customId(this.customId)
	      .softDescriptor(softDescriptor);	
	 
	 unit.items(initItemList(cart));
	 
	 unit.shippingDetail(initShippingAddress(shipping));
	 
	 if(this.testFaultyRequest) {
		 this.testFaultyRequest = false;
		 unit.amountWithBreakdown(initFaultyAmount(cart)) ;
		 return unit;
	 }
	 
	 unit.amountWithBreakdown(initAmountWithBreakdown(cart));	
	 
	 return unit;
 }
 
 private AmountWithBreakdown initAmountWithBreakdown(Cart cart) {
	 
	 AmountWithBreakdown amountWith = new AmountWithBreakdown()
			 .currencyCode("USD")
			 .value(cart.getFormattedGrand())
			 .amountBreakdown(
					 new AmountBreakdown().itemTotal(
							 new Money().currencyCode("USD").value(cart.getFormattedSubtotal()))
		                .shipping(
		                	 new Money().currencyCode("USD").value(cart.getFormattedShipping()))
		                .handling(new Money().currencyCode("USD").value("0.00"))
		                .taxTotal(new Money().currencyCode("USD").value(cart.getFormattedTax()))
		                .shippingDiscount(new Money().currencyCode("USD").value("0.00"))
		       );
	 return amountWith;
 }
 
 private AmountWithBreakdown initFaultyAmount(Cart cart) {
	 AmountWithBreakdown amountWith = new AmountWithBreakdown()
			 .currencyCode("USD")
			 .value("0.00")
			 .amountBreakdown(
					 new AmountBreakdown().itemTotal(
							 new Money().currencyCode("USD").value(cart.getFormattedSubtotal()))
		                .shipping(
		                	 new Money().currencyCode("USD").value(cart.getFormattedShipping()))
		                .handling(new Money().currencyCode("USD").value("0.00"))
		                .taxTotal(new Money().currencyCode("USD").value(cart.getFormattedTax()))
		                .shippingDiscount(new Money().currencyCode("USD").value("0.00"))
		       );
	 return amountWith;
 }
 
 private List<Item> initItemList(Cart cart) {
	 
	 List<Item> itemList = new ArrayList<Item>() ;
	 
	 for(CartItem it : cart.getCartList())
         {
		   Book book = it.getBook();
           Item item = new Item()
        		   .name(book.getTitle())
        		   .description("ISBN: " + book.getIsbn())
        		   .sku("sku01")
                   .unitAmount(
                		   new Money().currencyCode("USD").value(it.formattedPrice()))
                   .quantity(it.getQuantity().toString())
                   .category("PHYSICAL_GOODS");
           itemList.add(item);
          
         }
	 return itemList;
   }			 
	 


 private ShippingDetail initShippingAddress(PostalAddress ship) {
	 
	Name name = new Name().fullName(ship.getFirstName() + " " + ship.getLastName());
			  
	 
	ShippingDetail detail = new ShippingDetail()
			
	  .name(name)
	  
      .addressPortable(
    		  new AddressPortable()
    		  
    		     .addressLine1(ship.getAddress())    
    		     
                 .adminArea2(ship.getCity())
                 
                 .adminArea1(ship.getState())
                 
                 .postalCode(ship.getPostalCode())
                 
                 .countryCode("US")
          );
	 
	 return detail;
 } 
	  
 private void throwIllegalArg(String message) {
		  
		  throw new IllegalArgumentException(this.getClass().getCanonicalName()
				  + "#createOrder: " + message);
	  } 
 
   private void debugPrint(HttpResponse<Order> response) {
	   
	   System.out.println("CreateOrder2#Status: " + response.statusCode());
	   
	   Order order =  response.result();	   
	   
	   if (response.statusCode() == 201) {
	        
	        System.out.println("Status: " + order.status());
	        
	        System.out.println("Order ID: " + order.id());
	        
	        System.out.println("Intent: " + order.checkoutPaymentIntent());
	        
	        System.out.println("CreateOrder2#Links: ");
	        
	        if(order.links() != null) {
	        	
	           
	            for (LinkDescription link : order.links()) {
	                System.out.println("\t" + link.rel() + ": " + link.href() + "\tCall Type: " + link.method());
	            }
	        }
	        
	        if(order.purchaseUnits() != null) {
	        	
	       	        System.out.println("Total Amount: " + 
	       	        
	                    order.purchaseUnits().get(0).amountWithBreakdown().value());
	        }
	   
      }
   } //end print

 } //end class
