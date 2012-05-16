AppHolidayUserRequest = (function($){
	var lineSelected = null;
	var tableView1 = null;
	var tableView2 = null;
	var buttonView = null;
	
	HolidayUserRequestPageView = Backbone.View.extend({
		el:"#request-manager-main",
		events: {
			
		},
	
		initialize: function(){
			
		},
		
		render:function(){
			tableView1 = new HolidayUserRequestPendingTableView().render();
			tableView2 = new HolidayUserRequestTableView().render();
			buttonView = new HolidayUserButtonsView().render();
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
	
	HolidayUserRequestTableView = Backbone.View.extend({
		el:"#user_request_table",
		
		table:null,
		initialize:function(){
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
				url:"/holiday/all/status",
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