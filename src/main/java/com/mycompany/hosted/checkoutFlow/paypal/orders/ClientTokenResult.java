package com.mycompany.hosted.checkoutFlow.paypal.orders;

public class ClientTokenResult {
	
	private String clientToken;
	private String idToken;
	private int expiresIn;
	
	
	public ClientTokenResult() {}
	
	public String getClientToken() {
		return clientToken;
	}
	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}
	public String getIdToken() {
		return idToken;
	}
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}
	public int getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	

}
