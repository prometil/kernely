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
			tableView1 = new HolidayUserRequestPendingTableView();
			tableView2 = new HolidayUserRequestTableView();
			buttonView = new HolidayUserButtonsView().render();
		}
	})
	
	HolidayUserRequestPendingTableLineView = Backbone.View.extend({
		tagName: "tr",
		className:"user_pending_request_table_line",
		
		vid: null,
		vrequesterComment : null,
		vbegin : null,
		vend : null,
		
		events: {
			"click" : "selectLine",
			"mouseover" : "overLine",
			"mouseout" : "outLine"
		},
		
		initialize: function(id, beginDate, endDate, requesterComment){
			this.vid=id;
			this.vbegin=beginDate;
			this.vend=endDate;
			this.vrequesterComment=requesterComment;		
		},
		
		selectLine : function(){
			$("#button_canceled").removeAttr('disabled');
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
			var view = {from : this.vto, requesterComment : this.vrequesterComment, beginDate : this.vbegin, endDate : this.vend};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			$(this.el).appendTo($("#user_pending_request_table"));
			return this;
		}
	})
	
	
	HolidayUserRequestTableLineView = Backbone.View.extend({
		tagName: "tr",
		className:"user_request_table_line",
		
		vid: null,
		vmanager : null,
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
		
		initialize: function(id, manager, beginDate, endDate, requesterComment, managerComment, status){
			this.vid=id;
			this.vmanager=manager;
			this.vbegin=beginDate;
			this.vend=endDate;
			this.vrequesterComment=requesterComment;
			this.vmanagerComment = managerComment;
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
			var template = '<td>{{manager}}</td><td>{{requesterComment}}</td><td>{{managerComment}}</td><td>{{beginDate}}</td><td>{{endDate}}</td><td>{{status}}</td>';
			var view = {manager : this.vmanager, requesterComment : this.vrequesterComment, managerComment : this.vmanagerComment, beginDate : this.vbegin, endDate : this.vend, status : statusTemplate};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			$(this.el).appendTo($("#user_request_table"));
			return this;
		}
	})
	
	HolidayUserRequestPendingTableView = Backbone.View.extend({
		el:"#user_pending_request_table",
		events:{
		
		},
		initialize:function(){
			$.ajax({
				type:"GET",
				url:"/holiday/all/pending",
				dataType:"json",
				success: function(data){
					if(data != null){
						if(data.holidayRequestDTO.length > 1 ){
							$.each(data.holidayRequestDTO, function (){
								var view = new HolidayUserRequestPendingTableLineView(this.id, this.beginDate, this.endDate, this.requesterComment);	
								view.render();
							});
						}
						else{
							var view = new HolidayUserRequestPendingTableLineView(data.holidayRequestDTO.id, data.holidayRequestDTO.beginDate, data.holidayRequestDTO.endDate, data.holidayRequestDTO.requesterComment);
							view.render();
						}
					}
				}
			});
		},
		
		reload: function(){
			$(".user_pending_request_table_line").html("");
			this.initialize();
			this.render();
		},
		
		render: function(){
			return this;
		}
	})
	
	HolidayUserRequestTableView = Backbone.View.extend({
		el:"#user_request_table",
		
		events:{
		
		},
		
		initialize:function(){
			$.ajax({
				type:"GET",
				url:"/holiday/all/status",
				dataType:"json",
				success: function(data){
					if(data != null){
						if(data.holidayRequestDTO.length > 1 ){
							$.each(data.holidayRequestDTO, function (){
								var view = new HolidayUserRequestTableLineView(this.id, this.manager, this.beginDate, this.endDate, this.requesterComment, this.managerComment, this.status);
								view.render();
							});
						}
						else{
							var view = new HolidayUserRequestTableLineView(data.holidayRequestDTO.id, data.holidayRequestDTO.manager, data.holidayRequestDTO.beginDate, data.holidayRequestDTO.endDate, data.holidayRequestDTO.requesterComment, data.holidayRequestDTO.managerComment, data.holidayRequestDTO.status);
							view.render();
						}
					}
				}
			});
		},
		
		reload: function(){
			$(".user_request_table_line").html("");
			this.initialize();
			this.render();
		},
		
		render: function(){
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
			this.formRequest = document.createElement("div");
			$(this.formRequest).html($("#new-request-form").html());
			$(this.formRequest).dialog({
				autoOpen: false,
				height: 150,
				width: 300,
				modal: true
			});
			
			var dates = $( "#from, #to" ).datepicker({
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
			var lang = $("#locale-lang").html();
			var country = $("#locale-country").html();
			$.datepicker.setDefaults($.datepicker.regional[lang+"-"+country]);
			
			return this;
		},
		
		newRequest : function(){
			$(this.formRequest).dialog( "open" );
		},
		
		canceled:function(){
			var template = $("#cancel-ask-template").html();
			var view = {};
			var html = Mustache.to_html(template, view);
			
			var answer = confirm(html);
			if (answer){
				$.ajax({
					url:"/holiday/cancel/" + lineSelected.vid,
					success: function(){
						var successHtml = $("#holiday-canceled-template").html();	
						$("#holiday_notifications").text(successHtml);
						$("#holiday_notifications").fadeIn(1000);
						$("#holiday_notifications").fadeOut(3000);
						tableView1.reload();
						$("#button_canceled").attr('disabled','disabled');
					}
				});
			}	
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