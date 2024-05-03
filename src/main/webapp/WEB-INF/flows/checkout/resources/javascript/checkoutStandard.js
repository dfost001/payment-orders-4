/**
 * 
 */
$(document).ready(function(){	
	
	var handleError = function(data) {		
			
		var message = "An error occurred. Please contact support to complete your order."
		
		
		$(".alert").slideDown(500);
		
		$("#alertContent").html(message);
		
		//Drop-Down information
		
		$("#errEmpty").html("");
		
		$("#exceptionType").html(data.exceptionType);
		
		$("#messages").html(data.messages);
		
		$("#handler").html(data.handler);		
		
	};
	
	var contextPath = function(){
		
		var href = window.location.href;
        
        var startPos = (location.protocol + "://").length;
        
        var startContextPosition = href.indexOf("/", startPos);           
        
        console.log("contextStartPosition=" + startContextPosition);
        
        var endContextPosition = href.indexOf("/", startContextPosition + 1);
        
        console.log("endContextPosition=" + endContextPosition);
        
       // Does not include trailing '/'
        var baseUrl = href.substring(startContextPosition, endContextPosition); 
        
        console.log("checkoutButtons#contextPath=" + baseUrl);
        
        return baseUrl;
	}
	
	 try {
	      paypal.Buttons({    	 
	    	fundingSource: paypal.FUNDING.PAYPAL,	    	
	        createOrder: function() {
	          return fetch(contextPath() + '/spring/paypal/order/create', {
	        	  
	        	  method: 'post',
	        	  headers: {
	        		  'content-type' : 'application/json',
	        		  'accept': 'application/json'
	        	  }
	          }).then(function(res) {        	  
	        	  
	        	  return res.json();
	        	  
	          }).then(function(data) {
	        	  
	        	  console.log("createOrder: "  + JSON.stringify(data)) ;
	        	  
	        	  if(data.id)	        	  
	       	          return data.id;
	        	  else handleError(data);
	    	  
	             });		  
	        },
	        onApprove: function(data, actions) { //Does not execute if Id not returned at createOrder
	        	
	        	console.log("onApprove: " + JSON.stringify(data));
	        	
	        	console.log("onApprove#actions: " + JSON.stringify(actions));
	        	
	        	if(!data.orderID)
	        		throw ("onApprove: Data does not contain an orderID");
	        	
	        	$("input[name='paymentId']").val(data.orderID);
	        	
	        	console.log("paymentId assigned: " + $("input[name='paymentId']").val());
	        	
	        	$("#myModal").modal("show");	        	
	        },
            onError: function(err) {
	    		
	    		$(".alert").slideDown(500);
	    		
	    		$("#alertContent").html("An error occurred. " + err);
	    		
	    	}
	      }).render('#paypal-button-container'); // Display payment options on your web page
	    } catch(e) {
	    	  alert("Error caught: " + e);}
	    
	    
	
}); //end ready
