package com.mycompany.hosted.checkoutFlow;

import java.util.Date;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;

import com.mycompany.hosted.cart.Cart;
import com.mycompany.hosted.cart.CartItem;
import com.mycompany.hosted.checkoutFlow.jpa.CustomerJpa;
import com.mycompany.hosted.checkoutFlow.paypal.orders.PaymentDetails;
import com.mycompany.hosted.errordetail.ErrorDetail.ErrorDetailReason;
import com.mycompany.hosted.errordetail.ErrorDetailBean;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.formatter.StringUtil;
import com.mycompany.hosted.model.Customer;
import com.mycompany.hosted.model.PostalAddress;
import com.mycompany.hosted.model.order.LineItemPayment;
import com.mycompany.hosted.model.order.OrderPayment;
import com.mycompany.hosted.model.order.OrderShipTo;
import com.mycompany.hosted.model.order.ServiceDetail;

@Component
public class JpaPersistOrder {
	
	@Autowired
	private CustomerJpa jpa;
	
	private ErrorDetailBean errorDetailBean;
	
	public OrderPayment persist(RequestContext context) {
		
		System.out.println("JpaPersistOrder#persist executing");
		
		SharedAttributeMap<Object> map = context.getExternalContext().getSessionMap();
		
		Customer customer = (Customer)map.get(WebFlowConstants.CUSTOMER_KEY);
		
		Cart cart = (Cart)map.get(WebFlowConstants.CART);			
		
		PostalAddress address = (PostalAddress)map.get(WebFlowConstants.SELECTED_POSTAL_ADDR);
		
		PaymentDetails paymentDetails = (PaymentDetails)map.get(WebFlowConstants.PAYMENT_DETAILS);
		
		evalNull(customer,address, cart,paymentDetails);
		
		OrderPayment order = this.initOrderCaptured(customer, cart, address, paymentDetails);
		
		OrderPayment updated = null;
		
		try {
			
			System.out.println("JpaPersistOrder#persist: Invoking transactional component");
			
			 updated = jpa.saveOrder(order);	
			 
		} catch (Exception e) {			
			 
			order.setOrderId(generateFailedId());	
			
			errorDetailBean = WebFlowConstants.errorBeanFromServletContext(context);
			
			errorDetailBean.addDetailToList(order, order.getOrderId(), e,
					this.getClass().getCanonicalName() + "#persist",
					ErrorDetailReason.PERSIST_ORDER_ERR);
			
			return order;
		}
		   
		
		debugPrintOrder(updated);
		
		return updated;
		
	}
	
	private void evalNull(Customer customer, PostalAddress postal, Cart cart, PaymentDetails details) {
		
		String err="";
		
		if(customer == null)
			err = "Customer ";
		if(postal == null)
			err += "PostalAddress ";
		if(cart == null)
			err += "Cart";
		if(details == null)
		    err += "PaymentDetails";
		if(!err.isEmpty()) {
			
			System.out.println("JpaPersistOrder#evalNull: throwing exception");
			
			err = "Null attributes: " + err;	
			
		    EhrLogger.throwIllegalArg(this.getClass(), "evalNull", err);
		}
	}
	
	
	
	private OrderPayment initOrderCaptured(Customer customer, Cart cart, 
			PostalAddress address, PaymentDetails details) {
		
		OrderPayment order = new OrderPayment();
		
		order.setCustomerId(customer);
		
		order.setOrderShipTo(initShipTo(address));
		
		order.setOrderAmountGrand(cart.getGrandTotal());
		
		order.setOrderDate(new Date());
		
		order.setOrderShippingFee(cart.getShippingFee());
		
		order.setOrderSubtotal(cart.getSubtotal());
		
		order.setOrderTax(cart.getTaxAmount());
		
		order.setPaymentStatus(details.getCaptureStatus().name());		
		
		order.setServiceDetail(initServiceDetail(details));
		
		order.setCaptureId(details.getTransactionId());
		
		addLineItems(order, cart);
		
		return order;
		
		
	}
	
	private OrderShipTo initShipTo(PostalAddress selected) {
		
		OrderShipTo shipTo = new OrderShipTo();
		
		shipTo.setAddress(selected.getAddress());
		
		shipTo.setCity(selected.getCity());
		
		shipTo.setPostalCode(selected.getPostalCode());
		
		shipTo.setEmail(selected.getEmail());
		
		shipTo.setState(selected.getState());
		
		shipTo.setFirstName(selected.getFirstName());
		
		shipTo.setLastName(selected.getLastName());
		
		shipTo.setPhone(selected.getPhone());
		
		return shipTo;
		
	}
	
	private ServiceDetail initServiceDetail(PaymentDetails details) {
		
		String err = PaymentObjectsValidator.validateDetailsAfterCapture(details);
		
		if(!StringUtil.isNullOrEmpty(err))
			EhrLogger.throwIllegalArg(this.getClass(), "initServiceDetail", err); 
		
		ServiceDetail service = new ServiceDetail();
		
		service.setCaptureId(details.getTransactionId());
		
		service.setCaptureStatus(details.getCaptureStatus().name());
		
		service.setCardDigits(details.getLastDigits());
		
		service.setCardExpiry(details.getExpiry());
		
		service.setCardType(details.getCardType());
		
		service.setCreateTime(details.getCaptureTime());
		
		service.setRefundId(null);
		
		service.setServiceId(details.getPayPalResourceId());
		
		service.setBillingAddress(details.getBillingAddressLine());
		
		service.setBillingEmail(details.getBillingEmail());
		
		service.setBillingName(details.getBillingName());
		
		service.setCaptureJson(details.getJson());
		
		return service;
		
	}
	
	private void addLineItems(OrderPayment order, Cart cart) {
		
		for(CartItem item : cart.getCartList()) {
			
			LineItemPayment lineItem = new LineItemPayment();
			
			lineItem.setDescription(item.getBook().getTitle());
			
			lineItem.setExtPrice(item.getExtPrice());
			
			lineItem.setPrice(item.getPrice());
			
			lineItem.setQuantity(item.getQuantity());
			
			lineItem.setProductId(item.getBook().getId());
			
			order.addLineItemPayment(lineItem);
		}
		
	}
	
	private Integer generateFailedId () {
		
		Long time = System.currentTimeMillis();
		
		Integer hash = time.hashCode();
		
		hash = hash > 0 ? -1 * hash : hash;	
		
		return hash;		
	}
	
	private void debugPrintOrder(OrderPayment order) {
		
		String err="";
		
		Customer custId = order.getCustomerId();
		
		if(custId == null)
			err="Customer ";
		
		OrderShipTo shipTo = order.getOrderShipTo();
		
		if(shipTo == null)
			err = "OrderShipTo ";
		else if(shipTo.getId() == 0)
			err="OrderShipTo#id is not initialized ";
		
		List<LineItemPayment> list= order.getLineItemPayments();
		
		if(list == null)
			err = "List<LineItemPayment> ";
		
		ServiceDetail detail = order.getServiceDetail();
		
		if(detail == null)
			err = "ServiceDetail ";
		else if(detail.getId() == 0)
			err = "ServiceDetail#id is not initialized";
		
		if(!err.isEmpty()) {
			err = "Null attributes: " + err;
			EhrLogger.throwIllegalArg(this.getClass(),"debugPrintOrder", err);
		}
			
		
		System.out.println("JpaPersistOrder#debugPrintOrder: "
				+ custId.getId() + " "
				+ shipTo.getFirstName() + " "
				+ list.get(0).getDescription() + " "
		        + detail.getCaptureId());
		
	}
	
	

}
