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
  
  <jsp:include page="flowHeader.jsp"></jsp:include>
  
  <div style="width:500px;margin:auto">
  
  <h2 style="color:#036fab">Webflow Payment Error Support</h2><br/>
  
  <div class="alert alert-danger">
      ${checkoutErrModel.friendly}
  </div>
  
   <c:if test="${checkoutErrModel.recoverable}">
	      <h4>This error may be recoverable:
	            <a href="<c:url value='/spring/${checkoutErrModel.retUrl}'/>">Retry Checkout</a></h4><br/>
	   </c:if>
  
       <h4>You may contact support to complete your order: <span>123-1234</span></h4>
	   
	   <h4>Customer: ${customer.firstName} ${customer.lastName}</h4>
	   
	   <h4>Customer Id: <span>${customer.id}</span></h4>   
	    
	    
	    <h5><a href="<c:url value='/spring/catalogue/view'/>" style="font-weight:bold">
	          Return Home</a></h5>
	    
	      <a href="#" id="support" style="font-weight:bold">
               Support <span class="glyphicon glyphicon-collapse-down"></span></a>
               
          <blockquote style="font-size:10pt; display:none" id="errContent">
		 
		   <p>Response Status: <span>${checkoutErrModel.responseCode}</span></p>
          
           <p>Messages: <span>${checkoutErrModel.messageTrace}</span></p>
           
           <p>Error Method: <span>${checkoutErrModel.errMethod}</span></p>          
           
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