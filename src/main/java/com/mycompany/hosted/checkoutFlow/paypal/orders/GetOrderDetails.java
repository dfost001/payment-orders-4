package com.mycompany.hosted.checkoutFlow.paypal.orders;

import java.io.IOException;

import java.util.List;

import com.mycompany.hosted.checkoutFlow.MyFlowAttributes;
import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.formatter.StringUtil;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutHttpException;
import com.mycompany.hosted.checkoutFlow.exceptions.EndpointRuntimeReason;
import com.mycompany.hosted.checkoutFlow.exceptions.PaymentSourceNullException;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails.GetDetailsStatus;
import com.mycompany.hosted.checkoutFlow.paypal.rest.OrderId;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.model.Customer;
//import com.paypal.orders.Authorization;
//import com.paypal.http.Headers;
//import com.mycompany.hosted.model.order.PaymentStatusEnum;
import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;
import com.paypal.http.exceptions.SerializeException;
import com.paypal.http.serializer.Json;
import com.paypal.orders.AddressPortable;
import com.paypal.orders.Card;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersGetRequest;
//import com.paypal.orders.PaymentCollection;
import com.paypal.orders.PaymentSource;
import com.paypal.orders.PurchaseUnit;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;

@Component
public class GetOrderDetails  {
	
	 private boolean testRecoverableException = false;
	 private boolean testPaymentSourceNullException = false;
	 
	 private String integrationType;
	 
	 private Customer customer;
	 
	 private PaymentDetails paymentDetails;
	 
	 private EndpointRuntimeReason reason;
	
	@Autowired
	private PayPalClient payClient;	
	
	 public String getOrder(RequestContext ctx, MyFlowAttributes flowAttrs) 
			 throws CheckoutHttpException {				  
		   
		   OrderId orderId = null;
		   
		   HttpResponse<Order> response = null;
		   
		   
		    
		    try {
		    	
		       orderId = evalRequest(ctx, flowAttrs); //Throws all IllegalArguments to catch-block		      
		      
		       if(testRecoverableException) {
				   
			      handleTestException(ctx,response, orderId.getId());
		       }
		 
		       OrdersGetRequest request = new OrdersGetRequest(orderId.getId());	
		    
		       response = payClient.client().execute(request);	//throws IOException			   
		    
		       String err = debugPrintOrder(response);
		    
		       if(!err.isEmpty()) {
		    	   
		    	this.reason = EndpointRuntimeReason.DETAILS_FIELDS_EMPTY;   
		    	throw new IllegalArgumentException(err);
		       }
		    
		       initOrderDetails(paymentDetails,response, ctx, customer); //throws test PaymentSourceNullException              
		    
		    } catch(IOException | IllegalArgumentException | PaymentSourceNullException ex) {
		    	
		    	if(ex instanceof HttpException)
		    		this.reason = EndpointRuntimeReason.DETAILS_FAILED_HTTP_STATUS;
		    	else if(ex instanceof IOException)
		    		this.reason = EndpointRuntimeReason.DETAILS_EXECUTE_IO;
		    	
		    	CheckoutHttpException httpEx = EhrLogger.initCheckoutException(ex,
		    			"getOrder", response, this.reason);
		    	
		    	String payPalId = orderId == null ? null : orderId.getId(); 
		    	
		    	httpEx.setPayPalId(payPalId);
		    	
		    	ctx.getExternalContext()
		    	   .getSessionMap()
		    	   .put(WebFlowConstants.CHECKOUT_HTTP_EXCEPTION, httpEx);
		    	
		    	throw httpEx;
		  
		    } 
		    
		    ctx.getExternalContext().getSessionMap()
            .put(WebFlowConstants.PAYMENT_DETAILS, paymentDetails);  
		    
		    GetDetailsStatus detailsStatus = paymentDetails.getCreatedStatus();
		    
		    if(detailsStatus == GetDetailsStatus.CREATED) //For  Advanced checkout only
		    	return "success";		    
		    else if(detailsStatus == GetDetailsStatus.APPROVED) //For Standard or PayPal login
		    	return "success";
		    else return "failed";		   
	  } 
	 
