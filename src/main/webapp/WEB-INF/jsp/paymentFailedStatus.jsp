<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>

<head>
   <title>Support</title>
   <meta name="viewport" content="width=device-width, initial-scale=1">     
	<link href="${pageContext.request.contextPath}/resources/css/bootstrap.css" rel="Stylesheet" />
	<link href="${pageContext.request.contextPath}/resources/css/styles.css" rel="Stylesheet" />	
	<script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js"></script>
	<script src="${pageContext.request.contextPath}/resources/javascript/bootstrap.min.js"></script>
	
</head>
  
<body>
  <div class="container">
  
  <jsp:include page="includes/header.jsp"></jsp:include>
  
  <div style="width:650px;margin:auto">
  
  <h2 style="color:#036fab">Payment Failed Support</h2><br/>  
  
       <h4 style="color:rgb(245,66,66)">We are unable to complete your payment.</h4> 
       
       Customer: ${customer.firstName} ${customer.lastName}
	   
	   Customer Id: <span>${customer.id}</span><br/><br/>  
  
      <p style="color:#036fab"> You may contact support to complete your order: <span>123-1234</span></p>
       
      <p style="color:#036fab">You may be required to contact the card-issuer to resolve the problem. </p>
       
       <div class="alert alert-danger">
         <ul>
           <c:forEach var="msg" items="${MESSAGE_LIST_KEY}">
            <li style="font-size:11pt;font-style:italic"> ${msg} </li>
           </c:forEach>
         </ul>
       </div>
	   
	   <p style="font-weight:bold">Please have the following information:</p>
	   
	     <c:if test="${not empty details.processorResponse}">    
            <p> Card Processor Codes: </p>  
             <blockquote style="font-size: 10pt">
              
              CVV: <span> ${details.processorResponse.cvvCode}</span><br/>
              Card Number: <span>${details.processorResponse.responseCode}</span><br/>
              Address: <span>${addrCodeValue}</span><br/>
             </blockquote>
        </c:if>	 
        
        <p> Card Information </p>
        <blockquote style="font-size: 10pt">
          Last Digits: ${details.lastDigits} <br/>        
          Card Type: ${details.cardType}<br/>
          Expiration: ${details.expiry} <br/>
        </blockquote> 
	   
	   <p>PayPal Transaction Information
	   <blockquote style="font-size: 10pt"> 
	   
	   <p>Transaction Id: <span>${details.transactionId}</span>
	   
	   <p>PayPal Resource Id: ${details.payPalResourceId}
	   
	   <p>Get Details Created Status: <span>${details.createdStatus}</span></p>
          
       <p>Captured Status: <span>${details.captureStatus}</span></p>       
           
       <c:if test="${not empty details.statusReason}">
             <p>Failed Capture Reason: <span>${details.statusReason}</span></p>   
       </c:if>    
            
	  </blockquote>
	    
	    <h5><a href="<c:url value='/spring/catalogue/view'/>" style="font-weight:bold">
	          Return Home</a></h5>
	    
	      <a href="#" id="support" style="font-weight:bold">
               Support <span class="glyphicon glyphicon-collapse-down"></span></a>
               
          <blockquote style="font-size:10pt; display:none" id="errContent">
          
           <p>Transaction Id: <span>$(details.transactionId}</span>
		 
		   <p>Get Details Created Status: <span>${details.createdStatus}</span></p>
          
           <p>Captured Status: <span>${details.captureStatus}</span></p>
           
           <p>Failed Capture Reason: <span>${details.statusReason}</span></p>              
           
           <p> Card Processor : </p>         
           
            <blockquote>
              Address: <span>${avs_code}</span>
              CVV: <span> ${cvv_code}</span>
              Card Number: <span>${response_code}</span>
             </blockquote>      
           
          </blockquote>     
        
          <script>
           $("#support").click(function(ev){
        	   
        	   ev.preventDefault();
        	   
        	   $(this).next().slideToggle();
        	   
           });
        
        </script>
	</div>   
 </div><!-- end container -->
</body>
</html>