<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
<link
	   href="${pageContext.request.contextPath}/spring/resources/css/bootstrap.css"	rel="Stylesheet" />
</head>
<body>
<div class="container">
<h2>Webflow Dispatched</h2>
<table class="table" style="width:200px">
<tr>
   <td><a href="${flowExecutionUrl}&_eventId=exit">Exit</a></td>
   <td><a href="${flowExecutionUrl}&_eventId=continue">Continue</a></td>
</tr>
</table>
</div>  
</body>
</html>