	 private OrderId evalRequest(RequestContext ctx, MyFlowAttributes flowAttrs) {
		 
		   OrderId orderId = evalOrderId(ctx); //throws IllegalArgument for null server-generated Id
	       
	       this.compareScriptToServerId(ctx, orderId); //throws if not equal, can refine message
	       
	       this.customer = handleNullCustomer(ctx);
	    
	       this.paymentDetails = new PaymentDetails(orderId.getId());	
	       
	       this.integrationType = ctx.getExternalContext().getSessionMap()
	    		   .get(WebFlowConstants.PAYPAL_INTEGRATION_TYPE).toString();
	       
	       if(StringUtil.isNullOrEmpty(integrationType)) {
	    	   
	    	   this.reason = EndpointRuntimeReason.NULL_ATTRIBUTE;
	    	   
	    	   EhrLogger.throwIllegalArg(this.getClass(), "evalRequest", 
	    			   "PayPal type,  Standard or Advanced, is not in the session");
	       }
	    
	       if(integrationType.contentEquals
	    		   (WebFlowConstants.IntegrationValue.AdvancedCheckout.name())) {
	    	   
	    	  compareAndInitPayerFromSession(paymentDetails, customer, ctx, 
	    			  flowAttrs.isErrorGetDetails()); //throws IllegalArgument
	    	  
	    	  flowAttrs.setErrorGetDetails(false);
	       }
	       return orderId;
		 
	 }
	 
  private Customer handleNullCustomer(RequestContext request) {
	  
	  Customer customer = (Customer)request.getExternalContext().getSessionMap()
				 .get(WebFlowConstants.CUSTOMER_KEY);
		 
		 if(customer == null) {	 	
			 
			 this.reason = EndpointRuntimeReason.DETAILS_NULL_CUSTOMER;
			 
			 EhrLogger.throwIllegalArg(this.getClass(),
					 "initOrderDetails", "Customer is null in the session");
		 }
		 
		 return customer;
  }
	 
  private void handleTestException(RequestContext ctx, 
		  HttpResponse<Order> response, String resourceId) throws CheckoutHttpException {
	  
	   this.testRecoverableException = false;	    
	   
	    
	    CheckoutHttpException ex = EhrLogger.initCheckoutException(new Exception("Testing Recoverable 503 Status"),
				"getOrder", response, EndpointRuntimeReason.DETAILS_FAILED_HTTP_STATUS); 				
		
	    
	    ex.setTestException(true);
	    
	    ctx.getExternalContext().getSessionMap()
  	          .put(WebFlowConstants.CHECKOUT_HTTP_EXCEPTION, ex);
	    
		throw ex;
  }
   
	 
	private OrderId evalOrderId(RequestContext ctx) {
		
	  SharedAttributeMap<Object> sharedSession = ctx.getExternalContext().getSessionMap();   		   
		 
	  OrderId orderId = (OrderId)sharedSession.get(WebFlowConstants.PAYPAL_SERVER_ID);		   
			
	 String err = "";
		
	 if (orderId == null || orderId.getId() == null || orderId.getId().isEmpty())
			err += "Cannot find created orderId in the session or Id is not assigned. ";
		
	 if(!err.isEmpty()) {
		 
		 this.reason = EndpointRuntimeReason.DETAILS_NULL_SERVER_ID;
		 EhrLogger.throwIllegalArg(this.getClass(), "getOrder", err);	
	 }
		
	 return orderId;
		
	}
	
	private void compareScriptToServerId (RequestContext ctx, OrderId orderId) {
		
		  String err = "";
		
		  SharedAttributeMap<Object> sharedSession = ctx.getExternalContext().getSessionMap(); 
		  
		  String paramId = (String)sharedSession.get(WebFlowConstants.PAYPAL_SCRIPT_ID); 
		  
		  if(paramId == null || paramId.isEmpty()) {
				err += "Cannot find Script orderId in the session. ";
			}	
			
			if (err.isEmpty() && !orderId.getId().contentEquals(paramId)) {
				
				err += "JavaScript orderId is not equal to created orderId";
			}
			
			if(!err.isEmpty()) {
				
				this.reason = EndpointRuntimeReason.DETAILS_COMPARE_SCRIPT_SERVER;
				EhrLogger.throwIllegalArg(this.getClass(), "getOrder", err);
			}
	}
	
	/*
	  * Note: Details created with the OrderId
	  */
	 private void initOrderDetails(PaymentDetails details,
			 HttpResponse<Order> response,
			 RequestContext request, Customer customer)
					 throws PaymentSourceNullException {	
		 
		 Order order = response.result();
		 
		 if(integrationType.equals("AdvancedCheckout"))		 
		     initPaymentSourceOrThrow(order, details);	//throws PaymentSourceNull 		 
		 
		 details.setCreatedStatus(GetDetailsStatus.valueOf(order.status())); //Enum declared on PaymentDetails		
		 
		 details.setJson(debugPrintJson(response));
		 
		 details.setCreateTime(order.createTime());						
		 
		 if(order.payer() != null) //Standard checkout or PayPal Login
			 initCardHolderFromPayer(details,order, customer, request);				
		
	 }
	 
	 private void initMessageContext(MessageContext messageCtx, 
			 String source, String text){
			messageCtx.addMessage(new MessageBuilder()			
			.error()
			.source(source)
			.defaultText(text)
			.build());
		}
	 
