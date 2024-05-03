<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
 
<!-- Bootstrap success modal with a link that returns the details view --> 

<div class="modal fade" tabindex="-1" role="dialog" id="myModal">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">       
        <h4 class="modal-title">Review Details</h4>
         <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      
        <p>Please authorize your payment: </p>
       
        <form action="${flowExecutionUrl}" method="post" id="formModal">
        <p>
        
           <input type="hidden" name="paymentId" value="" />
           <input type="hidden" name="cardHolderName" value="" />
           <input type="hidden" name="streetAddress" value="" />
           <input type="hidden" name="region" value="" />
           <input type="hidden" name="city" value="" />
           <input type="hidden" name="postalCode" value="" />
           <input type="hidden" name="countryCode" value="" />           
        
           <input type="submit" name="_eventId_reviewDetails" class="btn btn-info btn-lg"
               value="Review Payment Details" />   
            
        </p>
        </form>
       
      </div><!-- end modal-body -->
     <div class="modal-footer">        
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
      </div>
    </div><!-- end modal-content -->
  </div><!-- end modal-dialog -->
</div><!-- end modal -->

<!-- Bootstrap modal shown on error --> 
<div class="modal fade" tabindex="-1" role="dialog" id="myModalError">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">       
        <h4 class="modal-title">Review Details</h4>         
      </div>
      <div class="modal-body">
      
        <p style="color:red; font-size:14pt">A server error has occurred: </p>
        
        <p style="color:red; font-size:14pt" id="notEligible"> </p><!-- A custom message for not eligible goes here -->
       
        <form action="${flowExecutionUrl}" method="post" id="modalErrForm">
        <p>
          
           
            <input type="submit" name="_eventId_createPaymentError"
                      class="btn btn-info" value='Contact Support' />           
            
        </p><!-- Will dynamically alter the name attribute for notEligible transition -->
        </form>
       
      </div><!-- end modal-body -->
     <div class="modal-footer">        
       <p style="font-size:10pt; font-style:italic; color:blue; font-weight:bold; text-align:center"> 
             Please click support to complete your order:  </p>
      </div>
    </div><!-- end modal-content -->
  </div><!-- end modal-dialog -->
</div><!-- end modal -->