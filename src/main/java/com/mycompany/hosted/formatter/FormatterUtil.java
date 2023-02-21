/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hosted.formatter;

import com.mycompany.hosted.formatter.TextFormat.Format;

/*public static enum Format {
	DEFAULT,
	UPPER,
	PROPER,
	LOWER,
	NO_CASE,	
	EMAIL,
	PHONE,
	CREDIT_CARD,
	PROPER_NAME,
    POSTAL_NAME,
    ADDRESS_LINE,
    POSTAL_CODE,
    NO_FORMAT,      
    	
} ;*/

public class FormatterUtil {
    
    private boolean isProperName = false;
    private boolean isPostalName = false;
    private boolean isEmail = false;
    private boolean isAddressLine = false;
    private boolean isPostalCode = false;
    private boolean isDefault = false;
    private boolean isNoFormat = false;
    private boolean isUpper = false;
    private boolean isLower = false;
    private boolean isProper = false;
    private boolean isNoCase = false;
    
    //private static final String PERIOD = Character.valueOf((char)46).toString();
    private static  final String SPC = Character.valueOf((char)32).toString();

    
    /*
     * Ensure that formatting is consistent to one type of logical field
     * Iterate array Format constants and break after one type is found.
     * The type is assigned to a module-level variable, and accessed from
     * the format method.
     */
    public FormatterUtil(Format[] formats) {
        
        boolean fmtSpecified = false;
        boolean caseSpecified = false;
        
        for(Format fmt : formats) {
            if(fmt.equals(Format.PROPER_NAME)) {
                isProperName = true;
                fmtSpecified = true;
                break;
            } 
            else if(fmt.equals(Format.POSTAL_NAME)) {
                isPostalName = true;
                fmtSpecified = true;
                break;
            } 
            else if(fmt.equals(Format.EMAIL)){
                isEmail = true;
                fmtSpecified = true;
                break;
            }
            else if(fmt.equals(Format.NO_FORMAT)){
                isNoFormat = true;
                fmtSpecified = true;
                break;
            }
            else if(fmt.equals(Format.ADDRESS_LINE)) {
                isAddressLine = true;
                fmtSpecified = true;
                break;
            }
            else if(fmt.equals(Format.POSTAL_CODE)){
                isPostalCode = true;
                fmtSpecified = true;
            }
        }
        
       if(!fmtSpecified)
            isDefault = true;  
       
        
         for(Format fmt : formats) {
            if(fmt.equals(Format.UPPER)) {
                isUpper = true;
                caseSpecified = true;
                break;
            } 
            else if(fmt.equals(Format.LOWER)){
                isLower = true;
                caseSpecified = true;
                break;
            }
            else if(fmt.equals(Format.PROPER)){
                isProper = true;
                caseSpecified = true;
                break;
            }
            else if(fmt.equals(Format.NO_CASE)){
                isNoCase = true;
                caseSpecified = true;
                break;
            }
        }//end for
        if(!caseSpecified)
            isProper = true;
        
    }//end constructor
    
