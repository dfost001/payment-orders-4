package com.mycompany.hosted.checkoutFlow;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Path.Node;
import javax.validation.groups.Default;
import javax.validation.metadata.ConstraintDescriptor;

import org.springframework.binding.message.DefaultMessageResolver;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.DefaultMessageContext;
import org.springframework.binding.message.Severity;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;
import org.springframework.webflow.validation.WebFlowMessageCodesResolver;

import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.model.PostalAddress;
import com.mycompany.hosted.utility.CloneUtil;

@Component
public class ValidationUtil  implements MessageSourceAware{   
	
	private javax.validation.Validator validator;
	
	private MessageContext defaultMessageCtx;
	
	private MessageSource messageSource;
	
	@Autowired
	private AddressValidator addressValidator;
	
	
	public ValidationUtil() {
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		
		validator = factory.getValidator();		
		
	}	
	
	public void setMessageSource(MessageSource src) {
		
		messageSource = src;
	}
	/*
	 * Note: It is necessary to cast to Super. Fields are not declared on derived.
	 */
	public MessageContext validate(Object obj) {
		
		PostalAddress postal = null;
		
		defaultMessageCtx = new DefaultMessageContext(messageSource);
		
       if(PostalAddress.class.isAssignableFrom(obj.getClass())) {
			
			postal = (PostalAddress) obj;
			
			this.validateConstraints(defaultMessageCtx, postal);
			
		}
       
       addressValidator.validateAddressFormat(postal, defaultMessageCtx);
       
       System.out.println("ValidationUtil#validate: Printing generated DefaultMessageContext: ");
       
       debugPrintContext(defaultMessageCtx);
       
       return defaultMessageCtx;
	}
	
    private void validateConstraints(MessageContext mctx, PostalAddress address) {
		
        Set<ConstraintViolation<Object>> set = validator.validate(address, Default.class);
        
        System.out.println("ValidationUtil#validateAddress: Set.isEmpty=" + set.isEmpty());
        
        this.debugPrintAddress(address);
		
		if(!set.isEmpty())	{
			
		    // addMessages(set, mctx, address.getClass());
		     
		     addMessages(set, mctx, PostalAddress.class);		    
		}
		
	}
	
	private void addMessages(Set<ConstraintViolation<Object>> set,
			MessageContext context, Class<?> validatedEntity) {
		
		for(ConstraintViolation<Object> v : set) {
			
			String field = this.getName(v.getPropertyPath());
			
			String annotation = this.annotationName(v);
			
			String defaultText = field + ": " + v.getMessage();
			
			Object[] args = annotationArgs(v, annotation, field);
			
			MessageResolver messageResolver = this.initMessageResolver(field, validatedEntity, 
					annotation, args, defaultText);
			
			context.addMessage(messageResolver);			
			
		} //end for
	}    
	
	 private String getName(Path path) {
		 
			String name = "";
			
			Iterator<Node> node = path.iterator();
			
			while(node.hasNext())
				name +=  node.next().getName() + ".";      	                
	                
	       	return name.substring(0, name.length() - 1);
		} 
	 
	 /*
		 * see java.lang.annotation: Class<? extends Annotation> annotationType() 
		 */
	    private String annotationName(ConstraintViolation<?> cv)	{
	    	 
	    	 ConstraintDescriptor<?> desc = cv.getConstraintDescriptor();
	    	 
	    	 String name = desc.getAnnotation().annotationType().getSimpleName();
	    	 
	    	 //System.out.println("ValidationUtil#annotationName:" + name);
	    	 
	    	 return name;
	    }
	    
	    /*
	     * Returns an Object array containing the values of template variables in
	     * attribute-name alphabetical order
	     */
	    private Object[] annotationArgs(ConstraintViolation<?> cv, String annotation, String fld) { 	
	    	 
	    	
	    	 ConstraintDescriptor<?> desc = cv.getConstraintDescriptor();
	    	 
	    	 Map<String, Object> attrs = desc.getAttributes();
	    	 
	    	 switch(annotation) {
	    	 
	    	 case "Size" :
	    		 
	    		 Object min = attrs.get("min");
	        	 
	        	 Object max = attrs.get("max");
	        	 
	        	 return new Object[] {fld, max, min};
	        	 
	    	 case "Digits":
	    		 
	    		 Object integerPart = attrs.get("integer");
	    		 
	    		 Object fractionPart = attrs.get("fraction");
	    		 
	    		 return new Object[] {fld, fractionPart, integerPart} ;
	    	 
	    	 }
	    	 
	    	 return new Object[] {fld};
	    	
	    }
	    
		 private MessageResolver initMessageResolver
	       (String field, Class<?> validatedClz, String constraintName, 
	    		   Object[] args, String defaultText)  {
		 
		 Class<?> returnType = null;
		 
		 System.out.println("ValidationUtil#initMessageResolver class="
				 + validatedClz.getSimpleName() + "field=" + field);
		 
		 try {
			 
			 returnType = CloneUtil.getReturnType(field, validatedClz);			 
				
			    
		 } catch (NoSuchMethodException e) {
				 throw new IllegalArgumentException(
						 EhrLogger.doMessage(this.getClass(), "initMessageResolver", 
								 "Error getting field type"), e);
		   }
			 
		/* System.out.println("ValidationUtil#initMessageResolver: " +
		     "field= " + field + " returnType=" + returnType);*/
		
		 
		 String validatedName = validatedClz.getSimpleName().substring(0,1).toLowerCase() +
				 validatedClz.getSimpleName().substring(1);
		 
		 WebFlowMessageCodesResolver codesResolver = new WebFlowMessageCodesResolver ();
		 
		 String[] codes = codesResolver.resolveMessageCodes(
				 constraintName, validatedName, field, returnType);		 
		 
		 
		 DefaultMessageResolver messageResolver = new DefaultMessageResolver(
				 field, codes, Severity.ERROR, args, defaultText);
		 
		 return messageResolver;		
		 
	 } 
		 
    private void debugPrintContext(MessageContext mctx)	{
    	
    	if(mctx == null) {
			 
			 System.out.println("MessageContext is null");
			 
			 return;
		 }
		 
		 System.out.println("length=" + mctx.getAllMessages().length);
		 
		 for(Message m : mctx.getAllMessages()) {
			 
			 System.out.println(m.getText());
			 
		 }
    	
    }
    
    private void debugPrintAddress(PostalAddress address) {
    	
    	String first = address.getFirstName();
    	String last = address.getLastName();
    	String email = address.getEmail();
    	
    	System.out.println("Util#debugPrintAddress: " 
    			+ first + " " + last + " " + email);
    }
	 

}
