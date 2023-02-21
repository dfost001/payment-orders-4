package com.mycompany.hosted.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.mycompany.hosted.mock.service.Book;

@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class CartUpdateUtility {
	
	@Autowired
	private Cart cart;
	
	
	 public void deleteItem(Book book, List<String> successMessages) {
		 
		 cart.delete(book);
		 
		 this .doSuccessMessage(book, 0, successMessages);
		 
		 
	 }
	
	 public void processQuantity(String sqty, Book book,
	            List<String> failMessages, List<String>successMessages) {
	        
	         Integer qty = this.convertQuantity(sqty, book, failMessages);
	        
	            if(qty > 0) {
	                
	                cart.update(book, qty);  
	                
	                this.doSuccessMessage(book, qty, successMessages);
	                
	                //evalWarningMessage(book, qty, successMessages);
	                
	            } 
	            else if(qty == 0) {
	                
	               cart.delete(book);
	               
	               this.doSuccessMessage(book, qty, successMessages);
	                       
	                
	            }                   
	        
	    }
	     
	    private Integer convertQuantity(String value, Book book, List<String> errorMessages) {
	        
	         if(value == null || value.isEmpty()) {
	            this.doFailureMessage(book, errorMessages, "Did you forget to enter a quantity in the text box?");
	            return -1; 
	        }
	        
	        Integer qty = 0;
	        
	        try {
	            qty = new Integer(value);
	        }
	        catch(NumberFormatException ex){
	            this.doFailureMessage(book, errorMessages, 
	                    "Unable to convert  '" + value + "' to a number.");
	            return -1;
	        }
	        
	        if(qty < 0)
	            qty = Math.abs(qty);
	        
	        return qty;
	        
	    }  
	    
	    private void doSuccessMessage(Book book, Integer qty, List<String> successMessages) {
	    	
	          successMessages.add("Id #" + book.getId() + " '" + book.getTitle()
	                        + "' successfully updated to quantity "
	                        + qty
	                        + ".");
	     }
	    
	    
	    private void doFailureMessage(Book book, List<String> errorMessages, String msg) {
	         
	          errorMessages.add("Id #" + book.getId() + " '" +
	                    book.getTitle() + "': " + msg);
	     }

}
