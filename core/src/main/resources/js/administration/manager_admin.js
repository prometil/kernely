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
		initialize:function(){
			var parent = this; 
			
			var templateNameColumn = $("#table-manager-name-column").text();
			var templateManagedColumn = $("#table-manager-users-column").text();
			$(parent.el).kernely_table({
				columns:[templateNameColumn, templateManagedColumn],
				editable:true
			});
			
			$.ajax({
				type:"GET",
				url:"/admin/manager/all",
				dataType:"json",
				success: function(data){ 
					if(data != null){
						var dataManager = data.managerDTO;
						$(parent.el).reload_table({
							data: dataManager,
							idField:"id",
							elements:["name", "nbUsers"],
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
			$(".deleteButton").removeAttr('disabled');
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
	
		ManagerAdminButtonsView = Backbone.View.extend({
		el:"#manager_admin_container",
		
		events: {
			"click .createButton" : "createmanager",
			"click .editButton" : "editmanager",
			"click .deleteButton" : "deletemanager"
		},
		
		viewCreate:null,
		viewUpdate:null,
		
		initialize: function(){
			this.viewCreate = new ManagerAdminCreateView();
			this.viewUpdate =  new ManagerAdminUpdateView("", 0);
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
       		$("#modal_window_manager").css('top',  winH/2-$("#modal_window_manager").height()/2);
     		$("#modal_window_manager").css('left', winW/2-$("#modal_window_manager").width()/2);
     		$("#modal_window_manager").css('background-color', "#EEEEEE");
     		$("input:text").each(function(){this.value="";});
     		//transition effect
     		$("#modal_window_manager").fadeIn(500);
		},
		
		createmanager: function(){
			this.showModalWindow();
			this.viewCreate.render();
		},
		
		editmanager: function(){
			
			this.showModalWindow();
			this.viewUpdate.setFields(lineSelected.vname, lineSelected.vid);
			this.viewUpdate.render();
		},
		
		deletemanager: function(){
			
			var template = $("#confirm-manager-deletion-template").html();
			
			var view = {name: lineSelected.vname};
			var html = Mustache.to_html(template, view);
	
			$.kernelyConfirm(html,this.confirmdeletemanager);
		},
		
		confirmdeletemanager: function(){
			$.ajax({
				url:"/admin/manager/delete/" + lineSelected.vname,
				success: function(){
					var successHtml = $("#manager-success-template").html();
					
					$.writeMessage("success",successHtml);
					//tableView.reload();
				}
			});
		},
		
		render:function(){
			return this;
		}
	})
	
	ManagerAdminCreateView = Backbone.View.extend({
		el: "#modal_window_manager",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .createManager" : "registermanager"
		},
		
		initialize:function(){
		},
		
		render : function(){
			var template = $("#popup-manager-admin-create-template").html();
			$.ajax({
				type: "GET",
				url:"/admin/manager/combobox",
				dataType:"json",
				success: function(data){
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
				}
			});
			var view = {};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			new UserCBListViewCreate().render();
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_manager').hide();
       		$('#mask').hide();
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
					if (data.result == "ok"){
						var successHtml = $("#manager-success-template").html();
						
						$.writeMessage("success",successHtml);
						//tableView.reload();
					} else {
						$.writeMessage("error",data.result,"#errors_message");
					}
					parent.closemodal();
				}
			});
		}
	}) 
	
	ManagerAdminUpdateView = Backbone.View.extend({
		el: "#modal_window_manager",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .updateManager" : "updatemanager"
		},
		
		initialize:function(name, id){
			this.vid = id;
			this.vname = name;
		},
		
		setFields: function(name, id){
			this.vid = id;
			this.vname = name;
		},
		
		render : function(){
			var template = $("#popup-manager-admin-update-template").html();
			var view = {name : this.vname};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			new UserCBListView(this.vid).render();
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_manager').hide();
       		$('#mask').hide();
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
					if (data.result == "ok"){
						$('#modal_window_manager').hide();
						$('#mask').hide();
						var successHtml = $("#manager-success-template").html();
						
						$.writeMessage("success",successHtml);
						//tableView.reload();
					} else {
						$.writeMessage("error",data.result,"#errors_message");
					}
					parent.closemodal();
				}
			});
		}
	}) 
	
	UserCBListViewCreate = Backbone.View.extend({
		el:"#usersToLink",
		
		events:{
		
		},
		
		initialize:function(){
		},
		
		render: function(){
			var parent = this;
			$.ajax({
				type: "GET",
				url:"/admin/users/enabled",
				dataType:"json",
				success: function(data){
					if(data.userDetailsDTO.length > 1){
			    		$.each(data.userDetailsDTO, function() {
			    			$(parent.el).append('<input type="checkbox" id="'+ this.user.id +'">'+ this.lastname + ' ' + this.firstname+'</input><br/>');
			    		});
					}
					// In the case when there is only one user.
					else{
						$(parent.el).append('<input type="checkbox" id="'+ data.userDetailsDTO.user.id +'">'+ data.userDetailsDTO.lastname + ' ' + data.userDetailsDTO.firstname + ' ('+ data.userDetailsDTO.user.username +')'+'</input><br/>');
					}
				}
			});
			return this;
		}
	})
	
	UserCBListView = Backbone.View.extend({
		el:"#usersToLink",
		
		managerId: null,
		
		events:{
		
		},
		
		initialize:function(managerid){
			this.managerId = managerid;
		},
		
		render: function(){
			var parent = this;
			$.ajax({
				type: "GET",
				url:"/admin/users/enabled",
				dataType:"json",
				success: function(data){
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
						url:"/admin/manager/users/"+$("#manager-username").val(),
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