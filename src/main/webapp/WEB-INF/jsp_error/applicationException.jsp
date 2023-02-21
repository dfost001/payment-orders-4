<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta  charset="UTF-8">
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.css" rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/resources/css/exceptionView.css" rel="stylesheet" />
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js"></script>
        <title>Application Error</title>
        
    </head>
    <body>
        <div class="container">
        <h1>Application Exception</h1>
        <h3>Please contact 123-1234</h3><br/>
        <h3><a href="<c:url value='/spring/catalogue/view' />">Home</a></h3>
        <h4 class="plus" style="cursor:pointer">Technical Support</h4>
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
      
        </div><!--end container-->
        <script>
            $(document).ready(function(){
                $("h4").click(function(){
                    $(this).next().toggle(400);
                    $(this).toggleClass("plus minus");
                });
            });
        </script>
    </body>
</html>
