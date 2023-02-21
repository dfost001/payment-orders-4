package com.mycompany.hosted.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="ship_address", schema="springmvcsample")
public class ShipAddress extends PostalAddress implements Serializable {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)	   
	@Column(unique=true, nullable=false)
	private Integer id;	
		
	@JoinColumn (name="customer_id", nullable=false)
	@ManyToOne (fetch = FetchType.EAGER)
	private Customer customerId ;	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Customer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Customer customerId) {
		this.customerId = customerId;
	}
	
	

}
