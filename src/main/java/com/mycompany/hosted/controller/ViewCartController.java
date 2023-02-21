package com.mycompany.hosted.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mycompany.hosted.cart.Cart;
import com.mycompany.hosted.cart.CartUpdateUtility;
import com.mycompany.hosted.mock.service.Book;
import com.mycompany.hosted.mock.service.BookService;

@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ViewCartController {
	
	private final String EDIT ="Submit";
	private final String DELETE = "Delete";
	
	@Autowired
	private CartUpdateUtility cartUtility;
	
	@Autowired
	private Cart cart;
	
	@Autowired
	private BookService svc;
	
	private final String emptyMsg = "Currently, there are no items in your cart.";
	
	private List<String> successMessages = new ArrayList<>();
	private List<String> failMessages = new ArrayList<>();
	
	
	
	public List<String> getFailMessages() {
		return failMessages;
	}

	public List<String>getSuccessMessages() {
		return successMessages;
	}
	

	@SuppressWarnings("unchecked")
	@GetMapping(value="/viewCart/request")
	public String viewCartRequest(ModelMap model) {
		
		System.out.println("ViewCartController executing");
		
		if(cart.getCount() <= 0)
			model.addAttribute("cartMsg", emptyMsg);
		
		model.addAttribute("cart", cart);
		
		failMessages = (List<String>)model.get("failMessages");
		
		successMessages = (List<String>) model.get("successMessages");
		
		model.addAttribute("viewCartController", this);
		
		return "jsp/viewCart";
	}
	

	
	@GetMapping(value="/viewCart/updateItem")
	public String updateItem(@RequestParam("id") Integer id, @RequestParam("quantity") String qty, 
			@RequestParam("cmdValue") String action,
			ModelMap model, RedirectAttributes attrs) {
		
        Book book = this.retrieveBook(id);
		
		if(book == null)
			this.throwIllegalArg("updateItem", "Book cannot be retrieved by rendered id " + id);
		
		if(action.contentEquals(EDIT))		
		       cartUtility.processQuantity(qty, book, failMessages, successMessages);
		else if(action.contentEquals(DELETE))
			   cartUtility.deleteItem(book, successMessages);
		
		attrs.addFlashAttribute("successMessages", successMessages);
		
		attrs.addFlashAttribute("failMessages", failMessages);				
		
		return "redirect:/spring/viewCart/request";
	}
	
	private Book retrieveBook(Integer id) {
		
		Book book = svc.getBookById(id);
		
		return book;
		
		
		
	}
	
	/*@SuppressWarnings("unchecked")
	private void initMessages(ModelMap model) {
		
		
		
		List<String> success = null;		
		
		if(model.containsAttribute("redirect")) {
			
			success = (List<String>) model.get("successMessages");
			
			if(success != null)
			
			successMessages.addAll(0, success);
				
		}
		else successMessages.clear();
			
	}*/
	
	private void throwIllegalArg(String method, String msg) {
		
		String err = this.getClass().getCanonicalName() + "#" + method + ": " + msg;
		
		throw new IllegalArgumentException(err);
		
	}

}
