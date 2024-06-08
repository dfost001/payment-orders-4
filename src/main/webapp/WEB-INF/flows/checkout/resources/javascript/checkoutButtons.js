/**
 * 
 */
$(document).ready(function(){		
	
    var contextPath = function(){
		
		var href = window.location.href;
		console.log("location=" + href);
        
        var startPos = (location.protocol + "://").length;
        
        var startContextPosition = href.indexOf("/", startPos);           
        
       // console.log("contextStartPosition=" + startContextPosition);
        
        var endContextPosition = href.indexOf("/", startContextPosition + 1);
        
       // console.log("endContextPosition=" + endContextPosition);
        
       // Does not include trailing '/'
        var baseUrl = href.substring(startContextPosition, endContextPosition); 
        
        console.log("checkoutButtons#contextPath=" + baseUrl);
        
        return baseUrl;
	}
	
    /*
     * Not used: Causing cardFieldsInstance passed to submit-callback
     * to be undefined.
     */
	   var addValidityHandler = function(hostedFieldsInstance){
	    	
	        hostedFieldsInstance.on('validityChange', function (event) {
				
			    var field = event.fields[event.emittedBy];
			    
			    console.log("Validity Handler: ");
			    
			    if (field.isValid) {
			      console.log(event.emittedBy + ' is fully valid');
			    } else if (field.isPotentiallyValid) {
			      console.log(event.emittedBy + ' is potentially valid');
			    } else {
			      console.log(event.emittedBy + ' is not valid');
			    }
			  });
	    	
	    };
	/*
	 * Scroll to global message and adjust margin on number division
	 * Otherwise, alert shows on top of input
	 */
	var showSubmitInvalidAlert = function() {
		
		
		 
		var alert =  $("#alertInvalidFields");
		
		alert.show();
		
		var headerPanel = $("#flowHeaderPanel");
		
		var panelHeight = headerPanel.height();
		
		var offset = headerPanel.offset();
		
		$('body, html').animate ({scrollTop: offset.top + panelHeight, 
			scrollLeft: offset.left});
		
		//Prevent alert from covering card input		
		
		var marginDivNumber =  alert.height();
		
		$("#divCardNumber").css("margin-top", marginDivNumber + "px" );
		
		
	};
	
	var assignAlertList = function (key) {
		
		var list = $("#alertList");
		
		var message = "";
		
		switch (key) {
		
		case "number" :
			message = "Card-Number: One or more digits are missing or invalid" ;
		    break;
		case "expirationDate" :
			message = "Expiration-Date: A future 2-digit month/year is required" ;
			break;
		case "cvv" :
			message = "CVV-Security-Code: Three or Four digits are required."		
		}
		
		var element = "<li>" + message + "</li>";		
		
		list.show();
		
		list.append(element);
		
	};
	
	var doFormValidity = function(hostedFieldsInstance) {
		
		 var state = hostedFieldsInstance.getState();		 
		 
		 $("#alertList").empty();
		 
		 var valid = true;
		 
		 valid = Object.keys(state.fields).every(function (key) {
			 
			  console.log("doValidity key= " + key);
			  
			  console.log("doValidity isValid= " + state.fields[key].isValid);
			 
		      if(!state.fields[key].isValid) {
		    	  valid = false;
		    	  assignAlertList(key);
		      }
		      return valid;
		 });
		 
		 return valid;
		
	};
	
 
	
	var doSubmitHandler = function(cardFields) {		
		
		document.querySelector("#card-form").addEventListener("submit", function(event) {
		  
			event.preventDefault();	
			
			var valid = doFormValidity(cardFields);
			
			if(!valid) {
				showSubmitInvalidAlert();
				return;			
			}
			
			try {	
			    cardFields.submit(
					
					{
						 cardholderName: document.getElementById("card-holder-name").value.trim(),
				          
				          billingAddress: {
				           
				            streetAddress: document.getElementById("card-billing-address-street").value,
				            
				            //extendedAddress: document.getElementById("card-billing-address-unit").value,
				           
				            region: document.getElementById("card-billing-address-state").value,
				           
				            locality: document.getElementById("card-billing-address-city").value,
				          
				            postalCode: document.getElementById("card-billing-address-zip").value,
				          
				            countryCodeAlpha2: document.getElementById("card-billing-address-country").value
				          }//end billing address
					} //end option
					
			); //end submit		
			    
            } catch (e) { 
				
				//not working for a custom error thrown invalid/empty order id
				
				alert("Submit callback exception:" + e);
				
				return;
										
			}	           
                          
			 $("input[name='cardHolderName']").val(
			 $("#card-holder-name").val().trim()); //Note:trim is important for GetDetails#compare, if testing rejected card
			 $("input[name='streetAddress']").val(
			 $("#card-billing-address-street").val());
			 $("input[name='region']").val(
			 $("#card-billing-address-state").val());
			 $("input[name='postalCode']").val(
			 $("#card-billing-address-zip").val());
			 $("input[name='city']").val(
			 $("#card-billing-address-city").val());
			 $("input[name='countryCode']").val(
			 $("#card-billing-address-country").val());		
			 
			 $("#myModal").modal("show");
			 
		}); //end addEventListener
	}; // end doSubmit	
	
	
	function doHostedFields() {		
		
		try {
	
	      paypal.HostedFields.render({    	 
	    	    	
	        createOrder: function() {        	 
	        	
	              return  fetch(contextPath() + '/spring/paypal/order/create', {
	        	  
	        	  method: 'post',
	        	  headers: {
	        		  'content-type' : 'application/json',
	        		  'accept': 'application/json'
	        	  }
	          }).then(function(res) {        	  
	        	  
	        	  return res.json();
	        	  
	          }).then(function(data) {
	        	
	        	    console.log("createOrder: "  + JSON.stringify(data)) ;	        	   
	        	  
	        	    if(data.id)	 {  
	        	    
	        	      $("input[name='paymentId']").val(data.id);	  
	        	      
	        	    //  window.localStorage.setItem("idObtained", "true");
	
	       	          return data.id;
	        	    }
	        	    else {
	        	    	
	        	    	//window.localStorage.setItem("idObtained", "false");
	        	    	
	        	    	$("#myModalError").modal("show");	
	        	    	
	        	    	//throw new Error("Server did not return an Id"); --not working
	        		   
	        	    }
	        	
	           });	        
	        },		       
            onError: function(err) {
            	
            	alert("onError: An error occurred");
	    		
	    		//$(".alert").slideDown(500);
	    		
	    		//$("#alertContent").html("onError: An error occurred. " + err);
	    		
	    	},
	    	 styles: {
	    	      ".valid": {
	    	        color: "green",
	    	      },
	    	      ".invalid": {
	    	        color: "red",
	    	      },
	    	  },
	    	  fields: {
	    	      number: {
	    	        selector: "#card-number",
	    	        placeholder: "4111 1111 1111 1111",
	    	      },
	    	      cvv: {
	    	        selector: "#cvv",
	    	        placeholder: "123",
	    	      },
	    	      expirationDate: {
	    	        selector: "#expiration-date",
	    	        placeholder: "MM/YY",
	    	      }
	    	    }
	    	    
	      }) //end render	      
	      .then(function(fields){	 
	    	   
	    	   doSubmitHandler(fields);
	    	 	
	      });
	     
	    } catch(e) {
	    	  alert("Error caught: " + e);}
	    
	  
	    
	} //end doHostedFields
	
	var doChangeLocation = function() {
		
		var postBackUrl = window.location.href;
		
		postBackUrl += "&_eventId=notEligible";
		
		window.location.href = postBackUrl;
		
	} ;
	
	var showErrorModal = function() {
		
		var button = $("#modalErrForm :submit");
		
		button.attr("name", "_eventId_notEligible"); //Bind to transition
		
		button.attr("value", "PayPal Login");
		
		var message = "Please pay with PayPal. An account is not necessary. <br />" 
			+ "You may pay with your credit card";
		
		$("#myModalError .modal-body #notEligible").html(message);
		
		$("#myModalError").modal("show");
		
	};
	
	/*
	 *  Bind standard integration view/script via input on error Modal (see showErrorModal) 
	 */
	if (!paypal.HostedFields.isEligible()) {
		//doChangeLocation();	
		showErrorModal();
		
	} else {
		doHostedFields();
	}	
	
	//showErrorModal();
}); //end ready
