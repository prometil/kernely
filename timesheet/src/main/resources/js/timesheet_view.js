AppTimeSheetMonth = (function($){

	var mainView = null;
	var tableView = null;
	var monthSelected = 0;
	var yearSelected = 0;
	var daysAreFilled = null;
	var monthIsValidated = null;
	
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
					daysAreFilled = data.daysAreFilled;
					monthIsValidated = data.validated;
					console.log(data)
					tableView.render(data);
				}
			})
		},
		validateTimeSheet: function(){
			$.ajax({
				type: "GET",
				url:"/timesheet/validate",
				data:{month:monthSelected, year:yearSelected},
				success: function(data){
					$("#validate-month").addClass("hidden");
					$("#month-validated-message").removeClass("hidden");
				},
				error: function(){
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
			var totalAmount = 0.0;
			for(i = 0; i< nbDays; i ++){
				totalAmount = parseFloat(totalAmount) + parseFloat(parent.project.details[i].amount);
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
				
				if (monthIsValidated == "false" && daysAreFilled[i] == "false"){
					column.addClass("not-full-day");
				}
				
				$(parent.el).append(column);
			}
			// Total time for the project
			column = $("<td>", {
				class:"text-bold-black",
				text:totalAmount
			});
			$(parent.el).append(column);
			
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
					class:'totalMonth',
					text: html
				})
			);
			
			var column;
			var expenses = 0.0;
			for(i = 0; i< nbDays; i ++){
				expenses = parseFloat(expenses) + parseFloat(this.expenses[i]);
				
				// Filter to display only non null values
				var filteredExpense = "";
				if (this.expenses[i] != 0){
					filteredExpense = this.expenses[i];
				}
				
				column = $("<td>", {
					class:'totalMonth',
					text:filteredExpense
				});
				$(parent.el).append(column);
			}
			
			// Total expenses for the mounth
			column = $("<td>", {
				class:"totalMonth text-bold-black",
				text:expenses
			});
			$(parent.el).append(column);
			
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
			
			var html = $("#project-column-title").html();
			
			lineHeader.append($("<th>", {
				text:html,
				class: "border-element-r-b"
			}));

			for(var i = 0; i < nbDays; i++){
				var day = $("#day-"+this.data.daysOfWeek[i]+"-template").html();
				var dayClass;

				if (daysAreFilled[i] == "true" || monthIsValidated == "true"){
					dayClass = 'classical-header';
				} else {
					dayClass = 'not-full-day'
				}
				
				var th = $("<th>", {
					class:dayClass,
					html: day + " <br/> " + (i+1)
				})
				
				lineHeader.append(th);
			}
			// Total column
			var totalTemplate = $("#total-template").html();
			lineHeader.append($("<th>", {
				class: 'total',
				html: totalTemplate
			}));
			
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
				var view = {days: nbDays + 2}; // Days plus the row header plus the total header
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