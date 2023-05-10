package com.mycompany.hosted.checkoutFlow;



import org.springframework.stereotype.Component;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;

import com.mycompany.hosted.checkoutFlow.exceptions.FlowNavigationException;
import com.mycompany.hosted.checkoutFlow.exceptions.WebflowCartEmptyException;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.model.Customer;
import com.mycompany.hosted.model.PostalAddress;


@Component
public class EvalApplicationState {
	
	
	Customer currentCustomer;	
	PostalAddress currentSelectedAddress;
	PaymentDetails currentPaymentDetails;
	
	Customer entryCustomer;
	PostalAddress entrySelectedAddress;
	PaymentDetails entryPaymentDetails;
	
	
 /*
  * Invoked from <on-entry> of every flow view-sate	
  */
	
  public void setViewScopeComparisonAttrs(RequestContext ctx) {
		
		SharedAttributeMap<Object> sessionMap = ctx.getExternalContext().getSessionMap();
		
		MutableAttributeMap<Object> viewScope = ctx.getViewScope();
		
		Customer entryCustomer = (Customer)sessionMap.get(WebFlowConstants.CUSTOMER_KEY);
		   
		PostalAddress entrySelectedAddress = 
				(PostalAddress)sessionMap.get(WebFlowConstants.SELECTED_POSTAL_ADDR);	
		
		PaymentDetails paymentDetails = (PaymentDetails)sessionMap.get(WebFlowConstants.PAYMENT_DETAILS);
		
		viewScope.put(WebFlowConstants.CUSTOMER_KEY, entryCustomer);
		
		viewScope.put(WebFlowConstants.SELECTED_POSTAL_ADDR, entrySelectedAddress);	
		
		viewScope.put(WebFlowConstants.PAYMENT_DETAILS, paymentDetails);		
		
	}
	
	
	private void initCurrentSessionState(RequestContext ctx) {
		
	   SharedAttributeMap<Object> sessionMap = 	ctx.getExternalContext().getSessionMap();
		
	   currentCustomer = (Customer)sessionMap.get(WebFlowConstants.CUSTOMER_KEY);
	   
	   currentSelectedAddress = (PostalAddress)sessionMap.get(WebFlowConstants.SELECTED_POSTAL_ADDR);	 
	   
	   currentPaymentDetails = (PaymentDetails)sessionMap.get(WebFlowConstants.PAYMENT_DETAILS);
		
	}
	
	private void initEntrySessionState(RequestContext ctx) {
		
		MutableAttributeMap<Object> viewScope = ctx.getViewScope();
		
		entryCustomer = (Customer)viewScope.get(WebFlowConstants.CUSTOMER_KEY);
		
		entrySelectedAddress = (PostalAddress)viewScope.get(WebFlowConstants.SELECTED_POSTAL_ADDR);	
		
		entryPaymentDetails = (PaymentDetails)viewScope.get(WebFlowConstants.PAYMENT_DETAILS);
		
		//debugViewScope(ctx.getCurrentState().toString(), entryCustomer, entrySelectedAddress, entryPaymentDetails);
		
		
	}
	
