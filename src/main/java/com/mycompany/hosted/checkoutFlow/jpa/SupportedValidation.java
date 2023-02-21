package com.mycompany.hosted.checkoutFlow.jpa;

import java.util.List;

import com.mycompany.hosted.model.validation.States;

public interface SupportedValidation {
	
	List<States> getStates() ;
	
	States getStateByCode(String code);

}
