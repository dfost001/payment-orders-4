package com.mycompany.hosted.checkoutFlow.jpa;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CustomerNotFoundException extends Exception implements Serializable {
	
	public CustomerNotFoundException(String message) {
		super(message);
	}

}
