AppHolidayPlanning = (function($){
	
	// Initialize with January
	var monthSelected = 0;
	var yearSelected = 0;
	var mainView = null;
	var tableView = null;
	var legendView = null;
	var selectorView = null;
	var app_router = null;
	
	// How many days in the month
	var nbDays = 0;
	
	HolidayPlanningMainView = Backbone.View.extend({
		el:"#main-human-page-content",
		events:{
		
		},
		
		initialize: function(){
			mainView = this;
		},
		
		render: function(){
			legendView = new HolidayPlanningColorPartView();
			tableView = new HolidayPlanningTableView();
			selectorView = new HolidayPlanningMonthSelectorView().render();

			return this;
		},
		change: function(month, year){
			monthSelected = month;
			yearSelected = year;
			mainView.reloadTable(month, year);
		},
		reloadTable: function(month, year){
			$.ajax({
				url:"/holiday/planning/all",
				data: {month: month, year: year},
				dataType: "json",
				success: function(data){
					tableView.render(data);
					legendView.render(data);
					monthSelected = data.month;
					yearSelected = data.year;
				}
			});
		}
	})
	
	HolidayPlanningMonthSelectorView = Backbone.View.extend({
		el:"#monthSelector",
		render: function(){
			var selector = $("#monthSelector").kernely_date_navigator(
					{
						"onchange":mainView.change
					}
			);
			return this;
		},
	})
	
	HolidayPlanningTableLineView = Backbone.View.extend({
		tagName: "tr",
		
		user : null,
		
		events:{
		
		},
	
		initialize: function(user){
			this.user = user;
		},
		
		render: function(){
			var i = 1;
			var parent = this;
			$(this.el).append(
				$("<td>", {
					class:'row-header-user border-element-r-b',
					text: this.user.fullName
				})
			);
			
			var column1 = null;
			var column2 = null;
			for(i = 1; i<= nbDays; i ++){
				column1 = $("<td>", {
					class:'column-part-day-user border-element-r-b'
				});
				
				column2 = $("<td>", {
					class:'column-part-day-user border-element-r-b'
				});
				
				column1.css("background-color", "inherit");
				column2.css("background-color", "inherit");
				if(typeof(this.user.details) != "undefined"){
					if(this.user.details.length > 1){
						$.each(this.user.details, function(){
							if(this.dayOfMonth == i){
								if(this.am == "true"){
									
									column1.css("background-color", this.color);
								}
								if(this.pm == "true"){
									
									column2.css("background-color", this.color);
								}
							}
						});
					}
					else{
						
					}
				}
				$(parent.el).append(column1);
				$(parent.el).append(column2);
			}	
			return this;
		}
	})

	HolidayPlanningTableView = Backbone.View.extend({
		el:"#usersHoliday",
		
		data: null,
		
		events:{
		
		},
		
		initialize: function(){
		},
		render: function(data){
			this.data = data;
			$(this.el).html("");
			var parent = this;
			lineHeader = $("<tr>", {
				class:'table-header border-element-r-b'
			});
			
			lineHeader.append($("<td>", {
				class: "border-element-r-b"
			}));
			for(var i = 1; i <= this.data.nbDays; i++){
				lineHeader.append($("<td>", {
					class: 'day-header-cell border-element-r-b',
					colspan: 2,
					text: i
				}));
			}
			$(this.el).append(lineHeader);
			
			
			
			nbDays = this.data.nbDays;
			
			if(this.data.usersManaged.length > 1){
				$.each(this.data.usersManaged, function(){
                  $(parent.el).append(new HolidayPlanningTableLineView(this).render().el);
				});
			}
			else{
				$(parent.el).append(new HolidayPlanningTableLineView(this.data.usersManaged).render().el);
			}
			return this;
		}
	})
	
	HolidayPlanningColorPartView = Backbone.View.extend({
		el:"#color-legend",
		
		events:{
		
		},
		
		data : null,
		
		initialize : function(){
		},
		render: function(data){
			this.data = data;
			$(this.el).html("");
			var parent = this;
			
			if(typeof(this.data.balances) != "undefined"){
				if(this.data.balances.length > 1){
					$.each(this.data.balances, function(){
	                    $(parent.el).append(new HolidayPlanningColorCellView(this.nameOfType, this.color, this.idOfType).render().el);
					});
				}
				else{
					$(parent.el).append(new HolidayPlanningColorCellView(this.data.balances.nameOfType, this.data.balances.color, this.data.balances.idOfType).render().el);
				}
			}
			return this;
		}
	})
	
	
	HolidayPlanningColorCellView = Backbone.View.extend({
		tagName:"div",
		className: "balance-cell-legend",
		
		color:null,
		name:null,
		idType: null,
		
		events: {
		},
		
		initialize : function(name, color, idType){
			this.color = color;
			this.name = name;
			this.idType = idType;
		},
		
		render : function(){
			var template = $("#balance-cells-legend").html();
            var view = {name: this.name};
            var html = Mustache.to_html(template, view);
            $(this.el).html(html);
            $(this.el).css('background-color', this.color);
			return this;
		}
	})
	
	var self = {};
	self.start = function(){
		mainView = new HolidayPlanningMainView().render();
	}
	return self;
})

$( function() {
	console.log("Starting holiday human resource application")
	new AppHolidayPlanning(jQuery).start();
})