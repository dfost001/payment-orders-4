package com.mycompany.hosted.checkoutFlow.paypal.orders;

public class PayPalErrorDetail {
	
	 private String field;
	 private String issue;
	 private String value;
	 private String location;
	 private String description;
	    
	    public PayPalErrorDetail(){}

	    public String getField() {
	        return field;
	    }

	    public void setField(String field) {
	        this.field = field;
	    }

	    public String getIssue() {
	        return issue;
	    }

	    public void setIssue(String issue) {
	        this.issue = issue;
	    }

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}  
	    

}
