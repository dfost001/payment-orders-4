/**
 * Bug: browser not requesting this file. So dynamic construction of baseUrl has not
 * been tested. Old file hardcoded baseUrl.
 */

var processAjax = function () {
	
	//var baseUrl = "http://localhost:7070/spring-mvc-example";
	
	var getBaseUrl = function() {
        
        var href = window.location.href;
        
        var startPos = (location.protocol + "://").length;
        
        var contextPosition = href.indexOf("/", startPos);
        
       // console.log("contextPosition=" + contextPosition);
        
        var requestPathPosition = href.indexOf("/", contextPosition + 1);
        
       // console.log("requestPathPos=" + requestPathPosition);
        
        var baseUrl = href.substring(0, requestPathPosition); //does not include trailing '/'
        
       // console.log("ajax.js#getBaseUrl:" + baseUrl);
        
        return baseUrl;
        
    };
	
	
	var doLooseMatchAjax = function(text){
		
		var baseUrl = getBaseUrl();
		
		var request = $.ajax({
			url: baseUrl + "/bookMatch.do",
			method: "get",
			dataType: "xml",
			headers: {
				Accept:"application/xml"
			},
			data: {chars:text}
		});
		return request;
	};
	
	var doCardRequest = function(handler) {
		
		var baseUrl = getBaseUrl();
		
		console.log("doCardRequest#baseUrl=" + baseUrl + "/" + handler);
		
		var request = $.ajax({
			
			url: baseUrl + "/" + handler,
			method: "get",
			dataType: "text",
			headers: {
				Accept: "text/plain, application/json"
			}
			
		});
		
		return request;
	};
	/*
	 * <span> tag id's corresponding to model keys are hard-coded into support division
	 */
	var extractErrors = function(errObject) {
		
		$("#support").css("display", "block");
		
		$("#support").on("click", null, null, function(){
			
			$(this).next().slideToggle();
			
		});
		
		$.each(errObject, function(index, value){
			
			var selector = "#" + index;
			
			$(selector).html(value);
			
		});
	};
	
	var showAlert = function(message) {		
		
		$("#alert").animate({top:"-300px"}, null, null, function(){
			
			   $(this).show();
			   $("#alertMsg").html(message);
			   $(this).css("top", "0");
			 
		});
	};
	
	var doError = function(xhr) {
		
		   var message = "You may contact support to complete your order.";
			    
		   
		   if(!xhr || xhr.status <= 0) {
			   message= "Please check your Internet connection and retry. " +
			      "You may also contact support."
			   showAlert(message)	;   
			   return;
		   }		  
		   
		   var errObj = JSON.parse(xhr.responseText);
			   
			if(errObj) {
			
			  
			   extractErrors(errObj);
			   showAlert(message);
			
		   }
		   else {			   
			   
			   showAlert(message);			   
			  
		   }			
				
		};
		
	
	return {
		
		doCardRequest:doCardRequest,
		doLooseMatch:doLooseMatchAjax,
		doError:doError
	};
};