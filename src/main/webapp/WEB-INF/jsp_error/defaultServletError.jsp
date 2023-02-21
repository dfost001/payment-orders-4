<%@page isErrorPage="true" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
   <link
	   href="${pageContext.request.contextPath}/resources/css/bootstrap.css"	rel="Stylesheet" />
   <link
	  href="${pageContext.request.contextPath}/resources/css/styles.css"	rel="Stylesheet" />	 
   <script
	  src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js"></script>
   <script
	  src="${pageContext.request.contextPath}/resources/javascript/bootstrap.min.js"></script>
<title>Error</title>
</head>
<body>
<div class="container">
<h2>Servlet Exception </h2>
        <h3><a href="#">Customer Support: 123-1234 </a></h3>
        
        
        <h4  style="cursor:pointer">Technical Support</h4>
        <div style="display:block">
            <p>Handler: error-page in web.xml</p>
            <p>Message: ${pageContext.errorData.throwable.message}</p>
            <p>Exception Class: ${pageContext.exception.class.name} </p>
            <p>Request Uri: ${pageContext.errorData.requestURI}</p>
            <p>Stack Trace:</p>
            <div style="margin-left:20px">
                <c:forEach var="el" items="${pageContext.exception.stackTrace}">
                    <p> ${el.className}.${el.methodName}()[${el.fileName}:${el.lineNumber}]</p>
                </c:forEach>
            </div>
            
            
        </div>
        
        

</div><!-- end container -->

</body>
</html>