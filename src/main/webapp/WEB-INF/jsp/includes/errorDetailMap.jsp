<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<style>
   .divErrDetail {
	   
	     border-bottom: 2px dotted black;
	     font-size:10pt
	  }
</style>
 <h5>Error Details:</h5>		       
		       <c:forEach var="entry" items="${errorDetailMap}">
		         <label>OrderId: ${entry.key}</label>
		         <c:forEach var="err" items="${entry.value}">
		          <div class="divErrDetail">
		            <label>ErrorDetailReason:</label>&nbsp;${err.errorDetailReason} <br/>
		            <label>Order #:</label>&nbsp;${err.localOrderId} <br/>
		            <label>Customer: </label>&nbsp;${err.order.customerId.id} <br/>
		            <label>Message:</label>&nbsp;${err.errMessage} <br/>
		            <label>Method:</label>&nbsp;${err.errMethod} <br/>
		            <label>Exception:</label>&nbsp;${err.exceptionClass.simpleName} <br/>
		            <label>Service Id:</label>&nbsp;${err.order.serviceDetail.serviceId} <br />
		            <label>Capture Id:</label>&nbsp;${err.svcTransactionId} <br />
		            <label>Time:</label>&nbsp;${err.errTime} <br/>
		         </div>
		        </c:forEach>
		      </c:forEach>