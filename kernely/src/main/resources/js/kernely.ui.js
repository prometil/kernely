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
		
		var divMess = document.createElement("div");
		$(divMess).addClass(status+"-notification");
		$(divMess).html(message);
		
		$(vdiv).hide();
		$(vdiv).stop(true,true);
		$(vdiv).append($(divMess));
		$(vdiv).show();
		
		$(divMess).fadeIn(1000);
    	$(divMess).delay(3000);
    	$(divMess).fadeOut(3000);
	},
	
	round: function(num, dec){
		var decimal;
		if(dec == null){
			decimal = 2;
		}
		else{
			decimal = dec;
		}
		return (Math.round(num*Math.pow(10,decimal))/Math.pow(10,decimal)).toFixed(decimal);
	},
	
	// Create a dialog
		// content : the id of the template of the dialog
		// h : height
		// w : width
	kernelyDialog: function(title, content,h,w){
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
			title : title,
			autoOpen: false,
			height: vh,
			width: vw,
			resizable:false,
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

var kernelyTableGroups = new Array();

/* View used to generate table lines.*/
TableLineView = Backbone.View.extend({
	tagName: "tr",
	className: 'kernely_table_line',
	
	styles:null,
	
	data: null,
	
	editable : true,
	
	tableParent : null,
	
	events: {
		"click" : "select",
		"mouseover" : "over",
		"mouseout" : "out"
	},
	
	eventNames:null,
	
	eventsActions:null,
	
	idLine: null,
	
	initialize: function(idLine, data,eventNames, events, styles, tableParent, editable){
		this.styles = styles;		
		this.data = data;
		this.idLine = idLine;
		
		this.eventNames = eventNames;
		this.eventsActions = events;
		this.editable = editable;
		this.tableParent = tableParent;
		return this;
	},
	
	select : function(){
		if(this.idLine != -1 && this.editable){
			if(this.tableParent.group == null){
				$(this.tableParent.el).find(".line_selected").removeClass("line_selected");
				$(this.tableParent.el).find(this.el).addClass("line_selected");
			}
			else{
				$.each(kernelyTableGroups[this.tableParent.group], function(){
					
					this.unselectLine();
				});
				$(this.tableParent.el).find(".line_selected").removeClass("line_selected");
				$(this.tableParent.el).find(this.el).addClass("line_selected");
			}
		}
	},
	over : function(){
		if(this.idLine != -1 && this.editable){
			$(this.el).addClass("over");
		}
	},
	out : function(){
		if(this.idLine != -1 && this.editable){
			$(this.el).removeClass("over");
		}
	},
	render:function(){
		var parent = this;
		if(this.idLine == -1){
			var td = document.createElement("td");
			$(td).attr("colspan", "100%");
			$(td).html(this.data);
			$(this.el).append($(td));
		}
		else{
			var i = 0;
			
			for(var value in this.data){
				var td = document.createElement("td");
				$(td).html("" + this.data[value]); // String casting
				if($.isArray(parent.styles[i])){
					$.each(parent.styles[i], function(){
						$(td).addClass(""+this); // String casting
					});
				}
				else{
					$(td).addClass(parent.styles[i]);
				}
				$(parent.el).append($(td));
				i++;
			}
			
			if($.isArray(this.eventNames)){
				$.each(this.eventNames, function(){
					
					if(this.lastIndexOf('.') != -1){
						var event = this.substring(0, this.lastIndexOf('.')-1);
						var element= this.substring(this.lastIndexOf('.'));
						$(parent.el).find(element).bind("" + event, {line: parent.idLine, data: parent.data} ,parent.eventsActions[this]);
						
					}
					else{
						$(parent.el).bind("" + this, {line: parent.idLine, data: parent.data} ,parent.eventsActions[this]);
					}
					
				});
			}
			else{
				if(this.eventNames.lastIndexOf('.') != -1){
					var event = this.eventNames.substring(0, this.eventNames.lastIndexOf('.')-1);
					var element= this.eventNames.substring(this.eventNames.lastIndexOf('.'));
					$(parent.el).find(element).bind("" + event, {line: parent.idLine, data: parent.data} ,parent.eventsActions[this.eventNames]);
				}
				else{
					$(parent.el).bind("" + this.eventNames, {line: parent.idLine, data: parent.data} ,parent.eventsActions[this.eventNames]);
				}
			}
		}
		return this;
	}
	
});

TableView = Backbone.View.extend({
        
        columns:null,
        
        styles:null,
        
        elements:null,
        
        idField: null,
        
        events:null,
        
        eventName:null,
        
        editable:true,
        
        group:null,
        
        initialize: function(element){
                this.styles= new Array();
                this.el = element;
        },
        
        render: function(){
        		// Add this table into the tale group
        		if(this.group != null){
        			if(kernelyTableGroups[this.group] == null){
        				kernelyTableGroups[this.group] = new Array();
        			}
        			kernelyTableGroups[this.group][kernelyTableGroups[this.group].length] = this;
        		}
        	
                // Add the header to the table
                var thead = document.createElement("thead");
                var tr = document.createElement("tr");
                var parent = this;
                $.each(parent.columns, function(){
                        var th = document.createElement("th");
                        $(th).html(this.name); // String casting
                        if($.isArray(this.style)){
                                $.each(this.style, function(){
                                        $(th).addClass("" + this);
                                });
                        }
                        else{
                                $(th).addClass(this.style);
                        }
                        $(tr).append($(th));
                        parent.styles.push(this.style);
                });
                $(thead).append($(tr));
                this.el.append($(thead));
                return this;
        },
        
        reload:function(data){
                var body = this.el.find("tbody");
                body.empty();
                if(typeof(data) != "undefined" && data != null){
                        var table = $(this.el);
                        var view = this;
                        if($.isArray(data)){
                                var parent;
                                $.each(data, function(){
                                        var array = {};
                                        parent = this;
                                        var elem;
                                        if($.isArray(view.elements)){
                                                $.each(view.elements, function(){
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
                                                        array[this] = elem;
                                                });
                                        }
                                        else{
                                                if(view.elements.lastIndexOf(".") != -1){
                                                        var temp = view.elements.split(".");
                                                        elem = parent;
                                                        $.each(temp, function(){
                                                                elem = elem[this];
                                                        });
                                                }
                                                else{
                                                        elem = parent[view.elements];
                                                }
                                                if(typeof(elem) == "undefined"){
                                                        elem = "";
                                                }
                                                array[view.elements] = parent[view.elements];
                                        }
                                        table.append(new TableLineView(this[view.idField],array, view.eventName, view.events, view.styles, view, view.editable).render().el);
                                });
                        }
                        else{
                                var array = new Array();
                                if($.isArray(view.elements)){
                                        $.each(view.elements, function(){
                                                array[this] = data[this];
                                        });
                                }
                                else{
                                        array[elements] = data[elements];
                                }
                                table.append(new TableLineView(data[view.idField], array, view.eventName, view.events, view.styles, view, view.editable).render().el);
                        }
                }
        },
        
        clear: function(){
        	this.el.find("tbody").empty();
        },
        
        noData: function(){
        	$(this.el).append(new TableLineView(-1, $("#no-data-template").html(), null, null, this.styles, this, this.editable).render().el);
        },
        
        unselectLine: function(){
        	$(this.el).find(".line_selected").removeClass("line_selected");
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
		"/week/:week/:year" : "selectWeek",
		"*actions" : "defaultRoute"
	},
	
	initialize: function(element, onchange){
		this.element = element;
		this.onchange = onchange;
        Backbone.history.start();
	},

	selectMonth: function(month,year){
		if (this.navigator == null){
			this.navigator = new DateNavigatorView(this,this.element, this.onchange, null, null, month, year);
		}
	},
	selectDay: function(){
		
	},
	selectYear: function(){
		
	},
	selectWeek: function(week, year){
		if (this.navigator == null){
			this.navigator = new DateNavigatorView(this,this.element, this.onchange, null, week, null, year);
		}
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
    
    
    render: function(){
		var template = $("#calendarSelector").html();
		var template4Week = $("#week-selector-template").html();
		var view4Week = {week : weekSelected};
		var html = Mustache.to_html(template4Week, view4Week);
		var view = {week : html, year: yearSelected};
		html = Mustache.to_html(template, view);
		$(this.el).html(html);
		return this;
	},
	refresh: function(){
		this.render();
	},
    
    
    initialize: function(router,element, onchange, day, week, month, year){
		// Start Backbone history a neccesary step for bookmarkable URL's
		var parent = this;
		this.onchange = onchange;
		this.router = router;
		this.daySelected = day;
		this.defaultDay = day;
		this.weekSelected = week;
		this.defaultWeek = week;
		this.monthSelected = month;
		this.defaultMonth = month;
		this.yearSelected = year;
		this.defaultYear = year;
		
		this.el = element;
		var imgp = document.createElement("img");
		$(imgp).attr("src", "/images/icons/previous_icon.png");
	    $(imgp).addClass("clickable");
	    $(imgp).addClass("float-left");
	    $(imgp).addClass("k-ui-left-button");

	    var span = document.createElement("span");
	    $(span).addClass("k-ui-navigator");
	    this.el.addClass("k-ui-navigator-container");
	    var imgn = document.createElement("img");
	    $(imgn).addClass("clickable");
	    $(imgn).addClass("k-ui-right-button");
	    $(imgn).attr("src", "/images/icons/next_icon.png");

	    var todayTemplate = $("#today-template").html();
        $(span).text(todayTemplate);

	    this.el.append(imgp);
	    this.el.append(span);
	    this.el.append(imgn);
	    if (day == null && week == null && month != null && year != null){
	        // Associates events for month management
	        $(imgp).bind("click", function(){parent.previousMonth(parent.monthSelected,parent.yearSelected)});
	        $(span).bind("click", function(){parent.toDefaultMonth()});
	        $(imgn).bind("click", function(){parent.nextMonth(parent.monthSelected,parent.yearSelected)});
		    this.onchange(this.monthSelected, this.yearSelected);
	    } else if (day == null && week != null && month == null && year != null){
	        // Associates events for week management
	        $(imgp).bind("click", function(){parent.previousWeek(parent.weekSelected,parent.yearSelected)});
	        $(span).bind("click", function(){parent.toDefaultWeek()});
	        $(imgn).bind("click", function(){parent.nextWeek(parent.weekSelected,parent.yearSelected)});
		    this.onchange(this.weekSelected, this.yearSelected);
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
		this.monthSelected = moment().format("M");
		this.yearSelected = moment().format("YYYY");
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
	
	nextWeek: function(){
		this.weekSelected ++;
		if(this.weekSelected == 53){
			this.weekSelected = 1;
			this.yearSelected ++;
		}
		this.actualizeWeek();
	},
	previousWeek: function(){
		this.weekSelected --;
		if(this.weekSelected == 0){
			this.weekSelected = 52;
			this.yearSelected --;
		}
		this.actualizeWeek();
	},
	toDefaultWeek:function(){
		this.weekSelected = this.defaultWeek;
		this.yearSelected = this.defaultYear;
		this.actualizeWeek();
	},
	
	actualizeMonth: function(){
		this.router.navigate("/month/" + this.monthSelected + "/" + this.yearSelected, {trigger: true, replace: true});
	    this.onchange(this.monthSelected, this.yearSelected);
	},
	
	actualizeWeek: function(){
		this.router.navigate("/week/" + this.weekSelected + "/" + this.yearSelected, {trigger: true, replace: true});
	    this.onchange(this.weekSelected, this.yearSelected);
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
	// - editable : if the lines in the table can be selected
	// - group : if this table is a part of a group of table. If group doesn't exist, it will create it.
	kernely_table: function(options){
		// Force options to be an object
		options = options || {};
		options.columns = options.columns || {};
		options.events = options.events || {};
		options.eventNames = options.eventNames || "";
		
		var table = new TableView(this);
		table.columns = options.columns;
		table.group = options.group;
		table.elements = options.elements;
		table.idField = options.idField;
		table.events = options.events;
		table.eventName = options.eventNames;
		table.editable = options.editable;
		table.render();
		return table;
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
							zIndex: 12});

			var parent = this;
			this.eventNames = options.eventNames;
			this.eventsActions = options.events;
			
			if($.isArray(this.eventNames)){
				$.each(this.eventNames, function(){
					
					if(this.lastIndexOf('.') != -1){
						var event = this.substring(0, this.lastIndexOf('.')-1);
						var element= this.substring(this.lastIndexOf('.'));
						$(parent).find(element).bind("" + event, parent.eventsActions[this]);
					}
					else{
						$(parent).find(element).bind("" + this, parent.eventsActions[this]);
					}
					
				});
			}
			else{
				if((""+this.eventNames).lastIndexOf('.') != -1){
					var event = this.eventNames.substring(0, this.eventNames.lastIndexOf('.')-1);
					var element= this.eventNames.substring(this.eventNames.lastIndexOf('.'));
					$(parent).find(element).bind("" + event, parent.eventsActions[this.eventNames]);
				}
				else{
					$(parent.el).bind("" + this.eventNames, parent.eventsActions[this.eventNames]);
				}
			}
		}
	},
	
	// Fills an element with a navigator of dates
	// The navigator is automatically created by the url :
	// - url : /#/day/D/M/Y => day navigator, where D is the day, M the month and Y the year. Not implemented yet.
	// - url : /#/month/M/Y => month navigator, where M is the month and Y the year
	// - url : /#/year/Y => year navigator, where Y is the year.  Not implemented yet.
	// - url : /#/week/w/Y => week navigator, where w is the week and Y the year
	// options can be filled with the following data :
	// - onchange : the function to call when the date change.
	//				this function will be called with a number
	//				of arguments depending of the url at the
	//				creation of the selector (month and year in
	//				a month selector).
	kernely_date_navigator: function(options){
        // Force options to be an object
        options = options || {};
        var router = new DateNavigatorRouter(this,options.onchange);
	}
	

});