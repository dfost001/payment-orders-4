/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hosted.formatter;



import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

import com.mycompany.hosted.formatter.TextFormat.Format;




/**
 *
 * @author Dinah
 */
public class TextFormatter implements Formatter<String> {
    
    private final Format[] formats;
    
    public TextFormatter(Format[] formats){
        this.formats = formats;
    }

    @Override
    public String print(String fld, Locale locale) {
        
        return fld;
    }

    @Override
    public String parse(String value, Locale locale) throws ParseException {       
        
        if(value == null)
            return "";
        
        FormatterUtil util = new FormatterUtil(formats);
        
        String edited = util.format(value);
        
        return edited;
        
    }
    
}
