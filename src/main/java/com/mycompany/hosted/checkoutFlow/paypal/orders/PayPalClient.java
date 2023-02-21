package com.mycompany.hosted.checkoutFlow.paypal.orders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.mycompany.hosted.exception_handler.EhrLogger;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;

@Component
public class PayPalClient {
	
	protected String clientId;
	
	protected String clientSecret;
	
	private PayPalEnvironment environment;
	
	private PayPalHttpClient client;
	
	public PayPalClient() {		
		
		initCredentials();
		
        System.out.println("PayPalClient constructor: clientId=" + clientId);
		
		System.out.println("PayPalClient constructor executing: clientSecret=" + clientSecret);
		
		environment = new PayPalEnvironment.Sandbox(
			    this.clientId,
			    this.clientSecret);
		
		client = new PayPalHttpClient(environment);
		
	}
	
	private void initCredentials() {
		
        Properties properties = new Properties();
		
		InputStream is = this.getClass().getResourceAsStream("paypal-cred.properties") ;
		
		try {
		
		    properties.load(is);
		    
		} catch (IOException e) {
			
			throw new IllegalArgumentException(EhrLogger.doMessage(this.getClass(), 
					"initCredentials", "Unable to load PayPal credentials: " + e.getMessage()));
			
		}
		
		clientId = properties.getProperty("user") ;
		
		clientSecret = properties.getProperty("secret") ;		
		
		
	}
	
	public String getClientId() {
		
		return this.clientId;
	}
	
	public String getClientSecret() {
		
		return this.clientSecret;
	}
	
	  public PayPalHttpClient client() {
		    return this.client;
	  }
	
	
	/**
	   *Set up the PayPal Java SDK environment with PayPal access credentials.  
	   *This sample uses SandboxEnvironment. In production, use LiveEnvironment.
	   */
	 /* private PayPalEnvironment environment = new PayPalEnvironment.Sandbox(
	    this.clientId,
	    this.clientSecret);*/

	  /**
	   *PayPal HTTP client instance with environment that has access
	   *credentials context. Use to invoke PayPal APIs.
	   */
	//  PayPalHttpClient client = new PayPalHttpClient(environment);

	  /**
	   *Method to get client object
	   *
	   *@return PayPalHttpClient client
	   */
	 

}
