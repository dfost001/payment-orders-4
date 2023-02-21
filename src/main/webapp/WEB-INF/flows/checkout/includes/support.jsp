<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    
<div style="width:500px;cursor:pointer" >	
	  
	  <h5 style="cursor:pointer" id="restoreFailure">
	        <span class="glyphicon glyphicon-triangle-bottom"></span>Support:</h5>
	  <div  style="display:none">     
	       <h6 >Restore Failure: ${RESTORE_FAILURE}</h6>
	       <h6> Error On GetDetails: ${ERR_GET_DETAIL}</h6>
	       <h6>Cart count: ${cart.count}</h6>	      
	       <h6>flowExecutionUrl: ${flowExecutionUrl}</h6>
	  </div>
	</div> <!-- end support -->	
		
<script>
	   $("#restoreFailure").click(function(){
		   
		   $(this).next().toggle();
		   
	   });
	</script>