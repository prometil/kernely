$.extend({
	// Writes a message for the user.
	// If div is not defined, write in the default div.
	// status can be :
	//		- "success"
	//		- "error"
	//		- "info"
	writeMessage: function(status, message, div){
		console.log("Write appelé");
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
  }
});


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
	// It contains X fields :
	// - data : 
	// - idField : 
	// - elements :
	// - columns :
	// - eventName :
	// - events :
	// - reload :
	kernely_table: function(options, editable){
		// Force options to be an object
		options = options || {};
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
					if($.isArray(options.elements)){
						$.each(options.elements, function(){
							array.push(parent[this]);
						});
					}
					else{
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