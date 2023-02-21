package com.mycompany.hosted.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.annotation.RequestScope;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mycompany.hosted.mock.service.BookService;

import com.mycompany.hosted.cart.CartUpdateUtility;
import com.mycompany.hosted.mock.service.Book;

@RequestScope
@Controller
@SessionAttributes({"cart"})
public class CatalogueCartController {
	
	@Autowired
	private BookService bookService;	
	
	@Autowired
	private CartUpdateUtility util;
	
	private List<String> successMessages = new ArrayList<> ();
	private List<String> failMessages = new ArrayList<>();
	
	
	
	@PostMapping(value="/catalogueCart/update")	
	public String updateCart(@RequestParam(value="bookChecked", required=false) 
			ArrayList<Integer> selected, HttpServletRequest request, RedirectAttributes attrs) 
					throws Exception {
		
		String selectionRequired = "Please make a selection or enter a quantity. " +
		"Enter 0 to remove from cart.";
		
		
		if(selected==null)
			attrs.addFlashAttribute("selectionRequired", selectionRequired);		
		
		else {		
			
			
			for(Integer i : selected) {
				
				Book book = bookService.getBookById(i);
				
				if(book == null)
					
				 this.throwIllegalArg("updateCart", "BookService returned null for Id #" + i);
				
				else {
					
				   String qty = request.getParameter(i.toString())	;
				
				   util.processQuantity(qty, book, failMessages, successMessages);
				}				
			} // end for
			
		}
		
		attrs.addFlashAttribute("successMessages", successMessages);
		
		attrs.addFlashAttribute("failMessages", failMessages);
		
		return "redirect:/spring/catalogue/view" ;
		
	}
	
   private void throwIllegalArg(String method, String msg) {
		
		String err = this.getClass().getCanonicalName() + "#" + method + ": " + msg;
		
		throw new IllegalArgumentException(err);
		
	}

}
