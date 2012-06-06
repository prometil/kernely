AppHolidayManagerUsers = (function($){
	
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
	
	HolidayManagerUserMainView = Backbone.View.extend({
		el:"#main-manager-page-content",

		initialize: function(){
			mainView = this;
			$("#btn-planning").click(function(){
				window.location="/holiday/planning/#/month/" + monthSelected + "/" + yearSelected;
			});
		},
		
		render: function(){
			tableView = new HolidayManagerUserTableView();
			selectorView = new HolidayManagerMonthSelectorView().render();

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
	})
	
	HolidayManagerMonthSelectorView = Backbone.View.extend({
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
	
	HolidayManagerUserTableLineView = Backbone.View.extend({
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

	HolidayManagerUserTableView = Backbone.View.extend({
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
			
			lineHeader.append($("<th>", {
				class: "border-element-r-b"
			}));
			for(var i = 1; i <= this.data.nbDays; i++){
				lineHeader.append($("<th>", {
					class: 'day-header-cell border-element-r-b',
					colspan: 2,
					text: i
				}));
			}
			var thead = document.createElement("thead");
			$(thead).append(lineHeader);
			$(this.el).append($(thead));
			
			
			
			nbDays = this.data.nbDays;
			
			if(this.data.usersManaged.length > 1){
				$.each(this.data.usersManaged, function(){
                  $(parent.el).append(new HolidayManagerUserTableLineView(this).render().el);
				});
			}
			else{
				$(parent.el).append(new HolidayManagerUserTableLineView(this.data.usersManaged).render().el);
			}
			return this;
		}
	})
	
	var self = {};
	self.start = function(){
		mainView = new HolidayManagerUserMainView().render();
	}
	return self;
})

$( function() {
	console.log("Starting holiday manager users application")
	new AppHolidayManagerUsers(jQuery).start();
})