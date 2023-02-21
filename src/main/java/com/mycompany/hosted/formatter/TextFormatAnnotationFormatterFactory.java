package com.mycompany.hosted.formatter;



import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import com.mycompany.hosted.formatter.TextFormat.Format;

public class TextFormatAnnotationFormatterFactory 
     implements AnnotationFormatterFactory<TextFormat> {


	 @Override
	 public Set<Class<?>> getFieldTypes() {
	        return new HashSet<Class<?>>(asList(new Class<?>[] {
	            String.class }));
	    }


	@Override
	public Parser<String> getParser(TextFormat annotation, Class<?> arg1) {
		
		return configureFromAnnotation(annotation);
	}

	@Override
	public Printer<String> getPrinter(TextFormat annotation, Class<?> arg1) {
		
		return configureFromAnnotation(annotation);
	}
	
	private List<? extends Class<?>> asList(Class<?> [] arr){
		return Arrays.asList(arr);
	}
	
	private Formatter<String> configureFromAnnotation(TextFormat a){
		
		Format[] formatAttrs = a.value();	
		
		List<Format> list = Arrays.asList(formatAttrs);
		
		if(list.contains(Format.PHONE))
			return new PhoneFormatter();
		
		return new TextFormatter(formatAttrs);
	}

}
