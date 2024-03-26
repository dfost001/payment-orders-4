package com.mycompany.hosted.checkoutFlow.mvc.controller.paypal;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.picketbox.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.checkoutFlow.exceptions.CheckoutHttpException;
import com.mycompany.hosted.checkoutFlow.exceptions.EndpointRuntimeReason;
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
	
	private boolean testPrintResponseOrThrow = true; //RefundIdException thrown before order updated
	
	private boolean testRefundIdException = false; //Thrown after order updated
	
	private EndpointRuntimeReason reason;

	@Autowired
	private PayPalClient payPalClient;
	
	@Autowired
	private CustomerJpa jpa;	
	
	private HttpServletRequest httpRequest;	
		
	@RequestMapping(value="/refund/request/{orderId}/{serviceId}/{captureId}", method=RequestMethod.GET)
	public String refund(@PathVariable("orderId") Integer orderId,
			@PathVariable("serviceId") String payPalId,
			@PathVariable("captureId") String captureId,
			HttpServletRequest request, RedirectAttributes redirectAttrs) 
					throws CheckoutHttpException {				
			
		this.httpRequest = request;
		
		this.reason = null;
		
		HttpResponse<Refund> response = null;	
		
		OrderPayment orderPayment = null;
		
		String refundId = null;
		
	try {	
		   orderPayment = processOrder(orderId); //Throws NotRetrievableException for OrderNotFound		   
		
		   if(this.alreadyRefunded(orderPayment)) {
			
			redirectAttrs.addFlashAttribute(ALREADY_REFUNDED_KEY, true);
			redirectAttrs.addFlashAttribute(PaymentStatusController.ORDER, orderPayment);
			return "redirect:" + REDIRECT_STATUS_URL + orderId;
			 
		   }
		
		   CapturesRefundRequest refundRequest = new CapturesRefundRequest(orderPayment.getCaptureId());
		
		   refundRequest.requestBody(new RefundRequest());
		
		   refundRequest.prefer("return=representation");	   
		
		   response = payPalClient.client().execute(refundRequest);		
		   
		   EhrLogger.consolePrint(this.getClass(), "refund", "statusCode = " + response.statusCode());
		 
		   refundId = debugPrintResponseOrThrow(response.result()); 		   
		   
		  /* if(evalStatus()) --To Do
			   updateOrderAttrs(); --So Refund details can be shown on error view */
		   
		   OrderPayment updated = this.updateOrderStatus(orderPayment, response);	
		   
		   EhrLogger.consolePrint(this.getClass(), "refund",
					"Order#ServiceDetail#refundId: "
				    + updated.getServiceDetail().getRefundId());
		    
		   redirectAttrs.addFlashAttribute(PaymentStatusController.ORDER, updated);
		   
		   this.doRefundIdException(); // Set to false to see duplicate, but do here to see REFUNDED_ONPERSIST_ERR
		  	    
		   return "redirect:" + REDIRECT_STATUS_URL + orderPayment.getOrderId();
		
		
		} catch (IOException | RuntimeException | RefundIdException 
				| OrderNotRetrievableRefundException e) {
			
			if(e instanceof IOException)
				this.reason = EndpointRuntimeReason.REFUND_EXECUTE_IOEXCEPTION;
			
			CheckoutHttpException checkoutEx = EhrLogger.initCheckoutException(e, 
					"refund", response, reason);
			
			checkoutEx.setCapturedPaymentId(captureId);
			
			checkoutEx.setRefundId(refundId);
			
			checkoutEx.setPersistOrderId(orderId);
			
			checkoutEx.setPayPalId(payPalId);
			
			String id = ServletContextAttrs.setException(checkoutEx);
			
			return "redirect:" + this.REDIRECT_HTTP_ERROR + "?"
					+ WebFlowConstants.CHECKOUT_EXCEPTION_REQUEST_PARAM
					+ "=" + id;
			
		}	   
		
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
		
		/*EhrLogger.consolePrint(this.getClass(), "processOrder", "getErrorsFromSession: size=" +
                        errorBean.getErrorDetailList().size()); */
		
		ErrorDetail errDetail = errorBean.findDetail(orderId);		
		
		if(errDetail == null) {
			
			if(orderId < 0) {
				
				reason = EndpointRuntimeReason.REFUND_ERROR_DETAIL_NOT_FOUND;
				
				EhrLogger.throwIllegalArg(this.getClass(), "processOrder", 
						"OrderId is negative and not found in the ErrorDetailBean. ");
			}
			
			 return orderFromDb(orderId); // throws OrderNotRetrievable
		}
		
		if(errDetail.getOrder() == null) {
			
			reason = EndpointRuntimeReason.REFUND_NULL_ORDER_IN_ERROR_DETAIL;
			
			EhrLogger.throwIllegalArg(this.getClass(), "processOrder", "ErrorDetail contains a null Order");
		}
		EhrLogger.consolePrint(this.getClass(), "findOrder", "returning with errDetail.getOrder " +
                errDetail.getOrder().getOrderId());
		
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
			
		    this.reason = EndpointRuntimeReason.REFUND_ORDER_NOT_RETRIEVABLE;
			
			throw new OrderNotRetrievableRefundException(ex, orderId);
		}
		return order;
	}
	
	private boolean alreadyRefunded(OrderPayment order) {
		
		if(StringUtil.isNullOrEmpty(order.getServiceDetail().getRefundId())) {
			
			EhrLogger.consolePrint(this.getClass(), "alreadyRefunded",
					"Order#ServiceDetail#refundId is NULL or empty for order "
					+ order.getOrderId());
			
			return false;
		}		
		
		EhrLogger.consolePrint(this.getClass(), "alreadyRefunded",
				"Order#ServiceDetail#refundId is initialized for order "
				+ order.getOrderId());
		
		return true;
	}
	
	
	
	
	
	private OrderPayment updateOrderStatus(OrderPayment order, HttpResponse<Refund> response) {
		
		String json = GetOrderDetails.debugPrintJson(response);	 
		
		initRefundedOrder(order, response.result(), json);		
		
		if(order.getOrderId() <= 0) {
			
			String err = "Payment successfuly refunded for error Order: #" + order.getOrderId();		
			
			ErrorDetailBean errorBean = WebFlowConstants.errorBeanFromServletContext(httpRequest);
			
			errorBean.addDetailToList(order, order.getOrderId(), err,
					this.getClass().getCanonicalName() + 
					"#updateOrderStatus", ErrorDetail.ErrorDetailReason.REFUNDED_ONPERSIST_ERR);
			
			EhrLogger.consolePrint(this.getClass(), "updateOrderStatus", 
					"REFUNDED_ONPERSIST_ERR added to errorBean. " );
			
			return order;
		}
		
		OrderPayment updated = null;
		
		try {
			
			updated = jpa.updateRefundedOrder(order);	
			
			//debugPrintUpdatedOrder(updated); // Merge without cascade set on @ManyToOne ServiceDetails throws Runtime
			
		} catch (Exception ex ) {		
			
			ErrorDetailBean errorBean = WebFlowConstants.errorBeanFromServletContext(httpRequest);
			
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
	
	
	private String debugPrintResponseOrThrow(Refund refund) throws RefundIdException {		
		
		String id = refund.id();
		String amount = refund.amount().value();
		String createTime = refund.createTime();
		String updateTime = refund.updateTime();
		String reason = refund.statusDetails() == null ? null : refund.statusDetails().reason() ;
		String status = refund.status();		
	
		
		String debug = MessageFormat.format("id={0} amount={1} createTime={2} updateTime={3}"
				+ " reason={4} status={5}", id, amount, createTime, updateTime, reason, status);
		
		System.out.println(debug);
		
		if(refund.id() == null) {
			
		   this.reason = EndpointRuntimeReason.REFUND_ID_MISSING;	
		   throw new RefundIdException();
		}
		
		if(testPrintResponseOrThrow) {
			testPrintResponseOrThrow = false;
			throw new RefundIdException(EhrLogger.doMessage(
			this.getClass(), 
			"debugPrintResponseOrThrow", "Testing: Likey refunded, but Refund Id is not assigned."));
		}
		
		return refund.id();
		
	}
	
	private void doRefundIdException() throws RefundIdException{
		
		if(this.testRefundIdException) {
		    testRefundIdException = false;
		    this.reason = EndpointRuntimeReason.REFUND_ID_MISSING;
		    throw new RefundIdException(EhrLogger.doMessage(
		    this.getClass(), 
		    "doRefundIdException", "Testing: Likey refunded, but Refund Id is not assigned."));
	    }
		
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
