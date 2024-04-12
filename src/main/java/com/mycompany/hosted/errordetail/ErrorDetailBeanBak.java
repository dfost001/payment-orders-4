package com.mycompany.hosted.errordetail;

import java.util.ArrayList;

import java.util.GregorianCalendar;
import java.util.List;
//import java.util.Optional;

import com.mycompany.hosted.errordetail.ErrorDetail.ErrorDetailReason;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.model.order.OrderPayment;


public class ErrorDetailBeanBak {
	
	private List<ErrorDetail> errorDetailList = new ArrayList<>();	
	
	
	public List<ErrorDetail> getErrorDetailList() {
		return errorDetailList;
	}

	/*
	 * To do: Initialize Capture, Resource Ids from the order
	 */
	public ErrorDetail addDetailToList(OrderPayment order, Integer localOrderId,
			Exception ex, String errMethod, ErrorDetailReason reason) {
			
		
		ErrorDetail detail = new ErrorDetail();
		
		detail.setErrorDetailReason(reason);
		
		detail.setLocalOrderId(localOrderId);
		
		detail.setException(ex);
		
		detail.setExceptionClass(ex.getClass());		
				
		detail.setErrMessage(ex.getMessage());
		
		initDetail(detail, errMethod, order);
		
		errorDetailList.add(detail);
		
		return detail;
		
	}
	/*
	 * See REFUNDED_ONPERSIST_ERR
	 */
	public ErrorDetail addDetailToList( 
			OrderPayment order, Integer localOrderId,
			String errMsg, String errMethod, ErrorDetailReason reason) {
		
		ErrorDetail detail = new ErrorDetail();
		
		detail.setErrorDetailReason(reason);
		
		detail.setErrMessage(errMsg);
		
		detail.setLocalOrderId(localOrderId);
		
		this.initDetail(detail, errMethod, order);
		
		errorDetailList.add(detail);
		
		return detail;
		
	}
	
/*
 * To do: Possibly by caller. If REFUND_ID_MISSING, NOT_RETRIEVABLE succeeds.
 */
	
   public ErrorDetail findDetail(Integer orderId) {	  	   
	  
	  /* EhrLogger.consolePrint(this.getClass(), "findDetail", "param=" + orderId);*/
		
		for(int i=0; i< this.errorDetailList.size(); i++) {
			
		
			ErrorDetail tdetail = errorDetailList.get(i);			
						
			if(!tdetail.getLocalOrderId().equals(orderId)) continue;
				
				switch (tdetail.getErrorDetailReason()) {
				
				case NOT_RETRIEVABLE_FOR_REFUND:
					return null;
				
				case PERSIST_ORDER_ERR:
					
                    ErrorDetail redetail = null;						
					
					if( (redetail = refunded(orderId, i)) != null )
						return redetail;
					else return tdetail;	
				
					
				case REFUND_UPDATE_ERR:	
					return tdetail;	
					
				case REFUNDED_ONPERSIST_ERR:
					//Should not be reached	: handled by PERSIST_ORDER_ERR	
					EhrLogger.throwIllegalArg(this.getClass(), "findDetail", 
							"REFUNDED_ONPERSIST_ERR switch condition should not be reached");
				case REFUND_ID_MISSING:	
				      
			} //end switch
		} //end for
		
		EhrLogger.consolePrint(this.getClass(), "findDetail", "Not found for " + orderId);		
		
		return null;
	}
   
   private ErrorDetail refunded(int id, int current) {
	   
	   if(current + 1== errorDetailList.size())
		   return null;
	   
	   for(int i=current + 1; i< this.errorDetailList.size(); i++) {
		   
		   ErrorDetail err = errorDetailList.get(i);
		   
		   if(err.getOrder() == null)
			   continue;
		   
		   if(err.getOrder().getOrderId() == id && 
				   err.getErrorDetailReason().equals(ErrorDetailReason.REFUNDED_ONPERSIST_ERR))
			   
			   return err;		   
		   
	   }
	   
	   return null;
	   
   }
	
	private void initDetail(ErrorDetail detail, String method,
			OrderPayment order) {
		
        detail.setErrMethod(method);
		
		detail.setOrder(order);
		
		if(order != null)
			detail.setSvcTransactionId(order.getCaptureId());		
		
		detail.setErrTime(new GregorianCalendar().toString());
		
	}
	

}
