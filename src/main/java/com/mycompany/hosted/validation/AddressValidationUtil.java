package com.mycompany.hosted.validation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mycompany.hosted.model.validation.States;
import com.mycompany.hosted.exception_handler.EhrLogger;
import com.mycompany.hosted.checkoutFlow.jpa.SupportedValidation;
//import com.mycompany.hosted.checkoutFlow.jpa.SupportedValidationImpl;

@Component
public class AddressValidationUtil {
	
	//private final String zipErr = "5 or 5 plus 4 numeric digits required.";
	
	//private final String addrLineErr = "An all digit building number and street-name "
	//		+ "or PO Box number is required.";
	
	@Autowired
	private SupportedValidation jpa;
	
	
	public String isValidPostalCode(String stateCode, String zipCode) {
		
		this.throwNullOrEmpty("isValidPostalCode", stateCode, zipCode);
		
		
		String err = isValidZipFormat(zipCode);
		
		if(err.isEmpty())
			err = validateZipRange(stateCode, zipCode);
		
		return err;
	}
	
	
	private String isValidZipFormat(String postalCode) {
		
		String zip = postalCode.trim();
		
		String SPC = "";
		
		 if (zip.length() == 5) {
			 
	            if (!allDigits(zip)) {
	            	
	               return "Only digits, please.";
	               
	            } else {
	                return SPC;
	            }
	            
	        } else if (zip.length() == 10) {
	        	
	            if (!allDigits(zip.substring(0, 5))	            		
	                    || zip.charAt(5) != '-'
	                    || !allDigits(zip.substring(6))) {
	            	
	                return "5 plus 4 digits separated by a dash required.";
	                
	            } else {
	                return SPC;
	            }
	        } else {
	        	
	            return "5 or 5 plus 4 digits required.";
	            
	        }
		
	}
	
	private String validateZipRange(String stateCode, String zipCode) {
		
       
		
		States state = jpa.getStateByCode(stateCode);
		
		if(state == null)
			this.throwIllegalArg("validateZipRange", "Null ValidationJpa#getStateByCode");
		
		int code = Integer.parseInt(zipCode.substring(0,5));
        
        //Temporary fix for corrupt data in record at lastZip field
        int lastZip = Integer.parseInt(state.getLastZip().substring(0,5));  
       
        int firstZip = Integer.parseInt(state.getFirstZip().substring(0,5));
      
        String msg="";
       
        if(code < firstZip || code > lastZip) {
                
           msg = state.getStName() + " has a postal code between " +
               firstZip + " and " + lastZip;        
        }
       
        return msg;
	}
	
	public String validateAddressLineFormat(String addrLine) {	
		
		String err = "";
		
	        
        if(isPostalLine(addrLine))
            
           err = isValidPostOffice(addrLine);
        
        else err = isValidBuildingNo(addrLine);
        
        return err;
		
	}
	
	  public  Boolean isPostalLine(String addressLine) {
	        
	         String[] formats = {"PO", "RR", "UNIT"} ;
	         
	         String token[] = addressLine.split("\\s");
	         
	         for(int i = 0; i < formats.length; i++)
	             if(token[0].equalsIgnoreCase(formats[i]))
	                     return true;
	         
	         return false;
	        
	    }
	
	
	 private  String isValidBuildingNo(String addrLine) {        
	        
		 String[] token = addrLine.split("\\s");

	        int count = token.length;

	        String err = "";

	        if (count < 2) {
	            
	            return "Line must have at least a building number and street-name. ";
	            
	            
	        } else if (!allDigits(token[0])) {
	            
	                return "Line must begin with an all digits building number or 'PO Box 99'. ";
	               
	                
	        } else if (this.isStreetSuffix(token[1])) {
	            
	               return "Street-name following building number cannot be a suffix. ";
	                
	                
	        } else if (allDigits(token[1])) {
	            
	                return "Street-name following building number cannot be all digits. ";
	               
	        }
	        
	       return err;
	    }
	 
	    private boolean isStreetSuffix(String value) {
	        
	        String[] suffixList = {"St", "Ave", "Blvd", "Pl"};
	        
	        for(int i=0; i < suffixList.length; i++)
	            if(value.equalsIgnoreCase(suffixList[i]))
	                return true;
	        
	        return false;
	    }
	    
	    private String isValidPostOffice(String addressLine){       
	        
	    	  String[] token = addressLine.split("\\s");
	          
	          
	          int count = token.length;
	          
	          String designator = token[0].toUpperCase() ;
	          
	          

	          String err = "";        
	          
	                  switch(designator) {
	                      case "PO":    
	                                                  
	                              if(count != 3){   
	                                  err = "Expected format is 'PO Box 9999'";
	                                 
	                              }                           
	                              
	                              else if(!token[1].equalsIgnoreCase("Box"))  { 
	                                  err = "'Box' is expected after 'PO'" ;
	                                  
	                              }
	                              
	                              else if(!allAlphaDigits(token[2])) {
	                                  err = "PO Box number contains an invalid symbol(s)";
	                                 
	                              }                
	                              
	                              break;
	                      case "RR" :                         
	                           
	                           if(count != 2) {
	                               err = "Expected Rural Route format is 'RR 99'";
	                               
	                           }
	                           else if(!allAlphaDigits(token[1])) {
	                                  err = "Rural Route number contains an invalid symbol(s)";
	                                  
	                           }
	                           
	                           break;
	                           
	                      case "UNIT" :  
	                    	  
	                         if(count != 2)
	                        	 
	                        	   err = "Expected Military Unit format is 'Unit 999'";
	                           
	                          else if(!allAlphaDigits(token[1])) {
	                                  err = "Military Unit number contains an invalid symbol(s)";
	                                  
	                          }
	                           break;
	                     
	                  }//end switch 
	                  
	                 return err;  
	  }
	    
	
	 private boolean allDigits(String entry){
	        for(int i=0; i < entry.length(); i++)
	            if(!Character.isDigit(entry.charAt(i)))
	                   return false;
	        return true;
	    }
	 
	 private static boolean allAlphaDigits(String entry) {
	        
         if(entry == null || entry.trim().isEmpty())
            return false;         
        
        for(int i=0; i < entry.length(); i++)
            if(!Character.isDigit(entry.charAt(i)) && !Character.isAlphabetic(entry.charAt(i)))
                   return false;
        
        return true;
    } 
	
	private void throwNullOrEmpty(String method, String...params) {
		
		for(String value : params) {
			
			if(value == null || value.trim().isEmpty() )
					throw new IllegalArgumentException(
							EhrLogger.doMessage(this.getClass(), method, "Parameter is null or empty"));
					
		}
	}
	
	private void throwIllegalArg(String method, String message) {
		
		throw new IllegalArgumentException(EhrLogger.doMessage(this.getClass(), method, message));
	}

} //end class
