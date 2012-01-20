AppHolidayManagerRequest = (function($){
	var lineSelected = null;
	var tableView1 = null;
	var tableView2 = null;
	var buttonView = null;
	var viewVisualize = null;
	var dates = new Array();
	var allDayCells = new Array();
	var cellDayMorningCounter = 0;
	var cellDayAfternoonCounter = 1;
	var MORNING_PART = 1;
	var AFTERNOON_PART = 2;
	
	HolidayManagerRequestPageView = Backbone.View.extend({
		el:"#request-manager-main",
		events: {
			
		},
	
		initialize: function(){
			
		},
		
		render:function(){
			tableView1 = new HolidayManagerRequestPendingTableView();
			tableView2 = new HolidayManagerRequestTableView();
			buttonView = new HolidayManagerButtonsView();
		}
	})
	
	HolidayManagerRequestPendingTableLineView = Backbone.View.extend({
		tagName: "tr",
		className:"manager_pending_request_table_line",
		
		vid: null,
		vfrom : null,
		vrequesterComment : null,
		vbegin : null,
		vend : null,
		
		events: {
			"click" : "selectLine",
			"mouseover" : "overLine",
			"mouseout" : "outLine"
		},
		
		initialize: function(id, beginDate, endDate, user, requesterComment){
			this.vid=id;
			this.vbegin=beginDate;
			this.vend=endDate;
			this.vfrom=user;
			this.vrequesterComment=requesterComment;		
		},
		
		selectLine : function(){
			$("#button_accepted").removeAttr('disabled');
			$("#button_denied").removeAttr('disabled');
			$("#button_visualize").removeAttr('disabled');
			$(this.el).css("background-color", "#8AA5A1");
			if(typeof(lineSelected) != "undefined"){
				if(lineSelected != this && lineSelected != null){
					$(lineSelected.el).css("background-color", "transparent");
				}
			}
			lineSelected = this;
		},
		
		overLine : function(){
			if(lineSelected != this){
				$(this.el).css("background-color", "#EEEEEE");
			}
		},
		outLine : function(){
			if(lineSelected != this){
				$(this.el).css("background-color", "transparent");
			}
		},

		render:function(){
			var template = $("#status-accepted-or-denied-template").html(); 
			var view = {from : this.vfrom, requesterComment : this.vrequesterComment, beginDate : this.vbegin, endDate : this.vend};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			$(this.el).appendTo($("#manager_pending_request_table"));
			return this;
		}
	})
	
		
	
	HolidayManagerRequestTableLineView = Backbone.View.extend({
		tagName: "tr",
		className:"manager_request_table_line",
		
		vid: null,
		vfrom : null,
		vrequesterComment : null,
		vmanagerComment : null,
		vbegin : null,
		vend : null,
		vstatus : null,
		
		events: {
			"click" : "selectLine",
			"mouseover" : "overLine",
			"mouseout" : "outLine"
		},
		
		initialize: function(id, beginDate, endDate, user, requesterComment, managerComment, status){
			this.vid=id;
			this.vbegin=beginDate;
			this.vend=endDate;
			this.vfrom=user;
			this.vrequesterComment=requesterComment;
			this.vmanagerComment = managerComment
			this.vstatus = status;
		},
		
		selectLine : function(){
			$(this.el).css("background-color", "#8AA5A1");
			if(typeof(lineSelected) != "undefined"){
				if(lineSelected != this && lineSelected != null){
					$(lineSelected.el).css("background-color", "transparent");
				}
			}
			lineSelected = this;
		},
		
		overLine : function(){
			if(lineSelected != this){
				$(this.el).css("background-color", "#EEEEEE");
			}
		},
		outLine : function(){
			if(lineSelected != this){
				$(this.el).css("background-color", "transparent");
			}
		},
		
		render:function(){
			var statusTemplate="";
			if (this.vstatus==0){
				 statusTemplate=$("#status-denied-template").html();
			}
			else {
				statusTemplate=$("#status-accepted-template").html();
			}
			var template = '<td>{{from}}</td><td>{{requesterComment}}</td><td>{{managerComment}}</td><td>{{beginDate}}</td><td>{{endDate}}</td><td>{{status}}</td>';
			var view = {from : this.vfrom, requesterComment : this.vrequesterComment, managerComment : this.vmanagerComment, beginDate : this.vbegin, endDate : this.vend, status : statusTemplate};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			$(this.el).appendTo($("#manager_request_table"));
			return this;
		}
	})

	
	HolidayManagerRequestPendingTableView = Backbone.View.extend({
		el:"#manager_pending_request_table",
		events:{
		
		},
		initialize:function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/holiday/managers/request/all/pending",
				dataType:"json",
				success: function(data){
					if(data != null){
						if(data.holidayRequestDTO.length > 1 ){
							$.each(data.holidayRequestDTO, function (){
								var view = new HolidayManagerRequestPendingTableLineView(this.id, this.beginDate, this.endDate, this.user, this.requesterComment);	
								view.render();
							});
						}
						else{
							var view = new HolidayManagerRequestPendingTableLineView(data.holidayRequestDTO.id, data.holidayRequestDTO.beginDate, data.holidayRequestDTO.endDate, data.holidayRequestDTO.user, data.holidayRequestDTO.requesterComment);
							view.render();
						}
					}
				}
			});
		},
		
		reload: function(){
			$(".manager_pending_request_table_line").html("");
			this.initialize();
			this.render();
		},
		
		render: function(){
			return this;
		}
	})
	
	HolidayManagerRequestTableView = Backbone.View.extend({
		el:"#manager_request_table",
		events:{
		
		},
		
		initialize:function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/holiday/managers/request/all/status",
				dataType:"json",
				success: function(data){
					if(data != null){
						if(data.holidayRequestDTO.length > 1 ){
							$.each(data.holidayRequestDTO, function (){
								var view = new HolidayManagerRequestTableLineView(this.id, this.beginDate, this.endDate, this.user, this.requesterComment, this.managerComment, this.status);
								view.render();
							});
						}
						else{
							var view = new HolidayManagerRequestTableLineView(data.holidayRequestDTO.id, data.holidayRequestDTO.beginDate, data.holidayRequestDTO.endDate, data.holidayRequestDTO.user, data.holidayRequestDTO.requesterComment, data.holidayRequestDTO.managerComment, data.holidayRequestDTO.status);
							view.render();
						}
					}
				}
			});
		},
		
		reload: function(){
			$(".manager_request_table_line").html("");
			this.initialize();
			this.render();
		},
		
		render: function(){
			return this;
		}
	})

	HolidayManagerButtonsView = Backbone.View.extend({
		el:"#holiday_button_container",		
		
		viewAccept : null,
		viewDeny : null,
				
		events: {
			"click #button_accepted" : "acceptModal",
			"click #button_denied" : "denyModal",
			"click #button_visualize" : "visualizeModal"
		},
		
		initialize:function(){
			this.viewAccept = new HolidayRequestAcceptView();
			this.viewDeny = new HolidayRequestDenyView();
			viewVisualize = new HolidayManagerVisualizeView();
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
       		$("#modal_window_holiday_request").css('top',  winH/2-$("#modal_window_holiday_request").height()/2);
     		$("#modal_window_holiday_request").css('left', winW/2-$("#modal_window_holiday_request").width()/2);
     		$("#modal_window_holiday_request").css('background-color', "#EEEEEE");
     		$("input:text").each(function(){this.value="";});
     		//transition effect
     		$("#modal_window_holiday_request").fadeIn(500);
		},
		
		acceptModal:function(){
			this.showModalWindow();
			this.viewAccept.setFields(lineSelected.vid);
			this.viewAccept.render();
		},
		
		denyModal:function(){
 			this.showModalWindow();
 			this.viewDeny.setFields(lineSelected.vid);
			this.viewDeny.render();
		},
		
		visualizeModal:function(){
			this.showModalWindow();
			viewVisualize.setFields(lineSelected.vid);
			viewVisualize.render();
		},
		
		render:function(){
			
		}
	})

	
	HolidayRequestAcceptView = Backbone.View.extend({
		el: "#modal_window_holiday_request",
		
		vid: null,
		
		events:{
			"click .closeModal" : "closemodal",
			"click .validateHolidayRequest" : "accepted"
		},
		
		initialize:function(id){
			this.vid=id;
		},
		
		setFields:function(id){
			this.vid=id;
		},
		
		render : function(){
			var template = $("#popup-accepted-template").html();
			var view = {};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_holiday_request').hide();
       		$('#mask').hide();
		},
		
		accepted : function(){
			var parent = this;
			$.ajax({
				url : "/holiday/managers/request/accept/" + this.vid,
				success : function(){
					$.ajax({
						url : "/holiday/managers/request/comment/" + parent.vid + "/" + $("#comment").val(),
						success : function(){
							console.log("Sucess function");
							var successHtml = $("#holiday-accept-template").html();				
							$("#holiday_notifications").text(successHtml);
							$("#holiday_notifications").fadeIn(1000);
							$("#holiday_notifications").fadeOut(3000);
							tableView1.reload();
							tableView2.reload();
							$("#button_accepted").attr('disabled', 'disabled');
							$("#button_denied").attr('disabled', 'disabled');
							$("#button_visualize").attr('disabled', 'disabled');
						}
					});
				}
			});
			parent.closemodal();
		}
	})

	HolidayRequestDenyView = Backbone.View.extend({
		el: "#modal_window_holiday_request",

		vid: null,
		
		events:{
			"click .closeModal" : "closemodal",
			"click .denyHolidayRequest" : "denied"
		},
		
		initialize:function(id, beginDate, endDate, user, requesterComment){
			this.vid=id;
			this.vbegin=beginDate;
			this.vend=endDate;
			this.vfrom=user;
			this.vrequesterComment=requesterComment;
		},
		
		setFields:function(id){
			this.vid=id;
		},
		
		render : function(){
			var template = $("#popup-denied-template").html();
			var view = {};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_holiday_request').hide();
	   		$('#mask').hide();
		},
		
		denied : function(){
			var parent = this ; 
			$.ajax({
				url : "/holiday/managers/request/deny/" + this.vid,
				success : function(){
					$.ajax({
						url : "/holiday/managers/request/comment/" + parent.vid + "/" +  $("#comment").val(),
						success : function(){
							var successHtml = $("#holiday-accept-template").html();				
							$("#holiday_notifications").text(successHtml);
							$("#holiday_notifications").fadeIn(1000);
							$("#holiday_notifications").fadeOut(3000);
							return this;
							tableView1.reload();
							tableView2.reload();
							$("#button_visualize").attr('disabled', 'disabled');
							$("#button_accepted").attr('disabled', 'disabled');
							$("#button_denied").attr('disabled', 'disabled');
						}
					});
				}
			});
			this.closemodal();
		}
	})
	
	HolidayManagerVisualizeView = Backbone.View.extend({
		el:"#modal_window_holiday_request",
		
		managerRequestView : null,
		vid : null,
		data : null,
		listDay : null,
		
		events:{
			"click .closeModal" : "closemodal",
		},
		
		initialize: function(){
		
		},
		
		setFields:function(id){
			this.vid=id;
		},
		
		closemodal: function(){
			$('#modal_window_holiday_request').hide();
	   		$('#mask').hide();
		},
		
		render: function(){
			var parent =this;
			var template = $("#popup-visualize-request").html();
			var view = {};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			$.ajax({
				url : "/holiday/managers/request/details/"+ this.vid,
				dataType:"json",
				success : function(list){
					parent.listDay = list;	
					$.ajax({
						url : "/holiday/managers/request/get/"+ parent.vid,
						dataType:"json",
						success : function(data1){
							dates[0]=data1.holidayDetailDTO[0].day.substr(0,10);
							dates[1]=data1.holidayDetailDTO[1].day.substr(0,10);
							parent.data=data1;
							$.ajax({
								type: "GET",
								url:"/holiday/managers/request/construct/" + dates[0] + "/" + dates[1],
								dataType:"json",
								success: function(data2){
									// Clean the div content
									$('#calendarContent').html("");
									// Create the views
									$("#calendarRequest").show();
									new HolidayRequestCalendarView(data2, parent.data, parent.listDay).render();
								}
							});
						}
					});
				}
			});
			return this;
		}
	})

	HolidayRequestCalendarView = Backbone.View.extend({
		el:"#calendarContent",
		data : null,
		details : null,
		listDays:null,
		
		events:{

		},
		
		initialize: function(data, details, listDays){
			this.data = data;
			this.details = details;
			this.listDays = listDays; 
			console.log(data);
			console.log(details);
			console.log(listDays);
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
			
			var isColor = false;
			// Counter for the list building
			var cptBuildingList = 0;
			// Counter for the header list
			var cptHeaderList = 0;
			// Counter for the morning list
			var cptMorningList = 0;
			// Counter for the afternoon list
			var cptAfternoonList = 0;
			
			var cptDetailsList = 0;
			// Contains a tr element to add a row for header
			var lineHeader;
			// Contains a tr element to add a row for morning
			var lineMorning;
			// Contains a tr element to add a row for afternoon
			var lineAfternoon;
			// Count the number of weeks created
			var nPath = 0;
			

			// organise date
			var dateTake = new Array();
			var iDate = 0;
			
			if (this.listDays.holidayDetailDTO.length>1){
				$.each(this.listDays.holidayDetailDTO, function(){
					var year = this.day.substr(0,4);
					var month = this.day.substr(5,2);
					var day = this.day.substr(8,2);
					var dayDone = month+"/"+day+"/"+year;
					dateTake[iDate] = dayDone;
					iDate++;
				});
			}
			else {
				var year = this.listDays.holidayDetailDTO.day.substr(0,4);
				var month = this.listDays.holidayDetailDTO.day.substr(5,2);
				var day = this.listDays.holidayDetailDTO.day.substr(8,2);
				var dayDone = month+"/"+day+"/"+year;
				dateTake[iDate] = dayDone;
				iDate++;
			}
			
			// Building the header list
			// Building the morning list
			// Building the afternoon list
			if(this.data.days.length > 1){
				$.each(this.data.days, function(){
					headerList[cptBuildingList] = this.day;
					morningList[cptBuildingList] = "true";
					afternoonList[cptBuildingList] = "true";
					cptBuildingList ++;
				});
			}
			else{
				headerList[0] = this.data.days.day;
				morningList[0] = "true";
				afternoonList[0] = "true";
			}
			
			
			while (nPath < this.data.nbWeeks){
				// Create tr element
				lineHeader = $("<tr>", {
					class:'day-header'
				});
				// Adds all the headers for the week
				while(cptHeaderList < 5){
					lineHeader.append($(new HolidayRequestDayView(headerList[cptHeaderList + (nPath * 5)], true, null, true, false, -1).render().el));
					cptHeaderList ++;
				}
				$(parent.el).append(lineHeader);
				cptHeaderList = 0;
				
				// Create the tr element
				lineMorning = $("<tr>", {
					class:'morning-part'
				});
				
				// Adds all the mornings for the week
				while(cptMorningList < 5){
					lineMorning.append($(new HolidayRequestDayView(headerList[cptHeaderList + (nPath * 5)], morningList[cptMorningList + (nPath * 5)], null, false, true, cellDayMorningCounter, MORNING_PART, dateTake, parent.listDays).render().el));
					cellDayMorningCounter += 2;
					cptMorningList ++;
					cptHeaderList++;
				}
				
				$(parent.el).append(lineMorning);
				cptHeaderList = 0;
				
				// Create the tr element
				lineAfternoon = $("<tr>", {
					class:'afternoon-part'
				});
				
				// Adds all the afternoons for the week
				while(cptAfternoonList < 5){
					lineAfternoon.append($(new HolidayRequestDayView(headerList[cptHeaderList + (nPath * 5)], afternoonList[cptAfternoonList + (nPath * 5)], null, false, false, cellDayAfternoonCounter, AFTERNOON_PART, dateTake, parent.listDays).render().el));
					cellDayAfternoonCounter += 2;
					cptAfternoonList ++;
					cptHeaderList ++;
				}
				$(parent.el).append(lineAfternoon);
				
				// Add an empty line for the separation of the week
				$(parent.el).append($("<tr>", {
					class:'separation-part'
				}));
				
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
		details:null,
		available : null,
		week : null,
		color : null,
		isHeader: false,
		// We just specify if morning, if this is false, and header too ,this is afternoon
		isMorning: false,
		isColored : false,
		partOfDay: null,
		selectedBy: -1,
		// An id only reserved to the view to allow the shift + clic event.
		viewRank: -1,
		

		events:{
		},

		initialize: function(day, available, week, header, morning, rank, part, take, details){
			this.day = day;
			this.details = details;
			this.available = available;
			this.week = week;
			this.isHeader = header;
			this.viewRank = rank;
			this.isMorning = morning;
			for (xDate in take){
				if (this.details.holidayDetailDTO.length > 1){
					if (take[xDate]==this.day && this.isMorning.toString() == this.details.holidayDetailDTO[xDate].am){
						this.color=details.holidayDetailDTO[xDate].color;
						this.isColored = true ;
					}
				}
				else{
					if (take[xDate]==this.day && this.isMorning.toString() == this.details.holidayDetailDTO.am){
						this.color=details.holidayDetailDTO.color;
						this.isColored = true ;
					}
				}
			}
			// Store the view into the array of all cell day views
			if(this.viewRank != -1){
				allDayCells[this.viewRank] = this;
			}			
			this.partOfDay = part;
		},
		
			
		render: function(){
			if(this.isHeader){
				$(this.el).text(this.day);
				$(this.el).addClass("dayHeader-part");
			}
			else{
				if(this.available == "true"){
					if(this.isMorning){
						$(this.el).addClass("am-part");
						if (this.isColored == true && this.isHeader == false && this.isMorning == true){
							$(this.el).css('background-color', this.color);
						}
					}
					else{
						$(this.el).addClass("pm-part");
						if (this.isColored == true && this.isHeader == false && this.isMorning == false){
							$(this.el).css('background-color', this.color);
						}
					}
				}
				else{
					$(this.el).attr('disabled', '');
					$(this.el).addClass('day-disabled');
				}
			}	
			return this;
		}

	})

	
	var self = {};
	self.start = function(){
		new HolidayManagerRequestPageView().render();
	}
	return self;
})

$(function() {
	console.log("Starting holiday manager request application")
	new AppHolidayManagerRequest(jQuery).start();
})