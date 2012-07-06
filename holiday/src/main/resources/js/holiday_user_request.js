AppHolidayUserRequest = (function($){
	var lineSelectedPending = null;
	var lineSelectedStatued = null;
	var tablePendingView = null;
	var tableStatuedGroup = new Array();
	var buttonView = null;
	var balanceSummary = null;
	var viewVisualize = null;

	var dayNameCounter = 1;
	var cellDayMorningCounter = 0;
	var cellDayAfternoonCounter = 1;
	var MORNING_PART = 1;
	var AFTERNOON_PART = 2;
	
	HolidayUserRequestPageView = Backbone.View.extend({
		el:"#request-manager-main",
		events: {
			
		},
	
		initialize: function(){
			viewVisualize = new HolidayVisualizeView();
		},
		
		render:function(){
			tablePendingView = new HolidayUserRequestPendingTableView().render();
			new HolidayUserYearContainerView();
			buttonView = new HolidayUserButtonsView().render();
			new HolidayCancelPendingButtonView();
			new HolidayCancelStatuedButtonView();
			new HolidayVisualizePendingButtonView();
			new HolidayVisualizeStatuedButtonView();
			balanceSummary = new HolidayBalanceSummaryView().render();
		}
	})
	
	HolidayBalanceSummaryView = Backbone.View.extend({
		el:"#balance-summary",
		
		render:function(){
			var parent = this;
			$(parent.el).empty();
			$.ajax({
				type: 'GET',
				url:"/holiday/balances",
				dataType: "json",
				success: function(data){
					if(data != null){
						if(data.calendarBalanceDetailDTO != null && data.calendarBalanceDetailDTO.length > 1){
							$.each(data.calendarBalanceDetailDTO, function(){
								$(parent.el).append(new HolidayRequestColorPickerCell(this.nameOfType, this.nbAvailable, this.color, this.idOfType, this.limitOfAnticipation).render().el);
			                });
						}
						else{
							$(parent.el).append(new HolidayRequestColorPickerCell(data.calendarBalanceDetailDTO.nameOfType, data.calendarBalanceDetailDTO.nbAvailable, data.calendarBalanceDetailDTO.color, data.calendarBalanceDetailDTO.idOfType, data.calendarBalanceDetailDTO.limitOfAnticipation).render().el);				
						}
					}
					else{
						$(parent.el).html($("#no-balance-template").html());
					}
				},
				error: function(data){
					console.log(data);
					$.writeMessage("error",$("#balance-loading-error-template").html());
				}
			});
			return this;
		}
	})
	
	HolidayUserRequestPendingTableView = Backbone.View.extend({
		el:"#user_pending_request_table",
		
		table:null,
		initialize:function(){
			var parent = this;
			var templateCommentColumn = $("#table-request-comment-column").text();
			var templateBeginColumn = $("#table-begin-date-column").text();
			var templateEndColumn = $("#table-end-date-column").text();
			this.table = $(parent.el).kernely_table({
				columns:[
				       {"name":templateCommentColumn, "style":""},
				       {"name":templateBeginColumn, "style":"text-center"},
				       {"name":templateEndColumn, "style":"text-center"}
				],
				idField:"id",
				elements:["requesterComment", "beginDate", "endDate"],
				eventNames:["click"],
				events:{
					"click": parent.selectLine
				},
				editable: true
			});
		},
		selectLine : function(e){
			
			$("#button_cancel_pending").removeAttr('disabled');
			$("#button_visualize_pending").removeAttr('disabled');
			lineSelectedPending = e.data.line;
		},
		reload: function(){
			this.render();
		},
		
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/holiday/all/pending",
				dataType:"json",
				success: function(data){
					if(data != null){
						var dataRequest = data.holidayRequestDTO;
						if($.isArray(dataRequest)){
							$.each(dataRequest, function(){
								this.beginDate = moment(this.beginDate).format("L");
								this.endDate = moment(this.endDate).format("L");
							});
						}
						else{
							dataRequest.beginDate = moment(dataRequest.beginDate).format("L");
							dataRequest.endDate = moment(dataRequest.endDate).format("L");
						}
						parent.table.reload(dataRequest);
					}
					else{
						parent.table.clear();
						parent.table.noData();
					}
				},
				error: function(){
					$.writeMessage("error",$("#pending-loading-error-template").html());
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
				url:"/holiday/years",
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
			$(this.el).find(".content-block-year").html(new HolidayUserRequestTableView(this.year).render().el);
			
			if(this.initVisible){
				if(!$(this.el).find(".content-block-year").is(":visible")){
					$(this.el).find(".content-block-year").css("display", "block");
				}
			}
			return this;
		}
	})
	
	HolidayUserRequestTableView = Backbone.View.extend({
		tagName:"table",
		className:"kernely_table",
		
		table:null,
		year : null,
		
		initialize:function(year){
			this.year = year;
			var parent = this;
			var templateManagerColumn = $("#table-manager-column").text();
			var templateManagerCommentColumn = $("#table-manager-comment-column").text();
			var templateCommentColumn = $("#table-request-comment-column").text();
			var templateBeginColumn = $("#table-begin-date-column").text();
			var templateEndColumn = $("#table-end-date-column").text();
			
			this.table = $(parent.el).kernely_table({
				columns:[
				       {"name":templateCommentColumn, style:"comment-column"},
				       {"name":templateBeginColumn, style:"text-center"},
				       {"name":templateEndColumn, style:"text-center"},
				       {"name":"", style:["text-center", "icon-column"]},
				       {"name":templateManagerColumn, style:""},
				       {"name":templateManagerCommentColumn, style:"comment-column"},
				       {"name":"", style:"invisible"}
				],
				idField:"id",
				elements:["requesterComment", "beginDate", "endDate", "status", "manager", "managerComment", "cancelable"],
				eventNames:["click"],
				events:{
					"click": parent.selectLine
				},
				editable:true,
				group:1
			});
			tableStatuedGroup.push(parent);
		},
		
		selectLine : function(e){
			if(e.data.data.cancelable == "true"){
				$("#button_cancel_statued").removeAttr('disabled');
			}
			else{
				$("#button_cancel_statued").attr('disabled', 'disabled');
			}
			lineSelectedStatued = e.data.line;
			$("#button_visualize_statued").removeAttr('disabled');
		},
		
		reload: function(){
			this.render();
		},
		
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/holiday/all/status/date",
				data:{year : parent.year},
				dataType:"json",
				success: function(data){
					if(data != null){
						var dataRequest = data.holidayRequestDTO;
						if($.isArray(dataRequest)){
							$.each(dataRequest, function(){
								if(this.status == 1 || this.status == 3){
									this.status = "<img src='/images/icons/accept_icon.png' />";
								}
								else if(this.status == 0){
									this.status = "<img src='/images/icons/deny_icon.png' />";
								}
								this.beginDate = moment(this.beginDate).format("L");
								this.endDate = moment(this.endDate).format("L");
							});
						}
						else{
							if(dataRequest.status == 1 || dataRequest.status == 3){
								dataRequest.status = "<img src='/images/icons/accept_icon.png' />";
							}
							else if(dataRequest.status == 0){
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
				},
				error: function(){
					$.writeMessage("error",$("#statued-loading-error-template").html());
				}
			});
			return this;
		}
	})
	
	HolidayUserButtonsView = Backbone.View.extend({
		el:"#holiday_button_container",		
		
		viewCancel : null,
		vid: null,
		vrequesterComment : null,
		vbegin : null,
		vend : null,
		
		formRequest:null,
		
		
		initialize:function(id, beginDate, endDate, requesterComment){
			this.vid=id;
			this.vbegin=beginDate;
			this.vend=endDate;
			this.vrequesterComment=requesterComment;
		},
		
		events: {
			"click #new_request" : "newRequest"
		},
		
		setFields:function(id, beginDate, endDate,requesterComment){
			this.vid=id;
			this.vbegin=beginDate;
			this.vend=endDate;
			this.vrequesterComment=requesterComment;
		},
		
		render:function(){
			var parent = this;
			this.formRequest = $.kernelyDialog($("#new-request-title-template").text(), "#new-request-form", null, 270);
			$("#cancel-request-form").bind("click",function(){$(parent.formRequest).kernely_dialog( "close" );});
			var dates = $( "#from, #to" ).datepicker({
				showOn: "both",
				buttonImage: "/images/icons/calendar_icon.png",
				buttonImageOnly: true,
				changeMonth: false,
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
			$.datepicker.setDefaults($.datepicker.regional[lang+"-"+country]);
			$(this.formRequest).find("form").submit(function(){
				if($("#from").val() == ""){
					$.writeMessage("error",$("#from-date-error-template").html(), "#notification_dialog_to_user");
					return false;
				}
				else if($("#to").val() == ""){
					$.writeMessage("error",$("#to-date-error-template").html(), "#notification_dialog_to_user");
					return false;
				}
				else{
					return true;
				}
			});
			return this;
		},
		
		newRequest : function(){
			$(this.formRequest).kernely_dialog( "open" );
			$(this.formRequest).find("input:text").val("");
		}		
		
	})
	
	HolidayCancelPendingButtonView = Backbone.View.extend({
		el:"#button_cancel_pending",
		
		events: {
			"click" : "canceled"
		},
	
		canceled:function(){
			var template = $("#cancel-ask-template").html();
			var view = {};
			var html = Mustache.to_html(template, view);
			
			$.kernelyConfirm($("#holiday-cancel-template").text(),html,this.confirmCancel);
		},
		
		confirmCancel: function(){
			parent = this;
			$.ajax({
				url:"/holiday/cancel",
				data:{id:lineSelectedPending},
				success: function(){
					var successHtml = $("#holiday-canceled-template").html();	
					$.writeMessage("success",successHtml);
					tablePendingView.reload();
					balanceSummary.render();
					$("#button_cancel_pending").attr('disabled','disabled');
				},
				error: function(data){
					$.writeMessage("error",$("#cancel-error-template").html());
				}
			});
		}
	
		
	})
	
	HolidayCancelStatuedButtonView = Backbone.View.extend({
		el:"#button_cancel_statued",
		
		events: {
			"click" : "canceled"
		},
	
		canceled:function(){
			var template = $("#cancel-ask-template").html();
			var view = {};
			var html = Mustache.to_html(template, view);
			
			$.kernelyConfirm($("#holiday-cancel-template").text(),html,this.confirmCancel);
		},
		
		confirmCancel: function(){
			parent = this;
			$.ajax({
				url:"/holiday/cancel",
				data:{id:lineSelectedStatued},
				success: function(){
					var successHtml = $("#holiday-canceled-template").html();	
					$.writeMessage("success",successHtml);
					for(var view in tableStatuedGroup){
						tableStatuedGroup[view].reload();
					}
					balanceSummary.render();
					$("#button_cancel_statued").attr('disabled','disabled');
				},
				error: function(data){
					$.writeMessage("error",$("#cancel-error-template").html());
				}
			});
		}
	
		
	})
	
	HolidayVisualizePendingButtonView = Backbone.View.extend({
		el:"#button_visualize_pending",
		
		events: {
			"click" : "visualize"
		},
		
		visualize:function(){
			viewVisualize.render(lineSelectedPending);
		}
	})
	
	HolidayVisualizeStatuedButtonView = Backbone.View.extend({
		el:"#button_visualize_statued",
		
		events: {
			"click" : "visualize"
		},
		
		visualize:function(){
			viewVisualize.render(lineSelectedStatued);
		}
	})
	
	HolidayVisualizeView = Backbone.View.extend({
		el:"#modal_visualize_window_holiday_request",
		
		vid : null,
		
		events:{
			"click #button_close" : "close"
		},
		
		initialize: function(){
			var parent = this;
			var template = $("#popup-visualize-template").html();
			var titleTemplate = $("#new-donation-window-title").html();
			$(this.el).kernely_dialog({
				title: titleTemplate,
				content: template,
				eventNames:'click',
				width:"768px"
			});
		},
		
		render: function(id){
			$(this.el).find("#calendarContent").empty();
			this.vid=id;
			var parent =this;
			$.ajax({
				url : "/holiday/visualize",
				data:{idrequest: parent.vid},
				dataType:"json",
				success : function(data){
					new HolidayRequestTableView(data).render();
				},
				error : function(data){
				}
			});
			$(this.el).kernely_dialog("open");
			return this;
		},
		
		close: function(){
			$(this.el).kernely_dialog("close");
		}	
	})
	
	HolidayRequestTableView = Backbone.View.extend({
		el:"#calendar-table",
		
		data: null,
		
		initialize: function(data){
			this.data = data;
		},
		
		render:function(){
			var tr = document.createElement("tr");
			var td;
			for(var i = 1; i <= 5; i++){
				td = document.createElement("td");
				$(td).addClass("day-header-part");
				$(td).text($.datepicker.regional[lang + '-' + country].dayNames[i]);
				$(tr).append($(td));
			}
			$(this.el).find("thead").html($(tr));
			
			new HolidayRequestCalendarView(this.data).render();
		}
	})
	
	HolidayRequestCalendarView = Backbone.View.extend({
		el:"#calendarContent",
		data : null,
		
		initialize: function(data){
			this.data = data;
		},
		
		render: function(){
			$(this.el).empty();
			var parent = this;
			var view = null;
			
			// Variables declarations :
			// List of the headers : contains days
			var headerList = new Array();
			// List of the available mornings
			var morningList = new Array();
			// List of the available afternoons
			var afternoonList = new Array();
			// Lists of the color of the cell
			var morningColorList = new Array();
			var afternoonColorList = new Array();
			// List of the name of type coloring cells
			var morningNameTypeList = new Array();
			var afternoonNameTypeList = new Array();
			
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
					
					morningColorList[cptBuildingList] = this.morningHolidayTypeColor;
					afternoonColorList[cptBuildingList] = this.afternoonHolidayTypeColor;
					
					morningNameTypeList[cptBuildingList] = this.morningHolidayTypeName;
					afternoonNameTypeList[cptBuildingList] = this.afternoonHolidayTypeName;
					
					cptBuildingList ++;
				});
			}
			else{
				headerList[0] = this.data.days.day;
				morningList[0] = this.data.days.morningAvailable;
				afternoonList[0] = this.data.days.afternoonAvailable;
				morningColorList[0] = this.data.days.morningHolidayTypeColor;
				afternoonColorList[0] = this.data.days.afternoonHolidayTypeColor;
				morningNameTypeList[0] = this.data.days.morningHolidayTypeName;
				afternoonNameTypeList[0] = this.data.days.afternoonHolidayTypeName;
				
			}
			
			
			while (nPath < this.data.nbWeeks){
				// Create tr element
				lineHeader = $("<tr>", {
					class:'day-header'
				});
				// Adds all the headers for the week
				while(cptHeaderList < 5){
					lineHeader.append($(new HolidayRequestDayView(headerList[cptHeaderList + (nPath * 5)], null, true).render().el));
					dayNameCounter ++;
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
					lineMorning.append($(new HolidayRequestDayView(headerList[cptHeaderList + (nPath * 5)], null, false, MORNING_PART, morningColorList[cptHeaderList + (nPath * 5)], morningNameTypeList[cptHeaderList + (nPath * 5)]).render().el));
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
					lineAfternoon.append($(new HolidayRequestDayView(headerList[cptHeaderList + (nPath * 5)], null, false, AFTERNOON_PART,afternoonColorList[cptHeaderList + (nPath * 5)], afternoonNameTypeList[cptHeaderList + (nPath * 5)]).render().el));
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
				dayNameCounter = 1;
				// Increases week created
				nPath ++;
			}
			
			return this;
		}
	})

	HolidayRequestDayView = Backbone.View.extend({
		tagName: "td",

		day : null,
		week : null,
		isHeader: false,
		partOfDay: null,
		color: null,
		name : null,
		
		initialize: function(day, week, header, part, color, name){
			this.day = day;
			this.week = week;
			this.isHeader = header;	
			this.partOfDay = part;
			this.color = color;
			this.name = name;
		},
		
		render: function(){
			if(this.isHeader){
				$(this.el).text(this.day);
				$(this.el).addClass("day-header-part");
			}
			else{
				if(this.color != null){
					$(this.el).css("background-color", this.color);
				}
				else{
					$(this.el).css("background-color", "inherit");
				}
				if(this.name != null){
					$(this.el).text(this.name);
				}
				$(this.el).addClass("day-part");
			}
	
			return this;
		}

	})
	
	HolidayRequestColorPickerCell = Backbone.View.extend({
		tagName:"div",
		className: "balance-cell-display",
		
		color:null,
		name:null,
		nbAvailable:0.0,
		limitOfAnticipation:0.0,
		idType: null,
		
		initialize : function(name, avail, color, idType, anticipation){
			this.color = color;
			this.name = name;
			this.nbAvailable = avail;
			this.idType = idType;
			this.limitOfAnticipation = anticipation * (-1);
		},
		
		render : function(){
			var template;
            var view;
            
			template = $("#balance-cell-template").html();
			view =  {name: this.name, available: this.nbAvailable};
			
            var html = Mustache.to_html(template, view);
            $(this.el).html(html);
            $(this.el).find(".balance-cell-amount").css('background-color', this.color);
			return this;
		}
		
	})
	
	var self = {};
	self.start = function(){
		new HolidayUserRequestPageView().render();
	}
	return self;
})

$(function() {
	console.log("Starting holiday user request application")
	new AppHolidayUserRequest(jQuery).start();
})