package com.paypal.orders;

import com.paypal.http.annotations.Model;
import com.paypal.http.annotations.SerializedName;

@Model
public class ProcessorResponse {
	
	public ProcessorResponse() {}
	
	@SerializedName("avc_code")
	private String avsCode;
	
	public ProcessorResponse avsCode(String avs) {
		this.avsCode = avs;
		return this;
	}
	
	public String avsCode() {
		return this.avsCode;
	}
	
	public String getAvsCode() {
		return this.avsCode;
	}
	
	@SerializedName("cvv_code")
	private String cvvCode;
	
	public ProcessorResponse cvvCode(String cvv) {
		this.cvvCode = cvv;
		return this;
	}
	
	public String cvvCode() {
		return this.cvvCode;
	}
	
	public String getCvvCode() {
		return this.cvvCode;
	}
	
	@SerializedName("response_code")
	private String responseCode;
	
	public ProcessorResponse responseCode(String response) {		
		this.responseCode = response;
		return this;
	}
	
	public String responseCode() {
		return this.responseCode;
	}
	
	public String getResponseCode() {
		return this.responseCode;
	}

}
