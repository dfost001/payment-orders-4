/**
 * 
 */
$(document).ready(function(){
	
	var position = function(){
		
		var body = $(document);
		
		var div = $("#divProcessing");
		
		div.show();
		
		var top = (body.height() - div.height())/2;
		
		var left = (body.width() - div.width())/2;
		
		div.offset({top: top, left: left});
		
		$(".container").css("opacity", .5);
		
	};
	
	/*$("input[value='Select']").submit(function(){
		
		alert("Submit attached");
		
		//position();
		
	});*/
	
	$("input[value='Select']").on("click", function(event){
		
		//event.preventDefault();
		
		//window.alert("Submit attached");
		
		position();
		
	});
	
});