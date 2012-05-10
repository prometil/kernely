AppTimeSheetMonth = (function($){

	
	var MAX_VALUE = 8;
	var mainView = null;
	var calendar = new Array();
	var monthSelector = null;
	var allCellPicker = new Object();
	//The last day cell clicked (used for the shift + clic function)
	var lastClicked = null;
	var allDayCells = new Object();
	var allProjectRows = new Object();
	var nbSelected = 0;
	var shifted = false;
	var monthSelected = 0;
	var yearSelected = 0;
	var timeSheetId = new Array();
	
	var expense = new Array();
	var expenseMainView = null;

	TimeSheetPageView = Backbone.View.extend({
		el:"#timesheet-main",
		dates: null,
		events: {
			"click #validate-month" : "validate"
		},
		
		validate: function(){
			$.ajax({
				type: "GET",
				url:"/timesheet/validate",
				data:{month: monthSelected, year: yearSelected},
				success: function(data){
					// Update validate button
					$("#validate-month").addClass("hidden");
					$("#month-validated-message").removeClass("hidden");
				}
			});
		},
		
		render: function(month,year){
			monthSelected = month;
			yearSelected = year;
			// Delete old data
			allProjectRows = new Object();
			timeSheetId = new Array();
			expense = new Array();
			allDayCells = new Object();
			$("#timesheet-div").html("");
		
			$.ajax({
				type: "GET",
				url:"/timesheet/month",
				data:{month:monthSelected, year:yearSelected},
				success: function(data){
					// Create the views
					monthSelected = data.month;
					yearSelected = data.year;					
					
					// Update validate button
					if (data.validated == "true"){
						$("#validate-month").addClass("hidden");
						$("#month-validated-message").removeClass("hidden");
					} else {
						$("#validate-month").removeClass("hidden");
						$("#month-validated-message").addClass("hidden");
					}
					
					$.each(data.calendars, function(index){

						// Create the table
						var template = $("#timesheet-table-template").html();
						var view = {tableId: index};
						var html = Mustache.to_html(template, view);
						$("#timesheet-div").append(html)
						
						// Insert the calendar
						calendar.push(new CalendarView(this,index).render());
						var calendarExpense = new TimeSheetExpenseLineView(this.stringDates, index).render();
						if(this.timeSheet != null){
							timeSheetId.push(this.timeSheet.id);
						} else {
							timeSheetId.push(0);
						}

						// Calculate expense totals
						calendarExpense.setTotals();
						expense.push(calendarExpense);

					});

				}
			});
			return this;
		}
	}),
		
	CalendarView = Backbone.View.extend({
		el:"div",
		data : null,
		rowsTotals: null,
		columnsTotals: null,
		calendarIndex: null,
		projectsId: null,
		
		initialize: function(data,calendarIndex){
			this.calendarIndex = calendarIndex;
			this.data = data;
			
			// Initialize totals
			this.rowsTotals = new Object();
			this.columnsTotals = new Array();
			for (var i = 0 ; i < 7; i++){
				this.columnsTotals.push(0);
			}
		},
		
		addRow: function(projectName, projectId, amounts){
			if (projectId != null && projectName != null){
				$("#timesheet-content-"+this.calendarIndex).append(new ProjectRow(projectName, projectId,amounts,this.data.dates).render().el);
			}
		},
		
		getColumnTotal: function(index){
			var columnTotal = 0;
			for (var project in allDayCells){
				if (allDayCells[project] != null){
					columnTotal += parseFloat(allDayCells[project][index].amount);
				}
			}
			return columnTotal;
		},
		
		calculateTotal: function(projectId,index){
			var rowTotal = 0;
			var columnTotal = 0;
			if (allDayCells[projectId] != null){
				for (var i = 0; i < 7; i++){
					rowTotal += parseFloat(allDayCells[projectId][i].amount);
				}
			}
			this.rowsTotals[projectId] = rowTotal;
			for (var project in allDayCells){
				if (allDayCells[project] != null){
					columnTotal += parseFloat(allDayCells[project][index].amount);
				}
			}
			this.columnsTotals[index] = columnTotal;
			
			// Actualize the row total
			allProjectRows[projectId].actualizeTotal(rowTotal);

			// Actualize the column total
			$("#columnTotalsRow-"+this.calendarIndex).find("td").eq(parseInt(parseInt(index)+1)).html(columnTotal);
			
			this.calculateTimeSheetTotal();
			
			return columnTotal;
		},
		
		calculateAllTotals: function(){
			var allTotal = 0;
			var atLeastOneProject = false;
			
			if (! $.isArray(this.data.projectsId) && this.data.projectsId != null){
				var array = new Array();
				array.push(this.data.projectsId);
				this.data.projectsId = array;
			}
			
			if (this.data.projectsId != null){
				var i;
				for (i = 0; i < this.data.projectsId.length ; i++){
					var projectId = this.data.projectsId[i];
					atLeastOneProject = true;
					if (allDayCells[projectId] != null){
						for (var j = 0; j < 7; j++){
							allTotal += this.calculateTotal(projectId,i);
						}
					}
				}
			}
			
			if (! atLeastOneProject){
				// Set all totals to 0
				for (var i = 0; i < 8 ; i++){
					// Actualize the column total
					$("#columnTotalsRow").find("td").eq(parseInt(parseInt(i)+1)).html(0);
				}
			}
		},
		
		calculateTimeSheetTotal: function(){
			var total = 0;
			for (var i = 0; i < 7 ; i++){
				total += this.columnsTotals[i];
			}
			$("#columnTotalsRow-"+this.calendarIndex).find("td").eq(8).html(total);

		},
		
		render: function(){
			var parent = this;
			$("#date-line-"+this.calendarIndex).html("");
			var view = null;
			
			// Variables declarations :
			// List of the headers : contains days
			var headerList = new Array();

			// Build the header
			var template = $("#project-title-template").html();
			$("#date-line-"+this.calendarIndex).append("<td>"+template+"</td>");
			for (var i = 0 ; i < this.data.dates.length ; i++){
				$("#date-line-"+this.calendarIndex).append("<td>" + this.data.stringDates[i] + "</td>");
			}

			// Build projects rows only if the timesheet exists
			if (this.data.timeSheet != null){
				// Build rows with data
				if ($.isArray(this.data.projectsId)){
					for (var i in this.data.projectsId){
						var id = this.data.projectsId[i];
						var timeSheetDetails = new Array();
						var projectName;
						// Each projectId build a row
						// Search for details linked to the projectId
						for (var column in this.data.timeSheet.columns){
							if (this.data.timeSheet.columns[column].timeSheetDetails != null){
								if (this.data.timeSheet.columns[column].timeSheetDetails.length == null){
									if (this.data.timeSheet.columns[column].timeSheetDetails.projectId == id){
										// The detail matches the project id
										timeSheetDetails.push(this.data.timeSheet.columns[column].timeSheetDetails);
										projectName = this.data.timeSheet.columns[column].timeSheetDetails.projectName;
									}
									
								} else {
									for (var detail in this.data.timeSheet.columns[column].timeSheetDetails){
										if (this.data.timeSheet.columns[column].timeSheetDetails[detail].projectId == id){
											// The detail matches the project id
											timeSheetDetails.push(this.data.timeSheet.columns[column].timeSheetDetails[detail]);
											projectName = this.data.timeSheet.columns[column].timeSheetDetails[detail].projectName;
										}
									}
								}
							}
						}
						parent.addRow(projectName, id, timeSheetDetails, false);
					}
				} else {
					var id = this.data.projectsId;
					var timeSheetDetails = new Array();
					var projectName;
					// Each projectId build a row
					// Search for details linked to the projectId
					for (var column in this.data.timeSheet.columns){
						if (this.data.timeSheet.columns[column].timeSheetDetails != null){
							if (this.data.timeSheet.columns[column].timeSheetDetails.length == null){
								if (this.data.timeSheet.columns[column].timeSheetDetails.projectId == id){
									// The detail matches the project id
									timeSheetDetails.push(this.data.timeSheet.columns[column].timeSheetDetails);
									projectName = this.data.timeSheet.columns[column].timeSheetDetails.projectName;
								}
								
							} else {
								for (var detail in this.data.timeSheet.columns[column].timeSheetDetails){
									if (this.data.timeSheet.columns[column].timeSheetDetails[detail].projectId == id){
										// The detail matches the project id
										timeSheetDetails.push(this.data.timeSheet.columns[column].timeSheetDetails[detail]);
										projectName = this.data.timeSheet.columns[column].timeSheetDetails[detail].projectName;
									}
								}
							}
						}
					}
					parent.addRow(projectName, id, timeSheetDetails, false);
				}
				
			}
			
		
			this.calculateAllTotals();
			return this;
		}
	}),
	
	ProjectRow = Backbone.View.extend({
		tagName: "tr",
		projectId: null,
		projectName: null,
		total:0,
		initialize: function(projectName, projectId, timeSheetDays, days){
			allProjectRows[projectId] = this;
			
			this.projectId = projectId;
			this.projectName = projectName;
			
			allDayCells[this.projectId] = new Object();
			
			// Set the title
			$(this.el).append('<td class="projectTitle">' + projectName + '</td>');
			// Create a td for each day
			for (var i = 0 ; i < 7 ; i++){

				var found  = false;
				// Search for an existing timeSheetDay
				if(! $.isArray(timeSheetDays)){
					if (timeSheetDays.index == i){
						$(this.el).append(
								new TimeSheetDayView(timeSheetDays.index,
										timeSheetDays.amount,
										timeSheetDays.day,
										timeSheetDays.projectId,
										timeSheetDays.dayId).render().el
						);
						found = true;
					}
				} else {
					for (var j = 0 ; j < timeSheetDays.length ; j++){
						
						if (timeSheetDays[j].index == i){

							$(this.el).append(
									new TimeSheetDayView(timeSheetDays[j].index,
											timeSheetDays[j].amount,
											timeSheetDays[j].day,
											timeSheetDays[j].projectId,
											timeSheetDays[j].dayId).render().el
							);
							found = true;
						}
					}
				}
				if (! found){
					$(this.el).append(new TimeSheetDayView(i,0,days[i], this.projectId, 0).render().el);
				}
			}

			// Create the total cell
			$(this.el).append('<td class="columnTotal">' + this.total + '</td>');
		},
		
		actualizeTotal: function(value){
			this.total = value;
			$("td.columnTotal", this.el).html(this.total);
		},

		render: function(){
			return this;
		}
	}),
	
	TimeSheetDayView = Backbone.View.extend({
		tagName: "td",
		className: "timeSheetDetail",

		day : null,
		index:null,
		amount : null,
		isSelected: false,
		projectId: null,
		dayId: null,
		editMode : false,
		selectedBy: -1,
		autre:null,
		// An id only reserved to the view to allow the shift + clic event.
		viewRank: -1,
		initialize: function(index, amount, day, projectId, dayId){
			lastClicked = this;
			
			this.index = index;
			this.amount = amount;
			this.day = day;
			this.projectId = projectId;
			this.dayId = dayId;
			
			this.autre = dayId;
			
			// Insert into the cells array
			allDayCells[this.projectId][index] = this;
		},
		
		render: function(){
			var template = $("#detail-template").html();
			if (this.amount == 0){
				var view = {amount : ""};
			} else {
				var view = {amount : parseFloat(this.amount)};
			}
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			
			return this;
		}

	})
	
	//=========================================//
	//			EXPENSE MANAGEMENT			   //
	//=========================================//
	
	TimeSheetExpenseLineView = Backbone.View.extend({
		el:"#expense-line",
		
		days : null,
		totals : null,
		calendarIndex : null,

		initialize: function(days, calendarIndex){
			this.el = $("#expense-line-"+calendarIndex);
			// The id of the model of days are given in parameter
			this.days = days;
			this.calendarIndex = calendarIndex;
		},
		
		setTotals: function(){
			var parent = this;
			if (timeSheetId[this.calendarIndex] != 0){
				$.ajax({
					type: "GET",
					url:"/expense/totals",
					data:{idTimeSheet: timeSheetId[parent.calendarIndex]},
					success: function(data){
						parent.totals = data.totalExpenseDTO;
						parent.render();
					}
				});
			} else {
				parent.render();
			}
		},
		
		render: function(){
			$(this.el).html('<td class="expense_cell"></td>');
			parent = this;
			var i = 0;
			$.each(this.days, function(){
				if(parent.totals == null){
					$(parent.el).append(new TimeSheetExpenseCellView(parent.days[i],0.0).render().el);
				}
				else{
					$(parent.el).append(new TimeSheetExpenseCellView(parent.days[i], parent.totals[i].total).render().el);
				}
				
				i++;
			});
			return this;
		}
	})
	
	TimeSheetExpenseCellView = Backbone.View.extend({
		tagName:"td",
		className:"expense_cell",
		
		day : null,
		total : 0,
		
		initialize: function(dateString, total){
			this.day = dateString;
			this.total = total;
		},
		
		render: function(){
			$(this.el).text(this.total);
			return this;
		},
	})
	
	TimeMonthSelectorView = Backbone.View.extend({
		el:"#monthSelector",
		render: function(){
			console.log("RENDER")
			var selector = $("#monthSelector").kernely_date_navigator(
					{
						"onchange":mainView.render
					}
			);
			return this;
		},
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