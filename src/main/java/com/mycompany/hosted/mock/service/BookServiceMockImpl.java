package com.mycompany.hosted.mock.service;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;
import com.mycompany.hosted.exception_handler.EhrLogger;

@Component
public class BookServiceMockImpl implements BookService
{
	private static Logger logger ;
	private List<Book> testBooks = new ArrayList<Book>();

	public BookServiceMockImpl() 
	{
		logger = Logger.getLogger(this.getClass());
		testBooks.add(new Book("0385014805", "Arrow of God ", "Chinua Achebe", 10.99));
		testBooks.add(new Book("0791071715", "Things Fall Apart ", "Chinua Achebe", 10.99));
		testBooks.add(new Book("9780802131935", "Blood and Guts in High School ", "Kathy Acker", 10.99));
		testBooks.add(new Book("1400079586", "The Lambs of London ", "Peter Ackroyd", 10.99));
		testBooks.add(new Book("9780140171136", "Hawksmoor ", "Peter Ackroyd", 10.99));
		testBooks.add(new Book("9780140171174", "The House of Doctor Dee ", "Peter Ackroyd", 10.99));
		testBooks.add(new Book("9780517226957", "The Hitchhiker’s Guide to the Galaxy ", "Douglas Adams", 10.99));
		testBooks.add(new Book("9780671742515", "The Long Dark Teatime of the Soul ", "Douglas Adams", 10.99));
		testBooks.add(new Book("9780671746728", "Dirk Gently’s Holistic Detective Agency ", "Douglas Adams", 10.99));
		testBooks.add(new Book("9780192840509", "Aesop's Fables ", " Aesopus", 10.99));
		testBooks.add(new Book("9780810117099", "Novel With Cocaine ", "M. Ageyev", 10.99));
		testBooks.add(new Book("9781402714580", "Little Women ", "Louisa May Alcott", 10.99));
		testBooks.add(new Book("9781583220085", "The Man With the Golden Arm ", "Nelson Algren", 10.99));
		testBooks.add(new Book("9780143104841", "Fantômas ", "Marcel/Pierre Allain/Souvestre", 10.99));
		testBooks.add(new Book("9780553383805", "The House of the Spirits ", "Isabel Allende", 10.99));
		testBooks.add(new Book("9780299186449", "Tent of Miracles ", "Jorge Amado", 10.99));
		testBooks.add(new Book("9780375726743", "Cause for Alarm ", "Eric Ambler", 10.99));
		testBooks.add(new Book("9780099461050", "The Old Devils ", "Kingsley Amis", 10.99));
		testBooks.add(new Book("9780140088915", "Money: A Suicide Note ", "Martin Amis", 10.99));
		testBooks.add(new Book("9780141182599", "Lucky Jim ", "Kingsley Amis", 10.99));
		testBooks.add(new Book("9780517585160", "The Information ", "Martin Amis", 10.99));
		testBooks.add(new Book("9780679730347", "London Fields ", "Martin Amis", 10.99));
		testBooks.add(new Book("9780679734499", "Dead Babies ", "Martin Amis", 10.99));
		testBooks.add(new Book("9780679735724", "Time’s Arrow ", "Martin Amis", 10.99));
		testBooks.add(new Book("9780897332200", "The Green Man ", "Kingsley Amis", 10.99));
		testBooks.add(new Book("9780226020457", "The Bridge on the Drina ", "Ivo Andri?", 10.99));
		testBooks.add(new Book("9780553279375", "I Know Why the Caged Bird Sings ", "Maya Angelou", 10.99));
		testBooks.add(new Book("9780192750136", "The Thousand and One Nights ", " Anonymous", 10.99));
		testBooks.add(new Book("9780140435900", "The Golden Ass ", "Lucius Apuleius", 10.99));
		testBooks.add(new Book("9780195206487", "Memoirs of Martinus Scriblerus ", "J. Arbuthnot et al.", 10.99));
		testBooks.add(new Book("9781906174033 ", "The Green Hat ", "Michael Arlen", 10.99));
		testBooks.add(new Book("9780553293357", "Foundation ", "Isaac Asimov", 10.99));
		testBooks.add(new Book("9780553382563", "I, Robot ", "Isaac Asimov", 10.99));
		testBooks.add(new Book("0375430857", "The Blind Assassin ", "Margaret Atwood", 10.99));
		testBooks.add(new Book("9780385490443", "Alias Grace ", "Margaret Atwood", 10.99));
	}

	/*public List<Book> getBookByIsbn(String isbn) 
	{
		List<Book> returnList = new ArrayList<Book>();
		for (Book next : testBooks)
		{
			if (next.getIsbn().equals(isbn))
			{
				returnList.add(next);
			}
		}
		return returnList;	
	}*/
	
	public Book getBookByIsbn(String isbn) throws BookNotFoundException
	{
		Book found = null;
		
		for (Book next : testBooks)
		{
			if (next.getIsbn().equals(isbn))
			{
				found = next;
			}
		}
		
		if(found == null) {
			
			String msg = EhrLogger.doMessage(this.getClass(), "getBookByIsbn", 
					"Isbn " + isbn + " not found.");
			
			throw new BookNotFoundException(msg);
		}	
		
		return found;
	}

	public List<Book> getEntireCatalogue() 
	{
		
		return testBooks;
	}
	
	public Integer getBookCount() {
		return testBooks.size();
	}

	public void registerNewBook(Book newBook) 
	{
		testBooks.add(0,newBook);
	}

	public List<Book> getAllBooksByAuthor(String author) 
	{
		List<Book> returnList = new ArrayList<Book>();
		for (Book next : testBooks)
		{
			// OK viewers - no complaints about this method please
			// it's just a hack implementation to simulate a proper
			// search!!
			Collator collator = Collator.getInstance();
			collator.setStrength(Collator.PRIMARY);
			
			int comparison = collator.compare(next.getAuthor().toLowerCase(), author.toLowerCase());
			if (comparison == 0)
			{			
				returnList.add(next);
			}
		}
		return returnList;
	}

	public void deleteBookFromStock(Book bookToRemove) 
	{
		
		testBooks.remove(bookToRemove);
	}

	public Book getBookById(int id)
	{
		for (Book next : testBooks)
		{
			if (next.getId() == id)
			{
				return next;
			}
		}
		return null;
	}

	/**
	 * This method is currently unimplemented - an interesting exercise to do!
	 */
	public List<Book> getAllRecommendedBooks(String userId) {
		// TODO Auto-generated method stub
		throw new java.lang.UnsupportedOperationException();
	}


	public List<Book> searchBooksByLooseMatch(String chars) 
	{
		logger.info("BookService#looking for " + chars);
		List<Book> returnList = new ArrayList<Book>();
		String uchars = "";
		for (Book next : testBooks)
		{
			uchars = next.getTitle().toUpperCase();
			if (uchars.contains(chars.toUpperCase()))
			{
				returnList.add(next);
			}
		}
		Collections.sort(returnList, new Comparator<Book>() {

			@Override
			public int compare(Book b1, Book b2) {
				return b1.getTitle().compareTo(b2.getTitle());
			}			
		});
		logger.info("returnList size=" + returnList.size());
		return returnList;
	}

}
