AppHolidayManagerRequest = (function($){
	
	var mainView = null;
	var tableView1 = null;
	var tableStatuedGroup = new Array();
	var viewVisualize = null;
	var dates = new Array();
	var viewAccept =null;
	var viewDeny = null;
	
	HolidayManagerRequestPageView = Backbone.View.extend({
		el:"#request-manager-main",
		events: {
			
		},
	
		initialize: function(){
			viewAccept = new HolidayRequestAcceptView();
			viewDeny = new HolidayRequestDenyView();
			viewVisualize = new HolidayManagerVisualizeView();
		},
		
		render:function(){
			tableView1 = new HolidayManagerRequestPendingTableView().render();
			new HolidayUserYearContainerView();
		}
	})

	
	HolidayManagerRequestPendingTableView = Backbone.View.extend({
		el:"#manager_pending_request_table",
		events:{
		
		},
		
		table: null,
		
		initialize:function(){
			var parent = this;
			
			var templateFromColumn = $("#requester-column-template").text();
			var templateRequesterColumn = $("#requester-comment-column-template").text();
			var templateBeginColumn = $("#begin-column-template").text();
			var templateEndColumn = $("#end-column-template").text();
			this.table = $(parent.el).kernely_table({
				idField:"id",
				
				columns:[
						{"name":templateFromColumn, style:""},
						{"name":templateRequesterColumn, style:""}, 
						{"name":templateBeginColumn, style:""},
						{"name":templateEndColumn, style:""},
						{"name":"", style:["text-center", "icon-column"]},
						{"name":"", style:["text-center", "icon-column"]},
						{"name":"", style:["text-center", "icon-column"]}
				],
				elements:["user", "requesterComment", "beginDate", "endDate", "acceptButton", "denyButton", "visualizeButton"],
				
				eventNames:["click", "click .accept", "click .deny", "click .visualize"],
				events:{
					"click": parent.selectLine,
					"click .accept" : parent.accept,
					"click .deny" : parent.deny,
					"click .visualize" : parent.visualize
					
				},
				editable:true
			});
		},
		
		accept: function(e){
			viewAccept.render(e.data.line);
		},
		
		deny: function(e){
			viewDeny.render(e.data.line);
		},
		
		visualize: function(e){
			viewVisualize.render(e.data.line);
		},
		
		selectLine : function(e){
			$(".editButton").removeAttr('disabled');
			$(".lockButton").removeAttr('disabled');
			var template = null;
			lineSelected = e.data.line;
		},
		
		reload: function(){
			this.render();
		},
		
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/holiday/managers/request/all/pending",
				dataType:"json",
				success: function(data){
					if(data != null){
						var dataRequest = data.holidayRequestDTO;
						if($.isArray(dataRequest)){

							$.each(dataRequest, function(){
								this.acceptButton = "<img class='accept clickable' src='/images/icons/rights_icon.png' />";
								this.denyButton = "<img class='deny clickable' src='/images/icons/delete_icon.png' />";
								this.visualizeButton = "<img class='visualize clickable' src='/images/icons/visualize_icon.png' />";
								this.beginDate = moment(this.beginDate).format("L");
								this.endDate = moment(this.endDate).format("L");
							});
						}
						else{
							dataRequest.acceptButton = "<img class='accept clickable' src='/images/icons/rights_icon.png' />";
							dataRequest.denyButton = "<img class='deny clickable' src='/images/icons/delete_icon.png' />";
							dataRequest.visualizeButton = "<img class='visualize clickable' src='/images/icons/visualize_icon.png' />";
							dataRequest.beginDate = moment(dataRequest.beginDate).format("L");
							dataRequest.endDate = moment(dataRequest.endDate).format("L");
						}
						parent.table.reload(dataRequest);
					}
					else{
						parent.table.clear();
						parent.table.noData();
					}
				}
			});
			return this;
		}
	})
	
	
	HolidayUserYearContainerView = Backbone.View.extend({
		el:"#statued_requests",
		
		nbYear: null,
		
		initialize:function(){
			parent = this;
			$.ajax({
				type:"GET",
				url:"/holiday/managers/request/years",
				dataType:"json",
				success: function(data){
					parent.nbYear = data.begin - data.end;
					var i = data.begin;
					$(parent.el).append(new HolidayUserYearBlockView(i, true).render().el);
					i --;
					while(i >= data.end){
						$(parent.el).append(new HolidayUserYearBlockView(i, false).render().el);
						i --;
					}
				}
			});
		},
	
		render: function(){
			return this;
		}
	})
	
	HolidayUserYearBlockView = Backbone.View.extend({
		tagName:"div",
		className:"year-block",
		
		year : null,
		initVisible: null,
		
		events:{
			"click .header-block-year":"showHideContent"
		},
		
		initialize:function(year, visible){
			this.year = year;
			this.initVisible = visible;
		},
		
		showHideContent : function(){
			if($(this.el).find(".content-block-year").is(":visible")){
				$(this.el).find(".content-block-year").slideUp(500);
			}
			else{
				$(this.el).find(".content-block-year").slideDown(500);
			}
		},
		
		render: function(){
			var template = $("#year-block-request").html();
			var view = {year: this.year};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			$(this.el).find(".content-block-year").html(new HolidayManagerRequestTableView(this.year).render().el);
			
			if(this.initVisible){
				if(!$(this.el).find(".content-block-year").is(":visible")){
					$(this.el).find(".content-block-year").css("display", "block");
				}
			}
			return this;
		}
	})
	
	
	HolidayManagerRequestTableView = Backbone.View.extend({
		tagName:"table",
		className:"kernely_table",
		
		year: null,
		
		table: null, 
		
		initialize:function(year){
			this.year = year;
			var parent = this;
			
			var templateFromColumn = $("#requester-column-template").text();
			var templateRequesterColumn = $("#requester-comment-column-template").text();
			var templateManagerColumn = $("#manager-comment-column-template").text();
			var templateBeginColumn = $("#begin-column-template").text();
			var templateEndColumn = $("#end-column-template").text();
			this.table = $(parent.el).kernely_table({
				idField:"id",
				
				columns:[
						{"name":templateFromColumn, style:""},
						{"name":templateRequesterColumn, style:""},
						{"name":templateManagerColumn, style:""},
						{"name":templateBeginColumn, style:""},
						{"name":templateEndColumn, style:""},
						{"name":"", style:["text-center", "icon-column"]}
				],
				elements:["user", "requesterComment", "managerComment", "beginDate", "endDate", "status"],

				editable:false,
				group:1
			});
			tableStatuedGroup.push(parent);
		},
		
		reload: function(){
			this.render();
		},
		
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/holiday/managers/request/all/status/date",
				data:{year : parent.year},
				dataType:"json",
				success: function(data){
					if(data != null){
						var dataRequest = data.holidayRequestDTO;
						if($.isArray(dataRequest)){
							$.each(dataRequest, function(){
								if(this.status == 1){
									this.status = "<img src='/images/icons/accept_icon.png' />";
								}
								else{
									this.status = "<img src='/images/icons/deny_icon.png' />";
								}
								this.beginDate = moment(this.beginDate).format("L");
								this.endDate = moment(this.endDate).format("L");
							});
						}
						else{
							if(dataRequest.status == 1){
								dataRequest.status = "<img src='/images/icons/accept_icon.png' />";
							}
							else{
								dataRequest.status = "<img src='/images/icons/deny_icon.png' />";
							}
							dataRequest.beginDate = moment(dataRequest.beginDate).format("L");
							dataRequest.endDate = moment(dataRequest.endDate).format("L");
						}
						parent.table.reload(dataRequest);
					}
					else{
						parent.table.clear();
						parent.table.noData();
					}
				}
			});
			return this;
		}
	})
	
	HolidayRequestAcceptView = Backbone.View.extend({
		el: "#modal_accept_window_holiday_request",
		
		vid: null,
		
		events:{
			"click .validateHolidayRequest" : "accepted",
			"click .cancelPopup" : "cancel"
		},
		
		initialize:function(){
			var parent = this;
			var template = $("#popup-accept-template").html();
			var titleTemplate = $("#accept-template").html();
			$(this.el).kernely_dialog({
				title: titleTemplate,
				content: template,
				eventNames:'click',
				width:"395px"
			});
		},
		
		render : function(id){
			this.vid=id;
			$("#comment_accept").val("");
			$(this.el).kernely_dialog("open");
			return this;
		},
		
		accepted : function(){
			var parent = this;
			$.ajax({
				url : "/holiday/managers/request/accept/" + this.vid,
				success : function(){
					$.ajax({
						url : "/holiday/managers/request/comment/" + parent.vid,
						data : {comment: $("#comment_accept").val()},
						dataType: "json",
						success : function(){
							$(parent.el).kernely_dialog("close");
							var successHtml = $("#holiday-accept-template").html();				
							$.writeMessage("success",successHtml);
							$("#comment_accept").val("");
							tableView1.reload();
							for(var view in tableStatuedGroup){
								tableStatuedGroup[view].reload();
							}
						},
						error: function(){
							$.writeMessage("error",$("#acceptance-error-template").html(), "#notification_dialog_accept_to_user");
						}
					});
				},
				error: function(){
					$.writeMessage("error",$("#acceptance-error-template").html(), "#notification_dialog_accept_to_user");
				}
			});
		},
		
		cancel : function(){
			$(this.el).kernely_dialog("close");
		}
		
	})

	HolidayRequestDenyView = Backbone.View.extend({
		el: "#modal_deny_window_holiday_request",

		vid: null,
		
		events:{
			"click .denyHolidayRequest" : "denied",
			"click .cancelPopup" : "cancel"
		},
		
		initialize:function(){
			var parent = this;
			var template = $("#popup-deny-template").html();
			var titleTemplate = $("#deny-template").html();
			$(this.el).kernely_dialog({
				title: titleTemplate,
				content: template,
				eventNames:'click',
				width:"395px"
			});
		},
		
		render : function(id){
			this.vid=id;
			$("#comment_deny").val("");
			$(this.el).kernely_dialog("open");
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
						url : "/holiday/managers/request/comment/" + parent.vid,
						data : {comment: $("#comment_deny").val()},
						dataType: "json",
						success : function(){
							$(parent.el).kernely_dialog("close");
							var successHtml = $("#holiday-deny-template").html();				
							$.writeMessage("success",successHtml);
							$("#comment_deny").val("");
							tableView1.reload();
							for(var view in tableStatuedGroup){
								tableStatuedGroup[view].reload();
							}
						},
						error: function(){
							$.writeMessage("error",$("#refusal-error-template").html(), "#notification_dialog_deny_to_user");
						}
					});
				},
				error: function(){
					$.writeMessage("error",$("#refusal-error-template").html(), "#notification_dialog_deny_to_user");
				}
			});
		},
		
		cancel : function(){
			$(this.el).kernely_dialog("close");
		}
	})
	
	HolidayManagerVisualizeView = Backbone.View.extend({
		el:"#modal_visualize_window_holiday_request",
		
		managerRequestView : null,
		vid : null,
		data : null,
		listDay : null,
		
		events:{
			"click #button_accepted" : "acceptModal",
			"click #button_denied" : "denyModal",
			"click #button_close" : "close"
		},
		
		initialize: function(){
			var parent = this;
			var template = $("#popup-visualize-template").html();
			var titleTemplate = $("#visualize-template").html();
			$(this.el).kernely_dialog({
				title: titleTemplate,
				content: template,
				eventNames:'click',
				width:"768px"
			});
		},
		
		render: function(id){
			this.vid=id;
			var parent =this;
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
								url:"/holiday/managers/request/construct",
								data:{dateBegin:dates[0], dateEnd:dates[1]},
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
			$(this.el).kernely_dialog("open");
			return this;
		},
		
		close: function(){
			$(this.el).kernely_dialog("close");
		},
		
		acceptModal:function(){
			var parent = this;
			$.ajax({
				url : "/holiday/managers/request/accept/" + this.vid,
				success : function(){
					$.ajax({
						url : "/holiday/managers/request/comment/" + parent.vid,
						data : {comment: $("#comment_visu").val()},
						dataType: "json",
						success : function(){
							$(parent.el).kernely_dialog("close");
							var successHtml = $("#holiday-accept-template").html();				
							$.writeMessage("success",successHtml);
							$("#comment_visu").val("");
							tableView1.reload();
							for(var view in tableStatuedGroup){
								tableStatuedGroup[view].reload();
							}
						},
						error: function(){
							$.writeMessage("error",$("#acceptance-error-template").html(), "#notification_dialog_visu_to_user");
						}
					});
				},
				error: function(){
					$.writeMessage("error",$("#acceptance-error-template").html(), "#notification_dialog_visu_to_user");
				}
			});
		},
		
		denyModal:function(){
			var parent = this ; 
			$.ajax({
				url : "/holiday/managers/request/deny/" + this.vid,
				success : function(){
					$.ajax({
						url : "/holiday/managers/request/comment/" + parent.vid,
						data : {comment: $("#comment_visu").val()},
						dataType: "json",
						success : function(){
							$(parent.el).kernely_dialog("close");
							var successHtml = $("#holiday-deny-template").html();				
							$.writeMessage("success",successHtml);
							$("#comment_visu").val("");
							tableView1.reload();
							for(var view in tableStatuedGroup){
								tableStatuedGroup[view].reload();
							}
						},
						error: function(){
							$.writeMessage("error",$("#refusal-error-template").html(), "#notification_dialog_visu_to_user");
						}
					});
				},
				error: function(){
					$.writeMessage("error",$("#refusal-error-template").html(), "#notification_dialog_visu_to_user");
				}
			});
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
					dateTake[iDate] = moment(this.day).format("L");
					iDate++;
				});
			}
			else {
				dateTake[iDate] = moment(this.listDays.holidayDetailDTO.day).format("L");
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
					lineHeader.append($(new HolidayRequestDayView(headerList[cptHeaderList + (nPath * 5)], true, true, false, -1).render().el));
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
					lineMorning.append($(new HolidayRequestDayView(headerList[cptHeaderList + (nPath * 5)], morningList[cptMorningList + (nPath * 5)], false, true, false, dateTake, parent.listDays).render().el));
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
					lineAfternoon.append($(new HolidayRequestDayView(headerList[cptHeaderList + (nPath * 5)], afternoonList[cptAfternoonList + (nPath * 5)], false, false, true, dateTake, parent.listDays).render().el));
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

		day : null,
		details:null,
		available : null,
		color : null,
		isHeader: false,
		// We just specify if morning, if this is false, and header too ,this is afternoon
		isMorning: false,
		isAfternoon: false,
		isColored : false,
		

		events:{
		},

		initialize: function(day, available, header, morning, afternoon, take, details){
			this.day = day;
			this.details = details;
			this.available = available;
			this.isHeader = header;
			this.isMorning = morning;
			this.isAfternoon = afternoon;
			for (xDate in take){
				if (this.details.holidayDetailDTO.length > 1){
					if (take[xDate]==this.day && this.isMorning.toString() == this.details.holidayDetailDTO[xDate].am
						|| take[xDate]==this.day && this.isAfternoon.toString() == this.details.holidayDetailDTO[xDate].pm){
						this.color=details.holidayDetailDTO[xDate].color;
						this.isColored = true ;
					}
				}
				else{
					if (take[xDate]==this.day && this.isMorning.toString() == this.details.holidayDetailDTO.am
						|| take[xDate]==this.day && this.isAfternoon.toString() == this.details.holidayDetailDTO.pm){
						this.color=details.holidayDetailDTO.color;
						this.isColored = true ;
					}
				}
			}
		},
		
			
		render: function(){
			if(this.isHeader){
				$(this.el).text(this.day);
				$(this.el).addClass("day-header-part");
			}
			else{
				if(this.available == "true"){
					if(this.isMorning){
						$(this.el).addClass("am-part");
						if (this.isColored == true && this.isHeader == false && this.isMorning == true){
							$(this.el).css('background-color', this.color);
						}
					}
					if(this.isAfternoon){
						$(this.el).addClass("pm-part");
						if (this.isColored == true && this.isHeader == false && this.isAfternoon == true){
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
		mainView = new HolidayManagerRequestPageView();
		mainView.render();
	}
	return self;
})

$(function() {
	console.log("Starting holiday manager request application")
	new AppHolidayManagerRequest(jQuery).start();
})