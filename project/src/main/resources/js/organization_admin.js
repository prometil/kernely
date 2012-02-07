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
	

	OrganizationAdminTableLineView = Backbone.View.extend({
		tagName: "tr",
		className: 'organization_list_line',
		
		vid: null,
		vname : null,
		vaddress : null,
		vzip : null,
		vcity : null,
		vphone : null,
		vfax : null,

		events: {
			"click" : "selectLine",
			"mouseover" : "overLine",
			"mouseout" : "outLine"
		},
		
		
		initialize: function(id, name, address, zip, city, phone, fax){
			this.vid = id;
			this.vname = name;
			this.vaddress = address;
			this.vcity=city;
			this.vzip=zip;
			this.vphone=phone;
			this.vfax=fax;
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
			var template = '<td>{{name}}</td>';
			var view = {name : this.vname};
			var html = Mustache.to_html(template, view);
			
			$(this.el).html(html);
			$(this.el).appendTo($("#organization_admin_table"));
			return this;
		}
		
	})
	
	OrganizationAdminTableView = Backbone.View.extend({
		el:"#organization_admin_table",
		events:{
		
		},
		
		initialize:function(){
			var parent = this;
			var html= $("#table-header-template").html();

			$(this.el).html(html);
			$.ajax({
				type:"GET",
				url:"/admin/organizations/all",
				dataType:"json",
				success: function(data){
					if(data != null){
						if(data.organizationDTO.length > 1){
				    		$.each(data.organizationDTO, function() {
				    			var view = new OrganizationAdminTableLineView(this.id, this.name, this.address, this.zip, this.city, this.phone, this.fax);
				    			view.render();
				    		});
						}
					   	// In the case when there is only one element
			    		else{
							var view = new OrganizationAdminTableLineView(data.organizationDTO.id, data.organizationDTO.name, data.organizationDTO.address, data.organizationDTO.zip, data.organizationDTO.city, data.organizationDTO.phone, data.organizationDTO.fax);
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

	 OrganizationAdminButtonsView = Backbone.View.extend({
		el:"#organization_admin_container",
		
		events: {
			"click .createButton" : "createorganization",
			"click .editButton" : "editorganization",
			"click .deleteButton" : "deleteorganization"
		},
		
		viewCreate:null,
		viewUpdate:null,
		
		initialize: function(){
			this.viewCreate = new  OrganizationAdminCreateView();
			this.viewUpdate = new  OrganizationAdminUpdateView("", 0);
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
       		$("#modal_window_organization").css('top',  winH/2-$("#modal_window_organization").height()/2);
     		$("#modal_window_organization").css('left', winW/2-$("#modal_window_organization").width()/2);
     		$("#modal_window_organization").css('background-color', "#EEEEEE");
     		$("input:text").each(function(){this.value="";});
     		//transition effect
     		$("#modal_window_organization").fadeIn(500);
		},
		
		createorganization: function(){
			this.showModalWindow();
			this.viewCreate.render();
		},
		
		editorganization: function(){
			this.showModalWindow();
			this.viewUpdate.setFields(lineSelected.vid, lineSelected.vname, lineSelected.vaddress, lineSelected.vzip, lineSelected.vcity, lineSelected.vphone, lineSelected.vfax);
			this.viewUpdate.render();
		},
		
		deleteorganization: function(){
			var template = $("#confirm-organization-deletion-template").html();
			
			var view = {name: lineSelected.vname};
			var html = Mustache.to_html(template, view);
			
			var answer = confirm(html);
			if (answer){
				$.ajax({
					url:"/admin/organizations/delete/" + lineSelected.vid,
					success: function(){
						var successHtml = $("#organization-deleted-template").html();
					
						$("#organizations_notifications").text(successHtml);
						$("#organizations_notifications").fadeIn(1000);
						$("#organizations_notifications").fadeOut(3000);
						tableView.reload();
					}
				});
			}
		},
		
		render:function(){
			return this; 
		}
	})
	
	OrganizationAdminCreateView = Backbone.View.extend({
		el: "#modal_window_organization",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .createOrganization" : "registerorganization"
		},
		
		initialize:function(){
		},
		
		render : function(){
			var template = $("#popup-organization-admin-create-template").html();
			
			var view = {};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_organization').hide();
       		$('#mask').hide();
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
						$('#modal_window_organization').hide();
						$('#mask').hide();
						
						var successHtml = $("#organization-created-updated-template").html();
						tableView.reload();
						$("#organizations_notifications").text(successHtml);
						$("#organizations_notifications").fadeIn(1000);
						$("#organizations_notifications").fadeOut(3000);
					} else {
						$("#organizations_errors_create").text(data.result);
						$("#organizations_errors_create").fadeIn(1000);
						$("#organizations_errors_create").fadeOut(3000);
					}
				}
			});
		}
	}) 

	OrganizationAdminUpdateView = Backbone.View.extend({
		el: "#modal_window_organization",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .updateOrganization" : "updateorganization"
		},
		
		initialize:function(id, name, address, zip, city, phone, fax){
			this.vid = id;
			this.vname = name;
			this.vaddress = address;
			this.vcity=city;
			this.vzip=zip;
			this.vphone=phone;
			this.vfax=fax;
		},
		
		setFields: function(id, name, address, zip, city, phone, fax){
			this.vid = id;
			this.vname = name;
			this.vaddress = address;
			this.vcity=city;
			this.vzip=zip;
			this.vphone=phone;
			this.vfax=fax;
		},
		
		render : function(){
			var template = $("#popup-organization-admin-update-template").html();
			var view = {name : this.vname, address : this.vaddress, zip : this.vzip, city : this.vcity, phone : this.vphone, fax : this.vfax};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			new UserCBListView(this.vid).render();
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_organization').hide();
       		$('#mask').hide();
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
			var json = '{"id":"' +this.vid +'", "name":"'+$('input[name*="name"]').val()+'",'+ '"address":"'+$('input[name*="address"]').val() +'",'+ '"zip":"'+$('input[name*="zip"]').val()+'",' + '"city":"'+$('input[name*="city"]').val() +'",' + '"phone":"'+$('input[name*="phone"]').val()  +'",'+ '"fax":"'+$('input[name*="fax"]').val() + '", ' +users+'}';
			$.ajax({
				url:"/admin/organizations/create",
				data: json,
				type: "POST",
				dataType: "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$('#modal_window_organization').hide();
						$('#mask').hide();
						
						var successHtml= $("#organization-created-updated-template").html();

						$("#organizations_notifications").text(successHtml);
						$("#organizations_notifications").fadeIn(1000);
						$("#organizations_notifications").fadeOut(3000);
						tableView.reload();
					} else {
						$("#organizations_errors_update").text(data.result);
						$("#organizations_errors_update").fadeIn(1000);
						$("#organizations_errors_update").fadeOut(3000);
					}
				}
			});
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