package com.mycompany.hosted.jackson2Json;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;




@SuppressWarnings("serial")
public class MyObjectMapper extends ObjectMapper {
	
	
		
		public MyObjectMapper() {
			
			super();		
			
	        setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
	        
	       //  setSerializationInclusion(JsonInclude.Include.NON_NULL);		
	        
	        setSerializationInclusion(JsonInclude.Include.ALWAYS);	//default		

	   }

}
