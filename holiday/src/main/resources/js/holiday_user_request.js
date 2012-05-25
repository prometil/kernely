AppHolidayUserRequest = (function($){
	var lineSelected = null;
	var tableView1 = null;
	var buttonView = null;
	
	HolidayUserRequestPageView = Backbone.View.extend({
		el:"#request-manager-main",
		events: {
			
		},
	
		initialize: function(){
			
		},
		
		render:function(){
			tableView1 = new HolidayUserRequestPendingTableView().render();
			new HolidayUserYearContainerView();
			buttonView = new HolidayUserButtonsView().render();
			$.ajax({
				type: 'GET',
				url:"/holiday/balances",
				dataType: "json",
				success: function(data){
					if(data.calendarBalanceDetailDTO != null && data.calendarBalanceDetailDTO.length > 1){
						$.each(data.calendarBalanceDetailDTO, function(){
							$("#balance-summary").append(new HolidayRequestColorPickerCell(this.nameOfType, this.nbAvailable, this.color, this.idOfType, this.limitOfAnticipation).render().el);
		                });
					}
					else{
						$("#balance-summary").append(new HolidayRequestColorPickerCell(data.calendarBalanceDetailDTO.nameOfType, data.calendarBalanceDetailDTO.nbAvailable, data.calendarBalanceDetailDTO.color, data.calendarBalanceDetailDTO.idOfType, data.calendarBalanceDetailDTO.limitOfAnticipation).render().el);				
					}
				}
			});
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
				elements:["requesterComment", "beginDateString", "endDateString"],
				eventNames:["click"],
				events:{
					"click": parent.selectLine
				}
			});
		},
		selectLine : function(e){
			$("#button_canceled").removeAttr('disabled');
			lineSelected = e.data.line;
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
						parent.table.reload(dataRequest);
					}
					else{
						parent.table.clear();
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
			var templateCommentColumn = $("#table-request-comment-column").text();
			var templateBeginColumn = $("#table-begin-date-column").text();
			var templateEndColumn = $("#table-end-date-column").text();
			
			this.table = $(parent.el).kernely_table({
				columns:[
				       {"name":templateManagerColumn, style:""},
				       {"name":templateCommentColumn, style:""},
				       {"name":templateBeginColumn, style:"text-center"},
				       {"name":templateEndColumn, style:"text-center"},
				       {"name":"", style:["general-bg", "text-center", "no-border-right", "no-border-top", "no-border-bottom", "icon-column"]}
				],
				idField:"id",
				elements:["managerComment", "requesterComment", "beginDateString", "endDateString", "status"]
			});
		
			
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
							});
						}
						else{
							if(dataRequest.status == 1){
								dataRequest.status = "<img src='/images/icons/accept_icon.png' />";
							}
							else{
								dataRequest.status = "<img src='/images/icons/deny_icon.png' />";
							}
						}
						parent.table.reload(dataRequest);
					}
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
			"click #button_canceled" : "canceled",
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
			this.formRequest = $.kernelyDialog("#new-request-form",150,300);
			$("#cancel-request-form").bind("click",function(){$(parent.formRequest).kernely_dialog( "close" );});
			var dates = $( "#from, #to" ).datepicker({
				showOn: "both",
				buttonImage: "/images/icons/calendar_icon.png",
				buttonImageOnly: true,
				defaultDate: "+1w",
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
			var lang = $("#locale-lang").html();
			var country = $("#locale-country").html();
			$.datepicker.setDefaults($.datepicker.regional[lang+"-"+country]);
			
			return this;
		},
		
		newRequest : function(){
			$(this.formRequest).kernely_dialog( "open" );

		},
		
		canceled:function(){
			var template = $("#cancel-ask-template").html();
			var view = {};
			var html = Mustache.to_html(template, view);
			
			$.kernelyConfirm($("#holiday-cancel-template").text(),html,this.confirmCancel);
		},
		confirmCancel: function(){
			$.ajax({
				url:"/holiday/cancel/" + lineSelected,
				success: function(){
					var successHtml = $("#holiday-canceled-template").html();	
					$.writeMessage("success",successHtml);
					tableView1.reload();
					$("#button_canceled").attr('disabled','disabled');
				}
			});
		}
		
	})
	
	HolidayRequestColorPickerCell = Backbone.View.extend({
		tagName:"div",
		className: "balance-cell",
		
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