<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<table class="table" style="width:250px">
<caption>Bill To:</caption>
<tr>
  <td>${customer.firstName}&nbsp;${customer.lastName}</td></tr>
 <tr> 
  <td>${customer.address}</td></tr>
  <tr>
  <td>${customer.city}, ${customer.state} ${customer.postalCode}</td>
  </tr>
  <tr>
  <td>US</td>
  </tr>
</table>