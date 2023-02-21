/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hosted.formatter;




/**
 *
 * @author Dinah
 */
public class AddressLineFormatUtil {
    
    
    private static final String[] ordinalSfx = {"st", "nd", "rd", "th"};
	
    private static final String[] compass = {"NW", "NE", "SW", "SE"};
	
    private static final char SPACE = (char)32;
	
	public static String format(String line){
		
	   if(line == null || line.isEmpty())	return line;
            
       String edited = formatBoxLine(line);  
                
       if(!edited.isEmpty())
            return edited;
		
		edited = removeOrdinalSpaceSuffix(line);
                
        edited = lowerCaseOrdinalSuffix(edited);
                
		edited = capitalizeCompass(edited);
                
		return edited;
	}
	
	private static boolean ordMatches(String tok) {
		for(String s : ordinalSfx)
			if(tok.toLowerCase().equals(s))
				return true;
		return false;
	}
	
	/*private static boolean allDigits(String prevTok) {
		
		for(int i=0; i < prevTok.length(); i++){
			if(!Character.isDigit(prevTok.charAt(i)))
				return false;
		}
		return true;
		
	}*/
	
	private static String capitalizeCompass(String value) {
		
		String edited = "";
		
		String[] tokens = value.split("\\s");
		
		for(int i=0; i < tokens.length; i++) {
			
			for(String c : compass)
			   if(c.equals(tokens[i].toUpperCase())) {
				  tokens[i] = c;
				  break;
			   }
			edited += tokens[i] + SPACE;
		}
		return edited.trim();
    }
        
        private static String formatBoxLine(String entry)   {          
            
           String[] tokens = entry.trim().split("\\s") ;
           
           String line = "";
           
           for(String s : tokens) //remove spaces
               line += s;
           
           if(!line.toLowerCase().startsWith("pobox")) { //Not a PO Box
               return "";
           } 
           
           if(line.length() == "pobox". length())
        	   return "PO Box";
           
           String after = line.substring("pobox".length());
          
          line = "PO Box" + SPACE + after;          
            
          return line;
          
        }
        
        private static String removeOrdinalSpaceSuffix (String entry) {
            
            String[]tokens = entry.split("\\s");
		
	    String edited = tokens[0];
		
		for(int i=1; i < tokens.length; i++)  { //Skip the first token
                    
			if(ordMatches(tokens[i]) && StringUtil.allDigits(tokens[i-1]))
				edited += tokens[i].toLowerCase();
			else edited += SPACE + tokens[i];
                }
                
            return edited;    
            
        } //end
        
        private static String lowerCaseOrdinalSuffix(String line) {
            
            String[]tokens = line.split("\\s");		
	   
            
            for(int i=0; i < tokens.length; i++) {
                for(String ord : ordinalSfx) {
                    String tok = tokens[i];
                    if(tok.toLowerCase().endsWith(ord) && 
                            StringUtil.allDigits(tok.substring(0, tok.length()-2)))
                         tokens[i] = tok.toLowerCase();
                } 
            }
            
            String joined  = "";
            
            for(String tok : tokens) {
                
                joined += tok + SPACE;
                
            }
            
            return joined.trim();
        }

    
} //end class
