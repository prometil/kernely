AppHolidayManagerRequest = (function($){
	var lineSelected = null;
	var tableView1 = null;
	var tableView2 = null;
	var buttonView = null;
	
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
		},
		
		initialize:function(){
			this.viewAccept = new HolidayRequestAcceptView();
			this.viewDeny = new HolidayRequestDenyView();
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
			this.viewAccept.setFields(lineSelected.vid, lineSelected.vbeginDate, lineSelected.vendDate, lineSelected.vuser, lineSelected.vrequesterComment);
			this.viewAccept.render();
		},
		
		denyModal:function(){
 			this.showModalWindow();
 			this.viewDeny.setFields(lineSelected.vid, lineSelected.vbeginDate, lineSelected.vendDate, lineSelected.vuser, lineSelected.vrequesterComment);
			this.viewDeny.render();
		},
		
		render:function(){
			
		}
	})

	
	HolidayRequestAcceptView = Backbone.View.extend({
		el: "#modal_window_holiday_request",
		
		vid: null,
		vfrom : null,
		vrequesterComment : null,
		vbegin : null,
		vend : null,
		
		events:{
			"click .closeModal" : "closemodal",
			"click .validateHolidayRequest" : "accepted"
		},
		
		initialize:function(id, beginDate, endDate, user, requesterComment){
			this.vid=id;
			this.vbegin=beginDate;
			this.vend=endDate;
			this.vfrom=user;
			this.vrequesterComment=requesterComment;
		},
		
		setFields:function(id, beginDate, endDate, user, requesterComment){
			this.vid=id;
			this.vbegin=beginDate;
			this.vend=endDate;
			this.vfrom=user;
			this.vrequesterComment=requesterComment;
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
		vfrom : null,
		vrequesterComment : null,
		vbegin : null,
		vend : null,
		
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
		
		setFields:function(id, beginDate, endDate, user, requesterComment){
			this.vid=id;
			this.vbegin=beginDate;
			this.vend=endDate;
			this.vfrom=user;
			this.vrequesterComment=requesterComment;
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
							tableView1.reload();
							tableView2.reload();
						}
					});
				}
			});
			this.closemodal();
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