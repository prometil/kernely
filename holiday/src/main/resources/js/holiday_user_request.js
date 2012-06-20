AppHolidayUserRequest = (function($){
	var lineSelectedPending = null;
	var lineSelectedStatued = null;
	var tablePendingView = null;
	var tableStatuedGroup = new Array();
	var buttonView = null;
	var balanceSummary = null;
	
	HolidayUserRequestPageView = Backbone.View.extend({
		el:"#request-manager-main",
		events: {
			
		},
	
		initialize: function(){
		},
		
		render:function(){
			tablePendingView = new HolidayUserRequestPendingTableView().render();
			new HolidayUserYearContainerView();
			buttonView = new HolidayUserButtonsView().render();
			new HolidayCancelPendingButtonView();
			new HolidayCancelStatuedButtonView();
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
				error: function(){
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
				       {"name":templateManagerColumn, style:""},
				       {"name":templateCommentColumn, style:"comment-column"},
				       {"name":templateBeginColumn, style:"text-center"},
				       {"name":templateEndColumn, style:"text-center"},
				       {"name":"", style:["text-center", "icon-column"]},
				       {"name":templateManagerCommentColumn, style:"comment-column"},
				       {"name":"", style:"invisible"}
				],
				idField:"id",
				elements:["manager", "requesterComment", "beginDate", "endDate", "status", "managerComment", "cancelable"],
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
				lineSelectedStatued = e.data.line;
			}
			else{
				$("#button_cancel_statued").attr('disabled', 'disabled');
			}
			
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
				url:"/holiday/cancel/" + lineSelectedPending,
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
				url:"/holiday/cancel/" + lineSelectedStatued,
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