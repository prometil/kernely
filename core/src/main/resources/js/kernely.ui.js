$.extend({
	// Writes a message for the user.
	// If div is not defined, write in the default div.
	// status can be :
	//		- "success"
	//		- "error"
	//		- "info"
	writeMessage: function(status, message, div){
 		var vdiv;
		if (div == null){
			vdiv = "#notification_to_user";
		} else {
			vdiv = div;
		}
		
		$(vdiv).hide();
		$(vdiv).stop(true,true);
    	$(vdiv).removeClass();
    	$(vdiv).addClass(status+"-notification");
    	$(vdiv).html(message);
    	$(vdiv).fadeIn(1000);
    	$(vdiv).delay(3000);
    	$(vdiv).fadeOut(3000);
  },

	// Create a dialog
  	// content : the id of the template of the dialog
  	// h : height
  	// w : width
	kernelyDialog: function(content,h,w){
		div = document.createElement("div");
		$(div).html($(content).html());
		$(div).dialog({
			autoOpen: false,
			height: h,
			width: w,
			modal: true
		});
		return div;
	},

	// Create a dialog
	// 	- text : the text to display (usually, a question...)
	// 	- callback : the function to call when the user click on Yes
	//  - param : a param for the function (can be null)
	kernelyConfirm: function(content, callback, param){
		// Search for the confirm dialog
		div = $("#kernely-confirm-dialog");
		if ($(div).html() == null){
			// Create the div for the dialog
			div = document.createElement("div");
			$(div).attr("id","kernely-confirm-dialog");
		}

		var template = $("#kernely-confirm-dialog-template").html();
		var view = {question: content};
		var html = Mustache.to_html(template, view);
		$(div).html(html);
		$(div).dialog({
			autoOpen: false,
			modal: true
		});

		$("#confirm-yes-button").click(function(){callback(param); $(div).dialog("destroy")});
		$("#confirm-no-button").click(function(){$(div).dialog("destroy")});
		$(div).dialog("open");
	}

});