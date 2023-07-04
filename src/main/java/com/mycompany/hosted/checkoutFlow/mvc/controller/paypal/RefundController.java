package com.mycompany.hosted.checkoutFlow.mvc.controller.paypal;

import java.io.IOException;
import java.math.BigDecimal;


import javax.servlet.http.HttpServletRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutHttpException;
import com.mycompany.hosted.checkoutFlow.exceptions.OrderNotRetrievableRefundException;
import com.mycompany.hosted.checkoutFlow.exceptions.RefundIdException;

import com.mycompany.hosted.checkoutFlow.jpa.CustomerJpa;
import com.mycompany.hosted.checkoutFlow.paypal.orders.GetOrderDetails;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PayPalClient;
//import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails;
import com.mycompany.hosted.checkoutFlow.servlet_context.ServletContextAttrs;
import com.mycompany.hosted.errordetail.ErrorDetailBean;
import com.mycompany.hosted.errordetail.ErrorDetail;
import com.mycompany.hosted.exception_handler.EhrLogger;
//import com.mycompany.hosted.exception_handler.MvcNavigationException;

//import com.mycompany.hosted.model.order.LineItemPayment;
import com.mycompany.hosted.model.order.OrderPayment;

import com.mycompany.hosted.model.order.ServiceDetail;
//import com.mycompany.hosted.model.order.OrderShipTo;
import com.paypal.http.HttpResponse;
import com.paypal.payments.CapturesRefundRequest;
import com.paypal.payments.Refund;
import com.paypal.payments.RefundRequest;

@Controller
public class RefundController {
	
	
	
	private final String REDIRECT_STATUS_URL = "/spring/payment/status?orderId=";
	
	private final String REDIRECT_HTTP_ERROR = "/spring/paymentException/initErrorModel";
	
	private final String ALREADY_REFUNDED_KEY = "ALREADY_REFUNDED_KEY";	
	
	private boolean testRetrieveEx = true;
	
	private boolean testPrintResponseOrThrow = true;

	@Autowired
	private PayPalClient payPalClient;
	
	@Autowired
	private CustomerJpa jpa;	
	
	private HttpServletRequest httpRequest;
	
	private ErrorDetailBean errorBean;
	
	@RequestMapping(value="/refund/request/{orderId}/{serviceId}/{captureId}", method=RequestMethod.GET)
	public String refund(@PathVariable("orderId") Integer orderId,
			@PathVariable("serviceId") String payPalId,
			@PathVariable("captureId") String captureId,
			HttpServletRequest request, RedirectAttributes redirectAttrs) 
					throws CheckoutHttpException {				
			
		this.httpRequest = request;
		
		HttpResponse<Refund> response = null;	
		
		OrderPayment orderPayment = null;
		
		String refundId = null;
		
	try {	
		   orderPayment = processOrder(orderId); //Throws NotRetrievableException for OrderNotFound
		
		   if(this.alreadyRefunded(orderPayment)) {
			
			redirectAttrs.addFlashAttribute(ALREADY_REFUNDED_KEY, true);
			redirectAttrs.addFlashAttribute(PaymentStatusController.ORDER, orderPayment);
			return REDIRECT_STATUS_URL + orderId;
		   }
		
		   CapturesRefundRequest refundRequest = new CapturesRefundRequest(orderPayment.getCaptureId());
		
		   refundRequest.requestBody(new RefundRequest());
		
		   refundRequest.prefer("return=representation");	   
		
		   response = payPalClient.client().execute(refundRequest);
		 
		   System.out.println("RefundController#refund: statusCode = " + response.statusCode());
		 
		   refundId = debugPrintResponseOrThrow(response.result()); //To do: evaluate status field: Throws test   
		
		
		} catch (IOException | RuntimeException | RefundIdException 
				| OrderNotRetrievableRefundException e) {
			
			CheckoutHttpException checkoutEx = EhrLogger.initCheckoutException(e, 
					"refund", response, payPalId, orderId);
			
			checkoutEx.setCapturedPaymentId(captureId);
			
			checkoutEx.setRefundId(refundId);
			
			String id = ServletContextAttrs.setException(checkoutEx);
			
			return "redirect:" + this.REDIRECT_HTTP_ERROR + "?"
					+ WebFlowConstants.CHECKOUT_EXCEPTION_REQUEST_PARAM
					+ "=" + id;
			
		}			
		 	      
	   
	    OrderPayment updated = this.updateOrderStatus(orderPayment, response);	//Initalize from refund result  
	    
	    redirectAttrs.addFlashAttribute(PaymentStatusController.ORDER, updated);
	  	    
	    return "redirect:" + REDIRECT_STATUS_URL + orderPayment.getOrderId();
		
	}
	
