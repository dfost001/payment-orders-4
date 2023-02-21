package com.mycompany.hosted.checkoutFlow.paypal.orders;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class AccessTokenResponse implements Serializable{
	
	  public enum TokenTypeEnum  {Bearer};
	    
	    private String scope = "";
	    private String accessToken ;
	    private TokenTypeEnum tokenType;
	    private String appId = "";
	    private int expiresIn = 0;
	    private ArrayList<String> authnSchemes;
	    
	    public AccessTokenResponse(){}

	   
	    public String getScope() {
	        return scope;
	    }

	    public void setScope(String scope) {
	        this.scope = scope;
	    }

	    public String getAccessToken() {
	        return accessToken;
	    }

	    public void setAccessToken(String accessToken) {
	        this.accessToken = accessToken;
	    }

	    public String getAppId() {
	        return appId;
	    }

	    public void setAppId(String appId) {
	        this.appId = appId;
	    }

	    public ArrayList<String> getAuthnSchemes() {
	        return authnSchemes;
	    }

	    public void setAuthnSchemes(ArrayList<String> authnSchemes) {
	        this.authnSchemes = authnSchemes;
	    }

	    public int getExpiresIn() {
	        return expiresIn;
	    }

	    public void setExpiresIn(int expiresIn) {
	        this.expiresIn = expiresIn;
	    }

	    public TokenTypeEnum getTokenType() {
	        return tokenType;
	    }

	    public void setTokenType(TokenTypeEnum tokenType) {
	        this.tokenType = tokenType;
	    }


}
