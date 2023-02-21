package com.mycompany.hosted.mock.service;


public class BookNotFoundException extends Exception {
	private static final long serialVersionUID = 4631445786801082925L;
	
	public BookNotFoundException(String message) {
		
		super(message);
	}
}
