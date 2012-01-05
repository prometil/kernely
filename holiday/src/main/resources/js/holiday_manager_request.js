AppHolidayManagerRequest = (function($){
	var lineSelected = null;

	HolidayManagerRequestPageView = Backbone.View.extend({
		el:"#request-manager-main",
		events: {
			
		},
	
		initialize: function(){
			
		},
		
		render:function(){
			var table = new HolidayManagerRequestPendingTableView();
			var table = new HolidayManagerRequestTableView();
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
			var template = '<td>{{from}}</td><td>{{requesterComment}}</td><td>{{beginDate}}</td><td>{{endDate}}</td>';
			var view = {from : this.vfrom, requesterComment : this.vrequesterComment, beginDate : this.vbegin, endDate : this.vend};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			$(this.el).appendTo($("#manager_pending_request_table"));
			return this;
		}
	})
	
	HolidayManagerRequestTableLineView = Backbone.View.extend({
		tagName: "tr",
		className:"manager_pending_request_table_line",
		
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
			var template = '<td>{{from}}</td><td>{{requesterComment}}</td><td>{{managerComment}}</td><td>{{beginDate}}</td><td>{{endDate}}</td><td>{{status}}</td>';
			var view = {from : this.vfrom, requesterComment : this.vrequesterComment, managerComment : this.vmanagerComment, beginDate : this.vbegin, endDate : this.vend, status : this.vstatus};
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
			this.initialize();
			this.render();
		},
		
		render: function(){
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