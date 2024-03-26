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
	
	/*
	 * Property will be used at on-render evaluation
	 */
	public void assignMvcCart(Cart cart, RequestContext ctx, MyFlowAttributes myAttrs) {
		
		this.checkMvcCart(cart, ctx);
		
		myAttrs.setFlowCartItems(cart);
	}

	public void throwEmptyCart(Cart cart, RequestContext ctx, MyFlowAttributes flowAttrs) 
			throws WebflowCartEmptyException {
		
		System.out.println("WebflowDebug#throwEmptyCart: executing");				
		
		this.checkMvcCart(cart, ctx);

		if (cart.getCartList().size() == 0) {

			WebflowCartEmptyException ex = new WebflowCartEmptyException();

			MutableAttributeMap<Object> map = ctx.getFlashScope();

			map.put(EXCEPTION_KEY, ex);

			throw ex;

		} // end if

	} // end eval
	
    private void checkMvcCart(Cart cart, RequestContext ctx) {
		
		if(cart == null)
			throwApplicationException("throwEmptyCart", "Cart passed to procedure is null.");
		
		Cart mvcCart = (Cart) ctx.getExternalContext().getSessionMap().get("cart");

		if (!cart.equals(mvcCart)) {

			throwApplicationException("throwEmptyCart", "Webflow Cart does not point to cart in Http session");
		}		

		List<CartItem> items = cart.getCartList();	
		
		if(items == null) {
			throwApplicationException("throwEmptyCart", "Cart#getCartList returned a null");
		}
	}
	
	public void evalCartOnRender(RequestContext ctx, Cart cart, 
			MyFlowAttributes flowAttrs) throws OnRenderCartEmptyException{
		
		List<CartItem> items = flowAttrs.getFlowCartItems();
		
		if(items == null)
			
			throwApplicationException("evalCartOnRender",
					"MyFlowAttributes#flowCartItems property is not set or set with null. ");
		
		else if(items.isEmpty())
			
			throwApplicationException("evalCartOnRender",
					"checkout-flow entered with an empty cart");
		try {
			
		   this.throwEmptyCart(cart, ctx, flowAttrs); 
		   
		} catch (WebflowCartEmptyException e) {
			throw new OnRenderCartEmptyException();
		}
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
