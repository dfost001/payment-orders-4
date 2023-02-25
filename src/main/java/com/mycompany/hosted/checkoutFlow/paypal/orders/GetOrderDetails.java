package com.mycompany.hosted.checkoutFlow.paypal.orders;

import java.io.IOException;

import java.util.List;

import com.mycompany.hosted.checkoutFlow.MyFlowAttributes;
import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.formatter.StringUtil;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutHttpException;
import com.mycompany.hosted.checkoutFlow.exceptions.PaymentSourceNullException;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails.GetDetailsStatus;
import com.mycompany.hosted.checkoutFlow.paypal.rest.OrderId;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.model.Customer;
//import com.paypal.orders.Authorization;
//import com.paypal.http.Headers;
//import com.mycompany.hosted.model.order.PaymentStatusEnum;
import com.paypal.http.HttpResponse;
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
	
	 private boolean testException;	
	 
	 private String debugIntegrationType = "AdvancedCheckout" ;
	
	@Autowired
	private PayPalClient payClient;
	
	public GetOrderDetails() {
		
		testException = false;
	}
	
	 public String getOrder(RequestContext ctx, MyFlowAttributes flowAttrs) 
			 throws CheckoutHttpException, PaymentSourceNullException {		 
		  
		   
		   PaymentDetails paymentDetails = null;
		   
		   OrderId orderId = null;
		    
		    try {
		    	
		       orderId = evalOrderId(ctx); //throws IllegalArgument for null or not equal Id's
		       
		       Customer customer = handleNullCustomer(ctx);
		    
		       paymentDetails = new PaymentDetails(orderId.getId());		       
		    
		       if(debugIntegrationType.contentEquals("AdvancedCheckout" )) {
		    	   
		    	  compareAndInitPayerFromSession(paymentDetails, customer, ctx, 
		    			  flowAttrs.isErrorGetDetails());
		    	  
		    	  flowAttrs.setErrorGetDetails(false);
		       }
		       if(testException) {
				   
			      handleTestException(ctx);
		       }
		 
		       OrdersGetRequest request = new OrdersGetRequest(orderId.getId());	
		    
		       HttpResponse<Order> response = payClient.client().execute(request);	//throws IOException			   
		    
		       String err = debugPrintOrder(response);
		    
		       if(!err.isEmpty())
		    	throw new IllegalArgumentException(err);
		    
		       initOrderDetails(paymentDetails,response, ctx, customer);
		    
               ctx.getExternalContext().getSessionMap()
                 .put(WebFlowConstants.PAYMENT_DETAILS, paymentDetails);      
              
		    
		    } catch(IOException | IllegalArgumentException | PaymentSourceNullException ex) {
		    	
		        CheckoutHttpException httpEx = new CheckoutHttpException(ex, "getOrder");
		    	
		    	ctx.getExternalContext()
		    	   .getSessionMap()
		    	   .put("checkoutHttpException", httpEx);
		    	
		    	throw httpEx;
		   /* } catch (PaymentSourceNullException ex)	{		    	
		    	ctx.getFlashScope().put("paymentSourceNullException", ex);		    	
		    	throw ex; */
		    } catch (Exception ex) {
		    	throw ex;
		    }		  
		    
		    GetDetailsStatus detailsStatus = paymentDetails.getCreatedStatus();
		    
		    if(detailsStatus == GetDetailsStatus.CREATED) //For  Advanced checkout only
		    	return "success";		    
		    else if(detailsStatus == GetDetailsStatus.APPROVED) //For Standard or PayPal login
		    	return "success";
		    else return "failed";		   
	  } 
	 
  private Customer handleNullCustomer(RequestContext request) {
	  
	  Customer customer = (Customer)request.getExternalContext().getSessionMap()
				 .get(WebFlowConstants.CUSTOMER_KEY);
		 
		 if(customer == null) {	 			
			 
			 EhrLogger.throwIllegalArg(this.getClass(),
					 "initOrderDetails", "Customer is null in the session");
		 }
		 
		 return customer;
  }
	 
  private void handleTestException(RequestContext ctx) throws CheckoutHttpException {
	  
	   testException = false;			    
	    
	    CheckoutHttpException ex = new CheckoutHttpException(new Exception("Testing Exception"),
	    		"getOrder");
	    
	    ex.setTestException(true);
	    
	    ctx.getExternalContext()
  	   .getSessionMap()
  	   .put("checkoutHttpException", ex);
	    
		throw ex;
  }
   
	 
	private OrderId evalOrderId(RequestContext ctx) {
		
	  SharedAttributeMap<Object> sharedSession = ctx.getExternalContext().getSessionMap();   		   
		 
	  OrderId orderId = (OrderId)sharedSession.get(WebFlowConstants.PAYPAL_SERVER_ID);		   
		   
	  String paramId = (String)sharedSession.get(WebFlowConstants.PAYPAL_SCRIPT_ID); 		  
		
		String err = "";
		
		if (orderId == null || orderId.getId() == null || orderId.getId().isEmpty())
			err += "Cannot find created orderId in the session. ";
		
		if(paramId == null || paramId.isEmpty()) {
			err += "Cannot find Script orderId in the session. ";
		}	
		
		if (err.isEmpty() && !orderId.getId().contentEquals(paramId)) {
			System.out.println("GetDetails#evalOrderId: " + orderId.getId()
			  + ": " + paramId);
			err += "JavaScript orderId is not equal to created orderId";
		}
		
		if(!err.isEmpty())
			EhrLogger.throwIllegalArg(this.getClass(), "getOrder", err);
		
		return orderId;
		
	}
	 /*
	  * Note: Details created with the OrderId
	  */
	 private void initOrderDetails(PaymentDetails details,
			 HttpResponse<Order> response,
			 RequestContext request, Customer customer)
					 throws PaymentSourceNullException {	
		 
		 Order order = response.result();
		 
		 details.setCreatedStatus(GetDetailsStatus.valueOf(order.status()));
		 
		 String json = debugPrintJson(response);
		 
		 details.setJson(json);
		 
		 details.setCreateTime(order.createTime());		 		
		 
		 if(debugIntegrationType.equals("AdvancedCheckout"))		 
		     initPaymentSourceOrThrow(order, details);	//throws PaymentSourceNull 	
		 
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
		
		System.out.println("GetOrderDetails: responseCode=" + response.statusCode());	
		
		String err="";
		
		Order order = response.result();
		
		System.out.println("GetOrderDetails#debugPrintOrder:");
		
		if(order == null) {
			err += "Details: Order is null";
			System.out.println(err);
			return err;
		}
		String id = order.id();
		
		if(id == null || id.isEmpty()) {
			err += "Details: Order ID in deserialized response is null or empty";		
			System.out.println(err);
			return err;
		}
		
		List<PurchaseUnit> units = order.purchaseUnits();
		
		if(units == null || units.isEmpty()) {
			
			err += "List<PurchaseUnit> is empty";
			System.out.println(err);
			return err;
		}			
		
		
		return err; //empty message
	}

		
	private void initPaymentSourceOrThrow(Order order, PaymentDetails details) 
	     throws PaymentSourceNullException {	
		
		PaymentSource source = order.paymentSource();
		
		if(source == null) {
			System.out.println("GetOrderDetails#initPaymentSourceOrThrow: source is null");
			throw new PaymentSourceNullException();
		}
		if(source.card() == null) {
			System.out.println("GetOrderDetails#initPaymentSourceOrThrow: source.card is null");
			throw new PaymentSourceNullException();
		}
		
		Card card = source.card();
		
		System.out.println("GetOrderDetails: "
				+ "Card#lastDigits=" + card.lastDigits() + " expiry: " + card.expiry()
				+ " brand=" + card.brand());
		
		AddressPortable address = card.addressPortable();
		
		if(address == null)
			System.out.println("Card#PortableAddress is null");
		
		details.setLastDigits(card.lastDigits());		
		 
		details.setExpiry(card.expiry()); //empty without another connection
		
		details.setCardType(card.brand());
		
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
				region,city, zip))
			EhrLogger.throwIllegalArg(GetOrderDetails.class, 
					 "compareRequestParamstoSessionCustomer", "Missing request parameter(s)");
		
		String err = "";
		
		if(!cardHolderName.contentEquals(sesName))
		    err = "CardHolderName ";
		if(!streetAddress.contentEquals(sesAddress))
			err += "AddressLine ";
		if(!region.contentEquals(sesRegion))
			err += "State " ;
		if(!city.contentEquals(sesCity))
			err = "City ";
		if(!zip.contentEquals(sesZip))
			err = "PostalCode ";
		
		if(!err.isEmpty()) {			
		
			err = "Credit Card Fields are not equal to Session for: " + err;
		
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
					    + "Please verify." ) ;
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
