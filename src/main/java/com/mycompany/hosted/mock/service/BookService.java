package com.mycompany.hosted.mock.service;

import java.util.List;




public interface BookService 
{
	   public List<Book> getAllBooksByAuthor(String author);
	   public List<Book> getAllRecommendedBooks(String userId);
	   public Book getBookByIsbn(String isbn) throws BookNotFoundException;
	   public List<Book> getEntireCatalogue();
	   public void registerNewBook(Book newBook);
	   public Book getBookById(int id);
	   public List<Book> searchBooksByLooseMatch(String chars);
	   public Integer getBookCount();
	   
}
