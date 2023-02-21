package com.mycompany.hosted.checkoutFlow.mvc.controller.paypal;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import com.mycompany.hosted.cart.Cart;
import com.mycompany.hosted.checkoutFlow.WebFlowConstants;
import com.mycompany.hosted.checkoutFlow.exceptions.RefundPaymentException;
import com.mycompany.hosted.checkoutFlow.jpa.CustomerJpa;

import com.mycompany.hosted.errordetail.ErrorDetail;
import com.mycompany.hosted.errordetail.ErrorDetailBean;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.model.Customer;
import com.mycompany.hosted.model.order.LineItemPayment;
import com.mycompany.hosted.model.order.OrderPayment;
import com.mycompany.hosted.model.order.OrderShipTo;
import com.mycompany.hosted.model.order.ServiceDetail;
/*
 * To do: 
 * get items and Id's (status) from Order
 * Remove PaymentDetails from session.
 * Problem: Redirect request cannot be under cache-control. Transaction Id may
 * be empty for browser navigation.
 */
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class PaymentStatusController {
	
	
	@Autowired 
	private Cart cart;	
	
	@Autowired
	private CustomerJpa jpa;	
	
	public static final String ORDER = WebFlowConstants.ORDER_ENTITY_VALUE; //"order"
	
	private HttpSession session;
	
	@GetMapping(value="/payment/status")
	public String showOrderStatus(@RequestParam("orderId") Integer orderId, 			         
			HttpServletRequest request, ModelMap model) throws RefundPaymentException  {		
		
		System.out.println("PaymentStatusController is executing for order " + orderId);
		
		this.session = request.getSession();
		
		OrderPayment order = this.processOrder(orderId, model, request);
		
		debugPrintOrThrowOrder (order);		
		
		model.addAttribute(ORDER, order);
		
		model.addAttribute(WebFlowConstants.CART, cart);	
		
		model.addAttribute(WebFlowConstants.ERROR_DETAIL_BEAN, 
				WebFlowConstants.errorBeanFromServletContext(request));	
		
		model.addAttribute("orderId", orderId); //Rendered in Refund request
		
		return "jsp/paymentStatus";
		
	}
   /*
    * To do: Make sure orderId in session is the same as the parameter
    * To do: If order is negative and not found in ErrorBean, the application
    * may have been re-deployed (ServletContext reinitialized). More code to
    * compare date coded on error orderId and servletContext attribute.
    */
   private OrderPayment processOrder(Integer orderId,
		   ModelMap map,
		   HttpServletRequest request)  {	   
	   
	   OrderPayment order;	   
	   
	   order = (OrderPayment)	session.getAttribute(WebFlowConstants.ORDER_ENTITY_VALUE);
	   
	   if(order != null) {
		   
		   session.removeAttribute(WebFlowConstants.ORDER_ENTITY_VALUE);		   
		   
		   return order;
	   }
	   
	   order = (OrderPayment) map.get(ORDER); //FlashAttribute added on RefundController
	   
	   if(order != null)
		   return order;
	    
	   ErrorDetailBean errorBean = WebFlowConstants.errorBeanFromServletContext(request);
		
	   ErrorDetail errDetail = errorBean.findDetail(orderId);
		
		if(errDetail != null) {
			
			EhrLogger.consolePrint(this.getClass(), "processOrder", "Returning order from ErrorBean.");
			return errDetail.getOrder();
		}
			
		if(orderId < 0)
				EhrLogger.throwIllegalArg(this.getClass(), "processOrder", 
						"OrderId is negative and not found in the ErrorDetailBean. ");
			
		return orderFromDb(orderId);	 
	   
	}
	
  /*
   * To do: Configure Persistance Transalation, DataAccessException resolver.
   */
	
	private OrderPayment orderFromDb(Integer orderId)  {
		
		OrderPayment order = null;
		
		try {
			
			order = jpa.findOrderPayment(orderId);
			
		} catch (Exception ex) {           
			
			EhrLogger.throwIllegalArg(this.getClass(), "orderFromDb", 
					"Persistence error: Unable to retrieve Order. ", ex);
		}
		EhrLogger.consolePrint(this.getClass(), "orderFromDb", "Returning orderFromDb. ");
		return order;
	}	
	
	@GetMapping(value="/payment/receipt")
	public String showReceipt(ModelMap model) {
		
		
		
		return "jsp/receipt";
		
	}
	
	
	
	private void debugPrintOrThrowOrder(OrderPayment order) {
		
		if(order == null)
			EhrLogger.throwIllegalArg(this.getClass(), "showOrderStatus", "Persisted Order is not in the session. ");
		
        String err="";
		
		Customer custId = order.getCustomerId();
		
		if(custId == null)
			err=" Customer ";
		
		OrderShipTo shipTo = order.getOrderShipTo();
		
		if(shipTo == null)
			err += " OrderShipTo ";
		
		List<LineItemPayment> list= order.getLineItemPayments();
		
		if(list == null)
			err += " List<LineItemPayment> ";

		ServiceDetail serviceDetail = order.getServiceDetail();
		
		if(serviceDetail == null) {
			err = "ServiceDetails ";
		}
		else if(serviceDetail.getCaptureId() == null)
			err = "ServiceDetail#captureId ";
		
		if(!err.isEmpty()) {
			err = "Null OrderPayment attributes: " + err;
			System.out.println(err);
			EhrLogger.throwIllegalArg(this.getClass(),"debugPrintOrder", err);
		}
			
		
		System.out.println("PaymentStatusController#debugPrintOrder: "
				+ custId.getId() + " "
				+ shipTo.getFirstName() + " "
				+ list.get(0).getDescription()
		        + " refundId = " + serviceDetail.getRefundId());
		
	}

} //end Controller
