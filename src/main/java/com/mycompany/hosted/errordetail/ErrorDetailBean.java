package com.mycompany.hosted.errordetail;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
//import java.util.Optional;
import java.util.Map;

import com.mycompany.hosted.errordetail.ErrorDetail.ErrorDetailReason;
import com.mycompany.hosted.exception_handler.EhrLogger;
//import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.model.order.OrderPayment;


public class ErrorDetailBean {	
	
	private Map<Integer, List<ErrorDetail>> errMap= new LinkedHashMap<>();	
	
	public Map<Integer, List<ErrorDetail>> getErrMap() {
		return errMap;
	}
	/*
	 * To do: Initialize Capture, Resource Ids from the order
	 */
	public void addDetailToList(OrderPayment order, Integer localOrderId,
			Exception ex, String errMethod, ErrorDetailReason reason) {	
		
		ErrorDetail detail = initErrorDetail(order, localOrderId, errMethod, reason);
	
        detail.setException(ex);
		
		detail.setExceptionClass(ex.getClass());		
				
		detail.setErrMessage(ex.getMessage());      
		
		if(errMap.containsKey(localOrderId))
			errMap.get(localOrderId).add(detail);
		else {
			List<ErrorDetail> list = new ArrayList<>();
			list.add(detail);
			errMap.put(localOrderId, list);
		}			
	}
	
	/*
	 * See REFUNDED_ONPERSIST_ERR
	 */
	public void addDetailToList( 
			OrderPayment order, Integer localOrderId,
			String errMessage, String errMethod, ErrorDetailReason reason) {
		
		ErrorDetail detail = initErrorDetail(order,localOrderId,errMethod, reason);
		
		detail.setErrMessage(errMessage);      
		
		if(errMap.containsKey(localOrderId))
			errMap.get(localOrderId).add(detail);
		else {
			List<ErrorDetail> list = new ArrayList<>();
			list.add(detail);
			errMap.put(localOrderId, list);
		}		
	}
	
	private ErrorDetail initErrorDetail(
			OrderPayment order, Integer localOrderId,
			String errMethod, ErrorDetailReason reason) {    
		
		ErrorDetail detail = new ErrorDetail();
		
		detail.setErrorDetailReason(reason);
		
		detail.setLocalOrderId(localOrderId);	
		
		detail.setErrMethod(errMethod);
			
		detail.setOrder(order);
			
		if(order != null)
				detail.setSvcTransactionId(order.getCaptureId());		
			
		detail.setErrTime(this.formatZoned());
		
		return detail;
		
	}
	
	String formatZoned() {
        ZonedDateTime zdt = ZonedDateTime.now();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy h:mm:ss a (z)");	
		
		return formatter.format(zdt);
	}
/*
 * To do: Possibly by caller. If REFUND_ID_MISSING, NOT_RETRIEVABLE succeeds.
 */
	
   public ErrorDetail findMostRecentDetail(Integer orderId) {	  	 	 
	   
	   List<ErrorDetail> list = this.errMap.get(orderId);
	   
	   if(list == null) return null;
	   
	   ErrorDetail detail = list.get(list.size() - 1); //Most recent
	   				
	   switch (detail.getErrorDetailReason()) {
				
				case NOT_RETRIEVABLE_FOR_REFUND:
					detail = null;
				
				case PERSIST_ORDER_ERR:				
					
				case REFUND_UPDATE_ERR:	
									
				case REFUNDED_ONPERSIST_ERR:
					
				case REFUND_ID_MISSING:	
		      
			} //end switch		
		
		return detail;
	}
   
   public Map<Integer, List<ErrorDetail>> findDetailList(Integer...orderId) {
	   
	   if(orderId == null)
		   EhrLogger.throwIllegalArg(this.getClass(), "findDetailList",
				   "Param 'orderId' cannot be null");
	   
	   List<Integer> toFind = Arrays.asList(orderId);
	   
	   Map<Integer, List<ErrorDetail>> foundMap = new HashMap<>();
	   
	  for (Integer id : toFind) {
		  
		  List<ErrorDetail> foundList = this.errMap.get(id);
		  
		  if(foundList == null)
			  EhrLogger.throwIllegalArg(this.getClass(), "findDetailList",
					   "List<ErrorDetail> cannot be found by orderId " + id);
		  
		  foundMap.put(id,foundList);
		  
	  }
	  return foundMap; 
   }//end find
   
} //end class
