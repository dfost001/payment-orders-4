

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
	  	  
   <script src="https://www.paypal.com/sdk/js?client-id=${clientId}&commit=false&debug=false&components=buttons,funding-eligibility"></script>	  
 
   <script src="${pageContext.request.contextPath}/spring/resources/javascript/checkoutStandard.js"></script>
    <style>
       #paypal-button-container {
	      width: 350px;
	      float: left;
       }

      .divCart {
	     width: 450px;
	     float: left;
	     margin-left: 100px;
       }
       .modal-body p {
          text-align: center
       }
    </style>

</head>
    <body>
     <div class="container">
     
      <jsp:include page="flowHeader.jsp"></jsp:include>
      
      <form action="${flowExecutionUrl}" method="post">
      <div class="alert alert-danger" style="display:none;position:absolute;">
      
             <span id="alertContent"></span>&nbsp;             
             
             <input type="submit" name="_eventId_createPaymentError"
                      class="alert-link" value='Contact Support' />     
                             
       </div></form>       
        
        
        <h2>Credit Card Details</h2>
        
         <br/>         

         <div id="paypal-button-container"></div>
         
         <div class="divCart" style="font-size: 11pt">             
             <jsp:include page="includes/shippingAddress.jsp"></jsp:include>
             <jsp:include page="includes/cartItems.jsp"></jsp:include>
         
         
             <br /><br/>
        
             <a href="#" id="support" style="font-weight:bold">
               Support <span class="glyphicon glyphicon-collapse-down"></span></a>
               
		     <blockquote style="font-size:10pt; display:none" id="errContent">
                <p id="errEmpty">No error to report at this time.</p>
                <p>Exception Type: <span id="exceptionType"></span></p>
                <p>Handler: <span id="handler"></span></p>
                <p>Messages: <span id="messages"></span></p>
              </blockquote>
        
        </div><!-- end divCart -->
        
        <script>
           $("#support").click(function(ev){
        	   
        	   ev.preventDefault();
        	   
        	   $(this).next().slideToggle();
        	   
           });
        
        </script>
        
        <br style="clear:both" />
        
        <jsp:include page="includes/support.jsp" />
    
        <jsp:include page="includes/cardEntryModals.jsp" />
        

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
