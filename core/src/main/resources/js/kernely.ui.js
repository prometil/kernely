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
	// text : the text to display (usually, a question...)
	kernelyConfirm: function(content, callback){
		console.log("Confirm");
		div = document.createElement("div");

		var template = $("#kernely-confirm-dialog-template").html();
		var view = {question: content};
		var html = Mustache.to_html(template, view);
		$(div).html(html);
		$(div).dialog({
			autoOpen: false,
			modal: true
		});
		// $(div).dialog("close"); 
		$("#confirm-yes-button").click(function(){console.log("oui"); callback(); $(div).dialog("destroy")});
		$("#confirm-no-button").click(function(){console.log("non"); $(div).dialog("destroy")});
		$(div).dialog("open");
		console.log("Opened")
		
	}

});