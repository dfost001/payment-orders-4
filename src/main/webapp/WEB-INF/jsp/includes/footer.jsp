<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="todaysDate" value="<%= new java.util.Date() %>"/>

<div id="footer">
	<p style="color:blue">
	No rights reserved. Please visit our <a href="tc.jsp">Terms and Conditions</a> 
	page to find out why you probably don't want anything from this site.</p>
	<p style="color:blue" >
	 <fmt:formatDate value="${todaysDate}" type="date" pattern="EEE dd MMM yyyy" /></p>
</div>