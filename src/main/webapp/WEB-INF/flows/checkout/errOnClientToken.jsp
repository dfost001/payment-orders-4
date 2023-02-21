<%@ page contentType="text/html" pageEncoding="UTF-8" %> 

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
   <meta charset="UTF-8">
   <title>Server Error</title>
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
           
          <ul>
                  <li> A Server Error occurred while loading Payment View. </li>
                  <li> Contact Support at 123-123-1234.</li>     
                  <li> Exit to home.
                   <a href="${flowExecutionUrl}&_eventId=home" class="alert-link">
                             Home </a>
                  </li>    
                 </ul>  
                            
         </div>  <!--end alert -->
         
          <div class="message">          
           
           <h5 class="title">Server Error</h5>    
           
            <p>${exception.friendly}</p>         
             
            <h5>To report an application error or to complete your order: </h5>
            
            <p style="font-weight:bolder">Customer Support: (123) 123-1234</p> 
            
            <p>To continue use a link below</p> 
           
            <hr/>                           
                           
            <p><a href="${flowExecutionUrl}&_eventId=home">Exit to home</a></p>                     
                    
        </div><!-- end message --> 
        <h4 style="cursor:pointer" id="support"><span  class="glyphicon glyphicon-triangle-bottom"></span>Support</h4>
        <div style="display:none">
        <blockquote>
          Exception: ${exception.class.canonicalName}<br/>
          Message: ${exception.message}<br/>
          Raw Response: ${exception.debug}<br/>
          Response Status: ${exception.responseStatus}<br/>
          Friendly: ${exception.friendly} <br/>
          Method: ${exception.method}
        </blockquote>
        </div>
        
        <script>
        
          $("#support").click(function(){
        	  
        	  $(this).next().slideToggle();
        	  
          });
          
        </script>
        
   </div><!-- end container -->

<body>

</body>
</html>