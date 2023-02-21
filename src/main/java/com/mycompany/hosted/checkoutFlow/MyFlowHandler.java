package com.mycompany.hosted.checkoutFlow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.execution.repository.FlowExecutionRepositoryException;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;

import com.mycompany.hosted.checkoutFlow.exceptions.FlowNavigationException;
import com.mycompany.hosted.checkoutFlow.exceptions.OnRenderCartEmptyException;
import com.mycompany.hosted.checkoutFlow.mvc.controller.paypal.PaymentExceptionController;

import com.mycompany.hosted.exception_handler.EhrLogger;



public class MyFlowHandler extends AbstractFlowHandler {
	
	
	
	@Override
	public String getFlowId() {
		
		return "checkout-flow";
	}
	
		
	 @Override
		public MutableAttributeMap<Object> createExecutionInputMap(
				HttpServletRequest request) {
		 
		 MutableAttributeMap<Object> attrMap = new LocalAttributeMap<>();
		 
		 extractErrOnDetail(attrMap, request);
		 
		 extractErrOnCapture(attrMap, request);
		 
		 extractRestoreFailure(attrMap, request);		 
		 
		 return attrMap;
	 }
	 private void extractErrOnDetail(MutableAttributeMap<Object >map,
			 HttpServletRequest request) {
		 
		 String paramKey = PaymentExceptionController.ERR_GET_DETAIL;
		 
		 String val = request.getParameter(paramKey);
		 
		 if(val != null)
			 map.put(paramKey, true);
		 else map.put(paramKey, false);
		 
		 System.out.println("MyFlowHandler#extractErrOnDetail: "+
		      map.getBoolean(paramKey));
	 }
	 
	 private void extractErrOnCapture(MutableAttributeMap<Object >map,
			 HttpServletRequest request) {
		 
		 String paramKey = PaymentExceptionController.ERR_ON_CAPTURE;
		 
		 String val = request.getParameter(paramKey);
		 
		 if(val != null)
			 map.put(paramKey, true);
		 else map.put(paramKey, false);
		 
		 System.out.println("MyFlowHandler#extractErrOnCapture: "+
		      map.getBoolean(paramKey));
	 }
	 
	 private void extractRestoreFailure(MutableAttributeMap<Object >map,
			 HttpServletRequest request) {
		 
         String val = request.getParameter("RESTORE_FAILURE");
		 
		 if(val != null)
			 map.put("RESTORE_FAILURE", val);
		 else map.put("RESTORE_FAILURE", "none");
		 
	 }
	/*
	 * Note: Servlet mapping not necessary
	 * @see org.springframework.webflow.mvc.servlet.AbstractFlowHandler#handleExecutionOutcome(org.springframework.webflow.execution.FlowExecutionOutcome, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	 @Override
	   public String handleExecutionOutcome(FlowExecutionOutcome outcome,
			   HttpServletRequest request, HttpServletResponse response) {
		 
         System.out.println("MyFlowHandler#handleExecutionOutcome: executing" ) ;
         
         switch(outcome.getId()) {
         
         case "mvcCart" :
        	 return "/viewCart/request";
        	 
         case "mvcHome" :
        	 return "/catalogue/view";
        	 
         case "paymentCompleted" : 	
        	 Integer orderId = outcome.getOutput().getInteger("orderId");
             return "/payment/status?orderId=" + orderId; 
        
         case "errCheckoutException":        	
        	 return "/paymentException/initErrorModel";
        	 
         case "paymentStatusFailed" :
        	 return "/failedStatus/handle";
		
         default:
			 EhrLogger.throwIllegalArg(this.getClass(), "handleExecutionOutcome", 
					 "Unknown Outcome Id");
		  
         }
         
         return null;
	 }
	 

	 
	 @Override
		public String handleException
		   (FlowException e, HttpServletRequest request, HttpServletResponse response) {	 
		
			
			Throwable root = EhrLogger.getRootCause(e);

			String url = "";

			if(FlowExecutionRepositoryException.class.isAssignableFrom(e.getClass())) 
				url = "checkout-flow?RESTORE_FAILURE=" + e.getClass().getSimpleName();
				      
			
			else if(OnRenderCartEmptyException.class.isAssignableFrom(root.getClass()))  
				url = "checkout-flow?RESTORE_FAILURE=" +
			                root.getClass().getSimpleName();
			
			else if(FlowNavigationException.class.isAssignableFrom(root.getClass())) {
				
				FlowNavigationException navException =
						(FlowNavigationException)root;
				
				url = "checkout-flow?RESTORE_FAILURE="
						+ root.getClass().getSimpleName() + ": "
						+ navException.getMessage();
						
			}
			else throw e;	
			
			 System.out.println(this.getClass().getCanonicalName() + "#handleException: " + url);

			return url;
			
			
			
		} //end handle
	
	
  	 
} //end class
