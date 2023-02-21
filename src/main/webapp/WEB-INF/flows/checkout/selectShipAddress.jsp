<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>   
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
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
   	<script
	  src="${pageContext.request.contextPath}/spring/resources/javascript/showProcessing.js"></script>  
<title>SelectShipAddress</title>

 <style>
            .parentAddressContainer {
                width: 900px;
            }
            
            .customerContainer {
                border-bottom: 1px solid #0000BB;
                width:300px;
                float:left;
            }
           
            .checkedAddress {
                color: #CCCCFF
            }
            #divProcessing {
              width:350px;
              height:300px;
              margin:auto;
              z-index:10;
              display:none;
              position:absolute;
              background-color:white
            }
            #spinner {
              width:70px;
              height:70px;
              margin:auto;
              position:absolute
            }
  </style>
 
</head>
<body>
<div class="container">
      <jsp:include page="flowHeader.jsp"></jsp:include>
   
      <h3>Select Ship To Address</h3>
      
      <div class="panel panel-info">
          <div class="panel-heading">
                            
               <form action="${flowExecutionUrl}" method="post">
               
                   <input type="submit" name="_eventId_create" value="Add New" class="btn btn-sm btn-info" />
                             
               </form>
          </div>
      </div> <!-- end panel -->
      <div class="parentAddressContainer">
      
       <c:forEach var="postal" items="${addressList}" varStatus="status">  
                            
             <div class="customerContainer">
                           <form action="${flowExecutionUrl}" method="post" >
                            
                            <input type="hidden" name="addressIdentifier" value="${postal.id}_${status.index}" />
                            
                            <table class="table">
                                <tr>
                                    <td>
                                       <input type="checkbox" <c:if test="${status.index eq previousSelected}">
                                           checked</c:if> class="checkedAddress" onclick="return false"/>
                                    </td>
                                </tr>
                                <tr> 
                                    <td>${postal.firstName} ${postal.lastName}</td>
                                </tr>                               
                                <tr>
                                    <td>${postal.address}</td>
                                </tr>
                                <tr>
                                    <td>${postal.city},
                                        ${postal.state}&nbsp;${postal.postalCode}</td>
                                </tr>
                                <tr>
                                   <td><label>Id: </label>${postal.id}</td>
                                </tr>
                                
                                <tr>
                                    <td><input type="submit" name="_eventId_select" value="Select" 
                                               class="btn btn-sm btn-success" />
                                        &nbsp;&nbsp;
                                        <input type="submit" name="_eventId_edit" value="Edit" 
                                               class="btn btn-sm btn-default" />
                                        &nbsp;&nbsp;
                                        <c:if test="${status.index > 0}">
                                           <input type="submit" name="_eventId_delete" value="Delete" 
                                               class="btn btn-sm btn-danger" />
                                       </c:if>
                                    </td>
                                </tr>                                
                            </table> 
                           </form>      
                        </div><!--end customer container-->  
                        </c:forEach>
      
      </div><!-- end address container -->
      
      <br style="clear:both" />
      <jsp:include page="includes/support.jsp" />
      
 </div><!-- end container --> 
  <div id="divProcessing">
    <div id="spinner">
       <img src="<c:url value='/resources/images/spinner-3.gif' />" width="64" height="64"/>
    </div>
  </div>  
</body>
</html>