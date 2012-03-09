AppHolidayRequest = (function($){

	var mainView = null;
	var calendar = null;
	var weekSelector = null;
	var currentCellPickerSelected = null;
	var oldCellPickerSelected = null;
	var allCellPicker = new Array();
	//The last day cell clicked (used for the shift + clic function)
	var lastClicked = null;
	var allDayCells = new Array();
	var nbSelected = 0;
	var shifted = false;
	var weekSelected = 0;
	var yearSelected = 0;
	
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
			$("#timesheet-content").append(new ProjectRow(projectName, projectId,amounts,this.data.dates).render().el);
			$("#project-select option[value='" + projectId + "']").remove();
		},
		
		render: function(){
			var parent = this;
			$("#date-line").html("");
			var view = null;
			
			// Variables declarations :
			// List of the headers : contains days
			var headerList = new Array();

			// Building the header
			var template = $("#project-title-template").html();
			$("#date-line").append("<td>"+template+"</td>");
			for (var i = 0 ; i < this.data.dates.length ; i++){
				$("#date-line").append("<td>" + this.data.stringDates[i] + "</td>");
			}
			return this;
		}
	}),
	
	ProjectRow = Backbone.View.extend({
		tagName: "tr",
		projectId: null,
		projectName: null,
		
		initialize: function(projectName, projectId, amounts, days){
			this.projectId = projectId;
			this.projectName = projectName;
			
			// Set the title
			$(this.el).append(projectName);
			
			// Create a td for each day
			for (var i = 0 ; i < amounts.length ; i++){
				$(this.el).append(
						new TimeSheetDayView(this.day,i,amounts[i]).render().el
						);
			}
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
		partOfDay: null,
		selectedBy: -1,
		// An id only reserved to the view to allow the shift + clic event.
		viewRank: -1,
		
		events:{
			"click" : "increment"
		},

		initialize: function(day, index,amount){
			this.day = day;
		},
		
		increment : function(event){
			if(currentCellPickerSelected != null){
				if(this.selectedBy != currentCellPickerSelected.idType){
					// Color the cell with the Balance color
					$(this.el).css('background-color', currentCellPickerSelected.color);
					// decrease balance's available days
					currentCellPickerSelected.decrease();
					// If this cell was already selected, increase the old selection in order to not loose a day
					if(oldCellPickerSelected != null && this.isSelected){
						allCellPicker[this.selectedBy].increase();
					}
					this.isSelected = true;
					// Store the id of the type choosen for this cell
					this.selectedBy = currentCellPickerSelected.idType;
					nbSelected ++;
				}
				else{
					if(!shifted){
						// If we deselect a cell
						currentCellPickerSelected.increase();
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
			}
		},
		render: function(){
			$(this.el).text("0");
			return this;
		}

	})
	
	TimeWeekSelectorView = Backbone.View.extend({
		el:"#weekSelector",
		events:{
			"click .minusWeek" : "minusWeek",
			"click .plusWeek" : "plusWeek",
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
			var template = $("#calendarSelector").html();
			var template4Week = $("#week-selector-template").html();
			var view4Week = {week : weekSelected};
			var html = Mustache.to_html(template4Week, view4Week);
			var view = {week : html, year: yearSelected};
			html = Mustache.to_html(template, view);
			$(this.el).html(html);
			mainView.reloadCalendar();
		},
		minusWeek: function(){
			weekSelected --;
			weekSelected = ((weekSelected)%53);
			if(weekSelected == 0){
				weekSelected = 52;
				yearSelected --;
			}
			var template = $("#calendarSelector").html();
			var template4Week = $("#week-selector-template").html();
			var view4Week = {week : weekSelected};
			var html = Mustache.to_html(template4Week, view4Week);
			var view = {week : html, year: yearSelected};
			html = Mustache.to_html(template, view);
			$(this.el).html(html);
			mainView.reloadCalendar();
		}
	})
	
	TimePicker = Backbone.View.extend({
		el:"#timePicker",
		events:{
		
		},
		
		times: [8,4,2,1,0.5,0.25],
		
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
			template = $("#time-cell-template").html();
			view =  {amount: this.amount};
            var html = Mustache.to_html(template, view);
            $(this.el).html(html);
			return this;
		},
		
		selectTime : function(){
			$(this.el).addClass("time-cell-selected");
			
			oldCellPickerSelected = currentCellPickerSelected;
			if(oldCellPickerSelected != null){
				$(oldCellPickerSelected.el).removeClass("time-cell-selected");
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