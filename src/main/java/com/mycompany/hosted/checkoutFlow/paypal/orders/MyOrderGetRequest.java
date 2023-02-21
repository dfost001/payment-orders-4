package com.mycompany.hosted.checkoutFlow.paypal.orders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.mycompany.hosted.checkoutFlow.paypal.orders.MyOrder;
import com.paypal.http.HttpRequest;
/*
 * Not used
 */

public class MyOrderGetRequest extends HttpRequest<MyOrder> {
	
     String paramFields = "fields=payment_source"	;
	
	
	  public MyOrderGetRequest(String orderId) {
		  
	        super("/v2/checkout/orders/{order_id}?fields=payment_source", "GET", MyOrder.class);
	        
	        try {
	        	
	            path(path().replace("{order_id}", URLEncoder.encode(String.valueOf(orderId), "UTF-8")));
	            
	            //path(path() + paramFields);
	            
	        } catch (UnsupportedEncodingException ignored) {}

	        header("Content-Type", "application/json");
	        
	        header("Prefer", "return=representation");
	    }

}
