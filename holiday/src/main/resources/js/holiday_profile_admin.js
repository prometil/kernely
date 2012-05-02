/*
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
 */
AppHolidayAdmin = (function($){
	var lineSelected = null;
	var tableView = null;
	var typeLines = new Array();
	var arrayIndex = 0;
	var editedlines = 0; // Lines which are in edition mode
	var viewCreate = null;
	var viewUsers = null;
	
	// Updates the create/edit view
	function changeEditedLines(number){
		editedlines += number;
		if (editedlines > 0){
			$("#createHolidayProfile").attr("disabled","disabled");
		} else {
			$("#createHolidayProfile").removeAttr("disabled");
		}
	}
	
	function resetLinesMemory(){
		typeLines = new Array();
		arrayIndex = 0;
		editedlines = 0;
	}

	
	HolidayAdminTableView = Backbone.View.extend({
		el:"#holiday_admin_table",
		
		initialize:function(){
			var parent = this;
			
			var templateNameColumn = $("#table-profile-name-column").text();
			var templateTypesColumn = $("#table-profile-types-column").text();
			var templateUsersColumn = $("#table-profile-users-column").text();
			$(parent.el).kernely_table({
				columns:[templateNameColumn, templateTypesColumn, templateUsersColumn],
				editable:true
			});
		},
		
		reload: function(){
			this.render();
		},
		
		selectLine : function(e){
			$(".editButton").removeAttr('disabled');
			$(".usersButton").removeAttr('disabled');
			lineSelected = e.data.line;
		},
		
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/admin/holiday/all",
				dataType:"json",
				success: function(data){
					if (data != null){
						var dataProfile = data.holidayProfileDTO;
						if ($.isArray(dataProfile)){
							$.each(dataProfile, function(){
								this.typesString = "Details";
							});
						} else {
							dataProfile.typesString = "Details";
						}
//						if($.isArray(dataProfile.holidayTypes)){
//							$.each(dataProfile.holidayTypes, function(){
//								dataProfile.typesString += (this.name + ", "); 
//							});
//							dataProfile.typesString = dataProfile.typesString.substring(0, dataProfile.typesString.length-2);
//						}
//						else{
//							dataProfile.typesString = (dataProfile.holidayTypes.name); 
//						}
						
						$(parent.el).reload_table({
							data: dataProfile,
							idField:"id",
							elements:["name", "typesString", "nbUsers"],
							eventNames:["click"],
							events:{
								"click": parent.selectLine
							},
							editable:true
						});
						return parent;
					}
				}
			});
		}
	})	
	
	HolidayAdminButtonsView = Backbone.View.extend({
		el:"#holiday_admin_container",
		
		events: {
			"click .createButton" : "createholiday",
			"click .editButton" : "editholiday",
			"click .usersButton" : "editusers"
		},
		
		initialize: function(){
			viewCreate = this;
			viewUsers = new HolidayAdminProfileUsersView();
		},
		
		createholiday: function(){
			var parent = this;
			var template = $("#popup-holiday-admin-create-template").html();
			var view = {profilename:""}
			var html = Mustache.to_html(template,view);
			var titleTemplate = $("#create-template").html();
			$("#modal_window_holiday").kernely_dialog({
				title: titleTemplate,
				content: html,
				eventNames:'click',
				events:{
					'click': [
					          {"el":"#newHolidayButton", "event":parent.newEmptyHolidayLine},
					          {"el":"#createHolidayProfile", "event":parent.createUpdateHolidayProfile}]
				}
			});
			$("#modal_window_holiday").kernely_dialog("open");
			
			// Create an empty line
			this.newHolidayLine(true);
		},
		
		newEmptyHolidayLine: function(){
			console.log(this);
			viewCreate.newHolidayLine(true);
		},
		
		newHolidayLine : function(edit, id,name, unlimited, quantity, unity, effectivemonth,anticipation, color){
			var holidayTypeLine = new HolidayTypeLineView(edit, id, name, unlimited, quantity, unity, effectivemonth,anticipation, color).render();
			$("#holiday-types-list-table").append(holidayTypeLine);
		},
		
		createUpdateHolidayProfile: function(){
			if ($("#holiday-profile-name").val() == ""){
				var errorTemplate = $("#popup-holiday-profile-error-name-template").html();
				
				return;
			}
			
			// Create the holiday types list
			var holidayTypesId = '[';
			
			for (var i = 0; i < typeLines.length ; i++){
				if (typeLines[i] != null){
					holidayTypesId += '"'+typeLines[i].vid + '",'
				}
			}
			
			if (holidayTypesId.length > 0){ //Remove the last coma
				holidayTypesId = holidayTypesId.substring(0,holidayTypesId.length -1);
			}
			holidayTypesId += ']';
			
			var json = '{"id":"'+this.vid+'", "name":"'+ $("#holiday-profile-name").val() +'" , "holidayTypesId":' + holidayTypesId + '}'
			
			$.ajax({
				url:"/admin/holiday/create",
				data: json,
				type: "POST",
				dataType : "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					// data is the holiday type dto created
					$('#modal_window_holiday').kernely_dialog("close");
					
					var successHtml = $("#holiday-success-message-template").html();
					$.writeMessage("success",successHtml);
					resetLinesMemory();
					tableView.reload();
				}
			});
			
		},
		
		editholiday: function(){
			// TODO
		},
		
		editusers: function(){
			viewUsers.setFields(lineSelected);
			viewUsers.render();
		},
		
		render:function(){
			return this;
		}
	})
	
	HolidayAdminProfileUsersView = Backbone.View.extend({
		vid: null,
		
		events:{
			"click #addUserButton" : "addUser",
			"dblclick #out-listbox" : "addUser",
			"click #removeUserButton" : "removeUser",
			"dblclick #in-listbox" : "removeUser",
			"click #updateUsersButton" : "updateUsers",
		},
	
		initialize: function(){
		},
		
		setFields: function(id){
			this.vid = id;
			console.log("Field set")
		},
		
		render: function(){
			var parent = this;
			var html = $("#popup-holiday-admin-users-template").html();
			var titleTemplate = $("#edit-template").html();
			
			// Fill with existing users
			$.ajax({
				type:"GET",
				url:"/admin/holiday/profile/users",
				data:{"id":parent.vid},
				dataType:"json",
				success: function(data){
					// Build the out listbox with users which are not associated to the profile
					var outbox = '<select id ="out-listbox" size="10">';
					if (data != null){
						if(data.out != null){
							if (data.out.length > 1){
					    		$.each(data.out, function() {
					    			outbox += '<option id="'+this.user.username+'">'+ this.firstname+' '+ this.lastname+'</option>'
					    		});
							}
							else{
				    			outbox += '<option id="'+data.out.user.username+'">'+ data.out.firstname+' '+ data.out.lastname+'</option>'
							}
						}
					}
					outbox += "</select>";
					$("#out-listbox-div").html(outbox);
					
					// Build the in listbox
					var inbox = '<select id ="in-listbox" size="10">';
					if (data != null){
						if(data.in != null){
							if (data.in.length > 1){
					    		$.each(data.in, function() {
					    			inbox += '<option id="'+this.user.username+'">'+ this.firstname+' '+ this.lastname+'</option>'
					    		});
							}
							else{
				    			inbox += '<option id="'+data.in.user.username+'">'+ data.in.firstname+' '+ data.in.lastname+'</option>'
							}
						}
					}
					inbox += "</select>";
					$("#in-listbox-div").html(inbox);
					return parent;
				}
			});
			
			
			$("#modal_window_holiday").kernely_dialog({
				title: titleTemplate,
				content: html,
				width: '480px',
				eventNames:'click',
				events:{
					'click': [
					          {"el":"#addUserButton", "event":parent.addUser},
					          {"el":"#removeUserButton", "event":parent.removeUser},
					          {"el":"#updateUsersButton", "event":parent.updateUsers}],
					'dblclick' : [
						          {"el":"#out-listbox", "event":parent.addUser},
						          {"el":"#in-listbox", "event":parent.addUser},
					              ]
				}
			});
			$("#modal_window_holiday").kernely_dialog("open");
			
		},
		
		addUser: function(){
			// Adds only if a user is selected in out listbox
			if ($("#out-listbox").find(":selected").length > 0){
				var username = $("#out-listbox").find(":selected")[0].id;
				var text = $("#out-listbox :selected").val();
				// Add to the "in" box
				$('#in-listbox').append('<option id="'+username+'">'+text+'</option>');

				// Remove from the "out" box
				$('#out-listbox [id="'+username+'"]').remove();
			}
		},
		
		removeUser: function(){
			// Adds only if a user is selected in in listbox
			if ($("#in-listbox").find(":selected").length > 0){
				var username = $("#in-listbox").find(":selected")[0].id;
				var text = $("#in-listbox :selected").val();
				// Add to the "out" box
				$('#out-listbox').append('<option id="'+username+'">'+text+'</option>');
	
				// Remove from the "in" box
				$('#in-listbox [id="'+username+'"]').remove();
			}
		},
		
		updateUsers: function(){
			// Build json with only users associated to the profile
			var json ='{"id":"'+lineSelected+'","usernames":[';
			for(var i = 0; i < $("#in-listbox>option").length; i++){
				json += '"' + $("#in-listbox>option")[i].id + '"';
				json +=",";
			}
			if ($("#in-listbox>option").length > 0){
				json = json.substr(0,json.length -1);
			}
			json += "]}";
			
			// Send request
			$.ajax({
				url:"/admin/holiday/profile/users/update",
				data: json,
				type: "POST",
				dataType : "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					// data is the holiday type dto created
					var successHtml = $("#holiday-success-message-template").html();
					$.writeMessage("success",successHtml);
					$("#modal_window_holiday").kernely_dialog("close");
					// tableView.reload();
				}
			});
			
		}
		
	})
	
	HolidayTypeLineView = Backbone.View.extend({
		tagName: "tr",
		events:{
			"click .edit_button" : "editLine",
			"click .delete_button" : "deleteLine"
		},
		
		vrank: null, // Only here to know where the line is in the typeLines array
		
		veditmode: true,
		vid:0,
		vname:"",
		vunlimited:false,
		vquantity:0,
		vunity:12,
		veffectivemonth:0,
		vanticipation:false,
		vcolor:"#FFFFFF",
		
		initialize: function(editmode, id, name, unlimited, quantity, unity, effectivemonth,anticipation, color){
			
			this.vrank = arrayIndex;
			
			this.veditmode = editmode;
			if (! this.veditmode){
				// The line is filled with data
				this.vid = id;
				this.vname = name;
				this.vunlimited = unlimited;
				this.vquantity = quantity;
				this.vunity = unity;
				this.veffectivemonth = effectivemonth;
				this.vanticipation = anticipation;
				this.vcolor = color;
				
				changeEditedLines(1);
			}
			
			typeLines.push(this);
			
			// Actualise array index
			arrayIndex += 1;
		},
		
		render: function(){
			var template = $("#holiday-type-line-template").html();
			var view = {id:this.vid, name: this.vname, quantity: this.vquantity, color:this.vcolor};
			var html = Mustache.to_html(template, view);

			$(this.el).html(html);
			$(this.el).attr("id",this.vrank);
			$(this.el).appendTo($("#holiday-types-list-table"));
			this.changeDisplay();
			
			if (this.vanticipation == "true"){
				$(".holiday-anticipated",this.el).attr('checked','checked');
			}

			if (this.vunlimited == "true"){
				$(".holiday-unlimited",this.el).attr('checked','checked');
			}
			$(".holiday-quantity",this.el).val(this.vquantity);
			$(".holiday-unity",this.el).val(this.vunity);
			$(".holiday-effectivemonth",this.el).val(this.veffectivemonth);
			$(".holiday-color",this.el).val(this.vcolor);
			
			return this;
		},
		
		changeDisplay: function(){
			if (! this.veditmode){
				changeEditedLines(-1);
				$(".holiday-name",this.el).attr("disabled","disabled");
				$(".holiday-unlimited",this.el).attr("disabled","disabled");
				$(".holiday-quantity",this.el).attr("disabled","disabled");
				$(".holiday-unity",this.el).attr("disabled","disabled");
				$(".holiday-effectivemonth",this.el).attr("disabled","disabled");
				$(".holiday-anticipated",this.el).attr("disabled","disabled");
				$(".holiday-color",this.el).attr("disabled","disabled");
				$(".edit_button",this.el).val("Edit");
			} else {
				changeEditedLines(1);
				$(".holiday-name",this.el).removeAttr("disabled");
				$(".holiday-unlimited",this.el).removeAttr("disabled");
				$(".holiday-quantity",this.el).removeAttr("disabled");
				$(".holiday-unity",this.el).removeAttr("disabled");
				$(".holiday-effectivemonth",this.el).removeAttr("disabled");
				$(".holiday-anticipated",this.el).removeAttr("disabled");
				$(".holiday-color",this.el).removeAttr("disabled");
				$(".edit_button",this.el).val("Ok");
			}
		},
		
		editLine: function(){
			if (this.veditmode){
				
				// Get values
				var name = $(".holiday-name",this.el).val();
				var unlimited = ($(".holiday-unlimited",this.el).is(":checked"));
				var quantity = $(".holiday-quantity",this.el).val();
				var unity = $(".holiday-unity",this.el).val();
				var effectiveMonth = $(".holiday-effectivemonth",this.el).val();
				var anticipation = ($(".holiday-anticipated",this.el).is(":checked"));
				var color = $(".holiday-color",this.el).val();
								
				// Create the json

				var json = '{"id":"'+this.vid+'", "name":"'+ name + '", "unlimited":'+ unlimited + ', "quantity":"' + quantity +'", "unity":"'+ unity + '", "effectiveMonth":"' + effectiveMonth + '", "anticipation":'+ anticipation +', "color":"'+ color +'"}';
				
				// Create or edit the holiday type
				
				var parent = this;

				$.ajax({
					url:"/admin/holiday/createtype",
					data: json,
					type: "POST",
					dataType : "json",
					processData: false,
					contentType: "application/json; charset=utf-8",
					success: function(data){
						// data is the holiday type dto created
						parent.vid = data.id;
						parent.veditmode = false;
						parent.changeDisplay();
					}
				});
				
			} else {
				this.veditmode = true;
				this.changeDisplay();
			}
		},
		
		deleteLine: function(){
			var template = $("#confirm-holiday-type-deletion-template").html();
			var title = $("#delete-template").html();
			var nameToDisplay = $(".holiday-name",this.el).val()
			var view = {name: nameToDisplay};
			var html = Mustache.to_html(template, view);
			$.kernelyConfirm(title,html,this.deleteLineConfirm,this)
		},
		
		deleteLineConfirm: function(parent){
			if (parent.veditmode){
				changeEditedLines(-1);
			}
			typeLines[parent.vrank] = null;
			$("#"+parent.vrank).remove();
		}
		
	})

