package com.mycompany.hosted.model.validation;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name="states", schema="validation_support")
public class States implements Serializable{
	
	 private static final long serialVersionUID = 1L;
	 
	    @Id
	    @Basic(optional = false)
	    @NotEmpty
	    @Column(name = "StCode")
	    private String stCode;
	    
	   
	    @Column(name = "StName")
	    private String stName;
	    
	    @Column(name = "FirstZip")
	    private String firstZip;
	   
	    @Column(name = "LastZip")
	    private String lastZip;

	    public States() {
	    }

	    public States(String stCode) {
	        this.stCode = stCode;
	    }

	    public String getStCode() {
	        return stCode;
	    }

	    public void setStCode(String stCode) {
	        this.stCode = stCode;
	    }

	    public String getStName() {
	        return stName;
	    }

	    public void setStName(String stName) {
	        this.stName = stName;
	    }

	    public String getFirstZip() {
	        return firstZip;
	    }

	    public void setFirstZip(String firstZip) {
	        this.firstZip = firstZip;
	    }

	    public String getLastZip() {
	        return lastZip;
	    }

	    public void setLastZip(String lastZip) {
	        this.lastZip = lastZip;
	    }

	    @Override
	    public int hashCode() {
	        int hash = 0;
	        hash += (stCode != null ? stCode.hashCode() : 0);
	        return hash;
	    }

	    @Override
	    public boolean equals(Object object) {
	        // TODO: Warning - this method won't work in the case the id fields are not set
	        if (!(object instanceof States)) {
	            return false;
	        }
	        States other = (States) object;
	        if ((this.stCode == null && other.stCode != null) 
	        		|| (this.stCode != null && !this.stCode.equals(other.stCode))) {
	            return false;
	        }
	        return true;
	    }

	    @Override
	    public String toString() {
	        return "model.customer.States[ stCode=" + stCode + " ]";
	    }
	    

}
