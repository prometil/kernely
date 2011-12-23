AppHolidayRequest = (function($){

	var currentCellPickerSelected = null;
	var oldCellPickerSelected = null;

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
			
			
			
			var headerList = new Array();
			var morningList = new Array();
			var afternoonList = new Array();
			var cptBuildingList = 0;
			var cptHeaderList = 0;
			var cptMorningList = 0;
			var cptAfternoonList = 0;
			var lineHeader;
			var lineMorning;
			var lineAfternoon;
			var nPath = 0;
			
			var emptyTD = $("<td>");
			

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
				lineHeader = $("<tr>", {
					class:'day-header'
				});
				while(cptHeaderList < 5){
					lineHeader.append($(new HolidayRequestDayView(headerList[cptHeaderList + (nPath * 5)], true, null, true).render().el));
					cptHeaderList ++;
				}
				$(parent.el).append(lineHeader);
					
				lineMorning = $("<tr>", {
					class:'morning-part'
				});
				while(cptMorningList < 5){
					lineMorning.append($(new HolidayRequestDayView(null, morningList[cptMorningList + (nPath * 5)], null, false).render().el));
					cptMorningList ++;
				}
				$(parent.el).append(lineMorning);
				lineAfternoon = $("<tr>", {
					class:'afternoon-part'
				});
				while(cptAfternoonList < 5){
					lineAfternoon.append($(new HolidayRequestDayView(null, afternoonList[cptAfternoonList + (nPath * 5)], null, false).render().el));
					cptAfternoonList ++;
				}
				$(parent.el).append(lineAfternoon);
				cptHeaderList = 0;
				cptMorningList = 0;
				cptAfternoonList = 0;
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

		events:{
			"click" : "colorTheWorld"
		},

		initialize: function(day, available, week, header){
			this.day = day;
			this.available = available;
			this.week = week;
			this.isHeader = header;
		},
		colorTheWorld : function(){
			console.log(this.selectedBy);
			if(currentCellPickerSelected != null && !this.isHeader && this.available == "true"
				&& this.selectedBy != currentCellPickerSelected.idType){
				if(currentCellPickerSelected.nbAvailable > 0){
					$(this.el).css('background-color', currentCellPickerSelected.color);
					currentCellPickerSelected.decrease();
					if(oldCellPickerSelected != null && this.isSelected){
						oldCellPickerSelected.increase();
					}
				}
				this.isSelected = true;
				this.selectedBy = currentCellPickerSelected.idType;
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
				
			}
			return this;
		}
	})
	
	HolidayRequestColorPickerCell = Backbone.View.extend({
		tagName:"div",
		className: "balance-cell",
		
		color:null,
		name:null,
		nbAvailable:null,
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
			this.nbAvailable --;
			this.updateCounter();
		},
		
		increase: function(){
			this.nbAvailable ++;
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