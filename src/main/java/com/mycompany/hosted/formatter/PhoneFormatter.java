package com.mycompany.hosted.formatter;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

public class PhoneFormatter implements Formatter<String> {
	
	String[] formats = new String[] {"##########", "(###) ###-####", "###.###.####", "###-###-####"};
	
	private String empty = new String();
	
	//private String space = Character.valueOf((char)32).toString();
	
	private String defaultMessage = "Phone entry cannot be matched to a known format";

	@Override
	public String print(String value, Locale locale) {
		
		if(value==null || value.contentEquals(empty))
			return empty;
		
		return MaskUtil.format(value, formats[1], formats);
	}

	@Override
	public String parse(String text, Locale locale) throws ParseException {
		
		String parsed = MaskUtil.validateByFormats(text,  formats);
		
		if(parsed.contentEquals(empty))
			throw new IllegalArgumentException(defaultMessage);
		
		System.out.println("PhoneFormatter#parse: " + parsed);
		
		return parsed;
	}

	

}
