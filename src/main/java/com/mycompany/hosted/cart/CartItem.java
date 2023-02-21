package com.mycompany.hosted.cart;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.io.Serializable;

import com.mycompany.hosted.mock.service.Book;

@SuppressWarnings("serial")
public class CartItem implements Serializable{
	
	private Book book;
	
	private Integer quantity;
	
	private BigDecimal extPrice;
	
	private BigDecimal price;
	
	public CartItem(Book book, Integer quantity) {
		
		this.book = book;
		
		this.quantity = quantity;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public BigDecimal getExtPrice() {
		
		BigDecimal price = book.getPrice();
		
		extPrice = price.multiply(new BigDecimal(quantity));
		
		return extPrice;
	}
	
	public BigDecimal getPrice() {
		
		price = this.getBook().getPrice();
		
		return price;
	}
	
	public String formattedPrice() {
		return getFormattedValue(this.getPrice());
	}
	
	public String formattedExtPrice() {
		
		return this.getFormattedValue(this.getExtPrice());
	}
	
	private String getFormattedValue(BigDecimal value) {
		
		 DecimalFormat dfmt = new DecimalFormat(Cart.formatPattern);
		 
	     String formatted = dfmt.format(value);
	     
	     return formatted;
	}
	
	

}
