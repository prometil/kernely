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
 AppUserAdmin = (function($){
        var lineSelected = null;
        var tableView = null;
    

    UserAdminTableView = Backbone.View.extend({
		el:"#user_admin_table",

		initialize:function(){
			var parent = this;
			
			var templateNameColumn = $("#table-user-name-column").text();
			var templateFirstnameColumn = $("#table-user-firstname-column").text();
			var templateUsernameColumn = $("#table-user-username-column").text();
			var templateEmailColumn = $("#table-user-email-column").text();
			$(parent.el).kernely_table({
				columns:["", templateNameColumn, templateFirstnameColumn, templateUsernameColumn, templateEmailColumn],
				editable:true
			});
			
			$.ajax({
				type:"GET",
				url:"/admin/users/all",
				dataType:"json",
				success: function(data){
					if (data != null){
						var dataUser = data.userDetailsDTO;
						if($.isArray(dataUser)){
							$.each(dataUser, function(){
								if(this.user.locked == "true"){
									this.user.locked = '<img src="/images/icons/user_locked.png"/>';
								}
								else{
									this.user.locked = '<img src="/images/icons/user.png"/>';
								}
							});
						}
						else{
							if(dataUser.user.locked == 1){
								dataUser.user.locked = '<img src="/images/icons/user_locked.png"/>';
							}
							else{
								dataUser.user.locked = '<img src="/images/icons/user.png"/>';
							}
						}
						
						$(parent.el).reload_table({
							data: dataUser,
							idField:"id",
							elements:["user.locked", "lastname", "firstname", "user.username", "email"],
							eventNames:["click"],
							events:{
								"click": parent.selectLine
							},
							editable:true
						});
					}
				}
			});
		},
		selectLine : function(e){
			$(".editButton").removeAttr('disabled');
			$(".lockButton").removeAttr('disabled');
			var template = null;
			lineSelected = e.data.line;
		},
		reload: function(){
			this.initialize();
			this.render();
		},
		render: function(){
			return this;
		}
	})
	
	
	UserAdminButtonsView = Backbone.View.extend({
		el:"#user_admin_container",
		
		events: {
			"click .createButton" : "createuser",
			"click .editButton" : "edituser",
			"click .lockButton" : "lockuser"
		},

		viewUpdate:null,
		
		initialize: function(){
		},
		
		createuser: function(){
			var parent = this;
			var template = $("#popup-user-admin-create-template").html();
			var titleTemplate = "Creation";
			$("#modal_window_user").kernely_dialog({
				title: titleTemplate,
				content: template,
				eventNames:'click',
				events:{
					'click': {"el":".createUser", "event":parent.createNewUser}
				}
			});
			$("#modal_window_user").kernely_dialog("open");
		},
		
		createNewUser: function(e){
			var json = '{"id":"0", "firstname":"'+$('input[name*="firstname"]').val()+'","lastname":"'+$('input[name*="lastname"]').val()+'", "username":"'+$('input[name*="login"]').val()+'", "password":"'+$('input[name*="password"]').val()+'", "hire":"'+$('input[name*="hire"]')+'"}';
			$.ajax({
				url:"/admin/users/create",
				data: json,
				type: "POST",
				dataType:"json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
				  if (data.result == "ok"){
					$("#modal_window_user").kernely_dialog("close");

	       			var successHtml = $("#success-message-template").html();
					$.writeMessage("success",successHtml);
					tableView.reload();
				  } else {
					$.writeMessage("error",data.result,"#errors_message");
				  }
				}
			});
		},
		
		edituser: function(){
			var parent = this;
			
			// Get user data
			$.ajax({
				type: "GET",
				url:"/admin/users/details/" + lineSelected,
				dataType:"json",
				success: function(data){
					// Create the edit view
					var template = $("#popup-user-admin-update-template").html();
					var view = {login : data.user.username, firstname: data.firstname, lastname: data.lastname, hire: data.hire};
					var html = Mustache.to_html(template, view);
					
					var titleTemplate = "Edit user "+data.user.username;
					$("#modal_window_user").kernely_dialog({
						title: titleTemplate,
						content: html,
						eventNames:'click',
						events:{
							'click': {"el":".updateUser", "event":parent.updateuser}
						}
					});
					
					// Fill roles combo boxes
					var rolesToLink = $("#rolesToLink");
	
					$.ajax({
						type: "GET",
						url:"/roles/all",
						dataType:"json",
						success: function(data){
							if(data.roleDTO.length > 1){
					    		$.each(data.roleDTO, function() {
					    			$(rolesToLink).append('<input type="checkbox" id="'+ this.id +'">'+ this.name + '</input><br/>');
					    		});
							}
							// In the case when there is only one role.
							else{
								$(rolesToLink).append('<input type="checkbox" id="'+ data.roleDTO.id +'">'+ data.roleDTO.name +'</input><br/>');
							}
							
							$.ajax({
								type: "GET",
								url:"/admin/users/" + lineSelected + "/roles",
								dataType:"json",
								success: function(data){
									if(data != null && typeof(data) != "undefined"){
										if(data.roleDTO.length > 1){
								    		$.each(data.roleDTO, function() {
								    			$('#' + this.id).attr("checked", "checked");
								    		});
										}
										// In the case when there is only one user.
										else{
											$('#' + data.roleDTO.id).attr("checked", "checked");
										}
									}
								}
							});
						}
					});
					
					$("#modal_window_user").kernely_dialog("open");
				
				}
			});
			
		},
		
		updateuser: function(){
			var rolesCB = $("input:checked");
			var count = 0;
			var roles = "";
			if(rolesCB.length > 0){
				roles = '"roles":[';
				
				$.each(rolesCB, function(){
					// Just the id is useful, name is here just for the DTO
					roles += '{"id":"'+ $(this).attr('id') +'", "name":"null"}';
					count++;
					if(count<rolesCB.length){
						roles += ',';
					}
				});
				roles += "]";
			}
			else{
				roles = '"roles":{}';
			}
			var json = '{"id":"'+lineSelected+'", "firstname":"'+$('input[name*="firstname"]').val()+'","lastname":"'+$('input[name*="lastname"]').val()+'", "username":"'+$('input[name*="login"]').val()+'", "hire":"'+$('input[name*="hire"]')+'", ' + roles + '}';
			$.ajax({
				url:"/admin/users/create",
				data: json,
				type: "POST",
				dataType:"json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$('#modal_window_user').kernely_dialog("close");
						var successHtml = $("#success-message-template").html();
						$.writeMessage("success",successHtml);
						tableView.reload();
					} else {
						$.writeMessage("error",data.result,"#errors_message");
					}
				}
			});
		},
		
		lockuser: function(){
			var template = $("#user-change-state-confirm-template").html();
			
			$.kernelyConfirm(template, this.confirmlockuser);
		},
		
		confirmlockuser: function(){
			$.ajax({
				url:"/admin/users/lock/" + lineSelected,
				success: function(){
					var successHtml = $("#success-message-template").html();
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
		tableView = new UserAdminTableView().render();
		new UserAdminButtonsView().render();
	}
	return self;
})

$( function() {
        console.log("Starting user administration application")
        new AppUserAdmin(jQuery).start();
})