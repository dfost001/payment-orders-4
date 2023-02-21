<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script src="<c:url value='/spring/resources/javascript/dropdownCart.js' />" ></script>

<style>
    #catPanel a:link, #catPanel a:visited {
	      color: #045491;
	      font-weight: bold;
      }
      
     #cartAnchor a:link, #cartAnchor a:visited,
     #cartAnchor a:hover:link, #cartAnchor a:hover:visited  {  
           text-decoration: none
     }

      #catPanel li {
	    padding-left:25px;
	    padding-top: 10px;
      }

     #catPanel label {
	    display: block;
	    height: 25px;
	    margin-top: 7px;
	    
	 }
	
	 .rightAlign {
	     text-align:right
	 }
	 .collapse {
	    float:right;
	    margin-right:60px;	    
	 }
	 #popCartContent {
	     position: absolute;
	     border: 1px solid rgba(192,192,192,0.3);
	     border-radius: 15px;
	     padding: 10px;
	     display:none;
	     z-index:1;
	     background-color:#FFFFFF	    
	     
	 }
</style>

<p class="logo">JR <span>Hartley</span> <small>booksellers since 1923</small></p>



  <div class="panel panel-info">
	  
	      <div class="panel-body" id="catPanel">
	      
	       <label style="float:left; margin-left:30px; margin-top:15px">
	       
	            <a href='${pageContext.request.contextPath}/spring/catalogue/view'>
		            Home</a>  </label>
		            
		    <label style="float:left; margin-left:70px; margin-top:15px">
	       
	            <a href='${pageContext.request.contextPath}/spring/catalogue/printSession'>
		            Print Session</a>  </label>        
	      
	       <ul class="nav nav-pills" role="tablist" style="float:right; margin-right:30px">	  
	       
	           <c:if test="${cart.count gt 0}">
	              <c:choose>
	              <c:when test="${fn:contains(pageContext.request.requestURI, 'viewCart') }">
	                 <li>
	                  <label><a href="<c:url value='/spring/checkout-flow'/>" id="checkoutLink">
	                     Continue to Checkout</a></label>
	                </li>
	              </c:when>
	              <c:otherwise>
	               <li>
	                  <label><a href="<c:url value='/spring/viewCart/request'/>" id="checkoutLink">
	                      Checkout</a></label>
	                </li>
	              </c:otherwise>
	              </c:choose>
	           </c:if>
	       
	           <li>
	             <label><a href="<c:url value='/spring/viewCart/request'/>">
	                     Cart</a></label>
	           </li>       
	         
               <li>        
	               <label id="cartLabel"> 
	               
	                  <a href="#" id="cartAnchor" >
	                          
	                     <img src="${pageContext.request.contextPath}/spring/resources/images/cart-button.gif"
	                      width="20" height="15" >
	                          <span class="caret"></span></a> </label>                          
	                 
	           
	           </li>
	           <li>   
	                <label style="font-size:9pt;">${cart.formattedCount} </label>   
	           </li>          
	        </ul><!-- end nav --> 	     
	              
	     </div><!-- end panel-body -->	   
	     
	  </div><!-- end panel -->
	  
	  <div style="width:250px" id="popCartContent" >
	  
	        <jsp:include page="../../jsp_include/cartTotals.jsp"></jsp:include>
	                   
	  </div><!-- end dropdown -->            
	                      
	  
	   
   