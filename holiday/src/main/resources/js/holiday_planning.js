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
		
		// Mode of table : 0 for all users, 1 for only managed user
		mode:0,
		
		initialize: function(){
			mainView = this;
			$("#change-mode").click(function(){
				if(mainView.mode == 0){
					mainView.mode = 1;
					$(this).attr("value", $("#back-to-planning-template").text());
				}
				else{
					mainView.mode = 0;
					$(this).attr("value", $("#my-collaborators-template").text());
				}
				mainView.reloadTable(monthSelected, yearSelected);
			});
		},
		
		render: function(){
			tableView = new HolidayPlanningTableView();
			selectorView = new HolidayPlanningMonthSelectorView().render();

			return this;
		},
		change: function(month, year){
			monthSelected = month;
			yearSelected = year;
			mainView.reloadTable(month, year);
			
			var text = $("#"+month+"-month-template").html();
			$("#dates-title").html(text+ " "+year);
		},
		reloadTable: function(month, year){
			if(this.mode == 0){
				$.ajax({
					url:"/holiday/planning/all",
					data: {month: month, year: year},
					dataType: "json",
					success: function(data){
						tableView.render(data);
						monthSelected = data.month;
						yearSelected = data.year;
					}
				});
			}else if(this.mode == 1){
				$.ajax({
					url:"/holiday/manager/users/all",
					data: {month: month, year: year},
					dataType: "json",
					success: function(data){
						tableView.render(data);
						monthSelected = data.month;
						yearSelected = data.year;
					}
				});
			}
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
		weekEnds : null,
		
		events:{
		
		},
	
		initialize: function(user, weekends){
			this.user = user;
			this.weekEnds = weekends;
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
			
			var column;
			for(i = 1; i<= nbDays; i ++){
				
				column = $("<td>", {
					class:'day-planning'
				});
				if($.isArray(this.weekEnds)){
					$.each(this.weekEnds, function(){
						if(i == this){
							column.addClass("weekend");
						}
					});
				}
				else{
					if(i == this.weekEnds){
						column.addClass("weekend");
					}
				}
				if(typeof(this.user.details) != "undefined"){
					if(this.user.details.length > 1){
						$.each(this.user.details, function(){
							if(this.dayOfMonth == i){
								if(this.am == "true" && this.pm == "false"){

									column.addClass("am-day");
									if(column.hasClass("pm-day")){
										column.css("background-image", "url(/images/day_taken.png)");
									}else{
										column.css("background-image", "url(/images/am_taken.png)");
										
									}
								}
								else if(this.pm == "true" && this.am == "false"){
									column.addClass("pm-day");
									if(column.hasClass("am-day")){
										column.css("background-image", "url(/images/day_taken.png)");
									}
									else{
										column.css("background-image", "url(/images/pm_taken.png)");
									}
								}
								else if(this.am == "true" && this.pm == "true"){
									column.css("background-image", "url(/images/day_taken.png)");
								}
							}
						});
					}
				}
				$(parent.el).append(column);
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
			$(this.el).empty();
			var parent = this;
			lineHeader = $("<tr>", {
				class:'table-header border-element-r-b'
			});
			
			lineHeader.append($("<th>", {
				class: "border-element-r-b"
			}));
			for(var i = 1; i <= this.data.nbDays; i++){
				lineHeader.append($("<th>", {
					class: 'day-header-cell border-element-r-b',
					text: i
				}));
			}
			var thead = document.createElement("thead");
			$(thead).append(lineHeader);
			$(this.el).append($(thead));
			
			
			
			nbDays = this.data.nbDays;
			var weekEnds = this.data.weekends;
			
			if(this.data.usersManaged.length > 1){
				$.each(this.data.usersManaged, function(){
                  $(parent.el).append(new HolidayPlanningTableLineView(this, weekEnds).render().el);
				});
			}
			else{
				$(parent.el).append(new HolidayPlanningTableLineView(this.data.usersManaged, weekEnds).render().el);
			}
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