	private OrderPayment processOrder(int orderId) throws OrderNotRetrievableRefundException{
		
		if(testRetrieveEx) {
			
			testRetrieveEx=false;
			return findOrder(0);
		}
		else return findOrder(orderId);
	}
	
	private OrderPayment findOrder(Integer orderId) throws OrderNotRetrievableRefundException {		
		
		ErrorDetailBean errorBean = WebFlowConstants.errorBeanFromServletContext(httpRequest);
		
		EhrLogger.consolePrint(this.getClass(), "processOrder", "getErrorsFromSession: size=" +
                        errorBean.getErrorDetailList().size());
		
		ErrorDetail errDetail = errorBean.findDetail(orderId);		
		
		if(errDetail == null) {
			
			if(orderId < 0)
				EhrLogger.throwIllegalArg(this.getClass(), "processOrder", 
						"OrderId is negative and not found in the ErrorDetailBean. ");
			
			 return orderFromDb(orderId); // throws OrderNotRetrievable
		}
		
		if(errDetail.getOrder() == null)
			EhrLogger.throwIllegalArg(this.getClass(), "processOrder", "ErrorDetail contains a null Order");
		
		return errDetail.getOrder();
	}
	
	
	/*
	 * Note: Null order added to Error Bean on failure to retrieve
	 */
	private OrderPayment orderFromDb(Integer orderId) throws OrderNotRetrievableRefundException {
		
		OrderPayment order = null;
		
		try {
			
			order = jpa.findOrderPayment(orderId);
			
			EhrLogger.consolePrint(this.getClass(), "orderFromDb", "Order found:" + orderId);
			
		} catch (Exception ex) {
			
            ErrorDetailBean errorBean = WebFlowConstants.errorBeanFromServletContext(httpRequest);
			
			errorBean.addDetailToList(null, orderId, ex,
					this.getClass().getCanonicalName() + "#orderFromDb", 
					ErrorDetail.ErrorDetailReason.NOT_RETRIEVABLE_FOR_REFUND);			
			
			throw new OrderNotRetrievableRefundException(ex, orderId);
		}
		return order;
	}
	
	private boolean alreadyRefunded(OrderPayment order) {
		
		if(order.getServiceDetail().getRefundId() != null) {		
			
			return true;
					
		}	
		
		return false;
	}
	
	/*private void evalTransactionId(PaymentDetails details) throws MvcNavigationException {
		
		String err = "";

		if(details.getTransactionId() == null) {
			err = EhrLogger.doMessage(this.getClass(), "refund", "Transaction Id is null.");
			
			err += "Assuming browser navigation after details reset during a 2nd transaction";
			
			throw new MvcNavigationException(err);
		}
	}*/
	
	
	
