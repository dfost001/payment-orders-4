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
    <jsp:include page="../jsp/includes/header.jsp"></jsp:include>
      <div class="alert alert-danger">              
                <ul>
                  <li> Errors may occur if you are using the browser to navigate.
                   
                  <li> Continue Shopping 
                       <a href="<c:url value="/spring/catalogue/view" />" class="alert-link">                   
                            View Catalog</a>
                  </li>     
                  <c:if test="${not empty cart.cartList}">
                     <li>
                        Continue to Checkout <a href="<c:url value="/spring/checkout-flow" />">
                           Checkout</a>
                     </li>
                  </c:if>
                 </ul>                            
         </div>  <!--end alert -->
         
          <div class="message">          
           
           <h5 class="title">Browser Navigation Error</h5>    
           
            <p>${message}</p>         
             
            <h5>To report an application error or to complete your order: </h5>
            
            <p style="font-weight:bolder">Customer Support: (123) 123-1234</p> 
            
            <p>To continue use a link below</p> 
           
            <hr/>                           
                           
            <p><a href="<c:url value='/spring/catalogue/view' />" >                   
                            Continue Shopping</a></p> 
                            
             <c:if test="${not empty cart.cartList}">
                     
                     <p>   <a href="<c:url value='/spring/checkout-flow' />">
                           Continue to Checkout</a> </p>
                    
              </c:if>               
            
                       
                    
        </div><!-- end message --> 
        <h4 style= "cursor:pointer">
              <span class="glyphicon glyphicon-triangle-bottom"></span>Technical Support</h4>
        <div style="display:none">
            <p>Exception Message: <c:out value="${exception.message}" escapeXml="false" /></p>
            <p>Exception Class: ${exception.class.canonicalName} </p>
            <p>Handler: ${handler}</p>
            <p>Exception Resolver: ${exceptionResolver}</p>
            <c:if test="${requestScope.checkoutErrModel ne null}">
              <p>Http Status: ${checkoutErrModel.responseCode}</p>
            </c:if>
            <p>Message Trace:</p>
            <p><c:out value="${messages}" escapeXml="false" /></p>
            <p>Stack Trace:</p>
            <p>${trace}</p>            
        </div>
   </div><!-- end container -->
<script>
            $(document).ready(function(){
                $("h4").click(function(){
                    $(this).next().toggle(400);                    
                });
            });
        </script>
<body>

</body>
</html>