AppHolidayDonation = (function($){
	
	var historyTable = null;
	var newDonationView = null;
	
	HolidayDonationMainView = Backbone.View.extend({
		el:"#donation-main",

		events:{
			"click #new_donation_button" : "openWindow"
		},
		
		initialize: function(){
		
		},
		
		openWindow: function(){
			newDonationView.render();
		},
		
		render: function(){
			historyTable = new HolidayTableMainView().render();
			newDonationView = new HolidayDonationCreateView();
			return this;
		}
		
	})
	
	HolidayTableMainView = Backbone.View.extend({
		el:"#donation_history_table",
		
		table:null,
		
		initialize:function(){
			var parent = this;
			var templateReceiverColumn = $("#table-receiver-column").text();
			var templateDateColumn = $("#table-date-column").text();
			var templateAmountColumn = $("#table-amount-column").text();
			var templateTypeColumn = $("#table-type-column").text();
			var templateCommentColumn = $("#table-comment-column").text();
			this.table = $(parent.el).kernely_table({
				columns:[
				       {"name":templateReceiverColumn, "style":""},
				       {"name":templateDateColumn, "style":""},
				       {"name":templateAmountColumn, "style":""},
				       {"name":templateTypeColumn, "style":""},
				       {"name":templateCommentColumn, "style":""}
				],
				idField:"id",
				elements:["receiverUsername", "date", "amount", "typeInstanceName", "comment"],
				eventNames:["click"],
				events:{
					"click": parent.selectLine
				},
				editable: false
			});
		},
		selectLine : function(e){
			
		},
		reload: function(){
			this.render();
		},
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/holiday/donation/list",
				dataType:"json",
				success: function(data){
					if(data != null){
						var dataDonation = data.holidayDonationDTO;
						if($.isArray(dataDonation)){
							$.each(dataDonation, function(){
								this.date = moment(this.date).format("L");
							});
						}
						else{
							dataDonation.date = moment(dataDonation.date).format("L");
						}
						parent.table.reload(dataDonation);
					}
					else{
						parent.table.clear();
						parent.table.noData();
					}
				},
				error: function(){
					$.writeMessage("error",$("#donation-loading-error-template").html());
				}
			});
			return this;
		}
	})
	
	HolidayDonationCreateView = Backbone.View.extend({
		el:"#modal_donation_window",
		
		vid : null,
		
		events:{
			"click #button_cancel" : "close",
			"click #validate-donation" : "confirmCreate",
			"change #user-selector" : "changeType"
		},
		
		initialize: function(){
			var parent = this;
			var template = $("#new-donation-window-template").html();
			var titleTemplate = $("#new-donation-title").html();
			$(this.el).kernely_dialog({
				title: titleTemplate,
				content: template,
				eventNames:'click',
				width:"300px"
			});
		},
		
//		create:function(){
//			var amount = $("#donation-amount").val();
//			if(amount > 10){
//				$.writeMessage("error",$("#too-high-amount-template").html(), "#notification_dialog_to_user");
//			}
//			else{
//			
//				var template = $("#create-ask-template").html();
//				var view = {};
//				var html = Mustache.to_html(template, view);
//				
//				$.kernelyConfirm($("#create-confirm-title-template").text(),html,this.confirmCreate);
//			}
//		},
		
		confirmCreate: function(){
			var parent = this;
			var userId = $("#user-selector option:selected").val();
			var typeInstanceId = $("#holiday-type-selector option:selected").val();
			var amount = $("#donation-amount").val();
			
			if(amount > 10){
				$.writeMessage("error",$("#too-high-amount-template").html(), "#notification_dialog_to_user");
			}
			else{
			
				var json = '{' +
					'"comment" : "' + $("#donation-comment").val().replace('\n', "\\n") + '",' +
					'"amount": "' + amount + '",' +
					'"receiverId": "' + userId + '",' +
					'"typeInstanceId":"' + typeInstanceId + '"' +
				'}';
	
				$.ajax({
					type: "POST",
					url : "/holiday/donation/create",
					data:json,
					dataType:"json",
					processData: false,
					contentType: "application/json; charset=utf-8",
					success : function(data){
						historyTable.reload();
						parent.close();
					},
					error: function(data){
						$.writeMessage("error",$("#error-donation-creation-template").html(), "#notification_dialog_to_user");
					}
				});
			}
		},
		
		changeType: function(){
			var id = $("#user-selector option:selected").val();
			var parent =this;
			$.ajax({
				url : "/holiday/donation/balances",
				data:{userId : id},
				dataType:"json",
				success : function(data){
					var typeSelector = $("#holiday-type-selector");
					if(data != null){
						typeSelector.empty();
						if($.isArray(data.holidayTypeDTO)){
							$.each(data.holidayTypeDTO, function(){
								typeSelector.append($('<option>', { value : this.id })
								          .text(this.name));
							});
						}
						else{
							typeSelector.append($('<option>', { value : data.holidayTypeDTO.id })
							          .text(data.holidayTypeDTO.name));
						}
						typeSelector.removeAttr("disabled");
					}
					else{
						typeSelector.empty();
						typeSelector.attr("disabled", "disabled");
					}
				},
				error : function(data){
					$.writeMessage("error",$("#error-loading-types-template").html(), "#notification_dialog_to_user");
				}
			});
			return this;
		},
		
		render: function(id){
			this.vid=id;
			var parent =this;
			
			$(this.el).kernely_dialog("open");
			
			$("#donation-amount").val("");
			$("#donation-comment").val("");
			
			$.ajax({
				url : "/holiday/donation/users",
				dataType:"json",
				success : function(data){
					var userSelector = $("#user-selector");
					userSelector.empty();
					if($.isArray(data.userDTO)){
						var name;
						$.each(data.userDTO, function(){
							name = this.userDetails.firstname + " " + this.userDetails.lastname;
							userSelector.append($('<option>', { value : this.id })
							          .text(name));
						});
					}
					else{
						var name = data.userDTO.userDetails.firstname + " " + data.userDTO.userDetails.lastname;
						userSelector.append($('<option>', { value : data.userDTO.id })
						          .text(name));
					}
					parent.changeType();
				},
				error : function(data){
					$.writeMessage("error",$("#error-loading-users-template").html(), "#notification_dialog_to_user");
				}
			});
			$(this.el).kernely_dialog("open");
			return this;
		},
		
		close: function(){
			$(this.el).kernely_dialog("close");
		}	
	})	
	
	
	var self = {};
	self.start = function(){
		new HolidayDonationMainView().render();
	}
	return self;
})

$(function() {
	console.log("Starting holiday donation application")
	new AppHolidayDonation(jQuery).start();
})