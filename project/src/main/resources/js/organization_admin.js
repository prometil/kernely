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


AppOrganizationAdmin = (function($){
	var lineSelected = null;
	var tableView = null;
	
	
	OrganizationAdminTableView = Backbone.View.extend({
		el:"#organization_admin_table",
		
		table: null,
		initialize:function(){
			var parent = this;
			
			var templateNameColumn = $("#table-organization-name-column").text();
			this.table= $(parent.el).kernely_table({
				idField:"id",
				elements:["name"],
				eventNames:["click"],
				events:{
					"click": parent.selectLine
				},
				columns:[{"name":templateNameColumn, style:""}]
			});
		},
		reload: function(){
			this.render();
		},
		selectLine : function(e){
			$(".editButton").removeAttr('disabled');
			$(".deleteButton").removeAttr('disabled');
			lineSelected = e.data.line;
		},
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/admin/organizations/all",
				dataType:"json",
				success: function(data){
					if(data != null){
						var dataOrganization = data.organizationDTO;
						parent.table.reload(dataOrganization);
					}
				}
			});
			return this;
		}
	})	

	 OrganizationAdminButtonsView = Backbone.View.extend({
		el:"#organization_admin_buttons",
		
		events: {
			"click .createButton" : "createorganization",
			"click .editButton" : "editorganization",
			"click .deleteButton" : "deleteorganization"
		},
		
		initialize: function(){
		},
		
		createorganization: function(){
			console.log("FUNCTION "+parent.registerorganization);
			// Create the dialog
			var parent = this;
			var html = $("#popup-organization-admin-create-template").html();
			var titleTemplate = $("#create-template").html();
			$("#modal_window_organization").kernely_dialog({
				title: titleTemplate,
				content: html,
				eventNames:'click .createOrganization',
				events:{
					'click .createOrganization' : parent.registerorganization
				}
			});
			$("#modal_window_organization").kernely_dialog("open");
		},
		
		registerorganization: function(){
			var json = '{"id":"0", "name":"'+$('input[name*="name"]').val()+'",'+ '"address":"'+$('input[name*="address"]').val() +'",'+ '"zip":"'+$('input[name*="zip"]').val()+'",' + '"city":"'+$('input[name*="city"]').val() +'",' + '"phone":"'+$('input[name*="phone"]').val()  +'",'+ '"fax":"'+$('input[name*="fax"]').val() +'"}';
			$.ajax({
				url:"/admin/organizations/create",
				data: json,
				type: "POST",
				dataType : "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						var successHtml = $("#organization-created-updated-template").html();
						tableView.reload();
						$("#modal_window_organization").kernely_dialog("close");
						$.writeMessage("success",successHtml);
					} else {
						$.writeMessage("error",data.result,"#errors_message");
					}
				}
			});
		},
		
		editorganization: function(){
			var parent = this;

			// Get data from organization
			$.ajax({
				type:"GET",
				url:"/admin/organizations/"+lineSelected,
				dataType:"json",
				success: function(data){
					// Create the dialog
					var template = $("#popup-organization-admin-update-template").html();
					var view = {name : data.name, address : data.address, zip : data.zip, city : data.city, phone : data.phone, fax : data.fax};
					var html = Mustache.to_html(template, view);
					new UserCBListView(data.id).render();
					var titleTemplate = $("#create-template").html();
					$("#modal_window_organization").kernely_dialog({
						title: titleTemplate,
						content: html,
						eventNames:'click .updateOrganization',
						events:{
							'click .updateOrganization' : parent.updateorganization
						}
					});
					$("#modal_window_organization").kernely_dialog("open");
				}
			});
		},
		
		updateorganization: function(){
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
			var json = '{"id":"' + lineSelected +'", "name":"'+$('input[name*="name"]').val()+'",'+ '"address":"'+$('input[name*="address"]').val() +'",'+ '"zip":"'+$('input[name*="zip"]').val()+'",' + '"city":"'+$('input[name*="city"]').val() +'",' + '"phone":"'+$('input[name*="phone"]').val()  +'",'+ '"fax":"'+$('input[name*="fax"]').val() + '", ' +users+'}';
			$.ajax({
				url:"/admin/organizations/create",
				data: json,
				type: "POST",
				dataType: "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						var successHtml= $("#organization-created-updated-template").html();
						$("#modal_window_organization").kernely_dialog("close");
						$.writeMessage("success",successHtml);
						tableView.reload();
					} else {
						$.writeMessage("error",data.result,"#errors_message");
					}
				}
			});
		},
		
		deleteorganization: function(){
			var html = $("#confirm-organization-deletion-template").html();
			var title = $("#delete-template").html();
			
			$.kernelyConfirm(title,html,this.confirmDeleteOrganization);
		},
		
		confirmDeleteOrganization: function(){
			$.ajax({
				url:"/admin/organizations/delete/" + lineSelected,
				success: function(){
					var successHtml = $("#organization-deleted-template").html();
					$.writeMessage("success",successHtml);
					tableView.reload();
				}
			});
		},
		
		render:function(){
			return this; 
		}
	})
	
	UserCBListView = Backbone.View.extend({
		el:"#usersToLink",
		
		organizationId: null,
		
		events:{
		
		},
		
		initialize:function(organizationid){
			this.organizationId = organizationid;
		},
		
		render: function(){
			var parent = this;
			$.ajax({
				type: "GET",
				url:"/admin/users/client",
				dataType:"json",
				success: function(data){
					if(data != null){
						if(data.userDetailsDTO.length > 1){
				    		$.each(data.userDetailsDTO, function() {
				    			$(parent.el).append('<input type="checkbox" id="'+ this.user.id +'">'+ this.lastname + ' ' + this.firstname+'</input><br/>');
				    		});
						}
						// In the case when there is only one user.
						else{
							$(parent.el).append('<input type="checkbox" id="'+ data.userDetailsDTO.user.id +'">'+ data.userDetailsDTO.lastname + ' ' + data.userDetailsDTO.firstname + ' ('+ data.userDetailsDTO.user.username +')'+'</input><br/>');
						}
						
						$.ajax({
							type: "GET",
							url:"/admin/organizations/" + parent.organizationId + "/users",
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
				}
			});
			return this;
		}
	})
	
	// define the application initialization
	var self = {};
	self.start = function(){
		tableView = new OrganizationAdminTableView().render();
		new OrganizationAdminButtonsView().render();
	}
	return self;
})
$( function() {
	console.log("Starting organization administration application")
	new AppOrganizationAdmin(jQuery).start();
})