	public static String debugPrintJson(HttpResponse<?> response)  {		
		
		System.out.println("GetOrderDetailsdebugrintJson:");
		
		Json json = new Json();
		
		String ser = "";
		
		try {
			
		  ser = json.serialize(response.result());
		  
		} catch(SerializeException e) {
			
			EhrLogger.throwIllegalArg(GetOrderDetails.class, "debugPrintJson",
					"Error showing PayPal Json response", e);
		}
		
		JSONObject jsonObject = new JSONObject(ser);				
		
		//System.out.println(jsonObject.toString(4));
		
		String pretty = jsonObject.toString(4);
		
		//System.out.println("GetOrderDetails#debugPrintJson: doing pretty");
		
		pretty = pretty.replaceAll("\\n", "<br/>");
		
		pretty = pretty.replaceAll("\\s", "&nbsp;");		
		
		//System.out.println("GetOrderDetails#debugPrintJson: completed and returning pretty");
		
		return pretty;
		
	}
	
	private String debugPrintOrder(HttpResponse<Order> response) {
		
		EhrLogger.consolePrint(this.getClass(), "debugPrintOrder","responseCode=" + response.statusCode());	
		
		String err="";
		
		Order order = response.result();		
		
		if(order == null) {
			err += "HttpResponse<Order> is null";
			EhrLogger.consolePrint(this.getClass(), "debugPrintOrder", err);	
			return this.getClass().getCanonicalName() + ": " + err;
		}
		String id = order.id();
		
		if(id == null || id.isEmpty()) {
			err += "Order#id in deserialized response is null or empty. ";		
			EhrLogger.consolePrint(this.getClass(), "debugPrintOrder", err);	
			
		}
		if(order.status() == null || order.status().isEmpty()) {
			err += "Order#status in deserialized response is null or empty. ";		
			EhrLogger.consolePrint(this.getClass(), "debugPrintOrder", err);	
			
		}
		
		if(order.createTime() == null || order.createTime().isEmpty()) {
			err += "Order#createTime in deserialized response is null or empty. ";		
			EhrLogger.consolePrint(this.getClass(), "debugPrintOrder", err);	
			
		}
		     
		List<PurchaseUnit> units = order.purchaseUnits();
		
		if(units == null || units.isEmpty()) {
			
			err += "Order#purchaseUnits is empty. ";
			EhrLogger.consolePrint(this.getClass(), "debugPrintOrder", err);	
			
		}
		if(!err.isEmpty())
			err = this.getClass().getCanonicalName() + ": " + err;
		
		return err; 
	}

		
	private void initPaymentSourceOrThrow(Order order, PaymentDetails details) 
	     throws PaymentSourceNullException {	
		
		PaymentSource source = order.paymentSource();
		
		if(source == null) {
			
			EhrLogger.consolePrint(this.getClass(), "initPaymentSourceOrThrow", "PaymentSource is null");
			
			this.reason = EndpointRuntimeReason.DETAILS_NULL_CARD;
			
			throw new PaymentSourceNullException("GetOrderDetails#initPaymentSourceOrThrow: "
					+ "PaymentSource is null");
		}
		if(source.card() == null) {
			
			EhrLogger.consolePrint(this.getClass(), "initPaymentSourceOrThrow", "PaymentSource.card is null");
			
			this.reason = EndpointRuntimeReason.DETAILS_NULL_CARD;
			
			throw new PaymentSourceNullException("GetOrderDetails#initPaymentSourceOrThrow: "
					+ "PaymentSource.card is null");
		}
		
		Card card = source.card();
		
		EhrLogger.consolePrint(this.getClass(),"initPaymentSourceOrThrow",
				 "Card#lastDigits=" + card.lastDigits() + " expiry: " + card.expiry()
				+ " brand=" + card.brand());
		
		AddressPortable address = card.addressPortable();
		
		if(address == null)
			System.out.println("Card#PortableAddress is null");
		
		details.setLastDigits(card.lastDigits());		
		 
		details.setExpiry(card.expiry()); //empty without another connection
		
		details.setCardType(card.brand());
		
		 if(this.testPaymentSourceNullException) {
			 this.testPaymentSourceNullException = false;
			 this.reason = EndpointRuntimeReason.DETAILS_NULL_CARD;
			 throw new PaymentSourceNullException("GetOrderDetails#initPaymentSourceOrThrow: "
						+ "Testing Exception: PaymentSource.card is null");
		 }
		
	}
	
