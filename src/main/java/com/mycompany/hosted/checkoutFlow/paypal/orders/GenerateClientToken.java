package com.mycompany.hosted.checkoutFlow.paypal.orders;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.FieldNamingPolicy;
import com.mycompany.hosted.http.ApacheConnectBean2;
import com.mycompany.hosted.http.HttpClientException;
import com.mycompany.hosted.http.HttpConnectException;
import com.mycompany.hosted.http.HttpException;
import com.mycompany.hosted.http.ResponseUtil;


@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class GenerateClientToken {
	
	private String baseUrl = "https://api-m.sandbox.paypal.com";
	
	private String clientTokenUrl = "/v1/identity/generate-token";
	
	private String bearerTokenUrl = "/v1/oauth2/token";		
	
	@Autowired
	private PayPalClient payPalClient;
	
	@Autowired
	private ApacheConnectBean2 apacheConnect;
	
	
	public String generate() throws HttpException {
		
		String accessToken = "";
		
		String clientToken = "";
		
		try {
		
		 accessToken = doAuthorize();
		
		 clientToken = doClientToken(accessToken);
		
		//System.out.println("GenerateClientToken: " + clientToken);
		 
		} catch (HttpException ex) {			
			
			throw ex;
		}
		
		return clientToken;
		
	}
	
    private String doAuthorize() throws HttpException{
    	
    	String path = baseUrl + bearerTokenUrl;
    	
    	apacheConnect.setAuthProp(payPalClient.getClientId(), payPalClient.getClientSecret());
    	
    //	apacheConnect.setAccept("application/json");
    	
    	apacheConnect.setAccept("application/xml");
    	         
         HashMap<String,String> map = new HashMap<>();
         
         map.put("grant_type", "client_credentials");       
         
         byte[] jsonBytes = apacheConnect.doUrlEncodedFormPost(path, map); //throws HttpConnectException
         
         AccessTokenResponse tmpTokenResponse = (AccessTokenResponse)this.responseUtilEvaluate(jsonBytes, 
                 AccessTokenResponse.class, 
                 PayPalErrorResponse.class,
                 "doAuthorize"); //throws HttpClientException        
        
         // this.evalTokenResponse(tmpTokenResponse, jsonBytes); //throws PayPalExecuteException
      
       /*  System.out.println("GenerateClientToken: accessToken obtained: " + 
             tmpTokenResponse.getAccessToken());*/
         
         return tmpTokenResponse.getAccessToken();    	
    	
    }
    
  
    
    private String doClientToken(String bearer) 
    		throws HttpConnectException, HttpClientException {
    	
    	String path = baseUrl + this.clientTokenUrl;
    	
    	apacheConnect.setBearerAuthProp(bearer);
    	
    	apacheConnect.setAccept("application/json");
    	
    	byte[] jsonBytes = apacheConnect.doConnectPost(path, null, "");
    	
    	ClientTokenResult result =
    			(ClientTokenResult)this.responseUtilEvaluate(jsonBytes, ClientTokenResult.class, 
    			PayPalErrorResponse.class, "doClientToken");
    	
    	return result.getClientToken();
    }
    
    private Object responseUtilEvaluate(byte[] json, 
            Class<?> responseType, Class<?> errType, String method) throws HttpClientException{
        
         ResponseUtil util = new ResponseUtil(apacheConnect.getResponseCode(),
              apacheConnect.getHeaders(),
              json);
         
        
          Object obj = util.processResponse_JSON(
               responseType,
               errType,
               FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);             
          
          return obj;          
    }
}
