

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>


<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html>
    <head>
        <title>Credit Card Details</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
   <link
	   href="${pageContext.request.contextPath}/spring/resources/css/bootstrap.css"	rel="Stylesheet" />
   <link
	  href="${pageContext.request.contextPath}/spring/resources/css/styles.css"	rel="Stylesheet" />
   <link
	  href="${pageContext.request.contextPath}/spring/resources/css/checkout.css"	rel="Stylesheet" />  
   <script
	  src="${pageContext.request.contextPath}/spring/resources/javascript/jquery-1.11.1.js"></script>
   <script
	  src="${pageContext.request.contextPath}/spring/resources/javascript/bootstrap.min.js"></script>
    <style>
       #paypal-button-container {
	     /* width: 450px; */
	     /* float: left; */
       }

      .divCart {
	     width: 450px;
	     float: left;
	     margin-left: 100px;
       }
       .modal-body p {
          text-align: center
       }
     
     }
    </style>

</head>
    <body>
     <div class="container">
     
      <jsp:include page="flowHeader.jsp"></jsp:include>
        
        <h2>Credit Card Details</h2>
        
         <br/>
        

<script src="https://www.paypal.com/sdk/js?client-id=${clientId}&commit=false&debug=false&components=buttons,funding-eligibility,marks"></script>

         <div id="paypal-mark-container"></div>
         
         
         
  <script>
  var FUNDING_SOURCES = [
	    paypal.FUNDING.PAYPAL,	    
	    paypal.FUNDING.PAYLATER,
	    paypal.FUNDING.CREDIT,
	    paypal.FUNDING.CARD
	];
  
  paypal.getFundingSources().forEach(function(fundingSource) {

	    // Initialize the marks
	    var mark = paypal.Marks({
	        fundingSource: fundingSource
	    });

	    // Check if the mark is eligible
	    if (mark.isEligible()) {

	        // Render the standalone mark for that funding source
	        mark.render('#paypal-mark-container');
	    }
	});
    try {
      paypal.Marks({    	 
    	
        createOrder: function() {
          return fetch('/payment-orders/spring/paypal/order/create', {
        	  
        	  method: 'post',
        	  headers: {
        		  'content-type' : 'application/json',
        		  'accept': 'application/json'
        	  }
          }).then(function(res) {        	  
        	  
        	  return res.json();
        	  
          }).then(function(data) {
        	  
        	  console.log("createOrder: "  + JSON.stringify(data)) ;
        	  
       	      return data.id;
    	  
             });		  
        },
        onApprove: function(data, actions) {
        	
        	console.log("onApprove: " + JSON.stringify(data));
        	
        	console.log("onApprove#actions: " + JSON.stringify(actions));
        	
        	$("#myModal").modal("show");
         
        	
        }
      }); // Display payment options on your web page
    } catch(e) {
    	  alert("Error caught");
      }
    </script>
    
    
    <!-- Bootstrap modal with a link that returns the details view -->
    
 <div class="modal fade" tabindex="-1" role="dialog" id="myModal">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
       
        <h4 class="modal-title">Review Details</h4>
      </div>
      <div class="modal-body">
        <p>Please authorize your payment: </p>
        <p>
           <a href="${flowExecutionUrl}&_eventId=reviewDetails" class="btn btn-info btn-lg">
               Review Payment Details </a>  
            
        </p>
      </div>
     
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

    <script>
    
    var options = {
    		backdrop: 'static', 
    		keyboard: false, //don't close on escape
    		show:false //don't show on initialize
    };
    
    $('#myModal').modal(options);
    
    </script>
    
    
    </div><!-- end bootstrap container -->
    </body>
</html>
