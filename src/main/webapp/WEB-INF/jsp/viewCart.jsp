
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<html>

<head>

<meta name="viewport" content="width=device-width, initial-scale=1">
<link href="../../resources/css/bootstrap.css" rel="Stylesheet" />
<link href="../../resources/css/styles.css" rel="Stylesheet" />
<script src="../../resources/javascript/jquery-1.11.1.js"></script>
<script src="../../resources/javascript/bootstrap.min.js"></script>
<meta charset="UTF-8" />
<title>View Cart</title>
<style>
.cartTitle {
	margin: 20px auto;
	text-align: center;
	font-family: Arial, "Arial CE", "Lucida Grande CE", lucida,
		"Helvetica CE", sans-serif;
	border-bottom: 1px solid #AFAFAF;
	font-size: 24pt;
	font-weight: bold;
	padding: 5px;
	color: #ff6600;
}
.collapse {
    margin-left:8px
}
</style>
</head>

<body>
  <div class="container">
  
   <jsp:include page="includes/header.jsp"></jsp:include>
   
    <p class="cartTitle" >Your Cart <br/>
    <span style="color:#999999; font-size:12pt"> Grand Total: &dollar; ${cart.formattedGrand}</span> </p>

		<c:choose>
			<c:when test="${not empty cartMsg}">
               <span style="color: #FF6600">  ${cartMsg} </span>
            </c:when>
		    <c:when test="${not empty viewCartController.successMessages}">
		        <ul>
					<c:forEach var="message"
							items="${viewCartController.successMessages}">
						<li style="color: #00AA00">${message}</li>
								
					</c:forEach>
				</ul>
		    </c:when>
		    <c:when test="${not empty viewCartController.failMessages}">
		        <ul>
					<c:forEach var="message"
							items="${viewCartController.failMessages}">
						<li style="color: #FF0D20">${message}</li>
								
					</c:forEach>
				</ul>
		    </c:when>
		</c:choose>


		<c:forEach var="item" items="${cart.cartList}" varStatus="status">        
       
            
            <form action="<c:url value="/spring/viewCart/updateItem"  />" method="get">
                
                <input type="hidden" name="id" value="${item.book.id}" />
                
                
                <table class="table">
                    <tr style="width:120px">
                        <td> 
                        <img src="<c:url value="/resources/images/books-re-120.jpg" />"
                             width="100" height="100" alt="icon" />
                        </td>
                        <td style="width:200px">
                        ${item.book.title} <br/>
                        
                        <label>Id: </label><label>
                                &nbsp;${item.book.id}</label><br/>
                       
                        </td>
                        <td style="width:160px">
                            
                            
                            <label>Qty:</label><label>&nbsp;${item.quantity}</label><br/>
                            
                            <label>Price:</label><label class="rightAlign">
                                &nbsp;&dollar;${item.book.price}</label><br/>
                            
                            <label>Ext. Price:</label><label class="rightAlign">
                                &nbsp;&dollar;${item.extPrice}</label><br/>
                            
                        </td>
                        <td style="width:275px;vertical-align:25px">
                            <button  type="button" data-toggle="collapse" 
                                     data-target="#editQty-${item.book.id}" class="btn btn-link"> 
                                Edit 
                                <span  class="glyphicon glyphicon-triangle-bottom"></span></button>
                            <div id="editQty-${item.book.id}" class="collapse">
                                <span style="font-size:10pt;font-family: Arial">
                                    Quantity:</span>
                                <input type="text" size="10" name="quantity" />
                                <input type="submit" class="btn btn-link" value="Submit" name="cmdValue" />
                            </div>
                        </td>
                        <td style="vertical-align: 25px">
                           <input type="submit" class="btn btn-link" value="Delete" name="cmdValue" />
                        </td>
                </tr>
                </table>    
            </form>
         </c:forEach>
  
  <div style="width:300px;margin-left:700px">
      <jsp:include page="../jsp_include/cartTotals.jsp"></jsp:include>
  </div>
  
      
  </div><!-- end container -->


</body>
</html>