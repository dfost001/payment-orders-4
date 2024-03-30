package com.mycompany.hosted.checkoutFlow.paypal.orders;

import java.util.ArrayList;

public class PayPalErrorResponse {

	private String name;
    private String message;
   // private String[] informationLink;
    private String debugId;
    private ArrayList<PayPalErrorDetail> details;
    
    public PayPalErrorResponse(){}

    public String getDebugId() {
        return debugId;
    }

    public void setDebugId(String debugId) {
        this.debugId = debugId;
    }

  /*  public String getInformationLink() {
        return informationLink;
    }

    public void setInformationLink(String informationLink) {
    	this.informationLink = informationLink;
    }  */

    public ArrayList<PayPalErrorDetail> getDetails() {
        if(details == null)
            details = new ArrayList<PayPalErrorDetail>();
        return details;
    }

    public void setDetails(ArrayList<PayPalErrorDetail> details) {
        this.details = details;
    }    

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
	
}
