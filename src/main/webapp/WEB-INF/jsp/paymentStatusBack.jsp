<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>

<head>
   <title>Capture Completed Status</title>
   <meta name="viewport" content="width=device-width, initial-scale=1">     
	<link href="${pageContext.request.contextPath}/resources/css/bootstrap.css" rel="Stylesheet" />
	<link href="${pageContext.request.contextPath}/resources/css/styles.css" rel="Stylesheet" />	
	<script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js"></script>
	<script src="${pageContext.request.contextPath}/resources/javascript/bootstrap.min.js"></script>
	
	<style>
	  .divPaymentDetails {
	     width: 350px;
	     float: left;
	     margin-left: 20px
	  }
	  .divRefundDetails {
	     width: 350px;	     
	     margin-left: 20px 
	  
	  }
	  .divLineItems{
	     width:450px;
	     float:right;
	     margin-right:100px
	  }
	  .spTransactionId {
	     font-size: 12pt;
	     color: #3366AA
	  }
	  .divTransactionId {
	     width: 400px;
	     height: 45px;
	     border: 1px solid #BBBBBB;
	     background-color: #EEEEEE;
	     padding: 15px;
	     border-radius: 5px
	  }
	  .divPanelHeader {
	     padding-left: 7px;
	     width: 200px;
	     float: left
	  }
	  .myTopPanel {
	  
	     width: 100%;
	     height: 55px;
	     border: 1px solid #BBBBBB;
	     background-color: #B0E0E6;
	     padding-top: 4px;
	     border-radius: 5px
	  }
	  .rightAlign {
	     text-align: right;
	  }
	  .divErrDetail {
	     width: 400px;
	     border-bottom: 1px dotted black
	  }
	</style>
	
</head>
  
