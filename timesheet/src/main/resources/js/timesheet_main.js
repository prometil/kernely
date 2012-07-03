AppTimeSheet = (function($){

	var MAX_VALUE = 7;
	var mainView = null;
	var calendar = null;
	var weekSelector = null;
	var currentCellPickerSelected = null;
	var allCellPicker = new Object();
	//The last day cell clicked (used for the shift + clic function)
	var lastClicked = null;
	var allDayCells = new Object();
	var allProjectRows = new Object();
	var nbSelected = 0;
	var shifted = false;
	var weekSelected = 0;
	var yearSelected = 0;
	var timeSheetId = 0;
	var allProjectsList = 0;
	var lastWeekProjectsIdList = 0;
	var dayStatus = null;
	var availableDates = null;
	
	var tableExpenseView = null;
	var expense = null;
	var expenseMainView = null;

	TimeSheetPageView = Backbone.View.extend({
		el:"#timesheet-main",
		dates: null,
		events:{
			"click #add-project-button" : "addProject",
			"click #add-last-projects-button" : "addLastWeekProjects"
		},
		initialize: function(){
			mainView = this;
			new TimePicker().render();
			weekSelector = new TimeWeekSelectorView().render();
		},
		
		addProject: function(){
			var projectName = $("#project-select option:selected").text();
			var projectId = $("#project-select option:selected").val();
			this.addProjectProcess(projectName,projectId);
		},
		
		addLastWeekProjects: function(){
			var parent = this;
			if ($.isArray(lastWeekProjectsIdList)){
				$.each(lastWeekProjectsIdList, function(){
					// Fill a week with 0 amount of time
					var projectName = $("#project-select :first option[value='"+parseInt(this)+"']").text();
					var projectId = this;
					parent.addProjectProcess(projectName,projectId);
				});
			} else if (lastWeekProjectsIdList != 0){
				// Fill a week with 0 amount of time
				var projectName = $("#project-select :first option[value='"+lastWeekProjectsIdList+"']").text();
				var projectId = lastWeekProjectsIdList;
				this.addProjectProcess(projectName,projectId);
			}
		},

		addProjectProcess: function(projectName,projectId){
			if (projectName != null && projectName != ""){
				var amounts = new Array();
				for (var i = 0 ; i < 7 ; i++){
					amounts.push(0.0);
				}
				calendar.addRow(projectName,projectId,amounts,true);
			}
		},
		
		searchProject: function(id){
			var result;
			$.each(allProjectsList, function(){
				if (id == this.id){
					result = this;
					return;
				}
			});
			return result;
		},
		
		render: function(){
			return this;
		},
		reloadCalendar: function(week, year){
			weekSelected = week;
			yearSelected = year;

			// Empty the projects combo box
			$("#project-select").html("");

			// Reload projects
			$.ajax({
				type: "GET",
				url:"/project/list",
				success: function(data){

					// Create the views
					if (data != null){
						allProjectsList = data.projectDTO;
						var label = $("#all-projects-template").html();
						var toAdd = '<optgroup id="project-select-all" label="'+label+'">';
						if ($.isArray(data.projectDTO)){
							$("#project-select").removeAttr("disabled");
							$("#add-project-button").removeAttr("disabled");
							
							$.each(data.projectDTO, function(){
								toAdd += '<option value='+this.id+'>'+this.name+'</option>'
							});

						} else if (data.projectDTO != null){
							$("#project-select").removeAttr("disabled");
							$("#add-project-button").removeAttr("disabled");
							toAdd += '<option value='+data.projectDTO.id+'>'+data.projectDTO.name+'</option>'
						}
						toAdd += '</optgroup>';
						$("#project-select").append(toAdd);

					}
					$.ajax({
						type: "GET",
						url:"/timesheet/calendar",
						data:{week:weekSelected, year:yearSelected},
						success: function(data){
							MAX_VALUE = data.maxDayValue;
							availableDates = data.availableDates;
							// Set last week projects in combobox
							var label = $("#last-week-projects-template").html();
							
							var toAdd = '<optgroup id="project-select-last-week" label="'+label+'">';
							if (data.lastWeekProjectsId != null){
								lastWeekProjectsIdList = data.lastWeekProjectsId;
								if ($.isArray(data.lastWeekProjectsId)){
									$.each(data.lastWeekProjectsId, function(){
										toAdd+='<option value='+this+'>'+mainView.searchProject(this).name+'</option>';
									});

								} else if (data.lastWeekProjectsId != null){
									toAdd+='<option value='+data.lastWeekProjectsId+'>'+mainView.searchProject(data.lastWeekProjectsId).name+'</option>';
								}
							}
							toAdd += '</optgroup>';
							$("#project-select").prepend(toAdd);
							
							$('#project-select-last-week option:first-child').attr("selected","selected");
							
							if ($("optgroup#project-select-last-week option").length == 0){
								$("#add-last-projects-button").attr("disabled","disabled")
							} else {
								$("#add-last-projects-button").removeAttr("disabled")
							}

							
							// Reset display
							$("#timesheet-content").html('<tr id="date-line"></tr>');
							// Create the views
							weekSelected = data.week;
							yearSelected = data.year;
							calendar = new CalendarView(data).render();
							expense = new TimeSheetExpenseLineView(data.stringDates).render();
							expense.setTotals();
							if(data.timeSheet != null){
								timeSheetId = data.timeSheet.id;
							} else {
								timeSheetId = 0;
							}
						}
					});
				}
			});
		}
	})
		
	CalendarView = Backbone.View.extend({
		el:"#timesheet-content",
		data : null,
		rowsTotals: null,
		columnsTotals: null,
		
		events:{

		},
		initialize: function(data){
			this.data = data;
			
			// Initialize blocked days
			dayStatus = new Array();
			var atLeastOneNormal = false;
			for (var i = 0 ; i < 7 ; i++){
				if ((data.timeSheet != null) && (data.timeSheet.columns[i].day.validated == "true")){
					dayStatus[i] = "validated";
				} else {
					dayStatus[i] = "normal";
					atLeastOneNormal = true;
				}
			}
			
			// Hide or show the project selection
			if (! atLeastOneNormal){
				$("#project-select").hide();
				$("#add-project-button").hide();
			} else {
				$("#project-select").show();
				$("#add-project-button").show();
			}
			
			if (data.timeSheet != null){
				timeSheetId = data.timeSheet.id;
			} else {
				timeSheetId = 0
			}
			
			// Reset old totals
			allDayCells = new Object();
			
			// Initialize totals
			this.rowsTotals = new Object();
			this.columnsTotals = new Array();
			for (var i = 0 ; i < 7; i++){
				this.columnsTotals.push(0);
			}
		},
		
		addRow: function(projectName, projectId, amounts, empty){
			if (projectId != null && projectName != null){
				$("#timesheet-content").append(new ProjectRow(projectName, projectId,amounts,this.data.dates,empty).render().el);
				// Remove from the combobox
				$("#project-select option[value='" + projectId + "']").remove();
				// Disable buttons if needed
				if ($("#project-select option").length == 0){
					$("#project-select").attr("disabled","disabled");
					$("#add-project-button").attr("disabled","disabled");
				}
				
				if ($("optgroup#project-select-last-week option").length == 0){
					$("#add-last-projects-button").attr("disabled","disabled");
				}
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
			this.rowsTotals[projectId] = rowTotal.toFixed(2);
			for (var project in allDayCells){
				if (allDayCells[project] != null){
					columnTotal += parseFloat(allDayCells[project][index].amount);
				}
			}
			this.columnsTotals[index] = columnTotal.toFixed(2);

			// Actualize the row total
			allProjectRows[projectId].actualizeTotal(rowTotal.toFixed(2));

			// Actualize the column total
			$("#columnTotalsRow").find("td").eq(parseInt(parseInt(index)+1)).html(columnTotal.toFixed(2));
			
			this.calculateTimeSheetTotal();
			
			return columnTotal;
		},
		
		calculateAllTotals: function(){
			var allTotal = 0;
			var atLeastOneProject = false;
			for (var project in allDayCells){
				if (allDayCells[project] != null){
					atLeastOneProject = true;
					for (var i = 0; i < 7; i++){
						allTotal += this.calculateTotal(project,i);
					}
				}
			}
			if (! atLeastOneProject){
				// Set all totals to 0
				for (var i = 0; i < 8 ; i++){
					// Actualize the column total
					var cell = $("#columnTotalsRow").find("td").eq(parseInt(parseInt(i)+1));
					cell.html(0);
				}
			}
		},
		
		calculateTimeSheetTotal: function(){
			var total = 0;
			for (var i = 0; i < 7 ; i++){
				total += parseFloat(this.columnsTotals[i]);
			}
			$("#columnTotalsRow").find("td").eq(8).html(total.toFixed(2));

		},
		
		render: function(){
			var parent = this;
			$("#date-line").html("");
			var view = null;
			
			// Variables declarations :
			// List of the headers : contains days
			var headerList = new Array();

			// Build the title
			var template = $("#from-to-template").html();
			
			var view = {date_from: this.data.stringDates[0], date_to: this.data.stringDates[6]};
			var html = Mustache.to_html(template, view);
			
			$("#dates-title").html(html);
			
			// Build the header of the table
			var template = $("#project-title-template").html();
			$("#date-line").append("<td>"+template+"</td>");
			for (var i = 0 ; i < this.data.dates.length ; i++){
				$("#date-line").append($("#day-"+i+"-cell-template").html());
			}
			template = $("#total-template").text();
			$("#date-line").append("<td class='total'>"+ template +"</td>");
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
		events:{
			"click .deleteButton" : "removeLine",
		},
		initialize: function(projectName, projectId, timeSheetDays, days, empty){
			allProjectRows[projectId] = this;
			allDayCells[projectId] = this;
			
			this.projectId = projectId;
			this.projectName = projectName;
			
			allDayCells[this.projectId] = new Object();
			
			// Set the title
			$(this.el).append("<td class='text-bold-black'>" + projectName + "</td>");
			
			if (empty){
				// Create an empty day in timesheet to memorize the project
				var parent = this;
				this.total = 0;
				// Send data to server
				json = '{"index":"0","amount":"0","day":"'+days[0]
				+'","projectId":"'+this.projectId
				+'","dayId":"'+0
				+'","timeSheetId":"'+timeSheetId+'"}';
	
				$.ajax({
					type: "POST",
					url:"/timesheet/update",
					data:json,
					dataType:"json",
					async:false,
					contentType: "application/json; charset=utf-8",
					processData: false,
					success: function(data){

						// Create the first cell
						$(parent.el).append(new TimeSheetDayView(0,0,days[0], parent.projectId, data.dayId).render().el);

						// Create an empty td for each day after the first day
						for (var i = 1 ; i < 7 ; i++){
							$(parent.el).append(new TimeSheetDayView(i,0,days[i], parent.projectId, 0).render().el);
						}
						
						// Create the total cell
						$(parent.el).append('<td class="total">' + parent.total + '</td>');
						
						var buttonTemplate = $("#delete-button-template").html();
						$(parent.el).append("<td>" + buttonTemplate + "</td>");
						
						timeSheetId = data.timeSheetId;
					}
				});
			} else {
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
				$(this.el).append('<td class="total">' + this.total + '</td>');
				
				// Create the delete button only if the row can be deleted :
				// if the row is filled with undeletable data (because timesheet
				// has been validated), the delete button does'nt appears
				var deletionAllowed = true;
				for (var i = 0; i < dayStatus.length; i++){
					if (dayStatus[i] == "validated"){
						deletionAllowed = false;
					}
				}
				
				if (deletionAllowed){
					var buttonTemplate = $("#delete-button-template").html();
					$(this.el).append("<td>" + buttonTemplate + "</td>");
				}
			}
		},
		
		actualizeTotal: function(value){
			this.total = value;
			$("td.total", this.el).html(this.total);
		},
		
		removeLine : function(){
			var parent = this;
			
			var template = $("#confirm-remove-line-template").html();
			
			var view = {project: this.projectName};
			var html = Mustache.to_html(template, view);
			
			var totalRow = 0;
			for (var i = 0 ; i < 7 ; i++){
				totalRow += parseFloat(allDayCells[this.projectId][i].amount);
			}
			
			// Ask confirm only if the row is filled with data.
			if (totalRow > 0){
				$.kernelyConfirm($("#delete-template").text(), html,this.confirmRemoveLine,this);
			} else {
				this.confirmRemoveLine(this);
			}
		},
		
		confirmRemoveLine: function(parent){
			// Put the project in the combo box
			$('#project-select-all')
	          .append($('<option>', { value : parent.projectId })
	          .text(parent.projectName));

			// If needed, add the project in the last week group
			if ($.isArray(lastWeekProjectsIdList)){
				$.each(lastWeekProjectsIdList, function(){
					if (this == parent.projectId){
						$('#project-select-last-week')
				          .append($('<option>', { value : parent.projectId })
				          .text(mainView.searchProject(parent.projectId).name));
					}
				});
			} else if (lastWeekProjectsIdList != null && lastWeekProjectsIdList == parent.projectId){
				$('#project-select-last-week')
		          .append($('<option>', { value : parent.projectId })
		          .text(mainView.searchProject(parent.projectId).name));
			}
			
			$("#project-select").removeAttr("disabled");
			$("#add-project-button").removeAttr("disabled");

			// Remove from memorized data
			allDayCells[parent.projectId] = null;
			
			// Launch calcul to update columns calculs
			calendar.calculateAllTotals();
			
			// Delete line
			$(parent.el).remove();
			
			// Delete line in database: delete all amounts of time
			$.ajax({
				type: "GET",
				url:"/timesheet/removeline",
				data:{timeSheetUniqueId:timeSheetId, projectUniqueId:parent.projectId},
				success: function(data){
				}
			});

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
		week : null,
		isSelected: false,
		projectId: null,
		dayId: null,
		editMode : false,
		selectedBy: -1,
		autre:null,
		// An id only reserved to the view to allow the shift + clic event.
		viewRank: -1,
		
		events:{
			"click span" : "increment",
			"click .editButton" : "edit",
			"keypress input[type=text]": "filterOnEnter",
			"blur input[type=text]" : "onBlur",
			"mouseover" : "displayIcon",
			"mouseout" : "hideIcon"
		},
		initialize: function(index, amount, day, projectId, dayId){
			lastClicked = this;
			
			// Change display of the cell if the day has been validated
			if (dayStatus[index] == "validated"){
				$(this.el).addClass("day-validated");
			}
			if (availableDates[index] == 0){
				$(this.el).addClass("day-unavailable");
			}
			if (availableDates[index] == 0.5){
				$(this.el).addClass("day-half-available");
			}
			this.index = index;
			this.amount = amount;
			this.day = day;
			this.projectId = projectId;
			this.dayId = dayId;
			
			this.autre = dayId;
			
			// Insert into the cells array
			allDayCells[this.projectId][index] = this;
		},
		
		displayIcon: function(){
			if (! $(this.el).hasClass("day-validated") && ! $(this.el).hasClass("day-unavailable")){
				$(this.el).find(".editButton").show();
			}
		},
		
		hideIcon: function(){
			$(this.el).find(".editButton").hide();
		},
		
		increment : function(event){
			if (! this.editMode && (dayStatus[this.index] != "validated") && availableDates[this.index] > 0){
				
				var parent = this;
				if(currentCellPickerSelected != null){
					
					// Get value and increment
					var totalColumn = calendar.getColumnTotal(this.index);
					var val = parseFloat(this.amount) + parseFloat(currentCellPickerSelected.amount);
					var newTotalColumn = totalColumn + val - this.amount;
					
					// Limitation considering the column
					if (newTotalColumn > (MAX_VALUE * availableDates[this.index] )){
						if (val > this.amount){
							this.amount = parseFloat(parseFloat(this.amount) + parseFloat(MAX_VALUE * availableDates[this.index]) - parseFloat(totalColumn)).toFixed(2);
						}
					} else {
						this.amount = val;
					}
	
					this.save();
					
				}
				else{
					if(!shifted){
						// If we deselect a cell
						this.isSelected = false;
						this.selectedBy = -1;
					}
				}
				// If shift is pressed, we add data to all days between 
				if(typeof(event) != "undefined"){
					if(event.shiftKey){
						shifted = true;
						var lastIndex = lastClicked.index;
						var actualIndex = this.index;
						if (lastIndex <= this.index){
							while (lastIndex < actualIndex){
								allDayCells[this.projectId][lastIndex].increment();
								lastIndex ++;
							}
						} else {
							while (actualIndex < lastIndex){
								allDayCells[this.projectId][actualIndex].increment();
								actualIndex ++;
							}
						}
					}
				}
				shifted = false;
				lastClicked = this;
			}
		},
		
		edit: function(){
			// Save edited data
			if (this.editMode){
				// Get value and increment
				var totalColumn = calendar.getColumnTotal(this.index);
				var val = parseFloat($(".editAmount", this.el).val());
				if (isNaN(val)){
					val = 0;
				}
				if (val < 0){
					val = 0;
				}
				val = val.toFixed(2);
				
				var newTotalColumn = parseFloat(totalColumn) + parseFloat(val) - parseFloat(this.amount);

				// Limitation considering the column
				if (newTotalColumn > (MAX_VALUE * availableDates[this.index])){
					if (val > this.amount){
						this.amount = parseFloat(this.amount) + parseFloat(MAX_VALUE * availableDates[this.index]) - parseFloat(totalColumn);
					}
				} else {
					this.amount = val;
				}
				this.editMode = false;
				this.save();
			} else {
			
				// Change cell to edit mode
				this.editMode = true;
			
				// Change display
				var template = $("#detail-edit-template").html();
				var view = {amount : this.amount};
				var html = Mustache.to_html(template, view);
				$(this.el).html(html);
				$(".editAmount", this.el).focus().select();
			}
		},
		
		filterOnEnter:function(e){
			 if (e.keyCode == 13){
				this.edit();
			 }
		},
		onBlur:function(){
			this.edit();
		},
		
		save:function(){
			var parent = this;
			// Calculate total
			calendar.calculateTotal(this.projectId,this.index);
			
			// Send data to server
			json = '{"index":"'+this.index+'","amount":'+this.amount
			+',"day":"'+this.day
			+'","projectId":"'+this.projectId
			+'","dayId":"'+this.dayId
			+'","timeSheetId":"'+timeSheetId+'"}';
			$.ajax({
				type: "POST",
				url:"/timesheet/update",
				data:json,
				dataType:"json",
				contentType: "application/json; charset=utf-8",
				processData: false,
				success: function(data){
				
					parent.dayId = data.dayId;
					
					// Change display
					parent.render();
				}
			});
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
		
		events:{
		
		},
		initialize: function(days){
			// The id of the model of days are given in parameter
			this.days = days;
		},
		
		setTotals: function(){
			var parent = this;
			if (timeSheetId != 0){
				$.ajax({
					type: "GET",
					url:"/expense/totals",
					data:{idTimeSheet: timeSheetId},
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
			var title = $("#expense-line-title-template").html();
			$(this.el).html("<td>"+title+"</td>");
			parent = this;
			var i = 0;
			$.each(this.days, function(){
				if(parent.totals == null){
					$(parent.el).append(new TimeSheetExpenseCellView(parent.days[i],0.0).render().el);
				}
				else{
					if (parent.totals[i] != null){
						$(parent.el).append(new TimeSheetExpenseCellView(parent.days[i], $.round(parent.totals[i].total)).render().el);
					}
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
		
		events:{
			"click" : "editExpense"
		},
		
		initialize: function(dateString, total){
			this.day = dateString;
			this.total = total;
		},
		
		render: function(){
			$(this.el).text(this.total);
			return this;
		},
		
		editExpense: function(){
			
			var parent = this;
			$.ajax({
				type: "GET",
				url:"/timesheet/day",
				data:{day: parent.day},
				dataType:"json",
				contentType: "application/json; charset=utf-8",
				success: function(data){
					expenseMainView.render(data.id);
				}
			});
		}
	})
	
	
	ExpenseMainView = Backbone.View.extend({
		el:"#modal_window_expense",
		
		idDay : null,

		closemodal: function(){
			$("#modal_window_expense").kernely_dialog("close");
		},
		render: function(day){
			this.idDay = day;
			
			var parent = this;
			var template = $("#expense-window").html();
			var titleTemplate = $("#expense-line-title-template").html();
			$("#modal_window_expense").kernely_dialog({
				title: titleTemplate,
				content: template,
				eventNames:'click .create_expense',
				events:{
					'click .create_expense': parent.closemodal
				}
			});
			$("#modal_window_expense").kernely_dialog("open");
			
			tableExpenseView = new ExpenseTableView(this.idDay);
			tableExpenseView.render();
			new ExpenseFormView(this.idDay).render();
			return this;
		}
	})
	
	ExpenseTableView = Backbone.View.extend({
		el:"#expenses-lines",
		
		idDay : null,
		
		events:{
		
		},
		initialize: function(day){
			this.idDay = day;
		},
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/expense/all",
				data:{idDay : parent.idDay},
				dataType: "json",
				success: function(data){
					if(data != null){
						if(data.expenseDTO.length > 1){
							$.each(data.expenseDTO, function(){
								$(parent.el).append(new ExpenseTableLineView(this.id, this.amount, this.typeName, this.typeRatio, this.comment).render().el);
							});
						}
						else{
							$(parent.el).append(new ExpenseTableLineView(data.expenseDTO.id, data.expenseDTO.amount, data.expenseDTO.typeName, data.expenseDTO.typeRatio, data.expenseDTO.comment).render().el);
						}
					}
				}				
			});
			return this;
		}
	})
	
	ExpenseTableLineView = Backbone.View.extend({
		
		tagName: "tr",
		className: "expense_line",
		
		vid: null,
		vamount: null,
		vtypename: null,
		vtyperatio: null,
		vcomment: null,
	
		events:{
			"click .editLine" : "edit",
			"click .deleteLine" : "delete",
			"click .update" : "update"
		},
	
		initialize: function(id, amount, typeName, typeRatio, comment){
            this.vid = id;
            this.vamount = $.round(amount);
            this.vtypename = typeName;
            this.vtyperatio = typeRatio;
            this.vcomment = comment;
		},
		
		edit: function(){
			var parent = this;
			$.ajax({
				url:"/expense/type/all",
				dataType:"json",
				success: function(data){
					$('#expense-comment').removeAttr("disabled");
					var p = $('<div>');
					var select = $('<select>', {id: 'expense-type-select-update'});
					select.append($('<option>', { value : 0 })
				          .text(parent.vtypename + " (" + $("#actual-template").text() +")"));
					select.append($("<optgroup>").attr("label", $("#available-type-template").text()));
					if (data != null){
						if(data.expenseTypeDTO.length > 1){
							$.each(data.expenseTypeDTO, function(){
								select.append($('<option>', { value : this.id })
								          .text(this.name));
							});
						}
						else{
							select.append($('<option>', { value : data.expenseTypeDTO.id })
							          .text(data.expenseTypeDTO.name));
						}
					}
					select.append($("</optgroup>"));
					p.append(select);
					var html = '<td><input class="kernely_input" type="text" name="amount-update" id="expense-amount-update" value="'+parent.vamount+'"/></td><td>'+ p.html() +'</td><td class="text-center"><img class="update" src="/images/icons/update_icon.png"/></td><td></td>';
					$("#expense-comment").val(parent.vcomment);
			        $(parent.el).html(html);
				}
			});	
			
		},
		
		update: function(){
			var parent = this;
			var amountWithoutSpace = ($('input[name*="amount-update"]').val()).split(' ').join('');
			if(amountWithoutSpace < 100000){
				var json = '{"id":"'+ this.vid +'", "amount":"' + amountWithoutSpace + '",';
				if($("#expense-type-select-update option:selected").val() == 0){
					json += '"expenseTypeId" : "0", "typeName":"' + this.vtypename + '", "comment":"' + $('#expense-comment').val() +'", "typeRatio":"'+ this.vtyperatio +'", "timesheetDayId":"' + tableExpenseView.idDay + '"}';
				}
				else{
					json += '"expenseTypeId" : "' + $("#expense-type-select-update option:selected").val() + '", "timesheetDayId":"' + tableExpenseView.idDay + '"}';
				}	
				
				$.ajax({
					type: "POST",
					url:"/expense/create",
					data:json,
					dataType:"json",
					contentType: "application/json; charset=utf-8",
					processData: false,
					success: function(data){
						parent.vid = data.id;
						parent.vamount = $.round(data.amount);
						parent.vtypename = data.typeName;
						parent.vtyperatio = data.typeRatio;
						
						var template = '<td>{{amount}} &euro;</td><td>{{typeName}}</td><td class="text-center"><img class="editLine" src="/images/icons/edit_icon.png"/></td><td class="text-center"><img class="deleteLine" src="/images/icons/delete_icon.png"/></td>';
			            var view = {amount : parent.vamount, typeName: parent.vtypename};
			            var html = Mustache.to_html(template, view);
			            $("#expense-comment").val("");
			            $('#expense-comment').attr("disabled","disabled");
			            $(parent.el).html(html);
			            expense.setTotals();
					}
				});
			}
			else{
				$.writeMessage("error", $("#too-high-expense").text(), "#errors_message");
			}
		},
		
		delete: function(){
			var message = $("#confirm-remove-expense-line-template").html();
			var title = $("#delete-template").html();
			$.kernelyConfirm(title,message,this.confirmDelete,this);
		},
		
		confirmDelete: function(parent){
			$.ajax({
				type: "GET",
				url:"/expense/delete",
				data:{idExpense: parent.vid},
				success: function(){
					$(parent.el).remove();
		            expense.setTotals();
				}
			});
		},
		
		render:function(){
            var template = '<td>{{amount}} &euro;</td><td>{{typeName}}</td><td class="text-center"><img class="editLine" src="/images/icons/edit_icon.png"/></td><td class="text-center"><img class="deleteLine" src="/images/icons/delete_icon.png" /></td>';
            var view = {amount : this.vamount, typeName: this.vtypename};
            var html = Mustache.to_html(template, view);
            
            $(this.el).html(html);
            return this;
		}		
	})
	
	ExpenseFormView = Backbone.View.extend({
		el:"#expense-form",
		
		idDay : null,
		
		events:{
			"click #submit-expense" : "registerExpense"
		},
		
		initialize: function(day){
			this.idDay = day;
		},
		
		registerExpense: function(){
			var amountWithoutSpace = ($('input[name*="amount"]').val()).split(' ').join('');
			if(amountWithoutSpace < 100000){
			
				var json = '{"id":"0", "amount":"' + amountWithoutSpace + '", "comment":"' + $('#expense-comment').val() + '", "expenseTypeId" : "' + $("#expense-type-select option:selected").val() + '", "timesheetDayId":"' + this.idDay + '"}';
				$.ajax({
					type: "POST",
					url:"/expense/create",
					data:json,
					dataType:"json",
					contentType: "application/json; charset=utf-8",
					processData: false,
					success: function(data){
						timeSheetId = data.associatedTimeSheetId;
						$(tableExpenseView.el).append(new ExpenseTableLineView(data.id, data.amount, data.typeName, data.typeRatio).render().el);
						$('input[name*="amount"]').val("");
						$('input[name*="amount"]').focus();
			            expense.setTotals();
					}
				});
			}
			else{
				$.writeMessage("error", $("#too-high-expense").text(), "#errors_message");
			}
		},
		
		render: function(){
			$.ajax({
				url:"/expense/type/all",
				dataType:"json",
				success: function(data){
					if (data !=null){
						if(data.expenseTypeDTO.length > 1){
							$.each(data.expenseTypeDTO, function(){
								$("#expense-type-select").append($('<option>', { value : this.id })
								          .text(this.name));
							});
						}
						else{
							$("#expense-type-select").append($('<option>', { value : data.expenseTypeDTO.id })
							          .text(data.expenseTypeDTO.name));
						}
					}
				}
			});
			return this;
		}
	})
	
	TimeWeekSelectorView = Backbone.View.extend({
		el:"#weekSelector",
		render: function(){
			var selector = $("#weekSelector").kernely_date_navigator(
					{
						"onchange":mainView.reloadCalendar
					}
			);
			return this;
		},
	})
	
	TimePicker = Backbone.View.extend({
		el:"#timePicker",
		events:{
		
		},
		
		times: [8,6,4,2,1,0.5,0.25],
		
		initialize : function(){
		},
		render: function(){

			var parent = this;
			
			$.each(this.times, function(){
                 $(parent.el).append(new TimePickerCell(this).render().el);
			});

			return this;
		}
	})
	
	TimePickerCell = Backbone.View.extend({
		tagName:"div",
		className: "time-cell",
		
		amount: null,
		
		events: {
			"click" : "selectTime"
		},
		
		initialize : function(amount){
			this.amount = amount;
		},
		
		render : function(){

			var template;
            var view;
            var readableAmount;
            if (this.amount == 0.25){
            	readableAmount = $("#time-amount-025").html();
            } else if (this.amount == 0.5){
            	readableAmount = $("#time-amount-05").html();
            } else {
            	readableAmount = $("#time-amount-"+this.amount).html();
            }
			template = $("#time-cell-template").html();
			view =  {amount: readableAmount};
            var html = Mustache.to_html(template, view);
            $(this.el).html(html);

			return this;
		},
		unselect : function(){
			$(this.el).removeClass("time-cell-selected");
		},
		selectTime : function(){
			$(".time-cell-selected").removeClass("time-cell-selected");
			$(this.el).addClass("time-cell-selected");
			currentCellPickerSelected = this;
		},
		
	})

	// Initialization of the application
	var self = {};
	self.start = function(){
		mainView = new TimeSheetPageView().render();
		expenseMainView = new ExpenseMainView();
	}
	return self;
})

$( function() {
	console.log("Starting timesheet application")
	new AppTimeSheet(jQuery).start();
})