    /*
     * To do: if isNoFormat, and a case specified skip format, and apply case
     * To do: case '&' only evaluates alphabetic preceding and following chars, but can also be digits 
     * 
     */
    public String format(String pvalue) {
        
        String value = pvalue;
        String emailLocal = "";      
        
        if(isNoFormat)
            return value;        
        
        String edited = "";        
        
        boolean included = false;
        
       if(value == null) 
           return "";
       value = value.trim();
       if(value.isEmpty())
           return "";
       
        if(isEmail){ //Currently only string after the '@' is edited
            if(pvalue.indexOf('@') == -1)
                return pvalue; //constraint violation will message
            value = pvalue.substring(pvalue.indexOf('@'));
            emailLocal = pvalue.substring(0, pvalue.indexOf('@'));
        }
        
        char prev = (char)0;
        
        for(int i = 0; i < value.length(); i++) {
            
           included = true; 
           
           if(Character.isAlphabetic(value.charAt(i))) {
                edited += value.charAt(i);
               
            }
            else if(Character.isDigit(value.charAt(i)) && !isProperName && !isPostalName) {
                edited += value.charAt(i);
                if(isProperName || isPostalName)
                    included = false;
            }
            else {
                char c = value.charAt(i);
                switch(c){
                    case (char)32:
                         if(isEmail || isPostalCode){
                             included = false;                       
                         }
                         else if(Character.isAlphabetic(prev)|| Character.isDigit(prev) ){
                             edited += c;
                         }
                         else if(prev == '&'){                                                         
                             if(edited.substring(edited.length()-2, edited.length()-1).equals(SPC))
                                edited += c; //ampersand in edited string is preceded by a space
                         }    
                         else included = false;
                         break;
                    case '-': 
                        if(Character.isAlphabetic(prev)|| Character.isDigit(prev) ){
                             edited += c;
                         }
                        else included = false;
                         break;
                    case '/' :
                        if(isEmail || isProperName || isPostalName){
                            included = false;
                        }
                        else if(Character.isAlphabetic(prev)|| Character.isDigit(prev) ){
                             edited += c;
                         }
                        else included = false; 
                        break;
                    case '@' :
                        if(!isEmail)
                            included = false;
                        else if(prev == '@')
                            included = false;
                        else
                            edited += c;
                        break;
                    case (char)46  : //period
                        if(!isEmail)
                            included = false;
                        else if(prev == (char)46)
                            included = false;                           
                        else
                            edited += c;
                        break;
                    case '&' :
                        if(isEmail || isProperName || isPostalName)
                            included = false;
                        //include if ampersand joins two words
                        else if(prev == (char)32
                                && i + 2 < value.length() 
                                && value.charAt(i+1) == (char)32                                    
                                && Character.isAlphabetic(value.charAt(i+2)))
                              edited += c;  
                        //replace preceding space with amp if following char is alphabetic
                        else if(prev == (char)32
                                && i + 1 < value.length() 
                                && Character.isAlphabetic(value.charAt(i+1)))
                            edited =  this.replaceCharAtPosition(edited.length()-1, edited, '&');
                        //include if ampersand is pnemonic - preceeded by a letter
                        else if(Character.isAlphabetic(prev))
                            edited += c; //punctuation or space following will be removed
                        else
                            included = false;
                        break;
                    case '#' :
                        if(isEmail || isProperName || isPostalName)
                            included = false;
                        else if(edited.isEmpty())
                            edited += c;
                        else if(Character.valueOf(prev).toString().equals(SPC))
                            edited += c; //can only be followed by a letter or digit
                        else if(Character.isAlphabetic(prev) || Character.isDigit(prev)) 
                            edited += SPC + '#';
                        else included = false;
                        break;
                    default:
                        included = false;
                                
                } //end switch
            }//end else
            if(included)
                prev = value.charAt(i);
        }//end for  
         
       // System.out.println("Test: " + edited + "=" + edited.length());
        
        if(edited.isEmpty()) //no valid sequences
            return "";
        
        int i = edited.length() - 1;
        
        for(; i >= 0; i--)
            if(Character.isAlphabetic(edited.charAt(i))
                    || Character.isDigit(edited.charAt(i)))
                break;      
        
        edited = edited.substring(0, i+1);
       // System.out.println("test:" + edited);
        
        if(edited.isEmpty()) { //Trailing hyphen, slash, '@', email period delimiter
            if(isEmail && !emailLocal.isEmpty())
                edited = "@";
            else return "";
        }
        
        if(isProperName)
            edited = this.replaceSpace(edited);
        
        edited = doCase(edited);    
        
        if(isEmail)
             edited = emailLocal + edited.toLowerCase(); 
        if(isAddressLine)
            edited = AddressLineFormatUtil.format(edited);
        
        return edited;
    }
    
    private String replaceCharAtPosition(int offset, String edited, char replacement) {
      
       StringBuffer sb = new StringBuffer(edited);
       sb.setCharAt(offset, replacement);
       return sb.toString();
    }
    
    private String doCase(String edited) {
        if (isNoCase) {
            return edited;
        }
        if (isDefault || isProper) {
            edited = properCase(edited);
        } else if (isUpper) {
            edited = edited.toUpperCase();
        } else if (isLower) {
            edited = edited.toLowerCase();
        }
        return edited;
    }
        private String properCase(String val) {
		
		String formatted = "";
		int start = 0;
		String token="";
		val = val.trim();
		for(int i=0; i < val.length(); i++) {
			char c = val.charAt(i);
			if(c == '/' || c == '-' || c == (char)32) {
				token = val.substring(start,i);
				if(token.isEmpty()) {//embedded space, consecutive delimiter
					start += 1;
					continue;
				}
				token = capitalize(token);
				start = i + 1;
				formatted += token + new Character(c).toString();
			}
		}
		token = val.substring(start);
		formatted += capitalize(token);
		return formatted;
		
	}
	
	private String capitalize(String token) {
		String edit="";
                if(token.contains("&"))
                    return token.toUpperCase();
                else if(token.contains("#"))
                    return token.toUpperCase();
                /*else if(token.toUpperCase().equals(token))
                    return token;*/
                else if(Character.isDigit(token.charAt(0)))
                    return(token.toUpperCase());
		edit = token.substring(0,1).toUpperCase()
				+ token.substring(1,token.length()).toLowerCase();
		return edit;
	}	
    
    private String replaceSpace(String name) {     
      
      char replacement = '-';
        
      name = name.replace((char)32, replacement);
        
        return name;
    }
    
  /*  private boolean allSymbols(String value){
        for(int i=0; i < value.length(); i++)
            if(Character.isAlphabetic(value.charAt(i))
                  || Character.isDigit(value.charAt(i)))
                       return false;
        return true;
    }*/
    
}// end class
