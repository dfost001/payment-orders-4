<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>  
    
<!DOCTYPE html>

<html>
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Credit Card Details</title>
  <!-- To be replaced with your own stylesheet -->
  <link
	   href="${pageContext.request.contextPath}/spring/resources/css/bootstrap.css"	rel="Stylesheet" />
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/spring/resources/css/hosted-card-fields.css"/>
   <link
	  href="${pageContext.request.contextPath}/spring/resources/css/styles.css"	rel="Stylesheet" />
   <link
	  href="${pageContext.request.contextPath}/spring/resources/css/checkout.css"	rel="Stylesheet" />  
  <script
  src="${pageContext.request.contextPath}/spring/resources/javascript/jquery-1.11.1.js"></script>
  <script
	  src="${pageContext.request.contextPath}/spring/resources/javascript/bootstrap.min.js"></script>   
  <script
    src="https://www.paypal.com/sdk/js?components=buttons,hosted-fields,funding-eligibility&client-id=${clientId}&commit=false"
    data-client-token=${clientToken}
  ></script> 
  <script src="${pageContext.request.contextPath}/spring/resources/javascript/checkoutButtons.js"></script>
  
  <style>
      
  .card_container {
   
    background-color: #FFFFFF;
    padding-top: 20px;  
    width: 1025px;
  
    }     
      .divLeftContent {
        width: 450px;
        float:left;
      
      }
      .divCustomer {
	     width: 500px;
	     float: left;
	     padding-left: 75px
	     /*margin-left: 100px;*/
       }
       .postalField {
          width:125px;
          float:left;
       }
       .modal-body p {
          text-align: center
       }
     
     }
    </style>
  
</head>
<body>
  <div class="container">
  
    <jsp:include page="flowHeader.jsp"></jsp:include>
    
    
      <div class="alert alert-danger" id="alertInvalidFields"
          style="position:absolute;margin-bottom:20px;display:none">      
               <ul id="alertList">              
                 
                  <li><span style="font-style:italic">
                     You may also contact support to complete your order at 123-123-1234 </span></li>
               </ul>                            
       </div>
     
       
     <br /><br /> 
    
<div class="card_container" >

  <c:if test="${paymentSourceNullException ne null}">
     <div class="alert alert-danger" style="margin-bottom:8px">
         ${paymentSourceNullException.message}
     </div>
  </c:if>
  
  <form id="card-form">
   <div class="divLeftContent">
   
       <label for="card-number">Card Number</label><div id="card-number" class="card_field"></div>
    
       <label for="expiration-date">Expiration Date</label><div id="expiration-date" class="card_field"></div>
       
       <label for="cvv">CVV</label><div id="cvv" class="card_field"></div>
       
        <jsp:include page="includes/cartItems.jsp"></jsp:include>
       
       <button value="submit" id="submit" class="btn btn-success" 
            style="width:400px">Continue</button>
    
    </div><!-- end divLeftContent -->
    <div class="divCustomer">       
    
        <label for="card-holder-name">Name on Card</label>
         <input type="text" id="card-holder-name" name="card-holder-name" autocomplete="off" 
          placeholder="card holder name" value="${customer.firstName} ${customer.lastName}" 
          readonly />
          
      <label for="card-billing-address-street">Billing Address</label>
      <input type="text" id="card-billing-address-street" readonly
          name="card-billing-address-street" autocomplete="off" placeholder="street address"
          value="${customer.address}"/>   
          
      <label>&nbsp;</label>   
      <input type="text" id="card-billing-address-city" name="card-billing-address-city" 
      autocomplete="off" placeholder="city" value="${customer.city}"
      style="float:left;width:175px"
       readonly/>    
       &nbsp;
       <input type="text" id="card-billing-address-state" name="card-billing-address-state" 
      autocomplete="off" placeholder="state" value="${customer.state}"
       style="float:left;width:100px"
       readonly/> 
        &nbsp;  
       <input type="text" id="card-billing-address-zip" name="card-billing-address-zip" readonly
      autocomplete="off" placeholder="zip / postal code" value="${customer.postalCode}"
       style="float:left;width:125px"  />    <br/>
      
      <input type="text" id="card-billing-address-country" name="card-billing-address-country" 
      autocomplete="off" placeholder="country code" value="US" 
       style="width:100px" 
      readonly/>
      
       <jsp:include page="includes/shippingAddress.jsp"></jsp:include>
       
       <br />
       
       <jsp:include page="includes/support.jsp" />
          
    </div><!-- end divCustomer -->
  </form>
   
   
  </div><!-- card-container -->
   
  
      
     <!-- Bootstrap modal with a link that returns the details view -->  
     
  <div class="modal fade" tabindex="-1" role="dialog" id="myModal">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">       
        <h4 class="modal-title">Review Details</h4>
      </div>
      <div class="modal-body">
      
        <p>Please authorize your payment: </p>
       
        <form action="${flowExecutionUrl}" method="post" id="formModal">
        <p>
           <input type="hidden" name="cardHolderName" value="" />
           <input type="hidden" name="streetAddress" value="" />
           <input type="hidden" name="region" value="" />
           <input type="hidden" name="city" value="" />
           <input type="hidden" name="postalCode" value="" />
           <input type="hidden" name="countryCode" value="" />
           <input type="hidden" name="paymentId" value="" />
        
           <input type="submit" name="_eventId_reviewDetails" class="btn btn-info btn-lg"
               value="Review Payment Details" />   
            
        </p>
        </form>
       
      </div><!-- end modal-body -->
     
    </div><!-- end modal-content -->
  </div><!-- end modal-dialog -->
</div><!-- end modal -->

<div class="modal fade" tabindex="-1" role="dialog" id="myModalError">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">       
        <h4 class="modal-title">Review Details</h4>
      </div>
      <div class="modal-body">
      
        <p style="color:red; font-size:14pt">A server error has occurred: </p>
       
        <form action="${flowExecutionUrl}" method="post">
        <p>
          
           
            <input type="submit" name="_eventId_createPaymentError"
                      class="btn btn-info" value='Contact Support' />           
            
        </p>
        </form>
       
      </div><!-- end modal-body -->
     
    </div><!-- end modal-content -->
  </div><!-- end modal-dialog -->
</div><!-- end modal -->

    <script>
    
    var options = {
    		backdrop: 'static', 
    		keyboard: false, //don't close on escape
    		show:false //don't show on initialize
    };
    
    $('#myModal').modal(options);
    
    $('#myModalError').modal(options);
    
    </script>
     
    </div> <!-- end bootstrap container -->
  </body>
</html>
