/**
 * 
 */

jQuery(document).ready(function(){
	
	
	 var positionContent = function () {	 
		 
		 $("#popCartContent").show();
	        
	        var top = $(".panel-body").offset().top;
	        
	        top += $(".panel-body").innerHeight(false);       
	        
	        var left = $(".panel-body").outerWidth(true) - $("#popCartContent").width();	      
	        
	        $("#popCartContent").css({"left":left, "top":top});           
	        
	        $("#popCartContent").hide();
	        
	    };
	    
	    $("#cartAnchor").click(function() {
	    	
	    	$("#popCartContent").slideToggle(500);
	    	
	    });
	    
	    positionContent();
	
});