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
AppManagerAdmin = (function($){
	var lineSelected = null;
	var tableView = null;
	
	ManagerAdminTableView = Backbone.View.extend({
		el:"#manager_admin_table",
		events:{
		
		},
		table:null,
		initialize:function(){
			var parent = this; 
			
			var templateNameColumn = $("#table-manager-name-column").text();
			var templateManagedColumn = $("#table-manager-users-column").text();
			this.table = $(parent.el).kernely_table({
				idField:"id",
				columns:[
				      {"name":templateNameColumn, style:""},
				      {"name":templateManagedColumn, style:"text-center"}
				],
				elements:["name", "nbUsers"],
				eventNames:["click"],
				events:{
					"click": parent.selectLine
				},
				editable:true
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
				url:"/admin/manager/all",
				dataType:"json",
				success: function(data){ 	

					if(data != null){
						var dataManager = data.managerDTO;
						parent.table.reload(dataManager);
					}
					else{
						parent.table.clear();
						parent.table.noData();
					}
				}
			});
			return this;
		}
	})	
	
		ManagerAdminButtonsView = Backbone.View.extend({
		el:"#manager_admin_buttons",
		
		events: {
			"click .createButton" : "createmanager",
			"click .editButton" : "editmanager",
			"click .deleteButton" : "deletemanager"
		},
		
		initialize: function(){
		},
		
		createmanager: function(){
			var parent = this;
			$.ajax({
				type: "GET",
				url:"/admin/manager/combobox",
				dataType:"json",
				success: function(data){
					var template = $("#popup-manager-admin-create-template").html();
			
					var titleTemplate = $("#create-template").html();
					$("#modal_window_manager").kernely_dialog({
						title: titleTemplate,
						content: template,
						eventNames:'click .createManager',
						events:{
							'click .createManager' : parent.registermanager
						}
					});
					
					if(data != null){
						var option = "";
						if ($.isArray(data.userDTO)){
							$.each(data.userDTO, function(index, value){
								option = option + '<option value="' + this.username + '">'+ this.username +'</option>' ;
							});
							$("#combo").append('<select name="user-choice" id="combobox">' + option + '</select>');
						} else {
							option = option + '<option value="' + data.userDTO.username + '">'+ data.userDTO.username +'</option>' ;
							$("#combo").append('<select name="user-choice" id="combobox">' + option + '</select>');
						}
					}
					
					// Create list of users to manage
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
						}
					});
					
					$("#modal_window_manager").kernely_dialog("open");
				}
			});
		},
		
		registermanager: function(){	
			var parent = this;
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
			
			var json = '{"manager":"'+$("#combobox").val() + '", '+ users +'}';
			$.ajax({
				url:"/admin/manager/create",
				data: json,
				type: "POST",
				dataType : "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					var successHtml = $("#manager-success-template").html();
					$.writeMessage("success",successHtml);
					$("#modal_window_manager").kernely_dialog("close");
					tableView.reload();
				}
			});
		},
		
		editmanager: function(){
			// Get data from manager
			var parent = this;
			var managerUsers;
			$.ajax({
				type: "GET",
				url:"/admin/manager/"+lineSelected,
				dataType:"json",
				success: function(data){
					var template = $("#popup-manager-admin-update-template").html();
					var view = {name : data.name};
					var html = Mustache.to_html(template, view);
					
					var titleTemplate = $("#edit-template").html();
					$("#modal_window_manager").kernely_dialog({
						title: titleTemplate,
						content: html,
						eventNames:'click .updateManager',
						events:{
							'click .updateManager' : parent.updatemanager
						}
					});
				
					manager = data;
					
					var usersToLink = $("#usersToLink");
					
					$.ajax({
						type: "GET",
						url:"/admin/users/enabled",
						dataType:"json",
						success: function(data){
							// Build users boxes
							if(data.userDetailsDTO.length > 1){
					    		$.each(data.userDetailsDTO, function() {
					    			$(usersToLink).append('<input type="checkbox" id="'+ this.user.id +'">'+ this.lastname + ' ' + this.firstname+'</input><br/>');
					    		});
							}
							// In the case when there is only one user.
							else{
								$(usersToLink).append('<input type="checkbox" id="'+ data.userDetailsDTO.user.id +'">'+ data.userDetailsDTO.lastname + ' ' + data.userDetailsDTO.firstname + ' ('+ data.userDetailsDTO.user.username +')'+'</input><br/>');
							}
							
							// Check boxes
							if(manager != null && typeof(manager) != "undefined"){
								if(manager.users.length > 1){
						    		$.each(manager.users, function() {
						    			$('#' + this.id).attr("checked", "checked");
						    		});
								}
								// In the case when there is only one user. 
								else{
									$('#' + manager.users.id).attr("checked", "checked");
								}
							}
							$("#modal_window_manager").kernely_dialog("open");
						}
					});
				}
			});
		},
		
		updatemanager: function(){
			var parent = this;
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
			
			var json = '{"manager":"'+$('input[name*="name"]').val() + '", '+ users +'}';
			$.ajax({
				url:"/admin/manager/update",
				data: json,
				type: "POST",
				dataType: "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					var successHtml = $("#manager-success-template").html();
					$.writeMessage("success",successHtml);
					$('#modal_window_manager').kernely_dialog("close");
					tableView.reload();
				}
			});
		},
		
		deletemanager: function(){
			
			var html = $("#confirm-manager-deletion-template").html();
			var title= $("#delete-template").html();
			
			$.kernelyConfirm(title,html,this.confirmdeletemanager);
		},
		
		confirmdeletemanager: function(){
			$.ajax({
				url:"/admin/manager/delete/" + lineSelected,
				success: function(){
					var successHtml = $("#manager-success-template").html();
					
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
		tableView = new ManagerAdminTableView().render();
		new ManagerAdminButtonsView().render();
	}
	return self;
})

$( function() {
	console.log("Starting manager administration application");
	new AppManagerAdmin(jQuery).start();
})