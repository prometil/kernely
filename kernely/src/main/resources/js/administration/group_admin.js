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

AppGroupAdmin = (function($){
	var lineSelected = null;
	var tableView = null;

	GroupAdminTableView = Backbone.View.extend({
		el:"#group_admin_table",
		
		table: null,
		
		initialize:function(){
			var parent = this;
			
			var templateNameColumn = $("#table-group_name-column").text();
			var templateMembersColumn = $("#table-group_members-column").text();
			this.table = $(parent.el).kernely_table({
				idField:"id",
				columns:[
				         {"name" : templateNameColumn, "style": ""},
				         {"name" : templateMembersColumn, "style" : "text-center"}
				],
				elements:["name", "nbUser"],
				eventNames:["click"],
				events:{
					"click": parent.selectLine
				}
			});
		},
		selectLine : function(e){
			$(".editButton").removeAttr('disabled');
			$(".deleteButton").removeAttr('disabled');
			lineSelected = e.data.line;
		},
		reload: function(){
			this.render();
		},
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/admin/groups/all",
				dataType:"json",
				success: function(data){
					if(data != null){
						var dataGroup = data.groupDTO;
						parent.table.reload(dataGroup);
					}
				}
			});
			return this;
		}
	})	
	
	
	GroupAdminButtonsView = Backbone.View.extend({
		el:"#group_admin_buttons",
		
		events: {
			"click .createButton" : "creategroup",
			"click .editButton" : "editgroup",
			"click .deleteButton" : "deletegroup"
		},
		
		initialize: function(){
		},
		
		creategroup: function(){
			var parent = this;
			var template = $("#popup-group-admin-create-template").html();
			var titleTemplate = $("#create-template").html();
			$("#modal_window_group").kernely_dialog({
				title: titleTemplate,
				content: template,
				eventNames:'click .createGroup',
				events:{
					'click .createGroup' : parent.registergroup
				}
			});
			$("#modal_window_group").kernely_dialog("open");
		},

		registergroup: function(){
			var json = '{"id":"0", "name":"'+$('input[name*="name"]').val() + '"}';
			$.ajax({
				url:"/admin/groups/create",
				data: json,
				type: "POST",
				dataType : "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$("#modal_window_group").kernely_dialog("close");
						
						var successHtml = $("#group-created-updated-template").html();
						$.writeMessage("success",successHtml);
						tableView.reload();
					} else {
						$.writeMessage("error",data.result,"#errors_message");
					}
				}
			});
		},

		
		editgroup: function(){
			var parent = this;
			
			// Get user data
			$.ajax({
				type: "GET",
				url:"/admin/groups/" + lineSelected,
				dataType:"json",
				success: function(data){
					// Create the edit view
					var template = $("#popup-group-admin-update-template").html();
					var view = {name:data.name};
					var html = Mustache.to_html(template, view);
					
					var titleTemplate = $("#edit-template").html();
					$("#modal_window_group").kernely_dialog({
						title: titleTemplate,
						content: html,
						eventNames:'click .updateGroup',
						events:{
							'click .updateGroup' : parent.updategroup
						}
					});
					
					var usersToLink = $("#usersToLink");
					$.ajax({
						type: "GET",
						url:"/admin/users/enabled",
						dataType:"json",
						success: function(data){
							if(data.userDetailsDTO.length > 1){
					    		$.each(data.userDetailsDTO, function() {
					    			$(usersToLink).append('<input type="checkbox" id="'+ this.user.id +'">'+ this.lastname + ' ' + this.firstname+'</input><br/>');
					    		});
							}
							// In the case when there is only one user.
							else{
								$(usersToLink).append('<input type="checkbox" id="'+ data.userDetailsDTO.user.id +'">'+ data.userDetailsDTO.lastname + ' ' + data.userDetailsDTO.firstname + ' ('+ data.userDetailsDTO.user.username +')'+'</input><br/>');
							}
							
							$.ajax({
								type: "GET",
								url:"/admin/groups/" + lineSelected + "/users",
								dataType:"json",
								success: function(data){
									if(data != null && typeof(data) != "undefined"){
										if(data.userDTO.length > 1){
								    		$.each(data.userDTO, function() {
								    			$('#' + this.id).attr("checked", "checked");
								    		});
										}
										// In the case when there is only one user.
										else{
											$('#' + data.userDTO.id).attr("checked", "checked");
										}
									}
								}
							});
						}
					});
					
					$("#modal_window_group").kernely_dialog("open");
				}
			});
		},
		
		updategroup: function(){
			var usersCB = $("input:checked");
			var count = 0;
			var users = "";
				
			if(usersCB.length > 0){
				users = '"users":[';
				
				$.each(usersCB, function(){
					users += '{"id":"'+ $(this).attr('id') +'", "username":"null", "locked":"false"}';
					count++;
					if(count<usersCB.length){
						users += ',';
					}
				});
				users += "]";
			}
			else{
				users = '"users":{}';
			}
			var json = '{"id":"'+lineSelected+'", "name":"'+$('input[name*="name"]').val() + '", '+ users +'}';
			$.ajax({
				url:"/admin/groups/create",
				data: json,
				type: "POST",
				dataType: "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$('#modal_window_group').kernely_dialog("close");
						
						var successHtml= $("#group-created-updated-template").html();
	
						$("#groups_notifications").text(successHtml);
						$.writeMessage("success",successHtml);
						tableView.reload();
					} else {
						$.writeMessage("error",data.result,"#errors_message");
						
					}
				}
			});
		},
		
		deletegroup: function(){
			var html = $("#confirm-group-deletion-template").html();
			
			var titleTemplate = $("#delete-template").html();
			$.kernelyConfirm(titleTemplate,html,this.confirmdeletegroup);
		},
		
		confirmdeletegroup: function(){
			$.ajax({
				url:"/admin/groups/delete/" + lineSelected,
				success: function(){
					var successHtml = $("#group-deleted-template").html();
					$.writeMessage("success",successHtml);
					tableView.reload();
				}
			});
		},
		
		render:function(){
			return this;
		}
	})
	
	// define the application initialization
	var self = {};
	self.start = function(){
		tableView = new GroupAdminTableView().render();
		new GroupAdminButtonsView().render();
	}
	return self;
})

$( function() {
	console.log("Starting group administration application")
	new AppGroupAdmin(jQuery).start();
})