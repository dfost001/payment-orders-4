<%@ page contentType="text/html" pageEncoding="UTF-8" %> 

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
   <meta charset="UTF-8">
   <title>Navigation Error</title>
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
       .message {
                width: 550px;
                height: 350px;
                border: 1px solid #0000BB;
                background-color:#f7f975;
                border-radius:25px;
                margin: 40px auto auto auto;
                padding-top:20px;
            }              
            .message > h4, .message > p, h5 {
                text-align: center;
            }
            .title {
                text-align:center;
                color:#0066BB;               
                font-size: 12pt;
            }
            hr {
                color:#BBBBBB;
            }
            #support{
                cursor:pointer;
                width: 550px;
                margin:auto;
            }
           
            .divSupport {
                margin:auto;
                width:620px;    
            }
            .container {
               padding-bottom:2%
            }
            a {
              font-weight:bold;
            }
           
  </style>

</head>    
   <div class="container">
    <jsp:include page="flowHeader.jsp"></jsp:include>
      <div class="alert alert-danger">  
           
           <c:choose> 
               <c:when test="${empty cart.cartList}">
                <ul>
                  <li> Please add items to your cart. 
                       <a href="${flowExecutionUrl}&_eventId=viewCatalogue" class="alert-link">                   
                            View Catalog</a>
                  </li>     
                  <li>  Errors may occur if you are using the browser to navigate
                   <a href="${flowExecutionUrl}&_eventId=viewShoppingCart" class="alert-link">
                             View Shopping Cart </a>
                  </li>    
                 </ul>         
               </c:when>
               <c:otherwise>
                   Errors may occur if you are using the browser to navigate
                   <a href="${flowExecutionUrl}&_eventId=alertViewShoppingCart"
                      class="alert-link"> View Shopping Cart </a>
               </c:otherwise>
           </c:choose>   
                            
         </div>  <!--end alert -->
         
          <div class="message">          
           
           <h5 class="title">Browser Navigation Error</h5>    
           
            <p>${exception.friendly}</p>         
             
            <h5>To report an application error or to complete your order: </h5>
            
            <p style="font-weight:bolder">Customer Support: (123) 123-1234</p> 
            
            <p>To continue use a link below</p> 
           
            <hr/>                           
                           
            <p><a href="${flowExecutionUrl}&_eventId=viewCatalogue">Continue Shopping</a></p> 
            
            <p><a href="${flowExecutionUrl}&_eventId=viewShoppingCart">View Cart</a></p>             
                    
        </div><!-- end message --> 
        <jsp:include page="includes/support.jsp" />
   </div><!-- end container -->

<body>

</body>
</html>