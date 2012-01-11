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
        
        
        UserAdminTableLineView = Backbone.View.extend({
                tagName: "tr",
                className: 'user_list_line',
                
                vid: null,
                vlogin: null,
                vfirstname: null,
                vlastname: null,
                vlocked: null,
                vmail:null,
                
                events: {
                        "click" : "selectLine",
                        "mouseover" : "overLine",
                        "mouseout" : "outLine"
                },
                
                initialize: function(id, lastname, firstname, login, mail, locked){
                        this.vid = id;
                        this.vlogin = login;
                        this.vfirstname = firstname;
                        this.vlastname = lastname;
                        this.vmail = mail;
                        this.vlocked = locked;
                },
                selectLine : function(){
                        $(".editButton").removeAttr('disabled');
                        $(".lockButton").removeAttr('disabled');
                        $(this.el).css("background-color", "#8AA5A1");
                        if(typeof(lineSelected) != "undefined"){
                                if(lineSelected != this && lineSelected != null){
                                        $(lineSelected.el).css("background-color", "transparent");
                                }
                        }
                        lineSelected = this;
                        
                        // Update text of the lock button
                        
                        var template = null;
                        if(this.vlocked == "true"){
                        	template = $("#unlock-button-template").html();;
                        }
                        else{
                    		template = $("#lock-button-template").html();;
                        }
                        $(".lockButton").val(template);
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
                        var template = '<td><img src="{{icon}}"/></td><td>{{lastname}}</td><td>{{firstname}}</td><td>{{username}}</td><td>{{email}}</td>';
                        var image;
                        if(this.vlocked == "true"){
                                image = "./images/icons/user_locked.png";
                        }
                        else{
                                image = "/images/icons/user.png";
                        }
                        var view = {icon : image, lastname: this.vlastname, firstname: this.vfirstname, username: this.vlogin, email: this.vmail};
                        var html = Mustache.to_html(template, view);
                        
                        $(this.el).html(html);
                        $(this.el).appendTo($("#user_admin_table"));
                        return this;
                }
                
        })

	UserAdminTableView = Backbone.View.extend({
		el:"#user_admin_table",
		events:{
		
		},
		initialize:function(){
			var parent = this;
			
			var tableHeader = $("#table-header-template").html();
			
			$(this.el).html(tableHeader);
			$.ajax({
				type:"GET",
				url:"/admin/users/all",
				dataType:"json",
				success: function(data){
					if (data != null){
						if(data.userDetailsDTO.length > 1){
				    		$.each(data.userDetailsDTO, function() {
				    			var view = new UserAdminTableLineView(this.id, this.lastname, this.firstname, this.user.username, this.email, this.user.locked);
				    			view.render();
				    		});
						}
						else{
							var view = new UserAdminTableLineView(data.userDetailsDTO.id, data.userDetailsDTO.lastname, data.userDetailsDTO.firstname, data.userDetailsDTO.user.username, data.userDetailsDTO.email, data.userDetailsDTO.user.locked);
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
	
	
	UserAdminButtonsView = Backbone.View.extend({
		el:"#user_admin_container",
		
		events: {
			"click .createButton" : "createuser",
			"click .editButton" : "edituser",
			"click .lockButton" : "lockuser"
		},
		
		viewCreate:null,
		viewUpdate:null,
		
		initialize: function(){
			this.viewCreate =  new UserAdminCreateView();
			this.viewUpdate = new UserAdminUpdateView("","","",0);
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
       		$("#modal_window_user").css('top',  winH/2-$("#modal_window_user").height()/2);
     		$("#modal_window_user").css('left', winW/2-$("#modal_window_user").width()/2);
     		$("#modal_window_user").css('background-color', "#EEEEEE");
     		$("input:text").each(function(){this.value="";});
     		//transition effect
     		$("#modal_window_user").fadeIn(500);
		},
		
		createuser: function(){
			this.showModalWindow();
			this.viewCreate.render();
		},
		
		edituser: function(){
			this.showModalWindow();
			this.viewUpdate.setFields(lineSelected.vlogin,lineSelected.vfirstname,lineSelected.vlastname,lineSelected.vid);
			this.viewUpdate.render();
		},
		
		lockuser: function(){
			var template = $("#user-change-state-confirm-template").html();
			var view = {name : lineSelected.vlastname, firstname : lineSelected.vfirstname, username : lineSelected.vlogin};
			var html = Mustache.to_html(template, view);
			
			var answer = confirm(html);
			if (answer){
				$.ajax({
					url:"/admin/users/lock/" + lineSelected.vid,
					success: function(){
						var successHtml = $("#success-message-template").html();
						$("#users_notifications").text(successHtml);
						$("#users_notifications").fadeIn(1000);
						$("#users_notifications").fadeOut(3000);
						tableView.reload();
					}
				});
			}
		},
		
		render:function(){
			return this;
		}
	})
	
	UserAdminCreateView = Backbone.View.extend({
		el: "#modal_window_user",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .createUser" : "registeruser"
		},
		
		initialize:function(){
		},
		
		render : function(){
			var template = $("#popup-user-admin-create-template").html();
			
			var view = {};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_user').hide();
       		$('#mask').hide();
		},
		
		registeruser: function(){
			var json = '{"id":"0", "firstname":"'+$('input[name*="firstname"]').val()+'","lastname":"'+$('input[name*="lastname"]').val()+'", "username":"'+$('input[name*="login"]').val()+'", "password":"'+$('input[name*="password"]').val()+'"}';
			$.ajax({
				url:"/admin/users/create",
				data: json,
				type: "POST",
				dataType:"json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
				  if (data.result == "ok"){
					$('#modal_window_user').hide();
	       			$('#mask').hide();

	       			var successHtml = $("#success-message-template").html();
					$("#users_notifications").text(successHtml);
					$("#users_notifications").fadeIn(1000);
					$("#users_notifications").fadeOut(3000);
					tableView.reload();
				  } else {
                    $("#users_errors_create").text(data.result);
                    $("#users_errors_create").fadeIn(1000);
                    $("#users_errors_create").fadeOut(3000);
				  }
				}
			});
		}
	}) 
	
	UserAdminUpdateView = Backbone.View.extend({
		el: "#modal_window_user",
		
		vid: null,
		vlogin: null,
		vfirstname: null,
		vlastname: null,
		
		events:{
			"click .closeModal" : "closemodal",
			"click .updateUser" : "updateuser"
		},
		
		initialize:function(login, firstname, lastname, id){
			this.vid = id;
			this.vlogin = login;
			this.vfirstname = firstname;
			this.vlastname = lastname;
		},
		
		setFields: function(login, firstname, lastname, id){
			this.vid = id;
			this.vlogin = login;
			this.vfirstname = firstname;
			this.vlastname = lastname;
		},
		
		render : function(){
			var template = $("#popup-user-admin-update-template").html();
			
			var view = {login : this.vlogin, firstname: this.vfirstname, lastname: this.vlastname};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			
			new RolesCBListView(this.vid).render();
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_user').hide();
       		$('#mask').hide();
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
			var json = '{"id":"'+this.vid+'", "firstname":"'+$('input[name*="firstname"]').val()+'","lastname":"'+$('input[name*="lastname"]').val()+'", "username":"'+$('input[name*="login"]').val()+'", "password":"'+$('input[name*="password"]').val()+'", ' + roles + '}';
			$.ajax({
				url:"/admin/users/create",
				data: json,
				type: "POST",
				dataType:"json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$('#modal_window_user').hide();
						$('#mask').hide();

						var successHtml = $("#success-message-template").html();
						$("#users_notifications").text(successHtml);
						$("#users_notifications").fadeIn(1000);
						$("#users_notifications").fadeOut(3000);
						tableView.reload();
					} else {
						$("#users_errors_update").text(data.result);
						$("#users_errors_update").fadeIn(1000);
						$("#users_errors_update").fadeOut(3000);
					}
				}
			});
		}
	})
	
	RolesCBListView = Backbone.View.extend({
		el:"#rolesToLink",
		
		userId: null,
		
		events:{
		
		},
		
		initialize:function(userid){
			this.userId = userid;
		},
		
		render: function(){
			var parent = this;
			$.ajax({
				type: "GET",
				url:"/roles/all",
				dataType:"json",
				success: function(data){
					if(data.roleDTO.length > 1){
			    		$.each(data.roleDTO, function() {
			    			$(parent.el).append('<input type="checkbox" id="'+ this.id +'">'+ this.name + '</input><br/>');
			    		});
					}
					// In the case when there is only one user.
					else{
						$(parent.el).append('<input type="checkbox" id="'+ data.roleDTO.id +'">'+ data.roleDTO.name +'</input><br/>');
					}
					
					$.ajax({
						type: "GET",
						url:"/admin/users/" + parent.userId + "/roles",
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