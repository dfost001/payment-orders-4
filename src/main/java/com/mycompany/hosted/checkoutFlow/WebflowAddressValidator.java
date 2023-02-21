package com.mycompany.hosted.checkoutFlow;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
//import org.springframework.stereotype.Component;
import org.springframework.stereotype.Component;

import com.mycompany.hosted.validation.AddressValidationUtil;
import com.mycompany.hosted.utility.BeanUtil;

@Component
public class WebflowAddressValidator {	
	
	public enum PostalFieldKey {STREET, STATE, ZIP} ;
	
	private final String ZIP_BUNDLE_CODE = "postalCode.invalid";
	
	private final String ADDR_BUNDLE_CODE = "addressLine.invalid";
	
	@Autowired
	private AddressValidationUtil validatorUtil;
	
	private MessageContext ctx;
	
	private EnumMap<PostalFieldKey, String> fieldNamePathMap;		


	public synchronized void validateAddressFormat(String addressLine, String postalCode, String stateCode,
			MessageContext ctx, EnumMap<PostalFieldKey, String> fieldNamePath) {				
		
		for(PostalFieldKey key : PostalFieldKey.values())
			
			if(!fieldNamePath.containsKey(key))
			  doError("validatePostalCodeFormat", "Field name for " + key + " is not in EnumMap");
		
		this.fieldNamePathMap = fieldNamePath;
		
		this.ctx = ctx;
		
		validatePostalCodeFormat(postalCode, stateCode);
		
		validateAddressLineFormat(addressLine);
		
	}
	
	private void validatePostalCodeFormat(String postalCode, String stateCode) {
		
		debugPrint("validatePostalCodeFormat", "entering");	
		
		
		if(this.isNullOrEmpty(postalCode, stateCode))
			return;
		
		String msg = validatorUtil.isValidPostalCode(stateCode,postalCode);	
		
		
		if(!msg.isEmpty()) {
			
			String fieldName = this.fieldNamePathMap.get(PostalFieldKey.ZIP);
		
			this.addMessage(fieldName, this.ZIP_BUNDLE_CODE,
					new String[] {fieldName, msg});
		}
		
		
		debugPrint("validatePostalCodeFormat", "exiting");
	}
	
	private void validateAddressLineFormat(String addressLine) {
		
		debugPrint("validateAddressLineFormat", "entering");
		
		if(this.isNullOrEmpty(addressLine))
			return;
		
		String msg = this.validatorUtil.validateAddressLineFormat(addressLine) ;
		
		if(!msg.isEmpty()) {
			
			String fieldName = this.fieldNamePathMap.get(PostalFieldKey.STREET);
			
			this.addMessage(fieldName, this.ADDR_BUNDLE_CODE,
					new String[] {fieldName, msg});
		}
		
		debugPrint("validateAddressLineFormat", "exiting");
	}
	
	
	/*
	 * To do: if country field is empty, throw an unconditional exception. Assigned, not entered.
	 */
	public static void throwEmptyIfNotAnnotated(MessageContext ctx, 
			Class<?> addressClass,
			Object address,
			String...excludeField ) {
		
		List<String> excludeList = new ArrayList<>();
		
		if(excludeField != null)
			excludeList = Arrays.asList(excludeField);
		
		Field[] flds = addressClass.getDeclaredFields();
		
		for(Field field : flds) {
			
			if(excludeList.contains(field.getName()))
				continue;
			
			if(BeanUtil.isEmptyOrNullField(addressClass, address, field)) {				
				
				
				if(!messageBySource(ctx, field.getName()))
					doError("throwEmptyIfNotAnnotated", addressClass.getCanonicalName() + "#" +
							field.getName() + " is null or empty and there is no constraint violation.");
				
			}		
		}
		
		debugPrint("throwEmptyIfNotAnnotated",
				"parameter=" + addressClass.getSimpleName() + ": returning");
	} 
	
	
	/*
	 * Note Message#Source will contain name path, fieldName is simple.
	 */
	private static boolean messageBySource(MessageContext ctx, String fieldName) {
		
		//debugPrint("messageBySource", "fieldName=" + fieldName);
		
		Message[] messages = ctx.getAllMessages() ;
		
		for(Message m : messages) {
			
			String src = (String)m.getSource();
			
			//debugPrint("messageBySource", "src=" + src);
			
			if(src.toLowerCase().contains(fieldName.toLowerCase()))
				return true;
		}
		
		return false;
		
	}
	
	private void addMessage(String source, String code, String[] templateArgs) {		
		
		
			MessageBuilder builder = new MessageBuilder();
			
			builder =  builder.error()
					   .code(code)				   
					   .source(source);
			
			for(String arg : templateArgs) {
				
				builder = builder.arg(arg);
			}
			
			ctx.addMessage(builder.build());
					  		
	}	
	
	private static void doError(String method, String message) {
		
		String err = WebflowAddressValidator.class.getCanonicalName() +
				"#" + method + ": " + message;
		
		throw new IllegalArgumentException(err);
	}
	
	private boolean isNullOrEmpty(String...values) {
		
		for(String v : values)
			if(v == null || v.trim().isEmpty())
				return true;
		return false;
	}
	
	private static void debugPrint(String method, String message) {
		
		String line = "WebflowAddressValidator" + "#" +
		    method + ": " + message;
		
		System.out.println(line);
	}

} //end class