//		events:{
//			"click #newHolidayButton" : "newEmptyHolidayLine",
//			"click #createHolidayProfile" : "createUpdateHolidayProfile"
//		},
//		
//		initialize:function(){
//			this.vid = 0;
//			this.vname = "";
//			this.vholidayTypes = null;
//			this.veditmode = false;
//		},
//		
//		setFields: function(id, name, holidayTypes, editmode){
//			this.vid = id;
//			this.vname = name;
//			this.vholidayTypes = holidayTypes;
//			this.veditmode = editmode;
//		},
//
//		
//		render : function(){
//			var template = $("#popup-holiday-admin-create-template").html();
//			
//			var view = {profilename:this.vname};
//			
//			
//			var html = Mustache.to_html(template, view);
//			$(this.el).html(html);
//
//			var parent = this;
//			// Create new lines for existing holiday types
//			if (this.vholidayTypes != null){
//				if ($.isArray(this.vholidayTypes)){
//					$.each(this.vholidayTypes, function() {
//						parent.newHolidayLine(false, this.id, this.name,this.unlimited,this.quantity,this.periodUnit,this.effectiveMonth,this.anticipation,this.color);
//		    		});
//				} else {
//					this.newHolidayLine(false, this.vholidayTypes.id, this.vholidayTypes.name,this.vholidayTypes.unlimited,this.vholidayTypes.quantity,this.vholidayTypes.periodUnit,this.vholidayTypes.effectiveMonth,this.vholidayTypes.anticipation,this.vholidayTypes.color);
//				}
//			} else {
//				// Create an empty line
//				this.newHolidayLine(true);
//			}
//			
//			if (this.veditmode){
//				$("#createHolidayProfile").val($("#update-button-template").html());
//			}
//			
//			return this;
//		},
//		newEmptyHolidayLine : function(){
//			this.newHolidayLine(true);
//		},
//		newHolidayLine : function(edit, id,name, unlimited, quantity, unity, effectivemonth,anticipation, color){
//			var holidayTypeLine = new HolidayTypeLineView(edit, id, name, unlimited, quantity, unity, effectivemonth,anticipation, color).render();
//			$("#holiday-types-list-table").append(holidayTypeLine);
//		},
//		
//		createUpdateHolidayProfile: function(){
//			
//			if ($("#holiday-profile-name").val() == ""){
//				var errorTemplate = $("#popup-holiday-profile-error-name-template").html();
//				alert(errorTemplate);
//				return;
//			}
//			
//			// Create the holiday types list
//			var holidayTypesId = '[';
//			
//			for (var i = 0; i < typeLines.length ; i++){
//				if (typeLines[i] != null){
//					holidayTypesId += '"'+typeLines[i].vid + '",'
//				}
//			}
//			
//			if (holidayTypesId.length > 0){ //Remove the last coma
//				holidayTypesId = holidayTypesId.substring(0,holidayTypesId.length -1);
//			}
//			holidayTypesId += ']';
//			
//			var json = '{"id":"'+this.vid+'", "name":"'+ $("#holiday-profile-name").val() +'" , "holidayTypesId":' + holidayTypesId + '}'
//			
//			$.ajax({
//				url:"/admin/holiday/create",
//				data: json,
//				type: "POST",
//				dataType : "json",
//				processData: false,
//				contentType: "application/json; charset=utf-8",
//				success: function(data){
//					// data is the holiday type dto created
//					$('#modal_window_holiday').hide();
//					$('#mask').hide();
//					
//					var successHtml = $("#holiday-success-message-template").html();
//					$.writeMessage("success",successHtml);
//					resetLinesMemory();
//					
//					tableView.reload();
//				}
//			});
//			
//		}
//		
//	})
	
// define the application initialization
	var self = {};
	self.start = function(){
		tableView = new HolidayAdminTableView().render();
		new HolidayAdminButtonsView().render();
		
	}
	return self;
})

$( function() {
	console.log("Starting holiday administration application")
	new AppHolidayAdmin(jQuery).start();
})