package com.mycompany.hosted.checkoutFlow.mvc.controller.paypal;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails.CaptureStatusEnum;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.exception_handler.MvcNavigationException;
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
public class FailedPaymentStatusController {
	
	private ArrayList<String> messages = new ArrayList<>();
	
	private final String MESSAGE_LIST_KEY = "MESSAGE_LIST_KEY";
	
	private final String GET_DETAILS_MSG = "Unexpected VOIDED status returned for requested payment details. ";
	
	private final String CVV_INVALID_MSG = "CVV Security Code is invalid";
	
	private final String CARD_INVALID_MSG = "The card cannot be accepted. Try using a different card: ";
	
	private final String TRANSACT_NOT_PROCCESSED = "The transaction cannot be completed. Please contact " +
	    "either the card-issuer or support: ";
	
	private final String ADDRESS_ERR_MSG = "There is a problem with the Billing address. " +
	  "Either the address does not match the card or a postal field (city, state, zip) is incorrect";
	
	@GetMapping(value="/failedStatus/handle")
	public String handleFailedStatus(HttpSession session, ModelMap model)
	    throws MvcNavigationException {
		
		PaymentDetails details =
				(PaymentDetails)session.getAttribute(WebFlowConstants.PAYMENT_DETAILS);
		
		if(details == null)
			throw new MvcNavigationException();	
	
		
		if(!isGetDetailsError(details)) {
						
		      evalProcessorResponse(details, model);
		
		}
		
		if(details.getStatusReason() != null)
			messages.add("Failed Reason: " + details.getStatusReason().name());
		
		model.addAttribute(WebFlowConstants.PAYMENT_DETAILS, details);
		
		model.addAttribute(MESSAGE_LIST_KEY, messages);
		
		session.removeAttribute(WebFlowConstants.PAYMENT_DETAILS);
		
		return "jsp/paymentFailedStatus";
	}
	
	private boolean isGetDetailsError(PaymentDetails details) {		
		
		
		if(details.getCaptureTime() != null)
			return false;
		
		if(details.getCreatedStatus() == null)
			EhrLogger.throwIllegalArg(this.getClass(), "isGetDetailsError", 
					"PaymentDetails#createdStatus is null");
		
		switch (details.getCreatedStatus()) {
		   case CREATED: 
		   case SAVED: 
		   case APPROVED: 
		   case COMPLETED: 
			   EhrLogger.throwIllegalArg(this.getClass(), "isGetDetailsError", 
						"PaymentDetails#createdStatus has a successful expected value.");
		   case PAYER_ACTION_REQUIRED:	 
			   EhrLogger.throwIllegalArg(this.getClass(), "isGetDetailsError", 
						"PaymentDetails#createdStatus contains PAYER_ACTION_REQUIRED - developer error");
		   case VOIDED: 
			   this.messages.add(this.GET_DETAILS_MSG);
		       break;
		}
		
		return true;
	}
	
	private void evalProcessorResponse(PaymentDetails details, ModelMap map) {
		
		boolean valid = true;
		
		ProcessorResponse resp = details.getProcessorResponse();
		
		if(resp == null) //Should have already been thrown from CaptureOrder
			EhrLogger.throwIllegalArg(this.getClass(), "evalProcessorResponse", 
					"ProcessorResponse property of PaymentDetails is null");		
		
		if(isCvvError(resp.cvvCode())) {
			messages.add(this.CVV_INVALID_MSG);
			valid = false; 
		}
		if(resp.responseCode() != null && !resp.responseCode().contentEquals("0000")) {
			this.evalCardErrorMessage(resp.responseCode());
			valid = false;
		}
		
		if(resp.avsCode() != null) {
		    messages.add(this.ADDRESS_ERR_MSG);
		    valid =false;
		}
		
		//Check on CaptureOrder code
		if(valid && this.isValidCaptureStatus(details) && details.getStatusReason() == null)
			EhrLogger.throwIllegalArg(this.getClass(), "evalProcessorResponse", 
					"No failure has been evaluated: ProcessorResponse and CaptureStatus are OK");
		
		String addrCodeValue = resp.avsCode() == null ? "None" : resp.avsCode();
		
		map.addAttribute("addrCodeValue", addrCodeValue);
	}
	
	private boolean isCvvError(String cvvCode) {
		
		if(cvvCode == null)
		    return false;
		
		boolean isError = true;
		
		switch(cvvCode) {
		
		case "E" :
		case "M" :
		case "P" :
		case "S" :
		case "X" :
		   isError=false;	
		   break;
		default: 
		   isError=true;
		}
		
		return isError;
		
	}
	
	private void evalCardErrorMessage(String code) {
		
		String msg = "";
		
		switch (code) {
		
		case "9500":
			msg = this.CARD_INVALID_MSG + "Fraudulent Card";
			break;
		case "5100":
			msg = this.TRANSACT_NOT_PROCCESSED + "Card is declined";
			break;
		case "00N7":
			msg = this.TRANSACT_NOT_PROCCESSED + "CVC security digits failed";
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
		messages.add(msg);
	}
	
	private boolean isValidCaptureStatus(PaymentDetails details) {
		
		boolean valid = false;
		
		CaptureStatusEnum status = details.getCaptureStatus();
		
		switch (status) {
		case COMPLETED :
		case PARTIALLY_REFUNDED:
		case PENDING:
		case REFUNDED:
			valid = true;
			break;
		case FAILED:
		case DECLINED:	
			valid = false;
			break;
		default:
			EhrLogger.throwIllegalArg(this.getClass(), "isValidCaptureStatus", 
					"Unknown Capture Status value.");
			
		}
		
		return valid;
		
	}

}
