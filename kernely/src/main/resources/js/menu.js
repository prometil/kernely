$(document).ready(function(){

	$("ul.subnav").parent().append("<span></span>"); 
	$("ul.topnav li .menu_item").click(function() { 
		$(this).parent().find("ul.subnav").slideDown('fast').show(); 

		$(this).parent().hover(function() {
		}, function(){
			$(this).parent().find("ul.subnav").slideUp('slow'); 
		});

		}).hover(function() {
			$(this).addClass("subhover"); //On hover over, add class "subhover"
		}, function(){	
			$(this).removeClass("subhover"); //On hover out, remove class "subhover"
	});

});