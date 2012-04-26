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
			modal: true,
			resizable:false
		});
	
		$("#confirm-yes-button").click(function(){callback(param); $(div).dialog("destroy")});
		$("#confirm-no-button").click(function(){$(div).dialog("destroy")});
		$(div).dialog("open");
	}
});

/* View used to generate table lines.*/
TableLineView = Backbone.View.extend({
	tagName: "tr",
	className: 'kernely_table_line',
	
	data: null,
	
	events: {
		"click" : "select",
		"mouseover" : "over",
		"mouseout" : "out"
	},
	
	
	idLine: null,
	
	initialize: function(idLine, data,eventNames, events){
		this.data = data;
		this.idLine = idLine;
		var parent = this;
		if($.isArray(eventNames)){
			$.each(eventNames, function(){
				$(parent.el).bind(this, {line: parent.idLine} ,events[this]);
			});
		}
		else{
			$(parent.el).bind(eventNames, {line: parent.idLine} ,events[eventNames]);
		}
		
		$(this.el).bind("click", {line: this.idLine} ,events["click"]);
		return this;
	},
	
	select : function(){
		$(".line_selected").removeClass("line_selected");
		$(this.el).addClass("line_selected");
	},
	over : function(){
		$(this.el).addClass("over");
	},
	out : function(){
		$(this.el).removeClass("over");
	},
	render:function(){
		var parent = this;
		if($.isArray(this.data)){
			$.each(this.data, function(){
				$(parent.el).append("<td>"+this+"</td>");
			});
		}
		else{
			$(this.el).append("<td>"+this.data+"</td>");
		}
		return this;
	}
	
});

jQuery.fn.extend({
	// Defines a generic behavior for all tables in the application
	// The "options" parameter is the configuration of the table,
	// It contains 8 fields :
	// - data : The data to display in the table
	// - idField : The name of the field representing the id of the current line
	// - elements : The name of the fields present in data to localize the values
	// - columns : The names of the columns to diaplay in the header of the table
	// - eventName : The names of the different custom events to implements
	// - events : The association between the name and the function called of a custom event
	// - reload : If true, only reload the given data in the table
	// - editable : 
	kernely_table: function(options){
		// Force options to be an object
		options = options || {};
		options.events = options.events || {};
		options.eventNames = options.eventNames || {};
		if(!options.reload){
			// Add the header to the table
			var thead = document.createElement("thead");
			var tr = document.createElement("tr");
			if($.isArray(options.columns)){
				$.each(options.columns, function(){
					$(tr).append("<th>"+ this +"</th>");
				});
			}
			else{
				$(tr).append("<th>"+ options.columns +"</th>");
			}
			$(thead).append($(tr));
			this.append($(thead));
		}
	
		if(typeof(options.data) != "undefined"){
			var table = this;
			if($.isArray(options.data)){
				var parent;
				$.each(options.data, function(){
					var array = new Array();
					parent = this;
					var elem;
					if($.isArray(options.elements)){
						$.each(options.elements, function(){
							if(this.lastIndexOf(".") != -1){
								var temp = this.split(".");
								elem = parent;
								$.each(temp, function(){
									elem = elem[this];
								});
							}
							else{
								elem = parent[this];
							}
							array.push(elem);
						});
					}
					else{
						if(options.elements.lastIndexOf(".") != -1){
							var temp = options.elements.split(".");
							elem = parent;
							$.each(temp, function(){
								elem = elem[this];
							});
						}
						else{
							elem = parent[options.elements];
						}
						array.push(parent[option.elements]);
					}
					table.append(new TableLineView(parent[options.idField],array, options.eventName, options.events).render().el);
				});
			}
			else{
				var array = new Array();
				if($.isArray(options.elements)){
					$.each(options.elements, function(){
						array.push(options.data[this]);
					});
				}
				else{
					array.push(options.data[option.elements]);
				}
				table.append(new TableLineView(options.data[options.idField], array, options.eventName, options.events).render().el);
			}
		}
	},

	reload_table: function(options){
		var body = this.find("tbody");
		body.empty();
		options.reload = true;
		this.kernely_table(options);
	}
});