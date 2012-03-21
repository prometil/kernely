AppTimeSheet = (function($){

	
	var MAX_VALUE = 8;
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

	
	var tableExpenseView = null;
	var expense = null;
	var expenseMainView = null;

	TimeSheetPageView = Backbone.View.extend({
		el:"#timesheet-main",
		dates: null,
		events:{
			"click #add-project-button" : "addProject",
		},
		initialize: function(){
			new TimePicker().render();
			weekSelector = new TimeWeekSelectorView().render();
			$.ajax({
				type: "GET",
				url:"/project/list",
				success: function(data){
					// Create the views
					if (data != null){
						if ($.isArray(data.projectDTO)){
							$.each(data.projectDTO, function(){
								$('#project-select')
						          .append($('<option>', { value : this.id })
						          .text(this.name));
							});

						} else if (data.projectDTO != null){
						     $('#project-select')
					          .append($('<option>', { value : data.projectDTO.id })
					          .text(data.projectDTO.name));
						}
					}
					$.ajax({
						type: "GET",
						url:"/timesheet/calendar",
						data:{week:weekSelected, year:yearSelected},
						success: function(data){
							// Reset display
							$("#timesheet-content").html('<tr id="date-line"></tr>');
							
							// Create the views
							weekSelected = data.timeSheet.week;
							yearSelected = data.timeSheet.year;
							weekSelector.refresh();
							calendar = new CalendarView(data).render();
							expense = new TimeSheetExpenseLineView(data.stringDates).render();
							if(data.timeSheet != null){
								timeSheetId = data.timeSheet.id;
								expense.setTotals();
							}
						}
					});
				}
			});
		},
		addProject: function(){
			var amounts = new Array();
			// Fill a week with 0 amount of time
			for (var i = 0 ; i < 7 ; i++){
				amounts.push(0.0);
			}
			calendar.addRow($("#project-select option:selected").text(),$("#project-select option:selected").val(),amounts,true);

		},
		render: function(){
			return this;
		},
		reloadCalendar: function(){
			// Empty the projects combo box
			$("#project-select").html("");
			
			// Reload projects
			$.ajax({
				type: "GET",
				url:"/project/list",
				success: function(data){
					// Create the views
					if (data != null){
						if ($.isArray(data.projectDTO)){
							$("#project-select").removeAttr("disabled");
							$("#add-project-button").removeAttr("disabled");
						
							$.each(data.projectDTO, function(){
								$('#project-select')
						          .append($('<option>', { value : this.id })
						          .text(this.name));
							});

						} else if (data.projectDTO != null){
							$("#project-select").removeAttr("disabled");
							$("#add-project-button").removeAttr("disabled");
						     $('#project-select')
					          .append($('<option>', { value : data.projectDTO.id })
					          .text(data.projectDTO.name));
						}
					}
					$.ajax({
						type: "GET",
						url:"/timesheet/calendar",
						data:{week:weekSelected, year:yearSelected},
						success: function(data){
							// Reset display
							$("#timesheet-content").html('<tr id="date-line"></tr>');
							
							// Create the views
							weekSelected = data.timeSheet.week;
							yearSelected = data.timeSheet.year;
							weekSelector.refresh();
							calendar = new CalendarView(data).render();
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
			timeSheetId = data.timeSheet.id;
			
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
				if ($("#project-select option").length == 0){
					$("#project-select").attr("disabled","disabled");
					$("#add-project-button").attr("disabled","disabled");
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
			$("#columnTotalsRow").find("td").eq(parseInt(parseInt(index)+1)).html(columnTotal);
			
			this.calculateTimeSheetTotal();
			
			return columnTotal;
		},
		
		calculateAllTotals: function(){
			var allTotal = 0;
			for (var project in allDayCells){
				if (allDayCells[project] != null){
					for (var i = 0; i < 7; i++){
						allTotal += this.calculateTotal(project,i);
					}
				}
			}
		},
		
		calculateTimeSheetTotal: function(){
			var total = 0;
			for (var i = 0; i < 7 ; i++){
				total += this.columnsTotals[i];
			}
			$("#columnTotalsRow").find("td").eq(8).html(total);
		},
		
		render: function(){
			var parent = this;
			$("#date-line").html("");
			var view = null;
			
			// Variables declarations :
			// List of the headers : contains days
			var headerList = new Array();

			// Build the header
			var template = $("#project-title-template").html();
			$("#date-line").append("<td>"+template+"</td>");
			for (var i = 0 ; i < this.data.dates.length ; i++){
				$("#date-line").append("<td>" + this.data.stringDates[i] + "</td>");
			}
			var projectsIdList = new Array();
			
			// Search the list of projects in the timesheet
			for (var i = 0 ; i < this.data.timeSheet.columns.length ; i ++){
				if (this.data.timeSheet.columns[i].timeSheetDetails != null){
					if (this.data.timeSheet.columns[i].timeSheetDetails.length == null){
						var detail = this.data.timeSheet.columns[i].timeSheetDetails;
						// Add the project id if needed
						if ($.inArray(detail.projectId, projectsIdList) == -1){
							projectsIdList.push(detail.projectId);
						}						
					} else {
						for (var j = 0; j < this.data.timeSheet.columns[i].timeSheetDetails.length ; j++){
							var detail = this.data.timeSheet.columns[i].timeSheetDetails[j];

							// Add the project id if needed
							if ($.inArray(detail.projectId, projectsIdList) == -1){
								projectsIdList.push(detail.projectId);
	
							}
						}
					}
				}
			}

			// Build rows with data
			for (var i in projectsIdList){
				var id = projectsIdList[i];
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
			
			this.projectId = projectId;
			this.projectName = projectName;
			
			allDayCells[this.projectId] = new Object();
			
			// Set the title
			$(this.el).append("<td>" + projectName + "</td>");
			
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
						$(parent.el).append('<td class="columnTotal">' + parent.total + '</td>');
						
						// Create the delete button
						var buttonTemplate = $("#delete-button-template").html();
						$(parent.el).append("<td>" + buttonTemplate + "</td>");

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
				$(this.el).append('<td class="columnTotal">' + this.total + '</td>');
				
				// Create the delete button
				var buttonTemplate = $("#delete-button-template").html();
				$(this.el).append("<td>" + buttonTemplate + "</td>");

			}
		},
		
		actualizeTotal: function(value){
			this.total = value;
			$("td.columnTotal", this.el).html(this.total);
		},
		
		removeLine : function(){
			var parent = this;
			
			var template = $("#confirm-remove-line-template").html();
			
			var view = {project: this.projectName};
			var html = Mustache.to_html(template, view);
			
			var answer = confirm(html);
			if (answer){
				// Put the project in the combo box
				$('#project-select')
		          .append($('<option>', { value : this.projectId })
		          .text(this.projectName));

				$("#project-select").removeAttr("disabled");
				$("#add-project-button").removeAttr("disabled");

				// Remove from memorized data
				allDayCells[this.projectId] = null;
				
				// Launch calcul to update columns calculs
				calendar.calculateAllTotals();
				
				// Delete line
				$(this.el).remove();
				
				// Delete line in database: delete all amounts of time
				$.ajax({
					type: "GET",
					url:"/timesheet/removeline",
					data:{timeSheetUniqueId:timeSheetId, projectUniqueId:parent.projectId},
					success: function(data){
					}
				});
			}
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
			"blur input[type=text]" : "onBlur"
		},
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
		
		increment : function(event){
			if (! this.editMode){
				
				var parent = this;
				if(currentCellPickerSelected != null){
					// Get value and increment
					var totalColumn = calendar.getColumnTotal(this.index);
					var val = parseFloat(this.amount) + parseFloat(currentCellPickerSelected.amount);
					var newTotalColumn = totalColumn + val - this.amount;
					
					// Limitation considering the column
					if (newTotalColumn > MAX_VALUE){
						if (val > this.amount){
							this.amount = parseFloat(this.amount) + (MAX_VALUE - totalColumn);
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
				var newTotalColumn = totalColumn + val - this.amount;
				// Limitation considering the column
				if (newTotalColumn > MAX_VALUE){
					if (val > this.amount){
						this.amount += (MAX_VALUE - totalColumn);
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
			$(this.el).css("background-color", "#FAB600");
			
		},
		
		setTotals: function(){
			var parent = this;
			$.ajax({
				type: "GET",
				url:"/expense/totals",
				data:{idTimeSheet: timeSheetId},
				success: function(data){
					parent.totals = data.totalExpenseDTO;
					parent.render();
				}
			});
		},
		
		render: function(){
			$(this.el).html("<td></td>");
			parent = this;
			var i = 0;
			$.each(this.days, function(){
				if(parent.totals == null){
					$(parent.el).append(new TimeSheetExpenseCellView(parent.days[i]).render().el);
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
		
		events:{
			"click .closeModal" : "closemodal",
			"click .create_expense" : "closemodal",
			
		},
	
		initialize: function(){
		},
		
		showModalWindow: function(){
			//Get the screen height and width
       		var maskHeight = $(window).height();
       		var maskWidth = $(window).width();


            //Set height and width to mask to fill up the whole screen
            $('#mask').css({'width':maskWidth,'height':maskHeight});

            //transition effect    
            $('#mask').fadeIn(500);   
            $('#mask').fadeTo("fast",0.7); 

            //Get the window height and width
            var winH = $(window).height();
            var winW = $(window).width();


        	//Set the popup window to center
       		$("#modal_window_expense").css('top',  winH/2-$("#modal_window_expense").height()/2);
     		$("#modal_window_expense").css('left', winW/2-$("#modal_window_expense").width()/2);
     		$("#modal_window_expense").css('background-color', "#EEEEEE");
     		$("input:text").each(function(){this.value="";});
     		//transition effect
     		$("#modal_window_expense").fadeIn(500);
		},
		
		closemodal: function(){
			$('#modal_window_expense').hide();
       		$('#mask').hide();
       		expense.setTotals();
		},
		
		render: function(day){
			this.idDay = day;
			this.showModalWindow();
			var html = $("#expense-window").html();
			$(this.el).html(html);
			
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
								$(parent.el).append(new ExpenseTableLineView(this.id, this.amount, this.typeName, this.typeRatio).render().el);
							});
						}
						else{
							$(parent.el).append(new ExpenseTableLineView(data.expenseDTO.id, data.expenseDTO.amount, data.expenseDTO.typeName, data.expenseDTO.typeRatio).render().el);
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
	
		events:{
			"click .editLine" : "edit",
			"click .deleteLine" : "delete",
			"click .update" : "update"
		},
	
		initialize: function(id, amount, typeName, typeRatio){
            this.vid = id;
            this.vamount = amount;
            this.vtypename = typeName;
            this.vtyperatio = typeRatio;
		},
		
		edit: function(){
			var parent = this;
			$.ajax({
				url:"/expense/type/all",
				dataType:"json",
				success: function(data){
					var p = $('<div>');
					var select = $('<select>', {id: 'expense-type-select-update'});
					select.append($('<option>', { value : 0 })
				          .text(parent.vtypename));
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
					
					p.append(select);
					var html = '<td><input type="text" name="amount-update" id="expense-amount-update" value="'+parent.vamount+'"/></td><td>'+ p.html() +'</td><td><span class="update">Edit</span></td><td></td>';
			        $(parent.el).html(html);
				}
			});	
			
		},
		
		update: function(){
			var parent = this;
			var json = '{"id":"'+ this.vid +'", "amount":"' + $('input[name*="amount-update"]').val() + '",';
			if($("#expense-type-select-update option:selected").val() == 0){
				json += '"expenseTypeId" : "0", "typeName":"' + this.vtypename + '", "typeRatio":"'+ this.vtyperatio +'", "timesheetDayId":"' + tableExpenseView.idDay + '"}';
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
					parent.vamount = data.amount;
					parent.vtypename = data.typeName;
					parent.vtyperatio = data.typeRatio;
					
					var template = '<td>{{amount}} &euro;</td><td>{{typeName}}</td><td><span class="editLine">Edit</span></td><td><span class="deleteLine">Delete</span></td>';
		            var view = {amount : parent.vamount, typeName: parent.vtypename};
		            var html = Mustache.to_html(template, view);
		            
		            $(parent.el).html(html);
				}
			});
		},
		
		delete: function(){
			var parent = this;
			var answer = confirm("Are you sure ?"); // To modify
			if (answer){
				$.ajax({
					type: "GET",
					url:"/expense/delete",
					data:{idExpense: this.vid},
					success: function(){
						$(parent.el).remove();
					}
				});
			}
		},
		
		render:function(){
            var template = '<td>{{amount}} &euro;</td><td>{{typeName}}</td><td><span class="editLine">Edit</span></td><td><span class="deleteLine">Delete</span></td>';
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
			var json = '{"id":"0", "amount":"' + $('input[name*="amount"]').val() + '", "comment":"' + $('#expense-comment').val() + '", "expenseTypeId" : "' + $("#expense-type-select option:selected").val() + '", "timesheetDayId":"' + this.idDay + '"}';
			$.ajax({
				type: "POST",
				url:"/expense/create",
				data:json,
				dataType:"json",
				contentType: "application/json; charset=utf-8",
				processData: false,
				success: function(data){
					$(tableExpenseView.el).append(new ExpenseTableLineView(data.id, data.amount, data.typeName, data.typeRatio).render().el);
				}
			});
		},
		
		render: function(){
			$.ajax({
				url:"/expense/type/all",
				dataType:"json",
				success: function(data){
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
			});
			return this;
		}
	})
	
	TimeWeekSelectorView = Backbone.View.extend({
		el:"#weekSelector",
		events:{
			"click .minusWeek" : "minusWeek",
			"click .plusWeek" : "plusWeek",
			"click #week_current" : "currentWeek"
		},
		initialize: function(){
			
		},
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
		plusWeek: function(){
			weekSelected ++;
			weekSelected = ((weekSelected)%53);
			if(weekSelected == 0){
				weekSelected = 1;
				yearSelected ++;
			}
			mainView.reloadCalendar();
		},
		minusWeek: function(){
			weekSelected --;
			weekSelected = ((weekSelected)%53);
			if(weekSelected == 0){
				weekSelected = 52;
				yearSelected --;
			}
			mainView.reloadCalendar();
		},
		currentWeek:function(){
			weekSelected = 0;
			yearSelected = 0;
			mainView.reloadCalendar();
			
		}
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
			$(this.el).addClass("time-cell-selected");
			if (currentCellPickerSelected != null){
				currentCellPickerSelected.unselect();
			}
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