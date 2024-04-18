<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
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
	
	<style>
	  li {padding:3px}
	 
	</style>
	
</head>
  
<body>
  <div class="container-fluid" style="padding-bottom:50px">
  
  <jsp:include page="includes/header.jsp"></jsp:include>
  
  <div style="width:1000px;margin:auto">
  
  <h2 style="color:#036fab">Payment Error Support</h2><br/>
  
  <div class="alert alert-danger">
    <ul>
      <li style="font-weight:bold">${checkoutErrModel.friendly}</li>
      <c:if test="${checkoutErrModel.recoverable}">
        <li>This error may be recoverable:  &nbsp;&nbsp;
             <a href="<c:url value='/spring/${checkoutErrModel.retUrl}' />" class="alert-link">Retry Checkout</a></li>
      </c:if>
      <li>You may contact support to complete your order: <span>123-1234</span></li>
      
    </ul>
  </div>
  
     <div style="width:1000px">
      <div style="width:475px;float:left">
      
         <jsp:include page="../flows/checkout/includes/shippingAddress.jsp" />
         <jsp:include page="../flows/checkout/includes/billingAddress.jsp" />
          <c:if test="${not empty errorDetailMap}">
              <a href="#" id="supportDebug" style="font-weight:bold;">
               Error Detail <span class="glyphicon glyphicon-collapse-down"></span></a>
             <div style="display:none">  
		        <jsp:include page="includes/errorDetailMap.jsp" />
		     </div>   
		   </c:if>
       
      </div><!-- end address container -->
      
      <div style="width:525px;float:left;padding:30px">
      
         <jsp:include page="../flows/checkout/includes/cartItems.jsp" /> <br/>
         
           <a href="#" id="support" style="font-weight:bold;">
               Support <span class="glyphicon glyphicon-collapse-down"></span></a>
               
          <blockquote style="font-size:11pt; display:none" id="errContent">
          
          <c:choose>
            <c:when test = "${empty checkoutErrModel.payPalError}">
              <p><label>Message:</label> <span><c:out value="${checkoutErrModel.message}" escapeXml="false" />
                          </span></p>  
            </c:when>
            <c:otherwise>
               <p><label>Name: </label> <span>${checkoutErrModel.payPalError.name}</span></p>
               <p><label>Message:</label> <span>${checkoutErrModel.payPalError.message}</span></p>
               <p><label>Details:</label></p>
               <c:forEach var="err" items="${checkoutErrModel.payPalError.details}">
                 <blockquote style="font-size: 10pt">
                     <p>Field: <span>${err.field}</span></p>
                     <p>Issue: <span>${err.issue}</span></p>
                     <p>Value: <span>${err.value}</span></p>                     
                     <p>Description: <span>${err.description}</span></p>
                 </blockquote>
               </c:forEach>
            </c:otherwise>
          </c:choose>          
           
           <p><label>Exception UUID:</label> <span>${checkoutErrModel.uuid}</span></p>
           
           <p><label>Cause:</label> <span>${checkoutErrModel.cause}</span></p>
           
           <p><label>Reason (Error Code):</label> <span>${checkoutErrModel.exception.reason}</span></p> 
           
           <p><label>PayPal Resource Id:</label> <span>${checkoutErrModel.exception.payPalId}</span></p>
		 
		   <p><label>Response Status:</label> <span>${checkoutErrModel.responseCode}</span></p>
          
           <p><label>Recoverable:</label> <span>${checkoutErrModel.recoverable}</span></p>           
           
           <p><label>Error Content-Type:</label> <span>${checkoutErrModel.errContentType}</span>            
           
           <p><label>Friendly:</label> <span>${checkoutErrModel.friendly}</span></p>                
           
           <p><label>Error Method:</label> ${checkoutErrModel.errMethod}</p>
           
           <p><label>Local Order Id:</label> ${checkoutErrModel.exception.persistOrderId} </p> 
           
           <p><label>Captured Transaction Id:</label> ${checkoutErrModel.exception.capturedPaymentId} </p>
           
           <p><b>Message Trace:</b> <br/>
              <c:out value="${checkoutErrModel.messageTrace}" escapeXml="false" /></p>
           
        </blockquote>     
         
      </div><!-- end cart info -->
     </div><!-- end info container -->   
        
          <script>
           $("#support").click(function(ev){
        	   
        	   ev.preventDefault();
        	   
        	   $(this).next().slideToggle();
        	   
           });
           $("#supportDebug").click(function(ev){
        	   
        	   ev.preventDefault();
        	   
        	   $(this).next().slideToggle();
        	   
           });
        </script>
	</div> <!-- end div width:1000px -->  
 </div><!-- end container -->
</body>
</html>