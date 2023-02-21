<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<table class="table" style="width:250px">
<caption>Ship To:</caption>
<tr>
  <td>${selectedAddress.firstName}&nbsp;${selectedAddress.lastName}</td></tr>
 <tr> 
  <td>${selectedAddress.address}</td></tr>
  <tr>
  <td>${selectedAddress.city}, ${selectedAddress.state} ${selectedAddress.postalCode}</td>
  </tr>
  <tr>
  <td>US</td>
  </tr>
</table>