package com.mycompany.hosted.controller;



import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mycompany.hosted.cart.Cart;
import com.mycompany.hosted.mock.service.Book;
import com.mycompany.hosted.mock.service.BookService;



@Controller
@Scope("request")
public class CatalogueController {
	
	@Autowired
	private Cart cart;
	
	@Autowired
	private BookService bookService;
	
	private List<Book> allBooks;	
	
	private List<String> successMessages;
	
	private List<String> failMessages;
	
	
	public List<Book> getAllBooks() {
		return allBooks;
	}
	

	public List<String> getSuccessMessages() {
		return successMessages;
	}

	public List<String> getFailMessages() {
		return failMessages;
	}	

	@RequestMapping(value="/catalogue/view", method=RequestMethod.GET)
	public String allBooks(ModelMap model) {
		
		allBooks = bookService.getEntireCatalogue();	
	
		
		addModelAttributes(model);	
		
		
		return "jsp/catalogue";
		
	}
	
	
	@SuppressWarnings("unchecked")
	/*
	 * CartController redirects to this Controller with generated messages 
	 * as Flash attributes transparently added to the ModelMap
	 */
	private void addModelAttributes(ModelMap map) {
		
		successMessages = (List<String>) map.get("successMessages");
		
		failMessages = (List<String>)map.get("failMessages");
		
		map.addAttribute("catalogueController", this) ;
		
		map.addAttribute("cart", cart);
	}
	
	/*private void debugPrint(HttpServletRequest request) {
		
		String localName = request.getLocalName();
		
		String localAddr = request.getLocalAddr();
		
		//String server = request.getServerName();
		
		String protocol = request.getProtocol();
		
		System.out.println("localName=" + localName
				+ " localAddr=" + localAddr
				+ " protocol=" + protocol);
		
	}*/
	
	@GetMapping(value="catalogue/printSession")
	public String printSession(HttpSession session, ModelMap map) {
		
		Enumeration<String> enumer = session.getAttributeNames();		
		
		
		while(enumer.hasMoreElements())
			System.out.println(enumer.nextElement());
			
		this.addModelAttributes(map);
		
		return "jsp/catalogue";
		
	}

}
