package com.mycompany.hosted.checkoutFlow;

import java.util.EnumMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageContext;

import org.springframework.binding.validation.ValidationContext;
import org.springframework.stereotype.Component;

import com.mycompany.hosted.model.PostalAddress;
import com.mycompany.hosted.checkoutFlow.WebflowAddressValidator.PostalFieldKey;
import com.mycompany.hosted.checkoutFlow.WebflowAddressValidator;


@Component
public class AddressValidator {
	
	@Autowired
	WebflowAddressValidator flowValidator;
	
	public void validateAddressView(PostalAddress postalAddress, ValidationContext vcontext) {
		
		System.out.println("Validator executing");
		
        MessageContext mctx = vcontext.getMessageContext();
		
		//WebflowAddressValidator.throwEmptyIfNotAnnotated(mctx, PostalAddress.class, postalAddress);
		
		this.validateAddressFormat(postalAddress, mctx);
		
	}
	
    public void validateAddressFormat(PostalAddress postal, MessageContext mcontext ) {
		
		EnumMap<PostalFieldKey, String> fieldNameMap = 
				new EnumMap<PostalFieldKey,String> (PostalFieldKey.class);
		
		fieldNameMap.put(PostalFieldKey.STREET, "address");
		fieldNameMap.put(PostalFieldKey.STATE, "state");
		fieldNameMap.put(PostalFieldKey.ZIP, "postalCode");		
		
				
		
		flowValidator.validateAddressFormat(postal.getAddress(),
           postal.getPostalCode(),
           postal.getState(),
           mcontext,
           fieldNameMap);
		
	}

}
