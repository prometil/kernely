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


AppProjectAdmin = (function($){
	var lineSelected = null;
	var tableView = null;
	

	ProjectAdminTableLineView = Backbone.View.extend({
		tagName: "tr",
		className: 'project_list_line',
		
		vid: null,
		vname : null,
		
		events: {
			"click" : "selectLine",
			"mouseover" : "overLine",
			"mouseout" : "outLine"
		},
		
		initialize: function(id, name){
			this.vid = id;
			this.vname = name;
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
			$(this.el).appendTo($("#project_admin_table"));
			return this;
		}
		
	})
	
	
	ProjectAdminTableView = Backbone.View.extend({
		el:"#project_admin_table",
		events:{
		
		},
		
		initialize:function(){
			var parent = this;
			var html= $("#table-header-template").html();

			$(this.el).html(html);
			$.ajax({
				type:"GET",
				url:"/admin/projects/all",
				dataType:"json",
				success: function(data){
					if(data != null){
						if(data.projectDTO.length > 1){
				    		$.each(data.projectDTO, function() {
				    			var view = new ProjectAdminTableLineView(this.id, this.name);
				    			view.render();
				    		});
						}
					   	// In the case when there is only one element
			    		else{
			    			var view = new ProjectAdminTableLineView(data.projectDTO.id, data.projectDTO.name);
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
	
	
	ProjectAdminButtonsView = Backbone.View.extend({
		el:"#project_admin_container",
		
		events: {
			"click .createButton" : "createproject",
			"click .editButton" : "editproject",
			"click .deleteButton" : "deleteproject"
		},
		
		viewCreate:null,
		viewUpdate:null,
		
		initialize: function(){
			this.viewCreate = new ProjectAdminCreateView();
	//		this.viewUpdate = new ProjectAdminUpdateView("", 0);
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
       		$("#modal_window_project").css('top',  winH/2-$("#modal_window_project").height()/2);
     		$("#modal_window_project").css('left', winW/2-$("#modal_window_project").width()/2);
     		$("#modal_window_project").css('background-color', "#EEEEEE");
     		$("input:text").each(function(){this.value="";});
     		//transition effect
     		$("#modal_window_project").fadeIn(500);
		},
		
		createproject: function(){
			this.showModalWindow();
			this.viewCreate.render();
		},
		
		editproject: function(){
			this.showModalWindow();
			this.viewUpdate.setFields(lineSelected.vname, lineSelected.vid);
			this.viewUpdate.render();
		},
		
		deleteproject: function(){
			var template = $("#confirm-project-deletion-template").html();
			
			var view = {name: lineSelected.vname};
			var html = Mustache.to_html(template, view);
			
			var answer = confirm(html);
			if (answer){
				$.ajax({
					url:"/admin/projects/delete/" + lineSelected.vid,
					success: function(){
						var successHtml = $("#project-deleted-template").html();
					
						$("#projects_notifications").text(successHtml);
						$("#projects_notifications").fadeIn(1000);
						$("#projects_notifications").fadeOut(3000);
						tableView.reload();
					}
				});
			}
		},
		
		render:function(){
			return this;
		}
	})
	
	ProjectAdminCreateView = Backbone.View.extend({
		el: "#modal_window_project",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .createProject" : "registerproject"
		},
		
		initialize:function(){
		},
		
		render : function(){
			var template = $("#popup-project-admin-create-template").html();
			
			var view = {};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_project').hide();
       		$('#mask').hide();
		},
		
		registerproject: function(){
			var json = '{"id":"0", "name":"'+$('input[name*="name"]').val() + '"}';
			$.ajax({
				url:"/admin/projects/create",
				data: json,
				type: "POST",
				dataType : "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$('#modal_window_project').hide();
						$('#mask').hide();
						
						var successHtml = $("#project-created-updated-template").html();
						tableView.reload();
						console.log(successHtml);
						$("#projects_notifications").text(successHtml);
						$("#projects_notifications").fadeIn(1000);
						$("#projects_notifications").fadeOut(3000);
					} else {
						$("#projects_errors_create").text(data.result);
						$("#projects_errors_create").fadeIn(1000);
						$("#projects_errors_create").fadeOut(3000);
					}
				}
			});
		}
	}) 

	// define the application initialization
	var self = {};
	self.start = function(){
		tableView = new ProjectAdminTableView().render();
		new ProjectAdminButtonsView().render();
	}
	return self;
})
$( function() {
	console.log("Starting project administration application")
	new AppProjectAdmin(jQuery).start();
})