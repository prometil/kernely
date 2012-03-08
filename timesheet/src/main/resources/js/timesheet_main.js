AppHolidayRequest = (function($){

	var currentCellPickerSelected = null;
	var oldCellPickerSelected = null;
	var allCellPicker = new Array();
	//The last day cell clicked (used for the shift + clic function)
	var lastClicked = null;
	var allDayCells = new Array();
	var nbSelected = 0;
	var shifted = false;
	
	TimeSheetPageView = Backbone.View.extend({
		el:"#request-main",
		dates: null,
		events:{
			
		},
		initialize: function(){
			new TimePicker().render();
			$.ajax({
				type: "GET",
				url:"/timesheet/current",
				success: function(data){
					// Create the views
					new CalendarView(data).render();
				}
			});
		},
		render: function(){
			return this;
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
		render: function(){
			var parent = this;
			var view = null;
			
			// Variables declarations :
			// List of the headers : contains days
			var headerList = new Array();
			// Counter for the list building
			var cptBuildingList = 0;
			// Counter for the header list
			var cptHeaderList = 0;

			// Count the number of weeks created
			var nPath = 0;
			
			// Building the header list and the days list
			for (var i = 0 ; i < this.data.dates.length ; i++){
				$("#date-line").append("<td>" + this.data.stringDates[i] + "</td>");
				$("#amounts-line").append(
						new TimeSheetDayView(this.data.dates[i],i,0).render().el
						);
			}
			return this;
		}
	})

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
		new TimeSheetPageView().render();
	}
	return self;
})

$( function() {
	console.log("Starting holiday request application")
	new AppHolidayRequest(jQuery).start();
})