<body>
  <div class="container" style="padding-bottom:50px"> 
  
  <jsp:include page="includes/header.jsp"></jsp:include> 
  
  <c:if test="${ALREADY_REFUNDED_KEY ne null}" >
    <div class="alert alert-danger">"Your payment was previously refunded."</div>
  </c:if> 
  
  <c:if test="${order.serviceDetail.refundId ne null}">
  
    <input type="hidden" name="ALREADY_REFUNDED" value="true" />
   
  </c:if> <!-- read by JavaScript to set disabled -->
  
  <h2>Payment Status: ${order.paymentStatus}</h2>
  
   <form
      action="${pageContext.request.contextPath}/spring/refund/request/${orderId}/${serviceId}/${captureId}"
	  method="get" >
      <div class="myTopPanel">
		
			<div class="divPanelHeader">				
                   
					  <a href="<c:url value='/spring/payment/receipt'/>" class="btn btn-success" >
					       
						Print Payment Receipt  </a>	
			</div>                   
			<div class="divPanelHeader">
					   
				   <input type="submit" class="btn btn-warning"  id="btnRefund" value="Refund" />					
								
			</div>
			
        </div><!-- end panel -->  
    </form>
    
    <br style="clear:all" />
   
    <c:if test="${order.serviceDetail.refundId ne null}">
       <fieldset>
         <legend>Refund Details</legend>
          <table class="table" style="width:60%">
		   <tr>
		     <td><label>Refund Id:</label></td>
		     <td>${order.serviceDetail.refundId}</td>
		   </tr>		   
		   <tr>
		     <td><label>Refund Amount:</label></td>
		      <td>&dollar;${order.serviceDetail.refundAmount}</td>
		   </tr>
		   <tr>
		     <td><label>Refund Time:</label></td>
		     <td>${order.serviceDetail.refundTime}</td>
		   </tr>
		   </table>
       </fieldset>
    
    </c:if>   
    
    <c:if test="${order.serviceDetail.refundId eq null}">
       <div class="divTransactionId">
          <label>Transaction Id:</label> <span class="spTransactionId">${order.captureId}</span>
       </div>
       <br/>
    </c:if>
    
    <div class="divPaymentDetails">
      
    
    <fieldset>	
	 	<legend>Payment Details:</legend> 	
		<table class="table">
		   <tr>
		     <td><label>Order Id:</label></td>
		     <td>${order.orderId}</td>
		   </tr>
		   <tr>
		     <td><label>Transaction Id:</label></td>
		     <td>${order.captureId}</td>
		   </tr>
		   <tr>
		     <td><label>Payment Service Id:</label></td>
		     <td>${order.serviceDetail.serviceId}</td>
		   </tr>
		   <tr>
		     <td><label>Payment Status:</label></td>
		     <td>${order.paymentStatus}</td>
		   </tr>
		   <tr>
		     <td><label>Total Amount:</label></td>
		      <td>
                       <fmt:formatNumber 
                value="${order.orderAmountGrand}" type="currency" currencySymbol="$"/></td>
		   </tr>
		   <tr>
		     <td><label>Billing Name:</label></td>
		     <td>${order.serviceDetail.billingName}</td>
		   </tr>
		   <tr>
		    <td><label>Billing Email:</label></td>
		    <td>Funding details will be sent to: <label>${order.serviceDetail.billingEmail}</label></td>
		   </tr>
		    <tr>
		    <td><label>Funding Source:</label></td>
		    <td>****${order.serviceDetail.cardDigits}&nbsp;&nbsp;</td>
		   </tr>
		   <tr>
		     <td><label>Ship To:</label></td>
		     <td>${order.orderShipTo.firstName}&nbsp;${order.orderShipTo.lastName},
		          ${order.orderShipTo.address}, ${order.orderShipTo.city}&nbsp;
		          ${order.orderShipTo.state}&nbsp;${order.orderShipTo.postalCode}</td>
		   </tr>
		   <tr>
		     <td><label>Date Created:</label></td>
		     <td> <fmt:formatDate pattern="EEE, d MMM yyyy h:mm a z" type="both" value="${order.orderDate}"/></td>
		   </tr>
		   <tr>
		      <td colspan="2" style="font-style:italic">
		       ${order.customerId.firstName} ${order.customerId.lastName}: <br/>
		       ${order.customerId.email}<br/>
		       Customer since 
		      <fmt:formatDate pattern="EEE, d MMM yyyy" type="date" value="${order.customerId.dtCreated}"/> </td>		     
		   </tr>
		</table>
		</fieldset>	
    
    </div><!-- end details -->
    
    <div class="divLineItems">
		
		   <fieldset>
		   <legend>Items Purchased</legend>
		
		   <table class="table table-condensed" style="width:400px">
        
    <c:forEach var="item" items="${order.lineItemPayments}">
        <tr>
            <td>
                Product Id: ${item.productId}
            </td>
        </tr>
        <tr>
            <td colspan="3">
                <span style="color:#036fab;">${item.description}</span>
            </td>
        </tr>
        <tr>
            
            <td class="rightAlign">               
               <fmt:formatNumber 
                value="${item.price}" type="currency" currencySymbol="$"/>
            </td>
            <td>
               <span>${item.quantity}</span> 
            </td> 
            <td class="rightAlign">
                 <fmt:formatNumber 
                value="${item.extPrice}" type="currency" currencySymbol="$" /><span class="usd">USD</span> 
            </td>
        
        </tr>       
    </c:forEach>
    <tr>
       <td>&nbsp;</td>
       <td class="rightAlign">Subtotal:</td>
       <td class="rightAlign"><fmt:formatNumber 
                value="${order.orderSubtotal}" type="currency" currencySymbol="$"/>
                <span class="usd">USD</span>
                </td>
    </tr>
    <tr>
       <td>&nbsp;</td>
       <td class="rightAlign">Tax:</td>
       <td class="rightAlign"><fmt:formatNumber 
                value="${order.orderTax}" type="currency"  />
                <span class="usd">USD</span>
                </td>
    </tr>
    <tr>
       <td>&nbsp;</td>
       <td class="rightAlign">Shipping Fee:</td>
       <td class="rightAlign"><fmt:formatNumber 
                value="${order.orderShippingFee}" type="currency" currencySymbol="$"/>
                <span class="usd">USD</span>
                </td>
    </tr>
    <tr>
       <td>&nbsp;</td>
       <td class="rightAlign">Grand Total:</td>
       <td class="rightAlign"><fmt:formatNumber 
                value="${order.orderAmountGrand}" type="currency" currencySymbol="$" />
                <span class="usd">USD</span></td>
    </tr>
    </table><!--end div table-->    
		   
	</fieldset>	</div> <!-- end line items -->
		
        <br style="clear:both" />
    
    
    <a href="#" id="support" style="font-weight:bold">
               Support <span class="glyphicon glyphicon-collapse-down"></span></a>
               
		 <blockquote style="font-size:9pt; display:none">
		   
		   
		   <c:if test="${not empty errorDetailBean.errorDetailList}">
		      <h5>Order Error Details</h5>
		      <c:forEach var="err" items="${errorDetailBean.errorDetailList}">
		         <div class="divErrDetail">
		         <label>ErrorDetailReason:</label> ${err.errorDetailReason} <br/>
		         <label>Order #:</label>${err.localOrderId} <br/>
		         <label>Customer: </label>${err.order.customerId.id} <br/>
		         <label>Message:</label>${err.errMessage} <br/>
		         <label>Method:</label>${err.errMethod} <br/>
		         <label>Exception:</label>${err.exceptionClass} <br/>
		         <label>Service Id:</label>${order.serviceDetail.serviceId}
		         <label>Time:</label>${err.errTime} <br/>
		         </div>
		      </c:forEach>
		   </c:if>
		     
		   Capture Details: <br/>
           ${order.serviceDetail.captureJson}
           
           <br/>
           
           <c:if test="${order.serviceDetail.refundJson ne null}">
              <h5>Refund Details</h5>
              ${order.serviceDetail.refundJson}
           </c:if>
           
        </blockquote>
        
        <script>
           $("#support").click(function(ev){
        	   
        	   ev.preventDefault();
        	   
        	   $(this).next().slideToggle();
        	   
           });
          
           var el = $("input[name='ALREADY_REFUNDED']");
           if(el && el.val() === "true")                
                $("#btnRefund").prop("disabled", true).css("cursor", "not-allowed"); 
        </script> 
  
  </div><!-- end container -->

</body>
</html>