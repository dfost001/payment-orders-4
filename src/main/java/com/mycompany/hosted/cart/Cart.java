package com.mycompany.hosted.cart;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;


import com.mycompany.hosted.mock.service.Book;

@SuppressWarnings("serial")
@Scope(WebApplicationContext.SCOPE_SESSION)
@Component
public class Cart implements Serializable{
	
	    public static String formatPattern = "#,##0.00";
	
	    private static final int PRECISION = 8;
	    private static final int SCALE = 2;
	    private static final double TAX_PERCENT = .085;
	    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
	    private static final String SHIPPING_FEE = "5.00" ;
	    
	    private final MathContext context = new MathContext(PRECISION, ROUNDING_MODE);
	
	    private Map<Integer, CartItem> cartMap = new HashMap<>();	
	
	    private Integer count = 0; 
	    
	   // private BigDecimal subtotal;
	  //  private BigDecimal taxAmount;
	  //  private BigDecimal grandTotal;
	  //  private BigDecimal shippingFee;
	    
	
	
	public List<CartItem> getCartList() {
		
		List<CartItem> list = new ArrayList<>() ;
		
		list.addAll(cartMap.values());
		
		return list;
	}
	
	public Map<Integer,CartItem> getCartMap() {
		
		return cartMap;
	}
	
	public void update (Book book, Integer qty) {
		
		System.out.println("Cart#update: entering");
		
		if(cartMap.containsKey(book.getId())) {
			
			cartMap.get(book.getId()).setQuantity(qty);
		}
		else {
			cartMap.put(book.getId(), new CartItem(book, qty));
		}
	}
	
	public void delete(Book book) {
		 
		cartMap.remove(book.getId());
	}
	
	public void clearCart() {
		
		System.out.println("Cart#clearCart executing");
		
		cartMap.clear();
		
		System.out.println("Cart#clearCart: " + cartMap.size());
	}
	
	 public Integer getCount() {
	       this.count = 0;
	       for(CartItem it : cartMap.values())
	           count += it.getQuantity() ;
	       return count;
	   }
	
	public BigDecimal getSubtotal() {
		
		BigDecimal subtotal = new BigDecimal("0.00");
		
		for(CartItem item : cartMap.values())
			
			subtotal = subtotal.add(item.getExtPrice(), context);
		
		subtotal.setScale(SCALE, ROUNDING_MODE);
		
		return subtotal;
	}
	
	public BigDecimal getTaxAmount() {
		
		BigDecimal taxAmount = new BigDecimal(TAX_PERCENT).multiply(getSubtotal(), context);
		
		taxAmount.setScale(SCALE, ROUNDING_MODE);
		
		return taxAmount;		
		
	}
	
	public BigDecimal getShippingFee() {
		
		BigDecimal cmp = new BigDecimal("0");
		
		if(this.getSubtotal().compareTo(cmp)==0)
			return cmp;
		
		BigDecimal shippingFee = new BigDecimal(SHIPPING_FEE);
		
		return shippingFee;
	}
	
	public BigDecimal getGrandTotal() {
		
		BigDecimal grandTotal = getSubtotal().add(getTaxAmount()).add(getShippingFee());
		
		return grandTotal;
		
	}
	
	 public String getFormattedTax(){
	        
	        return this.format(getTaxAmount());
	    }
	   
	   public String getFormattedGrand(){
	       
	       return this.format(getGrandTotal());
	       
	   }
	   
	   public String getFormattedSubtotal() {
	       
	       return this.format(getSubtotal());
	   }
	   
	   public String getFormattedCount() {
	    	
	    	Integer ct = getCount();
	    	
	    	if(ct.equals(1))
	    		return ct.toString() + " item";
	    	else return ct.toString() + " items";
	    	
	    }
	   
	   public String getFormattedShipping() {
		   
		   return this.format(getShippingFee());
	   }
	   
	    private String format(BigDecimal value){
	        
	       
	        DecimalFormat dfmt = new DecimalFormat(formatPattern);
	        String formatted = dfmt.format(value);
	        return formatted;
	    }
	    
	  

}
