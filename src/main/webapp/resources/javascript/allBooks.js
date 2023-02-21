/**
 * 
 */
$(document).ready(function(){
	
	 $(":checkbox[name='bookChecked']:checked").each(function(){
	        
	        var row = $(this).parent().parent().next();
	        row.show();
	       
	        
	    });
	 
	  $(":checkbox[name='bookChecked']").click(function(){
	        
	        var row = $(this).parent().parent().next();
	        
	        if($(this).prop("checked") == true){            
	            
	            row.fadeIn(600);
	            
	        }
	        else row.fadeOut(600);  
	       
	       
	            
	    });
	
	
});