package com.mycompany.hosted.checkoutFlow.jpa;

import com.mycompany.hosted.model.Customer;
import com.mycompany.hosted.model.ShipAddress;
import com.mycompany.hosted.model.order.OrderPayment;


public interface CustomerJpa {
	
	public Customer findCustomer(Integer id) 
			throws CustomerNotFoundException;
	
	public Customer updateCustomer(Customer customer);	
	
	public Customer updateShipAddress(Customer customer, ShipAddress shipAddress);
	
	public OrderPayment saveOrder(OrderPayment order) throws Exception ;	
	
	public OrderPayment findOrderPayment(Integer orderId) throws Exception ;
	
	public OrderPayment updateRefundedOrder(OrderPayment order) throws Exception;

}
