package com.mycompany.hosted.formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.format.Formatter;

public class DateFormatter implements Formatter <Date> {
	
	private static final String DATE_FORMAT_OUT = "EEE, d MMM, yyyy";
	
	private String[] formats = {"MM/dd/yyyy", DATE_FORMAT_OUT, "MM-dd-yyyy"};

	@Override
	public String print(Date object, Locale locale) {
		
		if(object == null)
			return null;
		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_OUT, locale);
		
		String formatted = sdf.format(object);
		
		return formatted;
		
		
	}

	@Override
	public Date parse(String text, Locale locale) throws ParseException {
		
		
		Date dt = null ;
		
		for(String fmt : formats) {
		
		    dt = getDate(fmt, text, locale);
		    
		    if(dt == null)
		    	dt = getDate(fmt, text, Locale.getDefault());
		    
		    if(dt != null)
		    	break;
		
		} 
		
		if(dt == null)
		    throw new IllegalArgumentException("Cannot parse input String: " + text);
		
		return dt;
	}
	
	private Date getDate(String fmt, String source, Locale locale) {
		
		SimpleDateFormat sdf = new SimpleDateFormat(fmt, locale) ;
		
		sdf.setLenient(false);
		
		Date dt = null;
		
		try {
			 
			dt = sdf.parse(source);
			
		} catch(Exception ex) {
			
			return null;
		}
		
		return dt;
	}

}