	private OrderPayment updateOrderStatus(OrderPayment order, HttpResponse<Refund> response) {
		
		String json = GetOrderDetails.debugPrintJson(response);	 
		
		initRefundedOrder(order, response.result(), json);
		
		EhrLogger.consolePrint(this.getClass(), "updateOrderStatus",
				"initRefundedOrder returned: refundId=" + order.getServiceDetail().getRefundId());
		
		if(order.getOrderId() <= 0) {
			
			String err = "Payment successfuly refunded for error Order: #" + order.getOrderId();		
			
			errorBean = WebFlowConstants.errorBeanFromServletContext(httpRequest);
			
			errorBean.addDetailToList(order, order.getOrderId(), err,
					this.getClass().getCanonicalName() + 
					"#updateOrderStatus", ErrorDetail.ErrorDetailReason.REFUNDED_ONPERSIST_ERR);
			
			return order;
		}
		
		OrderPayment updated = null;
		
		try {
			
			updated = jpa.updateRefundedOrder(order);	
			
			//debugPrintUpdatedOrder(updated); // Merge without cascade set on @ManyToOne throws Runtime
			
		} catch (Exception ex ) {		
			
			errorBean = WebFlowConstants.errorBeanFromServletContext(httpRequest);
			
			errorBean.addDetailToList(order, order.getOrderId(),  ex,
				this.getClass().getCanonicalName() + "#updateOrderStatus", 
				ErrorDetail.ErrorDetailReason.REFUND_UPDATE_ERR);				
			
			return order;
		}
		
		
		return updated;
		
	}
	
	private void initRefundedOrder(OrderPayment order, Refund refund,
			String refundJson)  {
	
	order.setPaymentStatus(PaymentDetails.CaptureStatusEnum.REFUNDED.name()); // Cannot use Refund#status -> Completed
		
	ServiceDetail serviceDetail = order.getServiceDetail();
	
	serviceDetail.setRefundId(refund.id());
	
	serviceDetail.setRefundJson(refundJson);
	
	serviceDetail.setCaptureStatus(refund.status());
	
	BigDecimal refundAmount = new BigDecimal(refund.amount().value());
	
	serviceDetail.setRefundAmount(refundAmount);
	
	serviceDetail.setRefundTime(refund.createTime());
	
}
	/*
	 * Bug: PaymentExceptionController expects CheckoutException in ServletContext
	 */
	/*private void initCheckoutHttpException(Exception e) {
		
		CheckoutHttpException checkoutEx = new CheckoutHttpException(e, "refund");
		
		session.setAttribute("checkoutHttpException", checkoutEx);	
		
	}*/
	
	private String debugPrintResponseOrThrow(Refund refund) throws RefundIdException {		
		
		System.out.println("RefundController#debugPrintResponse: id=" 
			 + refund.id() + " amount="
		     + refund.amount().value());			
		
		if(refund.id() == null)
		   throw new RefundIdException();
		
		if(testPrintResponseOrThrow) {
			testPrintResponseOrThrow = false;
			throw new RefundIdException(EhrLogger.doMessage(
				this.getClass(), "debugPrintResponseOrThrow", "Testing: Likey refunded, but Refund Id is null in the Response"));
		}
		
		return refund.id();
		
	}
	
 /*private void debugPrintUpdatedOrder(OrderPayment order) {
		
        String err="";
		
		Customer custId = order.getCustomerId();
		
		if(custId == null)
			err="Customer ";
		
		OrderShipTo shipTo = order.getOrderShipTo();
		
		if(shipTo == null)
			err = "OrderShipTo ";
		
		List<LineItemPayment> list= order.getLineItemPayments();
		
		if(list == null)
			err = "List<LineItemPayment> ";
		
		ServiceDetail serviceDetail = order.getServiceDetail();
		
		if(serviceDetail == null) {
			err = "ServiceDetails ";
		}
		else if(serviceDetail.getRefundId() == null)
			err = "ServiceDetail#refundId ";
		
		if(!err.isEmpty()) {
			err = "Null attributes: " + err;
			EhrLogger.throwIllegalArg(this.getClass(),"debugPrintOrder", err);
		}
			
		
		System.out.println("RefundController#debugPrintOrder: "
				+ custId.getId() + " "
				+ "status=" + order.getPaymentStatus() + " "
				+ shipTo.getFirstName() + " "
				+ list.get(0).getDescription());
		
	} */

	
	
} //end class
