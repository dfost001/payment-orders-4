package com.mycompany.hosted.checkoutFlow;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.RequestContext;

import com.mycompany.hosted.cart.CartItem;
import com.mycompany.hosted.cart.Cart;
import com.mycompany.hosted.checkoutFlow.exceptions.OnRenderCartEmptyException;
import com.mycompany.hosted.checkoutFlow.exceptions.WebflowCartEmptyException;

import com.mycompany.hosted.exception_handler.EhrLogger;

@Component
public class WebflowDebug {

	public static final String EXCEPTION_KEY = "exception";

	public void throwEmptyCart(Cart cart, RequestContext ctx) throws WebflowCartEmptyException {
		
		System.out.println("WebflowDebug#throwEmptyCart: executing");		

		throwNullValue(cart, "Cart");

		Cart mvcCart = (Cart) ctx.getExternalContext().getSessionMap().get("cart");

		if (!mvcCart.equals(cart)) {

			throwApplicationException("throwEmptyCart", "Webflow Cart does not point to cart in Http session");
		}

		List<CartItem> items = cart.getCartList();		

		if (items == null || items.size() == 0) {

			WebflowCartEmptyException ex = new WebflowCartEmptyException();

			MutableAttributeMap<Object> map = ctx.getFlashScope();

			map.put(EXCEPTION_KEY, ex);

			throw ex;

		} // end if

	} // end eval
	
	public void evalCartOnRender(RequestContext ctx, Cart cart, 
			MyFlowAttributes flowAttrs) throws OnRenderCartEmptyException{
		
		List<CartItem> items = flowAttrs.getFlowCartItems();
		
		if(items == null || items.isEmpty())
			
			throwApplicationException("evalCartOnRender",
					"checkout-flow entered with an empty cart");
		try {
			
		   this.throwEmptyCart(cart, ctx); //MVC Cart
		   
		} catch (WebflowCartEmptyException e) {
			throw new OnRenderCartEmptyException();
		}
	}
	
    private void throwNullValue(Object obj, String title) {    	
		
		
		if(obj == null)
			throw new IllegalArgumentException(
					
					EhrLogger.doMessage(this.getClass(), "throwNullValue",
							 title + "is null"));
		
	}
    
    private void throwApplicationException(String method, String message) {
    	
    	throw new IllegalArgumentException(
				
				EhrLogger.doMessage(this.getClass(), method,
						 message));
    }
    
    public void debugPrintOnRefresh(RequestContext request, String phase) {
    	
    	String state = request.getCurrentState().getId();
    	
    	String msg = "WebflowDebug#debugPrintOnRefresh: phase=" + phase
    			+ " state=" + state;
    	
    	System.out.println(msg);
    }
    
    public void debugPrint(RequestContext request, String phase) {
    	
    	//String phase = request.getCurrentEvent().getId(); //NullPointer
    	
    	String state = request.getCurrentState().getId();
    	
    	String format = MessageFormat.format("{0} phase={1} state={2}",
    			"WebflowDebug#debugPrint executing:", phase, state);
    	
    	System.out.println(format);
    	
    }


} //end class
