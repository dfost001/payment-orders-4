<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>

<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">    
	<title>Payment Details</title>
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
	<meta charset="UTF-8" />
	<style>
	  .divBillTo {
	     width: 450px;
	     float: left;
	     margin-left: 20px
	  }
	  .divShipTo{
	     width:350px;
	     float:right;
	     margin-right:100px
	  }
	  .spTransactionId {
	     font-size: 12pt;
	     color: #3366AA
	  }
	  .divTransactionId {
	     width: 400px;
	     height: 45px;
	     border: 1px solid #BBBBBB;
	     background-color: #EEEEEE;
	     padding: 15px;
	     border-radius: 5px
	  }
	</style>
</head>
  
<body>

    <div class="container" style="padding-bottom:50px">
    
	<jsp:include page="flowHeader.jsp" />
	
	<c:if test="${not empty allMessages}">
	   <div class="alert alert-danger">
	    <ul>	       
	           <li>${allMessages[0].text} &nbsp; 
	                 <a href="${flowExecutionUrl}&_eventId=cancel" class="alert-link">Cancel</a></li>	       
	    </ul>
	   </div>
	</c:if>
    
    <c:choose>
       <c:when test="${empty details.transactionId}">
           <h2>Review Payment</h2>
       </c:when>
       <c:otherwise>
           <h2>Payment Completed</h2>
       </c:otherwise>    
    </c:choose>
     
     <form action="${flowExecutionUrl}" method="post">
     
      
     <jsp:include page="paymentHeaderInclude/reviewPayment.jsp" />
     
     
	 </form>   
	   
	 	<div class="divBillTo">
	 	<fieldset>	
	 	<legend>Billing Information:</legend> 	
		<table class="table">
		   <tr>
		     <td><label>Payment Id:</label></td>
		     <td>${details.payPalResourceId}</td>
		   </tr>
		   <tr>
		     <td><label>Payment Status:</label></td>
		     <td>${details.createdStatus}</td>
		   </tr>
		   <tr>
		     <td><label>Order Created:</label></td>
		     <td>${details.createTime}</td>
		   </tr>
		   <tr>
		     <td><label>Funding Source:</label></td>
		     <td style="font-style:italic">****${details.lastDigits}&nbsp;
		       ${details.cardType}&nbsp;${details.expiry}</td>
		   </tr>
		   <tr>
		     <td><label>Billing Name:</label></td>
		     <td>${details.billingName}</td>
		   </tr>
		   <tr>
		    <td><label>Billing Email:</label></td>
		    <td>Funding details will be sent to: <label>${details.billingEmail}</label>
		   </tr>		  
		   <c:if test="${not empty details.payerId}">
		   <tr>
		     <td><label>Payer Id:</label></td>
		     <td>${details.payerId}</td>
		   </tr></c:if>
		   <tr>
		   <td colspan="2" style="font-style:italic">${customer.firstName} ${customer.lastName}: Customer since 
		      <fmt:formatDate pattern="EEE, d MMM yyyy" type="date" value="${customer.dtCreated}"/> </td>
		     
		   </tr>
		</table>
		</fieldset>	
		
		<jsp:include page="includes/cartItems.jsp"></jsp:include>
			
		</div><!-- end divDetails 1 -->
		
		<div class="divShipTo">
		
		   <fieldset>
		   <legend>Shipping Information</legend>
		
		   <jsp:include page="includes/shippingAddress.jsp"></jsp:include>
		   
		   </fieldset>
		   
		   <br/><br/>	   
		   
		</div>
		
        <br style="clear:both" />
        
         <a href="#" id="support" style="font-weight:bold">
               Support <span class="glyphicon glyphicon-collapse-down"></span></a>
               
		 <blockquote style="font-size:10pt; display:none">
           ${details.json}
        </blockquote>
        <br/>   
        
        <script>
           $("#support").click(function(ev){
        	   
        	   ev.preventDefault();
        	   
        	   $(this).next().slideToggle();
        	   
           });
        
        </script>
        <jsp:include page="includes/support.jsp" />
     
    </div><!-- end container -->
     
</body>
</html>