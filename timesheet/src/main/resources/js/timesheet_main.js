AppHolidayRequest = (function($){

	
	var MAX_VALUE = 8;
	var mainView = null;
	var calendar = null;
	var weekSelector = null;
	var currentCellPickerSelected = null;
	var allCellPicker = new Array();
	//The last day cell clicked (used for the shift + clic function)
	var lastClicked = null;
	var allDayCells = new Array();
	var nbSelected = 0;
	var shifted = false;
	var weekSelected = 0;
	var yearSelected = 0;
	var timeSheetId = 0;
	
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
							// Create the views
							weekSelected = data.timeSheet.week;
							yearSelected = data.timeSheet.year;
							weekSelector.refresh();
							timeSheetId = data.timeSheet.id;
							calendar = new CalendarView(data).render();
						}
					});
				}
			});
		},
		addProject: function(){
			var amounts = new Array();
			amounts.push(0);
			amounts.push(0);
			amounts.push(0);
			amounts.push(0);
			amounts.push(0);
			amounts.push(0);
			amounts.push(0);
			calendar.addRow($("#project-select option:selected").text(),$("#project-select option:selected").val(),amounts);
		},
		render: function(){
			return this;
		},
		reloadCalendar: function(){
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
	})
		
	CalendarView = Backbone.View.extend({
		el:"#timesheet-content",
		data : null,
		
		events:{

		},
		initialize: function(data){
			this.data = data;
		},
		
		addRow: function(projectName, projectId, amounts){
			if (projectId != null && projectName != null){
				$("#timesheet-content").append(new ProjectRow(projectName, projectId,amounts,this.data.dates).render().el);
				// Remove from the combobox
				$("#project-select option[value='" + projectId + "']").remove();
			}
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
			// Build rows with data
			if( $.isArray(this.data.timeSheet.rows)){
				$.each(this.data.timeSheet.rows, function(){
					$("#timesheet-content").append(
							new ProjectRow(this.project.name,
									this.project.id,
									this.timeSheetDays,parent.data.dates).render().el
					);
					// Remove from the combobox
					$("#project-select option[value='" + this.project.id + "']").remove();
				});
			} else {
				// Verify if there is at least one row
				if (this.data.timeSheet.rows != null){
					$("#timesheet-content").append(
							new ProjectRow(this.data.timeSheet.rows.project.name,
									this.data.timeSheet.rows.project.id,
									this.data.timeSheet.rows.timeSheetDays,this.data.dates).render().el
					);
					// Remove from the combobox
					$("#project-select option[value='" + this.data.projectId + "']").remove();
				}
			}
			return this;
		}
	}),
	
	ProjectRow = Backbone.View.extend({
		tagName: "tr",
		projectId: null,
		projectName: null,
		events:{
			"click .deleteButton" : "removeLine",
		},
		initialize: function(projectName, projectId, timeSheetDays, days){
			
			this.projectId = projectId;
			this.projectName = projectName;
			
			// Set the title
			$(this.el).append("<td>" + projectName + "</td>");
			
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
										timeSheetDays.detailId).render().el
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
											timeSheetDays[j].detailId).render().el
							);
							found = true;
						}
					}
				}
				if (! found){
					$(this.el).append(new TimeSheetDayView(i,0,days[i], this.projectId, 0).render().el);
				}
			}
			
			// Create the delete button
			var buttonTemplate = $("#delete-button-template").html();
			$(this.el).append("<td>" + buttonTemplate + "</td>");
		},
		
		removeLine : function(){
			var parent = this;
			
			// Put the project in the combo box
			$('#project-select')
	          .append($('<option>', { value : this.projectId })
	          .text(this.projectName));
			
			// Delete line
			$(this.el).remove();
			
			console.log("TSID : "+timeSheetId+" PID : "+this.projectId)
			
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

		day : null,
		amount : null,
		week : null,
		isSelected: false,
		projectId: null,
		selectedBy: -1,
		// An id only reserved to the view to allow the shift + clic event.
		viewRank: -1,
		
		events:{
			"click" : "increment"
		},
		initialize: function(index, amount, day, projectId, detailId){
			this.index = index;
			this.amount = amount;
			this.day = day;
			this.projectId = projectId;
			this.detailId = detailId;
		},
		
		increment : function(event){
			var parent = this;
			if(currentCellPickerSelected != null){
				// Get value and increment
				var val = parseFloat($(this.el).text());
				val += currentCellPickerSelected.amount;
				if (val > MAX_VALUE){
					val = MAX_VALUE;
				}
				this.amount = val;
				$(this.el).text(val);
				
				json = '{"index":"'+this.index+'","amount":'+this.amount
				+',"day":"'+this.day
				+'","projectId":"'+this.projectId
				+'","detailId":"'+this.detailId
				+'","timeSheetId":"'+timeSheetId+'"}';
				// Send data to server
				$.ajax({
					type: "POST",
					url:"/timesheet/update",
					data:json,
					dataType:"json",
					contentType: "application/json; charset=utf-8",
					processData: false,
					success: function(data){
						parent.detailId = data.detailId;
					}
				});
				
			}
			else{
				if(!shifted){
					// If we deselect a cell
					$(this.el).css('background-color', '#dce8f1');
					this.isSelected = false;
					this.selectedBy = -1;
					nbSelected --;
				}
			}
			// If shift is pressed, we colore all fields between days selected 
			if(typeof(event) != "undefined"){
				if(event.shiftKey){
					shifted = true;
					var cpt = lastClicked.viewRank;
					while(cpt < this.viewRank){
						allDayCells[cpt].increment();
						cpt ++;
					}
				}
			}
			shifted = false;
			lastClicked = this;
		},
		render: function(){
			$(this.el).text(this.amount);
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
	}
	return self;
})

$( function() {
	console.log("Starting holiday request application")
	new AppHolidayRequest(jQuery).start();
})