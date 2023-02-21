package com.mycompany.hosted.model.order;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the order_ship_to database table.
 * 
 */
@Entity
@Table(name="order_ship_to", schema="springmvcsample")
@NamedQuery(name="OrderShipTo.findAll", query="SELECT o FROM OrderShipTo o")
public class OrderShipTo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue (strategy=GenerationType.IDENTITY)
	private int id;

	private String address;

	private String city;

	private String email;

	private String firstName;

	private String lastName;

	private String phone;

	private String postalCode;

	private String state;

	public OrderShipTo() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPostalCode() {
		return this.postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

}