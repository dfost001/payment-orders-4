package com.mycompany.hosted.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name="customer", schema="springmvcsample")
public class Customer extends PostalAddress implements Serializable{
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)	   
	@Column(unique=true, nullable=false)
	private Integer id;
	
	 @OneToMany(mappedBy="customerId",cascade=CascadeType.ALL,
	            fetch=FetchType.EAGER, targetEntity=ShipAddress.class)
	 
	 private List<ShipAddress> shipAddressList ;
	 
	 

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<ShipAddress> getShipAddressList() {
		
		 if(shipAddressList == null)
	            shipAddressList = new ArrayList<>();
		 
	        return shipAddressList;
	}

	public void setShipAddressList(List<ShipAddress> shipAddressList) {
		this.shipAddressList = shipAddressList;
	}
	 
	 
	public void addShipAddress(ShipAddress shipAddr) {
		
		shipAddr.setCustomerId(this);
		
		this.getShipAddressList().add(shipAddr);
	}
	
	public void editShipAddress(ShipAddress shipAddr) {
		
		int i = 0;
		
		for(; i < getShipAddressList().size(); i++)
			if(getShipAddressList().get(i).getId().equals(shipAddr.getId()))
				break;
		
		getShipAddressList().set(i, shipAddr);
			
		
	}

}
