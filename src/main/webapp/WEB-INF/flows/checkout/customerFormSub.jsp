<%@page contentType="text/html" pageEncoding="UTF-8"%>    
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>  

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Information Entry Form</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link
	href="${pageContext.request.contextPath}/spring/resources/css/bootstrap.css"
	rel="Stylesheet" />
<link
	href="${pageContext.request.contextPath}/spring/resources/css/styles.css"
	rel="Stylesheet" />
<link
	href="${pageContext.request.contextPath}/spring/resources/css/checkout.css"
	rel="Stylesheet" />

<script
	src="${pageContext.request.contextPath}/spring/resources/javascript/jquery-1.11.1.js"></script>
<script
	src="${pageContext.request.contextPath}/spring/resources/javascript/bootstrap.min.js"></script>

<script
	src="${pageContext.request.contextPath}/spring/resources/javascript/jquery.maskedinput.js"></script>	
	
	
	<style>
	  .customerInfoTable {
	        width:80%;
	        margin:auto
       }
       .customerInfoTable td {
           padding:8px
       }
	</style>
	
</head>

<body>
	<div class="container">

		<jsp:include page="flowHeader.jsp"></jsp:include>

		<h3>${myFlowAttrs.formTitle}</h3>

		<form:form method="post" commandName="address">
			<table class="customerInfoTable">
				<tr>
					<td colspan="2">
						<ul class="error">
							<c:forEach var="msg" items="${allMessages}">
								<li>${msg.text}</li>
							</c:forEach>
						</ul>
					</td>
				</tr>
				<tr>
					<td><label>First Name:</label></td>
					<td><form:input path="firstName" size="40" /><br /> <form:errors
							path="firstName" class="error" /></td>
				</tr>
				<tr>
					<td><label>Last Name:</label></td>
					<td><form:input path="lastName" size="40" /><br /> <form:errors
							path="lastName" class="error" /></td>
				</tr>
				<tr>
					<td><label>Address:</label></td>
					<td><form:input path="address" size="40" /><br /> <form:errors
							path="address" class="error" /></td>
				</tr>
				<tr>
					<td><label>City:</label></td>
					<td><form:input path="city" size="40" /><br /> <form:errors
							path="city" class="error" /></td>
				</tr>
				<tr>
					<td><label>State:</label></td>
					<td><form:select path="state">
							<form:option label="Please select a state" value="" />
							
							<c:forEach var="st" items="${states}">
							  <option value="${st.stCode}"
							  <c:if test="${st.stCode eq address.state}">selected</c:if>> 
							     ${st.stName}</option>
							</c:forEach>
							
						</form:select><br /> <form:errors path="state" class="error" /></td>
				</tr>
				<tr>
					<td><label>Postal Code:</label></td>
					<td><form:input path="postalCode" size="40" /><br /> <form:errors
							path="postalCode" class="error" /></td>
				</tr>
				<tr>
					<td><label>Notify Email:</label></td>
					<td><form:input path="email" size="40" /><br /> <form:errors
							path="email" class="error" /></td>
				</tr>
				<tr>
					<td><label>Phone:</label></td>
					<td><form:input path="phone" size="40" id="phone" /><br /> <form:errors
							path="phone" class="error" /></td>
				</tr>
				<tr>
					<td><label>Customer Created:</label></td>
					<td><form:input path="dtCreated" size="40" readonly="true" /><br />
						<form:errors path="dtCreated" class="error" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td><input type="submit" name="_eventId_submitCustomerInfo"
						value="Submit Changes" /> &nbsp;&nbsp; <input type="submit"
						name="_eventId_cancelCustomerInfo" value="Cancel Update" /></td>
				</tr>
			</table>
		</form:form>
       

	</div>
	<!-- end container -->
</body>
</html>