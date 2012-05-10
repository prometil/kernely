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
		if (h == null){
			vh = "auto";
		} else {
			vh = h;
		}
		if (w == null){
			vw = "auto";
		} else {
			vw = w;
		}
		div = document.createElement("div");
		$(div).html($(content).html());
		$(div).dialog({
			autoOpen: false,
			height: vh,
			width: vw,
			modal: true
		});
		return div;
	},
	
	// Create a dialog
	// 	- text : the text to display (usually, a question...)
	// 	- callback : the function to call when the user click on Yes
	//  - param : a param for the function (can be null)
	kernelyConfirm: function(confirmTitle,content, callback, param){
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
			title:confirmTitle,
			autoOpen: false,
			modal: true,
			resizable:false,
			height:"auto",
			width:"auto"
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

DateNavigatorRouter = Backbone.Router.extend({
	element:null,
	navigator:null,
	onchange: null,
	
	routes: {
		"/day/:day/:month/:year":  "selectDay",
		"/month/:month/:year":  "selectMonth",
		"/year/:year":  "selectYear",
		"*actions" : "defaultRoute"
	},
	
	initialize: function(element, onchange){
		this.element = element;
		this.onchange = onchange;
        Backbone.history.start();
	},

	selectMonth: function(month,year){
		if (this.navigator == null){
			this.navigator = new DateNavigatorView(this,this.element, this.onchange, null, month, year);
		}
	},
	selectDay: function(){
		
	},
	selectYear: function(){
		
	},
	defaultRoute: function(){
		
	}
});

DateNavigatorView = Backbone.View.extend({

	daySelected:null,
    monthSelected:null,
    yearSelected:null,
    router:null,
    onchange: null,
    defaultDay: null,
    defaultMonth: null,
    defaultYear: null,
    
    initialize: function(router,element, onchange, day, month, year){
		// Start Backbone history a neccesary step for bookmarkable URL's
		var parent = this;
		this.onchange = onchange;
		this.router = router;
		this.daySelected = day;
		this.defaultDay = day;
		this.monthSelected = month;
		this.defaultMonth = month;
		this.yearSelected = year;
		this.defaultYear = year;
		
		this.el = element;
		var imgp = document.createElement("img");
		$(imgp).attr("src", "/images/icons/previous_icon.png");
	    $(imgp).addClass("clickable");
	    var span = document.createElement("span");
	    $(span).addClass("k-ui-navigator");
	    var imgn = document.createElement("img");
	    $(imgn).addClass("clickable");
	    $(imgn).attr("src", "/images/icons/next_icon.png");
	    this.el.append(imgp);
	    this.el.append(span);
	    this.el.append(imgn);
	    if (day == null){
	    	// Display month and year
	        var template = $("#"+ month +"-month-template").html();
	        $(span).text(template + " " + this.yearSelected);
	    	
	        // Associates events for month management
	        $(imgp).bind("click", function(){parent.previousMonth(parent.monthSelected,parent.yearSelected)});
	        $(span).bind("click", function(){parent.toDefaultMonth()});
	        $(imgn).bind("click", function(){parent.nextMonth(parent.monthSelected,parent.yearSelected)});
		    this.onchange(this.monthSelected, this.yearSelected);

	    }
	    return this;
    },
    
	nextMonth: function(){
		this.monthSelected ++;
		if(this.monthSelected == 13){
			this.monthSelected = 1;
			this.yearSelected ++;
		}
		this.actualizeMonth();
	},
	
	toDefaultMonth: function(){
		this.monthSelected = this.defaultMonth;
		this.yearSeleted = this.defaultYear;
		this.actualizeMonth();
	},
	
	previousMonth: function(){
		this.monthSelected --;
		if(this.monthSelected == 0){
			this.monthSelected = 12;
			this.yearSelected --;
		}
		this.actualizeMonth();

	},
	
	actualizeMonth: function(){
		this.router.navigate("/month/" + this.monthSelected + "/" + this.yearSelected, {trigger: true, replace: true});
    	// Display month and year
        var template = $("#"+ this.monthSelected +"-month-template").html();
        $(".k-ui-navigator").text(template + " " + this.yearSelected);
	    this.onchange(this.monthSelected, this.yearSelected);
	},
	
    render: function(){
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
							if(typeof(elem) == "undefined"){
								elem = "";
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
						if(typeof(elem) == "undefined"){
							elem = "";
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
	},
	
	// Defines a generic behavior for all dialogs in the application
	// The "options" parameter is the configuration of the table,
	// It contains X fields :
	// - title : The title of the dialog
	// - content : The content of the dialog
	// - eventName : Names of the events
	// - events : Events
	kernely_dialog: function(options){
		
		if (options == "close"){
			$(this).dialog("close");
		} else if (options == "open"){
			$(this).dialog("open");
		} else {
			// Force options to be an object
			options = options || {};
			options.events = options.events || {};
			options.eventNames = options.eventNames || {};
			this.html(options.content);
			if (options.height == null){
				options.height = "auto";
			}
			if (options.width == null){
				options.width = "auto";
			}
			this.dialog({autoOpen: false,
							height: options.height,
							width: options.width,
							modal:true,
							title: options.title,
							resizable: false,
							zIndex: 2});

			var parent = this;
			
			// Considering events
			if($.isArray(options.eventNames)){
				// More than one event name ("click", "dblckick"...)
				$.each(options.eventNames, function(){
					var name = this;
					var events = options.events[this];
					// More than one element reactive to the event name
					if ($.isArray(events)){
						$.each(events, function(){
							$(this.el).bind(name, this.event);
						});
					} else {
						$(options.events[this].el).bind(this, options.events[this].event);
					}
				});
			}
			else{
				var events = options.events[options.eventNames];
				var name = options.eventNames;
				
				if ($.isArray(events)){
					$.each(events, function(){
							$(this.el).bind(name, this.event);
					});
				} else {
					if (events != null){
						$(events.el).bind(name,events.event);
					}
				}
			}
		}
	},
	
	// Fills an element with a navigator of dates
	// The navigator is automatically created by the url :
	// - url : /#/day/X/Y/Z => day navigator, where X is the day, Y the month and Z the year
	// - url : /#/month/X/Y => month navigator, where Y is the month and Z the year
	// - url : /#/year/Z => year navigator, where Z is the year
	// options can be filled with the following data :
	// - onchange : the function to call when the date change.
	//				this function will be called with a number
	//				of arguments depending of the url at the
	//				creation of the selector (three arguments
	//				for a day selector for instance).
	kernely_date_navigator: function(options){
        // Force options to be an object
        options = options || {};
        var router = new DateNavigatorRouter(this,options.onchange);
	}
	

});