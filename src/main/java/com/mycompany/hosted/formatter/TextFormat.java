package com.mycompany.hosted.formatter;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/*
 * Default: Proper-cased. '#', '/', '-', '&', SPC included if sequence valid
 */

@Target({ElementType.FIELD,
    ElementType.METHOD,
    ElementType.TYPE,
    ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface TextFormat {
	
	Format[] value();
	
	public static enum Format {
		DEFAULT,
		UPPER,
		PROPER,
		LOWER,
		NO_CASE,
		DIGITS,
		EMAIL,
		PHONE,
		CREDIT_CARD,
		PROPER_NAME,
        POSTAL_NAME,
        ADDRESS_LINE,
        POSTAL_CODE,
        NO_FORMAT,      
        	
	} ;

}
