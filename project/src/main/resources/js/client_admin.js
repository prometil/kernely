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


AppClientAdmin = (function($){
	var lineSelected = null;
	var tableView = null;
	

	ClientAdminTableLineView = Backbone.View.extend({
		tagName: "tr",
		className: 'client_list_line',
		
		vid: null,
		vname : null,
		vaddress : null,
		vemail :null,
		vzip : null,
		vcity : null,
		vphone : null,
		vfax : null,

		events: {
			"click" : "selectLine",
			"mouseover" : "overLine",
			"mouseout" : "outLine"
		},
		
		
		initialize: function(id, name, email, address, city, zip, phone, fax){
			this.vid = id;
			this.vname = name;
			this.vemail = email;
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
			var template = '<td>{{name}}</td><td>{{email}}</td><td>{{phone}}</td>';
			var view = {name : this.vname, email: this.vemail, phone : this.vphone};
			var html = Mustache.to_html(template, view);
			
			$(this.el).html(html);
			$(this.el).appendTo($("#client_admin_table"));
			return this;
		}
		
	})
	
	ClientAdminTableView = Backbone.View.extend({
		el:"#client_admin_table",
		events:{
		
		},
		
		initialize:function(){
			var parent = this;
			var html= $("#table-header-template").html();

			$(this.el).html(html);
			$.ajax({
				type:"GET",
				url:"/admin/clients/all",
				dataType:"json",
				success: function(data){
					if(data != null){
						if(data.clientDTO.length > 1){
				    		$.each(data.clientDTO, function() {
				    			var view = new ClientAdminTableLineView(this.id, this.name, this.address, this.email, this.zip, this.city, this.phone, this.fax);
				    			view.render();
				    		});
						}
					   	// In the case when there is only one element
			    		else{
							var view = new ClientAdminTableLineView(data.clientDTO.id, data.clientDTO.name, data.clientDTO.address, data.clientDTO.email, data.clientDTO.zip, data.clientDTO.city, data.clientDTO.phone, data.clientDTO.fax);
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

	 ClientAdminButtonsView = Backbone.View.extend({
		el:"#client_admin_container",
		
		events: {
			"click .createButton" : "createclient",
			"click .editButton" : "editclient",
			"click .deleteButton" : "deleteclient"
		},
		
		viewCreate:null,
		viewUpdate:null,
		
		initialize: function(){
			this.viewCreate = new  ClientAdminCreateView();
			//this.viewUpdate = new  ClientAdminUpdateView("", 0);
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
       		$("#modal_window_client").css('top',  winH/2-$("#modal_window_client").height()/2);
     		$("#modal_window_client").css('left', winW/2-$("#modal_window_client").width()/2);
     		$("#modal_window_client").css('background-color', "#EEEEEE");
     		$("input:text").each(function(){this.value="";});
     		//transition effect
     		$("#modal_window_client").fadeIn(500);
		},
		
		createclient: function(){
			this.showModalWindow();
			this.viewCreate.render();
		},
		
		editclient: function(){
			this.showModalWindow();
			this.viewUpdate.setFields(lineSelected.vname, lineSelected.vid);
			this.viewUpdate.render();
		},
		
		deleteclient: function(){
			var template = $("#confirm-client-deletion-template").html();
			
			var view = {name: lineSelected.vname};
			var html = Mustache.to_html(template, view);
			
			var answer = confirm(html);
			if (answer){
				$.ajax({
					url:"/admin/clients/delete/" + lineSelected.vid,
					success: function(){
						var successHtml = $("#client-deleted-template").html();
					
						$("#clients_notifications").text(successHtml);
						$("#clients_notifications").fadeIn(1000);
						$("#clients_notifications").fadeOut(3000);
						tableView.reload();
					}
				});
			}
		},
		
		render:function(){
			return this; 
		}
	})
	
	ClientAdminCreateView = Backbone.View.extend({
		el: "#modal_window_client",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .createClient" : "registerclient"
		},
		
		initialize:function(){
		},
		
		render : function(){
			var template = $("#popup-client-admin-create-template").html();
			
			var view = {};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_client').hide();
       		$('#mask').hide();
		},
		
		registerclient: function(){
			var json = '{"id":"0", "name":"'+$('input[name*="name"]').val()+'",'+ '"address":"'+$('input[name*="address"]').val() +'",'+ '"email":"'+$('input[name*="email"]').val() +'",'+
			'"zip":"'+$('input[name*="zip"]').val()+'",' + '"city":"'+$('input[name*="city"]').val() +'",' + '"phone":"'+$('input[name*="phone"]').val()  +'",'+ '"fax":"'+$('input[name*="fax"]').val() +'"}';
			$.ajax({
				url:"/admin/clients/create",
				data: json,
				type: "POST",
				dataType : "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$('#modal_window_client').hide();
						$('#mask').hide();
						
						var successHtml = $("#client-created-updated-template").html();
						tableView.reload();
						$("#clients_notifications").text(successHtml);
						$("#clients_notifications").fadeIn(1000);
						$("#clients_notifications").fadeOut(3000);
					} else {
						$("#clients_errors_create").text(data.result);
						$("#clients_errors_create").fadeIn(1000);
						$("#clients_errors_create").fadeOut(3000);
					}
				}
			});
		}
	}) 

	// define the application initialization
	var self = {};
	self.start = function(){
		tableView = new ClientAdminTableView().render();
		new ClientAdminButtonsView().render();
	}
	return self;
})
$( function() {
	console.log("Starting client administration application")
	new AppClientAdmin(jQuery).start();
})