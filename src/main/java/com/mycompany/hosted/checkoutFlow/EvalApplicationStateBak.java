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


//@Component
public class EvalApplicationStateBak {
	
	
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
	
	
	
	public void evalLogin(RequestContext ctx) throws FlowNavigationException {		
		
		initCurrentSessionState(ctx); //From session into module level variables
		
		initEntrySessionState(ctx); //From view-scope into module-level
		
		//debugViewScope("Login", entryCustomer, entrySelectedAddress, entryPaymentDetails);
		
		boolean expectedOnEnter = entryCustomer == null
				&& entrySelectedAddress == null && entryPaymentDetails == null;		
		
		boolean expectedOnRender = currentCustomer == null && currentSelectedAddress == null 
				&& currentPaymentDetails == null; 
		
		evalExpectedState(expectedOnEnter, "on-enter", "evalLogin", "");
		
		evalExpectedState(expectedOnRender, "on-render", "evalLogin", "");
     
	}
	
	/*
	 * If selected address does not compare, end-user may be at payment-buttons view.
	 */
	public void evalSelectAddress(RequestContext ctx) throws FlowNavigationException {
        
		
		initCurrentSessionState(ctx);
		
		initEntrySessionState(ctx);
		
		//debugViewScope("evalSelectAddress", entryCustomer, entrySelectedAddress, entryPaymentDetails);
		
		boolean expectedOnEnter = entryCustomer != null &&  entryPaymentDetails == null;		
		
		boolean expectedOnRender = currentCustomer != null && currentPaymentDetails == null
				&& currentSelectedAddress == entrySelectedAddress ; 
		
        String detail = currentSelectedAddress != entrySelectedAddress ?
    	    		"Selected address on-entry and in current session are not equal" : "";
			
        evalExpectedState(expectedOnEnter, "on-enter", "evalSelectAddress", "");
		
		evalExpectedState(expectedOnRender, "on-render", "evalSelectAddress", detail);
		
	}
	
	public void evalPaymentButtonView(RequestContext ctx) throws FlowNavigationException {
		   
			
			initCurrentSessionState(ctx);
			
			initEntrySessionState(ctx);
			
		//	debugViewScope("paymentButtonView", entryCustomer, entrySelectedAddress, entryPaymentDetails);
			
			boolean expectedOnEnter = entryCustomer != null &&  entrySelectedAddress != null
					&& entryPaymentDetails == null;			
			
			boolean expectedOnRender = currentCustomer != null && currentSelectedAddress != null
					&& currentPaymentDetails == null; 
			
			evalExpectedState(expectedOnEnter, "on-enter", "evalPaymentButtonView", "");
				
			evalExpectedState(expectedOnRender, "on-render","evalPaymentButtonView", "");
			
	      
	}
	/*
	 * Note: Removed application exception: hasDetails=false, transactionId=null
     * Bug: If enter at an open URL from the error support view, a Runtime error is thrown
     * Alternative: Do not remove hasDetails at Capture action-state until a successful transaction
	 */
	public void evalPaymentDetailView(RequestContext ctx) throws FlowNavigationException {		    
			
			initCurrentSessionState(ctx);
			
			initEntrySessionState(ctx);
			
			//debugViewScope("paymentDetailView", entryCustomer, entrySelectedAddress, entryPaymentDetails);			
			
			String issue = PaymentObjectsValidator.validateDetailsBeforeCapture(this.entryPaymentDetails);				
			
			boolean expectedOnEnter = entryCustomer != null &&  entrySelectedAddress != null				
					&& entryPaymentDetails != null 					
					&& issue.isEmpty();		
			
			evalExpectedState(expectedOnEnter, "on-enter", "evalPaymentDetailView", issue);		
			
			issue = PaymentObjectsValidator.validateDetailsBeforeCapture(this.currentPaymentDetails);	
			
			//Application error, not browser-navigation
			if(currentPaymentDetails != null && !issue.isEmpty())
				EhrLogger.throwIllegalArg(this.getClass(), "evalPaymentDetailView", 
						"currentDetails is non-null and not valid: " + issue)	;
			
			issue = currentPaymentDetails == null ? "Details view entered after Capture completed. " : "";
			
			boolean expectedOnRender = currentSelectedAddress != null
					&& currentPaymentDetails != null ;
			
			evalExpectedState(expectedOnRender, "on-render","evalPaymentDetailView", issue);			
	}
	
	
	
	public void evalPostalEditView(RequestContext ctx) 
			 throws FlowNavigationException {		
		
		
		this.initCurrentSessionState(ctx); //Copy session attrs into module-level
		
		this.initEntrySessionState(ctx); //Copy viewScope attrs into module-level
	
		
		boolean expectedOnEnter = this.entryPaymentDetails == null; //Session Customer may be null if creating	
		
		boolean expectedOnRender = entryCustomer == currentCustomer && currentPaymentDetails == null;	
					
	    evalExpectedState(expectedOnEnter, "on-enter", "evalPostalEditView", "");
        
        String issue = entryCustomer != currentCustomer ?
	    		"Customer on-entry is not equal to current session. " : ""; //Not cloned until success
       
		evalExpectedState(expectedOnRender, "on-render", "evalPostalEditView", issue);		
		
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
	
	public void print(RequestContext request, String detail) {
		
		String view = request.getCurrentView() == null ?
				"null" : request.getCurrentView().toString();
		
		String action = request.getCurrentState() == null ? "null" :
			request.getCurrentState().getId();
		
		String transition = request.getCurrentTransition() == null ?
				"null" : request.getCurrentTransition().getId();
		
		String event = request.getCurrentEvent() == null ? "null"
				: request.getCurrentEvent().getId();
		
		String line = detail + ": " + "view=" + view
				+ " currentState=" + action
				+ " transition=" + transition
				+ " event=" + event;
		
		System.out.println(line);
		
	}
	
	/*private void debugViewScope(String view, Customer customer, PostalAddress selected, Boolean details) {
		
		System.out.println("In viewScope map assigned on-entry at:" + view);
		System.out.println("Customer=" + customer + " selectedAddress=" + selected
				+ " details=" + details);
		
	}*/

}
