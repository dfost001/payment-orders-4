package com.mycompany.hosted.checkoutFlow.mvc.controller.paypal;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.WebApplicationContext;

import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.checkoutFlow.paypal.orders.CaptureOrder;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails;

import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.exception_handler.MvcNavigationException;
import com.mycompany.hosted.formatter.StringUtil;
import com.paypal.orders.ProcessorResponse;

/*
 * To do: Correlate status with a documented error message (Done).
 * 
 * Note: Failed status can occur either on GetDetails or on Capture (Stub)
 * 
 * Note: PaymentDetails are removed so flow will be not be re-entered at Details view
 * 
 */

@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class FailedPaymentStatusController {
	
	private ArrayList<String> messages = new ArrayList<>();
	
	private final String MESSAGE_LIST_KEY = "MESSAGE_LIST_KEY";
	
	private final String ADDR_CODE_VALUE_KEY = "addrCodeValue" ;
	
	private final String GET_DETAILS_MSG = "Unexpected VOIDED status returned for requested payment details. ";
	
	private final String CVV_INVALID_MSG = "CVV Security Code is invalid. ";
	
	private final String CVV_INVALID_CARD_OK = "CVV is invalid, but your card-number validates. " 
			+ "Not clear if payment was processed. Please contact the issuer.";
	
	private final String CARD_INVALID_MSG = "The card cannot be accepted. Try using a different card: ";
	
	private final String TRANSACT_NOT_PROCCESSED = "The transaction cannot be completed. Please contact " +
	    "either the card-issuer or support: ";
	
	private final String CARD_DECLINED_NO_REASON = "Your card has been declined. No reason is assigned. Contact the issuer. " ;
	
	private final String STATUS_SUCCESS_WITH_FAILED_REASON = "Status SUCCESS With Failed Reason: " 
			+ "Transacted-State is not certain. " 
			+ "Please contact the card-issuer. ";
	
	private final String ADDRESS_ERR_MSG = "There is a problem with the Billing address. " +
	  "Either the address does not match the card or a postal field (city, state, zip) is incorrect";
	
	private boolean errorOnGetDetails;
	
	@GetMapping(value="/failedStatus/handle")
	public String handleFailedStatus(HttpSession session, ModelMap model)
	    throws MvcNavigationException {
		
		PaymentDetails details =
				(PaymentDetails)session.getAttribute(WebFlowConstants.PAYMENT_DETAILS);
		
		if(details == null)
			throw new MvcNavigationException();		
	
		boolean cardValid = false;
		
		if(!(errorOnGetDetails = isGetDetailsError(details))) {
						
		     cardValid = evalProcessorResponse(details, model);
		
		}
		
		if(details.getStatusReason() != null)
			messages.add("Failed Capture Reason: " + details.getStatusReason().name());
		
		this.evalCaptureStatusOrThrow(details, cardValid);
		
		model.addAttribute(WebFlowConstants.PAYMENT_DETAILS, details);
		
		model.addAttribute(MESSAGE_LIST_KEY, messages);
		
		session.removeAttribute(WebFlowConstants.PAYMENT_DETAILS);
		
		return "jsp/paymentFailedStatus";
	}
	/*
	 * Returns if error occurred at Capture
	 * Throws Runtime if createdStatus is Null or status indicates success
	 */
	private boolean isGetDetailsError(PaymentDetails details) {		
		
		
		if(details.getCaptureTime() != null) 
			return false;
		
		if(details.getCreatedStatus() == null)
			EhrLogger.throwIllegalArg(this.getClass(), "isGetDetailsError", 
					"PaymentDetails#createdStatus is null. Should have been evaluated at GetDetails. ");
		
		switch (details.getCreatedStatus()) {
		   case CREATED: 
		   case SAVED: 
		   case APPROVED: 
		   case COMPLETED: 
			   EhrLogger.throwIllegalArg(this.getClass(), "isGetDetailsError", 
						"PaymentDetails#createdStatus has an unexpected successful value.");
		   case PAYER_ACTION_REQUIRED:	 
			   EhrLogger.throwIllegalArg(this.getClass(), "isGetDetailsError", 
						"PaymentDetails#createdStatus contains PAYER_ACTION_REQUIRED - developer error");
		   case VOIDED: 
			   this.messages.add(this.GET_DETAILS_MSG);
		       break;
		}
		
		return true;
	}
	
	private boolean evalProcessorResponse(PaymentDetails details, ModelMap map) {
		
		boolean valid = true;
		
		ProcessorResponse resp = details.getProcessorResponse();
		
		if(resp == null) //Should have already been thrown from CaptureOrder
			EhrLogger.throwIllegalArg(this.getClass(), "evalProcessorResponse", 
					"ProcessorResponse property of PaymentDetails is null");			
		
		if(CaptureOrder.isCvvError(resp.cvvCode())) {
			this.addCvvMessages(resp.cvvCode());
			valid = false;
		}
		
		if(CaptureOrder.isCvvError(resp.cvvCode()) && resp.getResponseCode().contentEquals("0000")) {
			messages.add(this.CVV_INVALID_CARD_OK);
			valid = false; 
		}
		
		if(resp.responseCode() != null && !resp.responseCode().contentEquals("0000")) {
			this.evalCardErrorMessage(resp.responseCode());
			valid = false;
		}
		
		if(resp.avsCode() != null) {
		    messages.add(this.ADDRESS_ERR_MSG);
		    valid =false;
		} else {
			map.addAttribute(ADDR_CODE_VALUE_KEY, "No error returned.");
		}
		
		return valid;		
	}
	
	private void addCvvMessages(String cvvCode) {
		
		if(StringUtil.isNullOrEmpty(cvvCode))
			return;
		
		String err = this.CVV_INVALID_MSG + ": ";
		
		switch(cvvCode) {
		
		case "P" : //Not Processed
			err += "Not processed due to card error. ";
			break;
		case "E" :	//Error-Unrecognized response
		case "S" :  //Service not supported
		case "X" :  //No response
		case "U" :	//Unknown issuer		  
			err += "Undefined error. "	;
			break;		  
		case "I" : //Invalid
		case "N" : //No Match			
		default: 
		   err += "Cannot be matched to a card. ";
	   }
		this.messages.add(err) ;
	}
	
	
	private void evalCardErrorMessage(String code) {
		
		String msg = "";
		
		switch (code) {
		case "0000" :
			break;
		case "9500":
			msg = this.CARD_INVALID_MSG + "Fraudulent Card";
			break;
		case "5100":
			msg = this.TRANSACT_NOT_PROCCESSED + "Card is declined";
			break;
		case "00N7":
			msg = this.TRANSACT_NOT_PROCCESSED + "CVC security digits failed. A retry is possible.";
			break;
		case "5110":
			msg = this.TRANSACT_NOT_PROCCESSED + "CVC security digits failed. Please contact the issuer. ";
			break;
		case "5180":
			msg = this.CARD_INVALID_MSG + "Card digits entered incorrectly";
			break;
		case "5120":
			msg = this.CARD_INVALID_MSG + "Insufficient funds";
			break;
		case "9520":
			msg = this.CARD_INVALID_MSG + "Card reported lost or stolen";
			break;
		case "0500" :
		    msg = this.TRANSACT_NOT_PROCCESSED + "Card refused";
		    break;
		default:
			msg = this.TRANSACT_NOT_PROCCESSED;
			break;		
		}
		if(!msg.isEmpty())
		   messages.add(msg);
	}
	
	private void evalCaptureStatusOrThrow(PaymentDetails details, boolean cardValid) {
		
		if(errorOnGetDetails)
			return;
		
		//Check on CaptureOrder code
		if(cardValid && details.getStatusReason() == null) {
			
			if(CaptureOrder.isValidCaptureStatus(details))
					EhrLogger.throwIllegalArg(this.getClass(), "evalCaptureStatusOrThrow", 
					"No failure reasons and a succcess CaptureStatus - Incorrect redirect to this controller. ");
			
			else messages.add(CARD_DECLINED_NO_REASON);
			
		} else if(CaptureOrder.isValidCaptureStatus(details)) { //One or both failed reason
			messages.add(STATUS_SUCCESS_WITH_FAILED_REASON);
		}
	}
	

 
  /*  private void debugPrintFailedReason(PaymentDetails details) {
    	if(details.getStatusReason() != null)
    		System.out.println(this.getClass().getName()
    				+ ": Details#statusReason is null");
    	else System.out.println(this.getClass().getName()
				+ ": Details#statusReason is NOT null: "
				+ details.getStatusReason().name());
    } */

}//end class
