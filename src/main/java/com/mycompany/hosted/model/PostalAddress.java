package com.mycompany.hosted.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import com.mycompany.hosted.formatter.TextFormat;
import com.mycompany.hosted.formatter.TextFormat.Format;

@SuppressWarnings("serial")
@MappedSuperclass
public class PostalAddress implements Serializable{	

	@TextFormat(value= {TextFormat.Format.ADDRESS_LINE, TextFormat.Format.PROPER})
	@NotEmpty
	@Size(min=2, max=45)
	@Column(nullable=false, length=45)
	private String address;

	@TextFormat(value= {TextFormat.Format.POSTAL_NAME, TextFormat.Format.PROPER})
	@NotEmpty
	@Size(min=2, max=25)
	@Column(nullable=false, length=25)
	private String city;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date dtCreated;

	@TextFormat(value= {TextFormat.Format.EMAIL, TextFormat.Format.LOWER})
	@Email
	@NotEmpty
	@Column(nullable=false, length=45)
	private String email;

	@TextFormat(value= {TextFormat.Format.PROPER_NAME,TextFormat.Format.UPPER})
	@NotEmpty
	@Size(min=2, max=45)
	@Column(nullable=false, length=45)
	private String firstName;

	@TextFormat(value= {TextFormat.Format.PROPER_NAME,TextFormat.Format.UPPER})
	//@NotEmpty
	//@Size(min=2, max=45)
	@Column(nullable=true, length=45)
	private String lastName;

	
	@NotEmpty 
	@TextFormat(value= {Format.PHONE})
	@Column(nullable=false, length=45)
	private String phone;

	@TextFormat(value= {TextFormat.Format.POSTAL_CODE, TextFormat.Format.UPPER})
	@NotEmpty
	@Size(min=5, max=10)
	@Column(nullable=false, length=10)
	private String postalCode;

	@TextFormat(value= {Format.PROPER_NAME, Format.UPPER})
	@NotEmpty
	@Size(min=2, max=2)
	@Column(nullable=false, length=2)
	private String state;	

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getDtCreated() {
		return dtCreated;
	}

	public void setDtCreated(Date dtCreated) {
		this.dtCreated = dtCreated;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	

} //end class
