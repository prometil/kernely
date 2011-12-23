AppHolidayRequest = (function($){


	HolidayRequestPageView = Backbone.View.extend({
		el:"#request-main",
		dates: null,
		events:{
			"click #submitPeriod" : "buildCalendar"
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
		buildCalendar: function(){
			new HolidayRequestCalendarView(dates[0].value, dates[1].value).render();
		}
		})
	
		HolidayRequestCalendarView = Backbone.View.extend({
			el:"#calendarContent",
			date1: null,
			date2: null,
			events:{
	
			},
			initialize: function(date1, date2){
				this.date1 = date1;
				this.date2 = date2;
			},
			render: function(){
				var parent = this;
				var view = null;
				$.ajax({
					type: "GET",
					url:"/holiday/request/interval",
					data: {date1: parent.date1, date2: parent.date2},
					dataType:"json",
					success: function(data){
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
						if(data.days.length > 1){
							$.each(data.days, function(){
								headerList[cptBuildingList] = this.day;
								morningList[cptBuildingList] = this.morningAvailable;
								afternoonList[cptBuildingList] = this.afternoonAvailable;
								cptBuildingList ++;
							});
						}
						else{
							headerList[0] = data.days.day;
							morningList[0] = data.days.morningAvailable;
							afternoonList[0] = data.days.afternoonAvailable;
						}
						
						
						while (nPath < data.nbWeeks){
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
					}
				});
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