package com.mycompany.hosted.checkoutFlow.paypal.rest;

import com.mycompany.hosted.exception_handler.EhrLogger;

public class OrderId {
	
	private String id;
	
	public OrderId() {}
	
	public OrderId(String id) {
		
		if(id==null || id.isEmpty())
			EhrLogger.throwIllegalArg(this.getClass(), "constructor", 
					"Id parameter is null or empty");
		
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		
		this.id = id;
	}

}
