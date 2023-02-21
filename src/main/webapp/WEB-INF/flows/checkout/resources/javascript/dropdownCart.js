/**
 * 
 */

jQuery(document).ready(function(){
	
	
	 var positionLogin = function () {	 
		 
		 $("#popCartContent").show();
	        
	        var top = $(".panel-body").offset().top;
	        
	        top += $(".panel-body").innerHeight(false);       
	        
	        var left = $(".panel-body").outerWidth(true) - $("#popCartContent").width();
	        
	       // var right = $(".panel-body").css("right");
	        
	       // console.log("right=" + right);
	        
	        $("#popCartContent").css({"left":left, "top":top});           
	        
	        $("#popCartContent").hide();
	        
	    };
	    
	    $("#cartAnchor").click(function() {
	    	
	    	$("#popCartContent").slideToggle(500);
	    	
	    });
	    
	    positionLogin();
	
});