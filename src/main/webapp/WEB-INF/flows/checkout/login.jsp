<%@page contentType="text/html" pageEncoding="UTF-8"%>    
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
   <meta charset="UTF-8">
   <title>Checkout Login</title>
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
     
  </style>

</head>
<body>
   <div class="container">
      <jsp:include page="flowHeader.jsp"></jsp:include>
   
      <h3 style="text-align:center">Login</h3>

		<div style="margin: auto">
			<ul class="error">
				<c:forEach var="msg" items="${errors}">
					<li><span>${msg.text}</span></li>
				</c:forEach>
			</ul>
		</div><!-- end error -->

		<div class="divLogin">

			<form method="post" action="${flowExecutionUrl}">
				<label>Your Customer ID:</label><br /> 
				<input type="text" size="30" name="customerId" value="${errCustomerId}" /><br /> 
				<span class="error">${errors[0].text}</span><br />
				<br /> 
				<input type="submit" name="_eventId_submitId" value="Submit"
					class="btn btn-default" />
			</form>
			<br />
			<div class="divAnchor">
				<br /> 
				<a href="${flowExecutionUrl}&_eventId=create">Create Account</a>
				    &nbsp;&nbsp; 
				<a href="${flowExecutionUrl}&_eventId=cancel">Return to Shopping</a>
			</div>
		</div>
		<!-- end divLogin -->	
		
		<br/>
		<jsp:include page="includes/support.jsp" />

	</div><!-- end bootstrap container -->
</body>
</html>