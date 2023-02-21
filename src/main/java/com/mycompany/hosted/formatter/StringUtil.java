/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hosted.formatter;

import com.mycompany.hosted.exception_handler.EhrLogger;


/**
 *
 * @author dinah
 * 
 * To do: Account for null arguments
 */
public class StringUtil {
    
    public static boolean compareRemoveSpacesIgnoreCase(String str1, String str2) {
        
        String cmp1 = str1.replaceAll("\\s", "").toLowerCase();
        String cmp2 = str2.replaceAll("\\s", "").toLowerCase();
        
        if(cmp1.contentEquals(cmp2))
            return true;
        
        return false;
        
    }
    
    public static boolean compareRemoveSpaces(String str1, String str2) {
        
        String cmp1 = str1.replaceAll("\\s", "");
        String cmp2 = str2.replaceAll("\\s", "");
        
        if(cmp1.contentEquals(cmp2))
            return true;
        
        return false;
        
    }
    
    public static boolean compareExact(String str1, String str2) {
        
        if(str1.contentEquals(str2))
            return true;
        return false;
    }
    
    public static boolean allDigits(String entry){
        
        if(entry == null || entry.trim().isEmpty())
            return false;      
       
        
        for(int i=0; i < entry.length(); i++)
            if(!Character.isDigit(entry.charAt(i)))
                   return false;
        
        return true;
    }
    
    public static String getValueOrEmpty(String value) {
        
        if(value == null || value.trim().isEmpty())
            return new String();
        return value;
    }
    
    public static String rTrimNonDigit(String value) {
    	
    	if(value == null || value.trim().isEmpty())
            return new String();
    	
    	for(int i = value.length() - 1; i >= 0; i--) {
    		
    		if(Character.isDigit(value.charAt(i)))   				
    	
    	         return value.substring(0, i + 1);
    	}
    	
    	return new String();
    }
    
    public static String lTrimNonDigit(String value) {
    	
    	if(value == null || value.trim().isEmpty())
            return new String();
    	
        for(int i = 0; i < value.length(); i++) {
    		
    		if(Character.isDigit(value.charAt(i)))   				
    	
    	         return value.substring(i);
    	}
    	
    	return new String();
    	
    }
    
public static boolean isNullOrEmpty(String... values) {
		
		if(values == null)
			EhrLogger.throwIllegalArg(StringUtil.class, "isNullOrempty",
					"VarArg String[] is empty");
		
		for (int i = 0; i < values.length; i++)
			if(values[i] == null || values[i].trim().isEmpty())
			     return true;
			
	    return false;		
		
	}
    
} //end utility
