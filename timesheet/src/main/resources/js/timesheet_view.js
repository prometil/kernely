AppTimeSheetMonth = (function($){

	var mainView = null;
	var tableView = null;
	var monthSelected = 0;
	var yearSelected = 0;
	
	// How many days in the month
	var nbDays = 0;
	
	TimeSheetPageView = Backbone.View.extend({
		el: "#timesheet-main",
		events:{
			"click #validate-month": "validateTimeSheet"
		},
		initialize: function(){
			tableView = new TimeSheetTableView();
		},
		
		reloadCalendar: function(month, year){
			monthSelected = month;
			yearSelected = year;
			$.ajax({
				type: "GET",
				url:"/timesheet/month",
				data:{month:monthSelected, year:yearSelected},
				success: function(data){
					tableView.render(data);
				}
			})
		},
		validateTimeSheet: function(){
			console.log("VALIDATE");
			$.ajax({
				type: "GET",
				url:"/timesheet/validate",
				data:{month:monthSelected, year:yearSelected},
				success: function(data){
					$("#validate-month").addClass("hidden");
					$("#month-validated-message").removeClass("hidden");
				},
				error: function(data){
					console.log(data)
					$.writeMessage("error",$("#validation-error-template").html());
				}
			})
		}
	})
	
	TimeMonthSelectorView = Backbone.View.extend({
		el:"#monthSelector",
		render: function(){
			var selector = $("#monthSelector").kernely_date_navigator(
					{
						"onchange":mainView.reloadCalendar
					}
			);
			return this;
		},
	})
	
	TimesheetTableLineView = Backbone.View.extend({
		tagName: "tr",
		
		project : null,
		
		events:{
		},
	
		initialize: function(project){
			this.project = project;
		},
		
		render: function(){

			var i = 0;
			var parent = this;
			$(this.el).append(
				$("<td>", {
					class:'row-header-project border-element-r-b',
					text: this.project.projectName
				})
			);
			
			var column;
			for(i = 0; i< nbDays; i ++){
				
				// Filter to display only non null values
				var filteredAmount = "";
				if (parent.project.details[i].amount != 0){
					filteredAmount = parent.project.details[i].amount;
				}
				
				column = $("<td>", {
					class:'day-timesheet-planning',
					text:filteredAmount
				});
				if (parent.project.details[i].status == "half_available"){
					column.addClass("day-half-available");
				} else if (parent.project.details[i].status == "unavailable"){
					column.addClass("day-unavailable");
					column.text("");
				}
				
				$(parent.el).append(column);
			}	
			return this;
		}
	})
	
	TimesheetExpensesLineView = Backbone.View.extend({
		tagName: "tr",
		
		expenses: null,
		
		events:{
		},
	
		initialize: function(expenses){
			this.expenses = expenses;
		},
		
		render: function(){

			var i = 0;
			var parent = this;
			var html = $("#expense-line-title-template").html();
			$(this.el).append(
				$("<td>", {
					class:'row-header-project border-element-r-b',
					text: html
				})
			);
			
			var column;
			for(i = 0; i< nbDays; i ++){
				
				// Filter to display only non null values
				var filteredExpense = "";
				if (this.expenses[i] != 0){
					filteredExpense = this.expenses[i];
				}
				
				column = $("<td>", {
					class:'day-timesheet-planning',
					text:filteredExpense
				});
				$(parent.el).append(column);
			}
			
			return this;
		}
	})
	
	TimeSheetTableView = Backbone.View.extend({
		el:"#timesheet-table",
		
		data: null,
		
		events:{
		},
		
		initialize: function(){
		},
		render: function(data){
			// Refresh the month
			var text = $("#"+data.month+"-month-template").html();
			$("#dates-title").html(text+ " "+data.year);
			
			this.data = data;
			nbDays = this.data.daysOfWeek.length;
			$(this.el).empty();
			var parent = this;
			lineHeader = $("<tr>", {
				class:'table-header border-element-r-b'
			});
			
			lineHeader.append($("<th>", {
				class: "border-element-r-b"
			}));

			for(var i = 0; i < nbDays; i++){
				var day = $("#day-"+this.data.daysOfWeek[i]+"-template").html();
				lineHeader.append($("<th>", {
					class: 'day-header-cell border-element-r-b',
					html: day + " <br/> " + (i+1)
				}));
			}
			var thead = document.createElement("thead");
			$(thead).append(lineHeader);
			$(this.el).append($(thead));
			
			if (this.data.projects != null){
				if(this.data.projects.length > 1){
					$.each(this.data.projects, function(){
	                  $(parent.el).append(new TimesheetTableLineView(this).render().el);
					});
				}
				else{
					$(parent.el).append(new TimesheetTableLineView(this.data.projects).render().el);
				}
				
				// Display expenses
				$(parent.el).append(new TimesheetExpensesLineView(this.data.expenses).render().el);
				
			} else {
				var template = $("#no-timesheet").html();
				var view = {days: nbDays + 1}; // Days plus the row header
				var html = Mustache.to_html(template, view);
				$(parent.el).append(html);
			}
			
			if (this.data.validated == "false"){
				$("#validate-month").removeClass("hidden");
				$("#month-validated-message").addClass("hidden");
			} else {
				$("#validate-month").addClass("hidden");
				$("#month-validated-message").removeClass("hidden");
			}
			if (this.data.toValidate == "false"){
				$("#validate-month").attr("disabled","disabled");
			} else {
				$("#validate-month").removeAttr("disabled");
			}
			
			return this;
		}
		
	})
	
	
	// Initialization of the application
	var self = {};
	self.start = function(){
		mainView = new TimeSheetPageView();
		monthSelector = new TimeMonthSelectorView().render();
		
	}
	return self;
})

$( function() {
	console.log("Starting timesheet month application")
	new AppTimeSheetMonth(jQuery).start();
})