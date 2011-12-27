AppHolidayRequest = (function($){

	var currentCellPickerSelected = null;
	var oldCellPickerSelected = null;
	var lastClicked = null;
	var allDayCells = new Array();
	var cellDayMorningCounter = 0;
	var cellDayAfternoonCounter = 1;
	var shifted = false;

	HolidayRequestPageView = Backbone.View.extend({
		el:"#request-main",
		dates: null,
		events:{
			"click #submitPeriod" : "buildCalendarAndPicker"
		},
		initialize: function(){
			dates = $( "#from, #to" ).datepicker({
				defaultDate: "+1w",
				changeMonth: true,
				onSelect: function( selectedDate ) {
				var option = this.id == "from" ? "minDate" : "maxDate",
						instance = $( this ).data( "datepicker" ),
						date = $.datepicker.parseDate(
								instance.settings.dateFormat ||
								$.datepicker._defaults.dateFormat,
								selectedDate, instance.settings );
				dates.not( this ).datepicker( "option", option, date );
			}
			});
		},
		render: function(){
			return this;
		},
		buildCalendarAndPicker: function(){
			$.ajax({
				type: "GET",
				url:"/holiday/request/interval",
				data: {date1: dates[0].value, date2: dates[1].value},
				dataType:"json",
				success: function(data){
					// Clean the div content
					$('#calendarContent').html("");
					$('#colorSelector').html("");
					// Create the views
					new HolidayRequestCalendarView(data).render();
					new HolidayRequestColorPicker(data).render();
				}
			});
			
		}
	})
		
	HolidayRequestCalendarView = Backbone.View.extend({
		el:"#calendarContent",
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
			// List of the available mornings
			var morningList = new Array();
			// List of the available afternoons
			var afternoonList = new Array();
			// Counter for the list building
			var cptBuildingList = 0;
			// Counter for the header list
			var cptHeaderList = 0;
			// Counter for the morning list
			var cptMorningList = 0;
			// Counter for the afternoon list
			var cptAfternoonList = 0;
			// Contains a tr element to add a row for header
			var lineHeader;
			// Contains a tr element to add a row for morning
			var lineMorning;
			// Contains a tr element to add a row for afternoon
			var lineAfternoon;
			// Count the number of weeks created
			var nPath = 0;
			

			// Building the header list
			// Building the morning list
			// Building the afternoon list
			if(this.data.days.length > 1){
				$.each(this.data.days, function(){
					headerList[cptBuildingList] = this.day;
					morningList[cptBuildingList] = this.morningAvailable;
					afternoonList[cptBuildingList] = this.afternoonAvailable;
					cptBuildingList ++;
				});
			}
			else{
				headerList[0] = this.data.days.day;
				morningList[0] = this.data.days.morningAvailable;
				afternoonList[0] = this.data.days.afternoonAvailable;
			}
			
			
			while (nPath < this.data.nbWeeks){
				// Create tr element
				lineHeader = $("<tr>", {
					class:'day-header'
				});
				// Adds all the headers for the week
				while(cptHeaderList < 5){
					lineHeader.append($(new HolidayRequestDayView(headerList[cptHeaderList + (nPath * 5)], true, null, true, -1).render().el));
					cptHeaderList ++;
				}
				$(parent.el).append(lineHeader);
				
				// Create the tr element
				lineMorning = $("<tr>", {
					class:'morning-part'
				});
				// Adds all the mornings for the week
				while(cptMorningList < 5){
					lineMorning.append($(new HolidayRequestDayView(null, morningList[cptMorningList + (nPath * 5)], null, false, cellDayMorningCounter).render().el));
					cellDayMorningCounter += 2;
					cptMorningList ++;
				}
				$(parent.el).append(lineMorning);
				
				// Create the tr element
				lineAfternoon = $("<tr>", {
					class:'afternoon-part'
				});
				// Adds all the afternoons for the week
				while(cptAfternoonList < 5){
					lineAfternoon.append($(new HolidayRequestDayView(null, afternoonList[cptAfternoonList + (nPath * 5)], null, false, cellDayAfternoonCounter).render().el));
					cellDayAfternoonCounter += 2;
					cptAfternoonList ++;
				}
				$(parent.el).append(lineAfternoon);
				// Reinitialize all counters
				cptHeaderList = 0;
				cptMorningList = 0;
				cptAfternoonList = 0;
				// Increases week created
				nPath ++;
			}
			
			return this;
		}
	})

	HolidayRequestDayView = Backbone.View.extend({
		tagName: "td",
		className : "daysPartCalendar",

		day : null,
		available : null,
		week : null,
		isHeader: false,
		isSelected: false,
		selectedBy: -1,
		// An id only reserved to the view to allow the shift + clic event.
		viewRank: -1,
		

		events:{
			"click" : "colorTheWorld"
		},

		initialize: function(day, available, week, header, rank){
			this.day = day;
			this.available = available;
			this.week = week;
			this.isHeader = header;
			this.viewRank = rank;
			// Store the view into the array of all cell day views
			if(this.viewRank != -1){
				allDayCells[this.viewRank] = this;
			}
		},
		colorTheWorld : function(event){
			if(currentCellPickerSelected != null && !this.isHeader && this.available == "true"){
				if(this.selectedBy != currentCellPickerSelected.idType){
					if(currentCellPickerSelected.nbAvailable > 0){
						// Color the cell with the Balance color
						$(this.el).css('background-color', currentCellPickerSelected.color);
						// decrease balance's available days
						currentCellPickerSelected.decrease();
						// If this cell was already selected, increase the old selection in order to not loose a day
						if(oldCellPickerSelected != null && this.isSelected){
							oldCellPickerSelected.increase();
						}
						this.isSelected = true;
						// Store the id of the type choosen for this cell
						this.selectedBy = currentCellPickerSelected.idType;
					}
				}
				else{
					if(!shifted){
						// If we deselect a cell
						currentCellPickerSelected.increase();
						$(this.el).css('background-color', 'transparent');
						this.isSelected = false;
						this.selectedBy = -1;
					}
				}
				// If shift is pressed, we colore all fields between days selected 
				if(typeof(event) != "undefined"){
					if(event.shiftKey){
						shifted = true;
						var cpt = lastClicked.viewRank;
						while(cpt < this.viewRank){
							allDayCells[cpt].colorTheWorld();
							cpt ++;
						}
					}
				}
				shifted = false;
				lastClicked = this;
			}
		},
		render: function(){
			if(this.isHeader){
				$(this.el).text(this.day);
			}
			
			if(this.available == "false"){
				$(this.el).attr('disabled', '');
				$(this.el).addClass('day-disabled');
			}
	
			return this;
		}

	})
	
	HolidayRequestColorPicker = Backbone.View.extend({
		el:"#colorSelector",
		events:{
		
		},
		
		data : null,
		
		initialize : function(data){
			this.data = data;
		},
		render: function(){
			var parent = this;
			
			if(this.data.details.length > 1){
				$.each(this.data.details, function(){
                    $(parent.el).append(new HolidayRequestColorPickerCell(this.nameOfType, this.nbAvailable, this.color, this.idOfType).render().el);
				});
			}
			else{
				$(parent.el).append(new HolidayRequestColorPickerCell(this.data.details.nameOfType, this.data.details.nbAvailable, this.data.details.color, this.data.details.idOfType).render().el);
			}
			return this;
		}
	})
	
	HolidayRequestColorPickerCell = Backbone.View.extend({
		tagName:"div",
		className: "balance-cell",
		
		color:null,
		name:null,
		nbAvailable:0.0,
		idType: null,
		
		events: {
			"click" : "selectColor"
		},
		
		initialize : function(name, avail, color, idType){
			this.color = color;
			this.name = name;
			this.nbAvailable = avail;
			this.idType = idType;
		},
		
		render : function(){
			var template = $("#balance-cell-template").html();
            var view = {name: this.name, available: this.nbAvailable};
            var html = Mustache.to_html(template, view);
            $(this.el).html(html);
            $(this.el).css('background-color', this.color);
			return this;
		},
		
		selectColor : function(){
			oldCellPickerSelected = currentCellPickerSelected;
			currentCellPickerSelected = this;
		},
		
		decrease: function(){
			this.nbAvailable -= 0.5;
			this.updateCounter();
		},
		
		increase: function(){
			this.nbAvailable += 0.5;
			this.updateCounter();
		},
		
		updateCounter : function(){
			$(this.el).find('span.available-cpt').text(this.nbAvailable);
		}
		
	})

	// Initialization of the application
	var self = {};
	self.start = function(){
		new HolidayRequestPageView().render();
	}
	return self;
})

$( function() {
	console.log("Starting holiday request application")
	new AppHolidayRequest(jQuery).start();
})