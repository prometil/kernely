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
	
	
	ManagerAdminTableLineView = Backbone.View.extend({
		tagName: "tr",
		className: 'manager_list_line',
		
		vname : null,
		vnbmembers : null,
		
		events: {
			"click" : "selectLine",
			"mouseover" : "overLine",
			"mouseout" : "outLine"
		},
		
		initialize: function(username, users){
			this.vname = username;
			this.vnbmembers = users;
		},
		selectLine : function(){
			$(".editButton").removeAttr('disabled');
			$(".deleteButton").removeAttr('disabled');
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
			var template = '<td>{{username}}</td><td>{{members}}</td>';
			var view = {username : this.vname, members: this.vnbmembers};
			var html = Mustache.to_html(template, view);
			
			$(this.el).html(html);
			$(this.el).appendTo($("#manager_admin_table"));
			return this;
		}		
	})
	
	ManagerAdminTableView = Backbone.View.extend({
		el:"#manager_admin_table",
		events:{
		
		},
		initialize:function(){
			var parent = this; 
			var html= $("#table-header-template").html();

			$(this.el).html(html);
			$.ajax({
				type:"GET",
				url:"/admin/manager/all",
				dataType:"json",
				success: function(data){ 
					if(data != null){
						if(data.managerDTO.length > 1 ){
				    		$.each(data.managerDTO, function() {
				    			if (this.users.length > 1){
					    			var view = new ManagerAdminTableLineView(this.name, this.users.length);
					    			view.render();
				    			}
				    			else{
				    				var view = new ManagerAdminTableLineView(this.name, 1);
					    			view.render();
				    			}
				    		});
						}
				    	// In the case when there is only one element
			    		else{		    		
			    			if ( data.managerDTO.users.length > 1){
			    				var view = new ManagerAdminTableLineView(data.managerDTO.name, data.managerDTO.users.length);
			    				view.render();
			    			}
			    			else{
			    				var view = new ManagerAdminTableLineView(data.managerDTO.name, 1);
			    				view.render();
			    			}
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
			
			var answer = confirm(html);
			if (answer){
				$.ajax({
					url:"/admin/manager/delete/" + lineSelected.vname,
					success: function(){
						var successHtml = $("#manager-success-template").html();
					
						$("#manager_notifications").text(successHtml);
						$("#manager_notifications").fadeIn(1000);
						$("#manager_notifications").fadeOut(3000);
						tableView.reload();
					}
				});
			}
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
						$.each(data.userDTO, function(index, value){
							option = option + '<option value="' + this.username + '">'+ this.username +'</option>' ;
						});
						$("#combo").append('<select name="user-choice" id="combobox">' + option + '</select>');
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
						
						$("#manager_notifications").text(successHtml);
						$("#manager_notifications").fadeIn(1000);
						$("#manager_notifications").fadeOut(3000);
						tableView.reload();
					} else {
						$("#manager_errors_create").text(data.result);
						$("#manager_errors_create").fadeIn(1000);
						$("#manager_errors_create").fadeOut(3000);
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
						
						$("#manager_notifications").text(successHtml);
						$("#manager_notifications").fadeIn(1000);
						$("#manager_notifications").fadeOut(3000);
						tableView.reload();
					} else {
						$("#managers_errors_update").text(data.result);
						$("#managers_errors_update").fadeIn(1000);
						$("#managers_errors_update").fadeOut(3000);
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
				url:"/admin/users/all",
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
				url:"/admin/users/all",
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