	private void compareAndInitPayerFromSession(PaymentDetails details, Customer customer,
			RequestContext request, Boolean errOnDetail) {
		
		String cardHolderName = customer.getLastName() == null ? customer.getFirstName() :
			customer.getFirstName() + " " +  customer.getLastName(); //May be reject test with null lastName
		
		String streetAddress = customer.getAddress();
		String region = customer.getState();
		String city = customer.getCity();
		String zip = customer.getPostalCode();
		
		if(!errOnDetail)
			compareRequestParamsToSessionCustomer(request, cardHolderName, streetAddress,
					region, city, zip);
		
        details.setBillingName(cardHolderName);
		
		String line = streetAddress + " " + city + ", "
		    + region + " " + zip;
		
		details.setBillingAddressLine(line);
		
		details.setBillingEmail(customer.getEmail());
		
	}
	private void compareRequestParamsToSessionCustomer(RequestContext request,
			String sesName, String sesAddress, String sesRegion,
			String sesCity, String sesZip) {
		
		ParameterMap parameters = request.getExternalContext().getRequestParameterMap();
		
		String cardHolderName = parameters.get("cardHolderName");
		String streetAddress = parameters.get("streetAddress");
		String region = parameters.get("region");
		String city = parameters.get("city");
		String zip = parameters.get("postalCode");
		
		
		System.out.println("GetOrderDetails:" + cardHolderName 
				+ " " + streetAddress + " " + region + "-parameters");
		System.out.println("GetOrderDetails:" + sesName 
				+ " " + sesAddress + " " + sesRegion + "-session");
		
		if(StringUtil.isNullOrEmpty(cardHolderName, streetAddress,
				region,city, zip)) {
			
			this.reason = EndpointRuntimeReason.DETAILS_COMPARE_PARAMETERS;
			EhrLogger.throwIllegalArg(GetOrderDetails.class, 
					 "compareRequestParamstoSessionCustomer", "Missing request parameter(s)");
		}
		String err = "";
		
		if(!cardHolderName.trim().contentEquals(sesName.trim()))
		    err = "CardHolderName ";
		if(!streetAddress.trim().contentEquals(sesAddress.trim()))
			err += "AddressLine ";
		if(!region.trim().contentEquals(sesRegion.trim()))
			err += "State " ;
		if(!city.trim().contentEquals(sesCity.trim()))
			err = "City ";
		if(!zip.trim().contentEquals(sesZip.trim()))
			err = "PostalCode ";
		
		if(!err.isEmpty()) {			
		
			err = "Credit Card Fields are not equal to Session for: " + err;
			
			this.reason = EndpointRuntimeReason.DETAILS_COMPARE_PARAMETERS;
			
		    EhrLogger.throwIllegalArg(GetOrderDetails.class, 
				"compareRequestParamsToSessionCustomer", err);
		}
	}
	
	
	private void initCardHolderFromPayer(PaymentDetails details, Order order, 
			Customer customer, RequestContext request) {		
	 
		     details.setPayerId(order.payer().payerId());		 
		 
		     String fullName = order.payer().name().givenName().toUpperCase() + " "
				 + order.payer().name().surname().toUpperCase();
		 
		     details.setBillingName(fullName);				
		 
		     if(!order.payer().email().toLowerCase().equals(customer.getEmail().toLowerCase())) {			
			 
			     initMessageContext(request.getMessageContext(),
					 "Billing Email",
					    "Your billing email is not the same as your customer email '"
					    + customer.getEmail().toLowerCase() + "'. "
					    + "Do you want to cancel the payment?" ) ;
		     }		 
		
		     details.setBillingEmail(order.payer().email().toLowerCase());			
	}
	
	
	
	/*private void debugPrintHeaders(HttpResponse<Order> response) {
	
	Headers headers = response.headers();
	
	if(headers == null) return;
	
	Iterator<String> it = headers.iterator();
	
	if(it == null) return;
	
	if(!it.hasNext()) {
		System.out.println("GetOrderDetails#debugPrintHeaders: no headers");
		return;
	}
	
	    System.out.println("GetOrderDetails#debugPrintHeaders: Printing Headers");
	    
	    String key="";
	    String value="";
	
	    while(it.hasNext()) {
	    	System.out.println("it.hasNext");
		    key = it.next();
		    System.out.println("key=" + key);
		    if(key != null) {
		      value = headers.header(key);
		      System.out.println(key + "=" + value);
		    }
	    }
	
}*/
	/*private void debugPrintAuthorization(Order order) {

		  PurchaseUnit purchaseUnit = order.purchaseUnits().get(0);
	      
	      PaymentCollection payments = purchaseUnit.payments();
	      
	      if(payments == null) {
	    	  System.out.println(
	    			  "GetOrderDetails#debugPrintAuthorizations: PaymentCollection is null");
	    	  return;
	      }
	      List<Authorization> list = payments.authorizations();	 
	      
	      if(list == null || list.isEmpty())
	    	  System.out.println(
	    			  "GetOrderDetails#debugPrintAuthorizations: authorization list is null");	      
	} */

} //end class
