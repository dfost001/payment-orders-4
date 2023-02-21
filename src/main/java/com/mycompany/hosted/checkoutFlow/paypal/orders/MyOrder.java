package com.mycompany.hosted.checkoutFlow.paypal.orders;

import java.util.List;

import com.paypal.http.annotations.Model;
import com.paypal.http.annotations.SerializedName;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Payer;
import com.paypal.orders.PaymentSource;
import com.paypal.orders.PurchaseUnit;
/*
 * Not used
 */
@Model
public class MyOrder {	

	    // Required default constructor
	    public MyOrder() {}

		/**
		* The intent to either capture payment immediately or authorize a payment for an order after order creation.
		*/
		@SerializedName("intent")
		private String checkoutPaymentIntent;

		public String checkoutPaymentIntent() { return checkoutPaymentIntent; }

		public MyOrder checkoutPaymentIntent(String checkoutPaymentIntent) {
		    this.checkoutPaymentIntent = checkoutPaymentIntent;
		    return this;
		}

		/**
		* The date and time, in [Internet date and time format](https://tools.ietf.org/html/rfc3339#section-5.6). Seconds are required while fractional seconds are optional.<blockquote><strong>Note:</strong> The regular expression provides guidance but does not reject all invalid dates.</blockquote>
		*/
		@SerializedName("create_time")
		private String createTime;

		public String createTime() { 
			return createTime;
		}

		public MyOrder createTime(String createTime) {
		    this.createTime = createTime;
		    return this;
		}

		/**
		* The date and time, in [Internet date and time format](https://tools.ietf.org/html/rfc3339#section-5.6). Seconds are required while fractional seconds are optional.<blockquote><strong>Note:</strong> The regular expression provides guidance but does not reject all invalid dates.</blockquote>
		*/
		@SerializedName("expiration_time")
		private String expirationTime;

		public String expirationTime() { return expirationTime; }

		public MyOrder expirationTime(String expirationTime) {
		    this.expirationTime = expirationTime;
		    return this;
		}

		/**
		* The ID of the order.
		*/
		@SerializedName("id")
		private String id;

		public String id() { return id; }

		public MyOrder id(String id) {
		    this.id = id;
		    return this;
		}

		/**
		* An array of request-related [HATEOAS links](/docs/api/reference/api-responses/#hateoas-links). To complete payer approval, use the `approve` link with the `GET` method.
		*/
		@SerializedName(value = "links", listClass = LinkDescription.class)
		private List<LinkDescription> links;

		public List<LinkDescription> links() { return links; }

		public MyOrder links(List<LinkDescription> links) {
		    this.links = links;
		    return this;
		}

		/**
		* The customer who approves and pays for the order. The customer is also known as the payer.
		*/
		@SerializedName("payer")
		private Payer payer;

		public Payer payer() { return payer; }

		public MyOrder payer(Payer payer) {
		    this.payer = payer;
		    return this;
		}

		/**
		* An array of purchase units. Each purchase unit establishes a contract between a customer and merchant. Each purchase unit represents either a full or partial order that the customer intends to purchase from the merchant.
		*/
		@SerializedName(value = "purchase_units", listClass = PurchaseUnit.class)
		private List<PurchaseUnit> purchaseUnits;

		public List<PurchaseUnit> purchaseUnits() { return purchaseUnits; }

		public MyOrder purchaseUnits(List<PurchaseUnit> purchaseUnits) {
		    this.purchaseUnits = purchaseUnits;
		    return this;
		}

		/**
		* The order status.
		*/
		@SerializedName("status")
		private String status;

		public String status() { return status; }

		public MyOrder status(String status) {
		    this.status = status;
		    return this;
		}

		/**
		* The date and time, in [Internet date and time format](https://tools.ietf.org/html/rfc3339#section-5.6). Seconds are required while fractional seconds are optional.<blockquote><strong>Note:</strong> The regular expression provides guidance but does not reject all invalid dates.</blockquote>
		*/
		@SerializedName("update_time")
		private String updateTime;

		public String updateTime() { return updateTime; }

		public MyOrder updateTime(String updateTime) {
		    this.updateTime = updateTime;
		    return this;
		}
		
		@SerializedName("payment_source")
		private PaymentSource paymentSource;
		
		public PaymentSource paymentSource() {
			return paymentSource;
		}	
	    public MyOrder paymentSource(PaymentSource paymentSource) {
	    	
	    	this.paymentSource = paymentSource;	    	
	    	return this;
		}
	}//end MyOrder



