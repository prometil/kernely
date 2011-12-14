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
	
	
	GroupAdminTableLineView = Backbone.View.extend({
		tagName: "tr",
		className: 'group_list_line',
		
		vid: null,
		vname : null,
		vnbmembers : null,
		
		events: {
			"click" : "selectLine",
			"mouseover" : "overLine",
			"mouseout" : "outLine"
		},
		
		initialize: function(id, name, members){
			this.vid = id;
			this.vname = name;
			this.vnbmembers = members;
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
			var template = '<td>{{name}}</td><td>{{members}}</td>';
			var view = {name : this.vname, members: this.vnbmembers};
			var html = Mustache.to_html(template, view);
			
			$(this.el).html(html);
			$(this.el).appendTo($("#group_admin_table"));
			return this;
		}
		
	})

	GroupAdminTableView = Backbone.View.extend({
		el:"#group_admin_table",
		events:{
		
		},
		initialize:function(){
			var parent = this;
			$(this.el).html("<tr><th>Name</th><th>Members</th></tr>");
			$.ajax({
				type:"GET",
				url:"/admin/groups/all",
				dataType:"json",
				success: function(data){
					if(data.groupDTO.length > 1){
			    		$.each(data.groupDTO, function() {
			    			var users = 0;
			    			if(this.users != null && typeof(this.users) != "undefined"){
			    				if(typeof(this.users.length) != "undefined"){
			    					users = this.users.length;
			    				}
			    				else{
			    					users = 1;
			    				}
			    			}
			    			var view = new GroupAdminTableLineView(this.id, this.name, users);
			    			view.render();
			    		});
					}
			    	// In the case when there is only one element
		    		else{
		    			var users = 0;
		    			if(data.groupDTO.users != null && typeof(data.groupDTO.users) != "undefined"){
		    				users = data.groupDTO.users.length;
		    			}
						var view = new GroupAdminTableLineView(data.groupDTO.id, data.groupDTO.name, users);
		    			view.render();
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
	
	
	GroupAdminButtonsView = Backbone.View.extend({
		el:"#group_admin_container",
		
		events: {
			"click .createButton" : "creategroup",
			"click .editButton" : "editgroup",
			"click .deleteButton" : "deletegroup"
		},
		
		viewCreate:null,
		viewUpdate:null,
		
		initialize: function(){
			this.viewCreate = new GroupAdminCreateView();
			this.viewUpdate =  new GroupAdminUpdateView("", 0);
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
       		$("#modal_window_group").css('top',  winH/2-$("#modal_window_group").height()/2);
     		$("#modal_window_group").css('left', winW/2-$("#modal_window_group").width()/2);
     		$("#modal_window_group").css('background-color', "#EEEEEE");
     		$("input:text").each(function(){this.value="";});
     		//transition effect
     		$("#modal_window_group").fadeIn(500);
		},
		
		creategroup: function(){
			this.showModalWindow();
			this.viewCreate.render();
		},
		
		editgroup: function(){
			this.showModalWindow();
			this.viewUpdate.setFields(lineSelected.vname, lineSelected.vid);
			this.viewUpdate.render();
		},
		
		deletegroup: function(){
			var answer = confirm(lineSelected.vname + " will be deleted. Do you want to continue ?");
			if (answer){
				$.ajax({
					url:"/admin/groups/delete/" + lineSelected.vid,
					success: function(){
						$("#groups_notifications").text("Operation completed successfully !");
						$("#groups_notifications").fadeIn(1000);
						$("#groups_notifications").fadeOut(3000);
						tableView.reload();
					}
				});
			}
		},
		
		render:function(){
			return this;
		}
	})
	
	GroupAdminCreateView = Backbone.View.extend({
		el: "#modal_window_group",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .createGroup" : "registergroup"
		},
		
		initialize:function(){
		},
		
		render : function(){
			var template = $("#popup-group-admin-create-template").html();
			
			var view = {};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_group').hide();
       		$('#mask').hide();
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
						console.log("coucou !!!");
						$('#modal_window_group').hide();
						$('#mask').hide();
						$("#groups_notifications").text("Operation completed successfully !");
						$("#groups_notifications").fadeIn(1000);
						$("#groups_notifications").fadeOut(3000);
						tableView.reload();
					} else {
						$("#groups_errors_create").text(data.result);
						$("#groups_errors_create").fadeIn(1000);
						$("#groups_errors_create").fadeOut(3000);
					}
				}
			});
		}
	}) 
	
	GroupAdminUpdateView = Backbone.View.extend({
		el: "#modal_window_group",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .updateGroup" : "updategroup"
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
			var template = $("#popup-group-admin-update-template").html();
			var view = {name : this.vname};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			
			new UserCBListView(this.vid).render();
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_group').hide();
       		$('#mask').hide();
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
			var json = '{"id":"'+this.vid+'", "name":"'+$('input[name*="name"]').val() + '", '+ users +'}';
			$.ajax({
				url:"/admin/groups/create",
				data: json,
				type: "POST",
				dataType: "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$('#modal_window_group').hide();
						$('#mask').hide();
						$("#groups_notifications").text("Operation completed successfully !");
						$("#groups_notifications").fadeIn(1000);
						$("#groups_notifications").fadeOut(3000);
						tableView.reload();
					} else {
						$("#groups_errors_update").text(data.result);
						$("#groups_errors_update").fadeIn(1000);
						$("#groups_errors_update").fadeOut(3000);
					}
				}
			});
		}
	}) 
	
	UserCBListView = Backbone.View.extend({
		el:"#usersToLink",
		
		groupId: null,
		
		events:{
		
		},
		
		initialize:function(groupid){
			this.groupId = groupid;
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
						url:"/admin/groups/" + parent.groupId + "/users",
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
		tableView = new GroupAdminTableView().render();
		new GroupAdminButtonsView().render();
	}
	return self;
})

$( function() {
	console.log("Starting group administration application")
	new AppGroupAdmin(jQuery).start();
})