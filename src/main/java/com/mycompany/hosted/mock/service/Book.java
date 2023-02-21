package com.mycompany.hosted.mock.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;

//import javax.validation.constraints.DecimalMax;
//import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;

//import org.hibernate.validator.constraints.*;
import org.springframework.format.annotation.NumberFormat;

//import com.mycompany.springmvc.customconstraint.IsbnValid;
//import com.mycompany.springmvc.formatter.TextFormat;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Book implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	private static int nextId = 1;
	
	@XmlTransient
	private int id;	
	
	//@ISBN
	@NotEmpty
	@XmlElement(required=true)
	private String isbn;	
	
	
	//@TextFormat(value=TextFormat.Format.PROPER)
	@XmlElement(required=true)
	@NotEmpty
	private String title;	
	
	
	//@TextFormat(value=TextFormat.Format.PROPER)
	@NotEmpty
	@XmlElement(required=true)
	private String author;
	
	@NumberFormat(style=NumberFormat.Style.CURRENCY)
	//@DecimalMin(value="5.00")
	//@DecimalMax(value="300.00")
	//@NotNull
	@XmlElement(required=true)
	@XmlSchemaType(name="decimal")
	private BigDecimal price;
	
	@NotNull
	@XmlElement(required=true)
	@XmlSchemaType(name="date")
	private Date lastUpdate;
	
	@NotNull
	@XmlElement(required=true)
	@XmlSchemaType(name="date")
	private Date datePublished;

	public Book(String isbn, String title, String author, double price) 
	{
		this.id = nextId++;
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.price = new BigDecimal(price, new MathContext(7,RoundingMode.HALF_UP));
		this.price.setScale(2);
		this.lastUpdate = new Date();
		this.datePublished = null;
	}

	public Book() {
		this.id = nextId++;
		this.lastUpdate = new Date();
		
	}

	public String toString()
	{
		return this.title + " by " + this.author;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}	

	public int getId() {
		return id;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Date getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}	
	
}
