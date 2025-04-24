<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>



<!DOCTYPE html>

<html>

<head>
   <title>All books for our store</title>
   <meta name="viewport" content="width=device-width, initial-scale=1">     
	<link href="../../resources/css/bootstrap.css" rel="Stylesheet" />
	<link href="../../resources/css/styles.css" rel="Stylesheet" />	
	<script src="../../resources/javascript/jquery-1.11.1.js"></script>
	<script src="../../resources/javascript/bootstrap.min.js"></script>
	<script src="../../resources/javascript/allBooks.js"></script>
</head>
  
<body>

    <div class="container">       
       
	
	   <jsp:include page="includes/header.jsp"></jsp:include>
	  
	  <c:if test="${selectionRequired ne null}">
	   <div class="alert alert-danger" id="selectAlert" style="position:relative">
		
		<span id="selectionRequired">${selectionRequired}</span>
		
		<button type="button" class="close fade in"
			data-dismiss="alert">
			<span>&times;</span>
		</button>
		
	  </div>
	  </c:if>
	  
	 
		    <c:if test="${not empty successMessages}">
		        <ul>
					<c:forEach var="message"
							items="${catalogueController.successMessages}">
						<li style="color: #00AA00">${message}</li>
								
					</c:forEach>
				</ul>
		    </c:if>
		    <c:if test="${not empty failMessages}">
		        <ul>
					<c:forEach var="message"
							items="${catalogueController.failMessages}">
						<li style="color: #FF0D20">${message}</li>
								
					</c:forEach>
				</ul>
		    </c:if>
		
	
   <form action="<c:url value='/spring/catalogueCart/update' />" method="post">	
   
	
		<table class="table" style="margin-top:20px">
		  <tr>
		     <td colspan="4"></td>		  
		   <c:choose >
		      <c:when test="${cart.count gt 0}">
		         <td><a href="<c:url value='/spring/viewCart/request' />" class="btn btn-info btn-sm">
		           Checkout</a> </td>
              </c:when>
              <c:otherwise>
                 <td>&nbsp;</td>
              </c:otherwise>
		    </c:choose>       
		   <td> <input type="submit" id="btnUpdate" value="Update Cart" class="btn btn-success btn-sm" /></td>
		  </tr>
		  <tr>
		    <th>ID</th>
		    <th>Title</th>
		    <th>Author</th>
		    <th>ISBN</th>
		    <th>Price</th>
		    <th>Select
		    </th>
		  </tr>     
		<c:forEach items="${catalogueController.allBooks}" var="book">
		  <tr>
		    <td><fmt:formatNumber 
		       type="number" 
		       value="${book.id}" 
		       groupingUsed="false" /></td> 
			<td>${book.title}</td>			
			<td>${book.author}</td>
			<td>${book.isbn}</td>
			<td><fmt:formatNumber 
			     value="${book.price}" 
			     type="currency" 
			     currencySymbol="$" /></td>
			 <td><input type="checkbox" 
			     name="bookChecked" 
			     value="${book.id}" 
			     <c:if test="${not empty cart.cartMap[book.id]}"> checked </c:if> />
			  </td>
	      </tr>	
	      <tr style="display:none">
                          <td colspan="4">&nbsp;</td>
                         
                          <td colspan="2"><label style='color:#ff6600'>Quantity:</label>&nbsp;
                              <input type="text" name="${book.id}"
                              value="${cart.cartMap[book.id].quantity}" /></td>
           </tr>		
		</c:forEach>		
		</table>
		
	</form>	
	</div><!-- end container -->
</body>

</html>