	public void evalState(RequestContext ctx) throws FlowNavigationException {
		
        initCurrentSessionState(ctx); //From session into module level variables
		
		initEntrySessionState(ctx); //From view-scope into module-level
		
		boolean expectedOnEnter = false;
		
		boolean expectedOnRender = false;
		
		String detail = "";
		
		String err = "";
		
		String viewId = ctx.getCurrentState().getId();
		
		EhrLogger.consolePrint(this.getClass(), "evalState",
				"viewId=" + viewId);
		
		if(!viewId.contentEquals("login") && !viewId.contentEquals("addressView")) {
			throwOnNullCustomer();
		}
		
		switch(viewId) {
		
		case "login":
			
			expectedOnEnter = entryCustomer == null
			    && entrySelectedAddress == null && entryPaymentDetails == null;			
	        expectedOnRender = currentCustomer == null && currentSelectedAddress == null 
			    && currentPaymentDetails == null; 	        
	        break;	
	        
		case "selectShipAddress":
			
			 expectedOnEnter =  entryPaymentDetails == null;			
			 expectedOnRender =  currentPaymentDetails == null;	         
	         break;
	         
		case "paymentButtons": 
			
			expectedOnEnter =  entrySelectedAddress != null
			   && entryPaymentDetails == null;		
	        expectedOnRender = currentSelectedAddress != null
			   && currentPaymentDetails == null;	        
	        break;
	        
		case "addressView":
			
			 expectedOnEnter = this.entryPaymentDetails == null; 			
			 expectedOnRender = entryCustomer == currentCustomer && currentPaymentDetails == null;		        
	         detail = entryCustomer != currentCustomer ?
		    		"Customer on-entry is not equal to current session. " : ""; //Copy not set into session until exited 
	         break;
	         
		case "showDetails":  
			
            err = PaymentObjectsValidator.validateDetailsBeforeCapture(this.entryPaymentDetails);				
			
			expectedOnEnter =  entrySelectedAddress != null				
					&& entryPaymentDetails != null 					
					&& err.isEmpty();	
			
			expectedOnRender = currentSelectedAddress != null
					&& currentPaymentDetails != null ;	
			
            if(currentPaymentDetails != null) {
            	
            	err = PaymentObjectsValidator.validateDetailsBeforeCapture(this.currentPaymentDetails);		
                throwIfInvalidDetails(err);            	
			}
            
            detail = currentPaymentDetails == null ? "Null Details - View entered after Capture completed. " 
            		: "";	
            break;
		
		}//end switch
		
        evalExpectedState(expectedOnEnter, "on-enter", viewId, err);
		
		evalExpectedState(expectedOnRender, "on-render", viewId, detail);
		
	}
	



	private void throwOnNullCustomer() {
		
		if(currentCustomer == null || entryCustomer == null)
			EhrLogger.throwIllegalArg(this.getClass(), "evalState", 
					"Customer is null on-enter or on-render"); 
	}
	
    private void throwIfInvalidDetails(String err) {
    	
		
		if(!err.isEmpty())
		    EhrLogger.throwIllegalArg(this.getClass(), "evalPaymentDetailView", 
				"currentDetails is non-null and not valid: " + err)	;
    	
    }	
	
	public void evalNavigationErrorView(WebflowCartEmptyException entryException,
			RequestContext request) throws FlowNavigationException {		
		
		if(entryException == null) {
			
			String error = "on-entry: CartEmptyException on-entry is null." ;
			
			EhrLogger.throwIllegalArg(this.getClass(), "evalNavigationErrorView", error);
		}
		
		WebflowCartEmptyException ex =
				(WebflowCartEmptyException) request.getFlashScope().get(WebflowDebug.EXCEPTION_KEY);
		
		if(ex==null) {
			
			String error = "on-render: CartEmptyException on-render is not in FlashScope";
			
			this.throwFlowNavigation(error, "evalNavigationErrorView");
		}
		
	}
	
	private void evalExpectedState(Boolean expected, String phase, String method, String issue) 
	
	                 throws FlowNavigationException {
		
		String error="";
		
		if(!expected)
			if(phase.contentEquals("on-entry")) {
				
				error = genErrText(phase, entryCustomer, entrySelectedAddress, 
						entryPaymentDetails, issue);
				
				EhrLogger.throwIllegalArg(this.getClass(), method, error);
			}
			else if(phase.contentEquals("on-render")) {
				
                error = genErrText(phase, currentCustomer, currentSelectedAddress, 
                		currentPaymentDetails, issue);
				
				throwFlowNavigation(error, method);
			}				
	}
	
	private String genErrText(String phase, Customer customer, PostalAddress selected, 
			   PaymentDetails currentDetails, String info) {
		
		String err = phase + ": ";
		
		 err += customer == null ? "Customer is null" : "Customer is non-null";
		 
		 err += ". ";
		 
		 err += selected == null ? "Selected Address is null" : "Selected Address is non-null";
		 
		 err += ". ";
		 
		 err += currentDetails == null ? "hasDetails is false/null" : "hasDetails is true/non-null";
		 
		 err += ". " + info;
		 
		 return err;
	}	
	
	private void throwFlowNavigation(String message, String method) throws FlowNavigationException {
		
		String error = method + ": " + message;
		
		throw new FlowNavigationException(error);
	}
	
	
	/*private void debugViewScope(String view, Customer customer, PostalAddress selected, Boolean details) {
		
		System.out.println("In viewScope map assigned on-entry at:" + view);
		System.out.println("Customer=" + customer + " selectedAddress=" + selected
				+ " details=" + details);
		
	}*/

}
