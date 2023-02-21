package com.mycompany.hosted.formatter;

public class MaskUtil {	
	
	 /*
     * Variable char substitutes
     */
    private static final char DIGIT = '#';
    private static final char ALPHA = '*';
    private static final char ALNUM = '^'; //digit or letter
    private static final String ESCAPE = "\\";    
   

	public static String validateByFormats(String entry,  String... formats) {
    	
    	String stripped = "";
    	
    	boolean found = false;
    	
    	if(entry == null || entry.trim().contentEquals(""))
    		return "";
    	
    	String tentry = removeChar(entry, "\\s", "");
    	
    	tentry = removePlusOne(tentry);
    	
    	//System.out.println("MaskUtil#validateByFormats: entry=" + tentry);
    	
    	for(String format : formats) {   		
    		
    		String tformat = removeChar(format, "\\s", "");
    		
    		//System.out.println("MaskUtil#format=" + tformat);
    		
    		if(compareByFormat(tformat, tentry)) {
    			
    			stripped = removeFormatChars(tformat, tentry);
    			
    			//ystem.out.println("MaskUtil#stripped=" + stripped);
    			
    			found = true;
    			
    			break;
    		}
    		
    	} //end for	
    	
    	
    	if(found)
    		return stripped;    	
    	
    	return "";
    }
	
	private static String removePlusOne(String value) {
		
		if(value.startsWith("+1"))
			return value.substring(2);
		
		else if(value.startsWith("(+1)"))
			return value.substring(4);
		
		return value;		
				
	}
    
    private static boolean compareByFormat(String tformat, String tentry) {
    	
    	
    	
    	boolean valid = true;
    	
    	int fidx = 0;
    	int eidx = 0;
    	
    	while(fidx < tformat.length()) {    		
    		
    		if(eidx == tentry.length())
    			valid = false; //entry too short 		
    		
    		
    		else if(isEscapeChar(tformat.substring(fidx, fidx + 1))) {    			
    			
    			valid = compareEscapedChar(tformat.charAt(fidx + 1), tentry.charAt(eidx));
    			
    			fidx++;
    		}
    		else {
    			
    			valid = compare(tformat.charAt(fidx), tentry.charAt(eidx));    			
    			
    		}
    		
    		if(!valid) {
    			
    			break;   
    		}
    		
    		fidx++;
    		eidx++;
    			
    	}
    	
    	if(valid && eidx < tentry.length()) { //entry too long
    		valid = false;
    	}
    	
    	//System.out.println("compareByFormat returning " + valid
    	//		+ " " + tentry + " " + tformat);
    		
    	return valid;
    	
    }
    
    private static boolean compare(char maskchar, char inchar) {
    	
    	boolean valid;
    	
    	if(!isVariableChar(maskchar)) { //format symbol
    		if(inchar == maskchar)
    			valid = true;
    		else valid = false;
    	}
    	else valid = charValid(maskchar, inchar);
    	
    	return valid;
    	
    }
    
    private static boolean compareEscapedChar(char maskchar, char inchar) {
    	if(inchar == maskchar)
    		return true;
    	return false;
    }
    
    private static String removeChar(String val, String remove, String replace) {
    	
    	String edited = val.replaceAll(remove, replace);
    	
    	return edited;
    }
    
    private static boolean isVariableChar(char c) {
        if(DIGIT == c || ALPHA == c || ALNUM == c)
            return true;
        return false;
    }
    
    private static boolean isEscapeChar(String c) {
    	
    	if(ESCAPE.contentEquals(c))
    		return true;
    	return false;
    	
    }
    
private static boolean charValid(char fchar, char echar){
        
        boolean valid = false;
        int error = 0;        
        
        switch(fchar) {
        case DIGIT:
            if(Character.isDigit(echar))
                valid = true;
            break;
        case ALPHA:
            if(Character.isAlphabetic(echar))
                valid = true;
            break;
        case ALNUM:
            if(Character.isDigit(echar) || Character.isAlphabetic(echar))
                valid = true;
            break;
        default:
           error = 1;
        }      
        if(error == 1)
             throw new IllegalArgumentException("MaskUtil"
               + "#charValid:unknown formatting char in mask.");
        return valid;
    }

    private static String removeFormatChars(String tformat, String tentry) {
    	
       String edited = "";    	
    	
       int fidx = 0;
       int eidx = 0;
    	
    	while(fidx < tformat.length()) {
    		
    		if(isEscapeChar(tformat.substring(fidx, fidx + 1))) {
    			edited += tformat.charAt(fidx + 1) ;
    			fidx++;
    		}
    		else if(isVariableChar(tformat.charAt(fidx)))
    			edited += tentry.charAt(eidx);
    		
    		fidx++;
    		eidx++;
    			
    	}
    	return edited;    	
    }
    
    public static String format(String edited, String printFormat, String...inFormat) {
    	
    	String stripped =validateByFormats(edited, inFormat);
    	
    	if(stripped.contentEquals(""))
    		return new String();   //Invalid format
    	
    	String fmt = "";
    	
    	int editidx = 0;
    	
    	for(int i=0; i < printFormat.length(); i++) {
    		
    		if(isEscapeChar(printFormat.substring(i, i + 1))) {
    			
    			fmt += printFormat.charAt(i + 1);
    			
    			i++;
    			
    		}  else if(!isVariableChar(printFormat.charAt(i))) {
    			
    			fmt += printFormat.charAt(i);
    			
    		} else {
    			
    			fmt += stripped.charAt(editidx);
    			
    			editidx++;
    		}
    		
    	}
    	
    	return fmt;
    	
    }
    
   /* private static String displayFormats(String [] formats) {
    	
    	String formatted = "{";
    	
    	for(String f : formats) {
    		
    		formatted += "'" + f + "', ";
    		
    	}
    	
    	formatted = formatted.substring(0, formatted.length() - 2);
    	
    	formatted = formatted + "}";
    	
    	return formatted;
    } */

	

} //end class
