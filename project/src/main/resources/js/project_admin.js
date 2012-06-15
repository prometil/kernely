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
	
	ProjectAdminTableView = Backbone.View.extend({
		el:"#project_admin_table",
		
		table:null,
		initialize:function(){
			var parent = this;
			
			var templateNameColumn = $("#table-project-name-column").text();
			this.table = $(parent.el).kernely_table({
				columns:[{"name":templateNameColumn, "style":""}],
				idField:"id",
				elements:["name"],
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
			$(".imageButton").removeAttr('disabled');
			lineSelected = e.data.line;
		},
		reload: function(){
			this.render();
		},
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/admin/projects/all",
				dataType:"json",
				success: function(data){
					if(data != null){
						var dataProject = data.projectDTO;
						parent.table.reload(dataProject);
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
	
	
	ProjectAdminButtonsView = Backbone.View.extend({
		el:"#project_admin_buttons",
		
		events: {
			"click .createButton" : "createproject",
			"click .editButton" : "editproject",
			"click .deleteButton" : "deleteproject",
			"click .imageButton" : "iconproject"
		},
		
		viewCreate:null,
		viewUpdate:null,
		viewIcon:null,
		
		initialize: function(){
			this.viewCreate = new ProjectAdminCreateView();
			this.viewUpdate = new ProjectAdminUpdateView("", "", 0);
		},
		
		createproject: function(){
			this.viewCreate.render();
		},
		
		editproject: function(){
			this.viewUpdate.setFields(lineSelected);
			this.viewUpdate.render();
		},
		
		deleteproject: function(){
			var html = $("#confirm-project-deletion-template").html();
			var title = $("#create-template").html();
			
			$.kernelyConfirm(title, html, this.confirmDeleteProject);
		},
		
		confirmDeleteProject: function(){
			$.ajax({
				url:"/admin/projects/delete/" + lineSelected,
				success: function(){
					var successHtml = $("#project-deleted-template").html();
					$.writeMessage("success",successHtml);
					tableView.reload();
				}
			});
		},
		
		iconproject: function(){
			// Get data about the project
			$.ajax({
				url:"/admin/projects/"+lineSelected,
				type: "POST",
				dataType : "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					var template = $("#popup-project-admin-icon-template").html();
					var iconToDisplay = data.icon;
					if (data.icon == null || data.icon == "undefined" || data.icon == ""){
						iconToDisplay = "default.png";
					}
					var view = {icon : iconToDisplay, name : data.name};
					var html = Mustache.to_html(template, view);
					var title =$("#edit-template").html();
					
					// Create the dialog
					$("#modal_window_project").kernely_dialog({
						title: title,
						content: html
					});
					$("#modal_window_project").kernely_dialog("open");
				}
			});
		},
		
		render:function(){
			return this;
		}
	})
	
	ProjectAdminCreateView = Backbone.View.extend({
		el: "#modal_window_project",
		
		events:{
		},
		
		initialize:function(){
		},
		
		render : function(){
			var parent = this;
			var html = $("#popup-project-admin-create-template").html();
			var title = $("#create-template").html();
			$("#modal_window_project").kernely_dialog({
				title: title,
				content: html,
				eventNames:'click .createProject',
				events:{
					'click .createProject' : parent.registerproject
				}
			});
			$.ajax({
				type: "GET",
				url:"/admin/projects/combobox",
				dataType:"json",
				success: function(data){
					if(data != null){
						var option = "";
						if (data.organizationDTO.length > 1){
							$.each(data.organizationDTO, function(index, value){
								option = option + '<option value="' + this.name + '">'+ this.name +'</option>' ;
							});
						}
						else{
							option = option + '<option value="' + data.organizationDTO.name + '">'+ data.organizationDTO.name +'</option>' ;
						}
						$("#combo").append('<select name="organization-choice" id="combobox">' + option + '</select>');
					}
					$("#modal_window_project").kernely_dialog("open");
					
				}
			});
			return this;
		},

		registerproject: function(){
			var json = '{"id":"0", "name":"'+$('input[name*="name"]').val() +'",' +'"organization":"'+$('#combobox').val() + '"}';
			$.ajax({
				url:"/admin/projects/create",
				data: json,
				type: "POST",
				dataType : "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$("#modal_window_project").kernely_dialog("close");
						
						var successHtml = $("#project-created-updated-template").html();
						$.writeMessage("success",successHtml);
						tableView.reload();
					} else {
						$.writeMessage("error",data.result,"#errors_message");
					}
				}
			});
		}
	}) 

	ProjectAdminUpdateView = Backbone.View.extend({
		el: "#modal_window_project",
				
		events:{
		},
	
		vid : null,
		orgaId: null,
		
		initialize:function(lineSelected){
			this.vid = lineSelected;
		},
		
		setFields: function(lineSelected){
			this.vid = lineSelected;
		},
		
		render : function(){
			var parent = this;
			// Get data about project
			$.ajax({
				url:"/admin/projects/"+lineSelected,
				type: "POST",
				dataType : "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					var template = $("#popup-project-admin-update-template").html();
					var view = {name : data.name};
					var html = Mustache.to_html(template, view);
					
					parent.orgaId = data.organization.id;

					var title = $("#edit-template").html();
					$("#modal_window_project").kernely_dialog({
						title: title,
						content: html,
						eventNames:'click .updateProject',
						events:{
							'click .updateProject' : parent.updateproject
						}
					});
					
					
					$.ajax({
						type: "GET",
						url:"/admin/projects/combobox",
						dataType:"json",
						success: function(data){
							if(data != null){
								var option = "";
								if (data.organizationDTO.length > 1){
									$.each(data.organizationDTO, function(index, value){
										if (this.id == parent.orgaId){
											option = option + '<option value="' + this.name + '"selected="selected">'+ this.name +'</option>' ;
										}
										else{
											option = option + '<option value="' + this.name + '">'+ this.name +'</option>' ;
										}
									});
								}
								else{
									option = option + '<option value="' + data.organizationDTO.name + '">'+ data.organizationDTO.name +'</option>' ;
								}
								$("#combo").append('<select name="organization-choice" id="combobox">' + option + '</select>');
							}
							new ProjectUserTableView(parent.vid).render();
							
							$("#modal_window_project").kernely_dialog("open");
						}
					});
				}
			});
			
			return this;
		},
		
		updateproject: function(){
			var count = 0;
			var users = "";
			var parent = this;
			var json = '{"id":"'+lineSelected+'", "name":"'+$('input[name*="name"]').val() + '", "icon":"'+this.vicon + '", "organization":"'+$('#combobox').val() +'"}';
			$.ajax({
				url:"/admin/projects/create",
				data: json,
				type: "POST",
				dataType: "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						var rights ;
						var right ;
						if($("input:checkbox:checked.contributor").length > 0 || $("input:checkbox:checked.project_manager").length > 0 || $("input:checkbox:checked.client").length>0){
							rights = '"rights":[';
							$("input:checkbox:checked.contributor").each(function(){
								rights += '{"id":"'+this.value+'","idType":"user", "permission":"contributor"},';
							});
							$("input:checkbox:checked.project_manager").each(function(){
								rights += '{"id":"'+this.value+'","idType":"user", "permission":"project_manager"},';
							});
							$("input:checkbox:checked.client").each(function(){
								rights += '{"id":"'+this.value+'","idType":"user", "permission":"client"},';
							}); 
							right = rights.substring(0,rights.length - 1);
							right += "]"
						}
						else{
							right = '"rights":{}';
						}
						var json2 = '{"projectid":"'+lineSelected+'",'+ right +'}';
						

						$.ajax({
							url:"/admin/projects/updaterights",
							data: json2,
							type: "POST",
							dataType: "json",
							processData: false,
							contentType: "application/json; charset=utf-8",
							success: function(data){
								
							}
						});
						
						$("#modal_window_project").kernely_dialog("close");
						
						var successHtml= $("#project-created-updated-template").html();
						$.writeMessage("success",successHtml);

						tableView.reload();
					} else {
						$.writeMessage("error",data.result,"#errors_message");
					}
				}
			});
		}
	}) 
	
	UserCBTableView = Backbone.View.extend({
		tagName:"tr",
		className:'project_list_line',
		
		vname : null,
		vcontributor:null,
		vproject_manager:null,
		vclient:null,
		vid:null,
		
		events:{
		
		},
		
		initialize:function(name, id , contributor, project_manager, client){
			this.vname=name;
			this.vcontributor=contributor; 
			this.vproject_manager=project_manager;
			this.vclient=client;
			this.vid=id;
		},
		
		render:function(){
			var checkCont ="";
			var checkClient = "";
			var checkProj ="";
			if (this.vcontributor==1){
				checkCont = '<input type="checkbox" value="'+this.vid+'" class="contributor" checked></input>';
			}else{
				checkCont = '<input type="checkbox" value="'+this.vid+'" class="contributor"></input>';
			}
			if (this.vclient==1){
				checkClient = '<input type="checkbox" value="'+this.vid+'" class="client" checked></input>';
			}else{
				checkClient = '<input type="checkbox" value="'+this.vid+'" class="client"></input>';
			}
			if (this.vproject_manager==1){
				checkProj = '<input type="checkbox" value="'+this.vid+'" class="project_manager" checked></input>';
			}else {
				checkProj = '<input type="checkbox" value="'+this.vid+'" class="project_manager"></input>';
			}
			var template = '<td>{{name}}</td><td>'+checkCont+'</td><td>'+checkProj+'</td><td>'+checkClient+'</td>';
			var view = {name : this.vname};
			var html = Mustache.to_html(template, view);
			
			$(this.el).html(html);
			$(this.el).appendTo($("#project_user_table"));
			return this;
		}
	})
	
	ProjectUserTableView = Backbone.View.extend({
		el:"#project_user_table",
		
		vprojectid : null,
		vcontributor:null,
		vclient:null,
		vprojectmanager : null,
		vtable :null,
		vtableid:null,
		
		events:{
		
		},
		
		initialize:function(projectid){
			this.vprojectid=projectid;			
			this.vcontributor=0;
			this.vclient=0;
			this.vprojectmanager=0;
			var parent = this;
			var html= $("#table-header-template2").html();

			$.ajax({
				type: "GET",
				url:"/admin/projects/rights/"+parent.vprojectid,
				dataType:"json",
				success: function(data){
					if(data != null && typeof(data) != "undefined"){
						parent.vtable = new Array(); //4 = type de permission +1
						var i = 0; 
						$.each(data.permission, function() {
							parent.vtable[i] = new Array();
							i++;
						});
						parent.vtableid = new Array(data.permission.length);
						i=0;
			    		$.each(data.permission, function() {
			    			if (this.user != null){
			    				if (this.right=="contributor"){
			    					parent.vcontributor=1;
			    				}
			    				if(this.right=="project_manager"){
			    					parent.vprojectmanager=1;
			    				}
			    				if(this.right=="client"){
			    					parent.vclient=1;
			    				}
			    			}
			    			var exist = $.inArray(this.user, parent.vtableid);
			    			if (exist != -1){
			    				if (parent.vtable[exist][1]==0){
					    			parent.vtable[exist][1] = parent.vcontributor;
			    				}
			    				if (parent.vtable[exist][2]==0){
			    					parent.vtable[exist][2] = parent.vprojectmanager;
			    				}
								if (parent.vtable[exist][3]==0){
					    			parent.vtable[exist][3] = parent.vclient;
								}
			    			}else{
				    			parent.vtableid[i] = this.user;
				    			parent.vtable[i][0] = this.user;
				    			parent.vtable[i][1] = parent.vcontributor;
				    			parent.vtable[i][2] = parent.vprojectmanager;
				    			parent.vtable[i][3] = parent.vclient;
				    			i++; 
			    			}
			    			parent.vcontributor=0;
			    			parent.vclient=0;
			    			parent.vprojectmanager=0;
			    		});
					}
					$.ajax({
						type: "GET",
						url:"/admin/users/enabled",
						dataType:"json",
						success: function(data){
							if(data.userDetailsDTO.length > 1){
					    		$.each(data.userDetailsDTO, function() {
					    			var index = $.inArray(this.user.id, parent.vtableid);
					    			if (index != -1){
					    				var view = new UserCBTableView(this.lastname + ' ' + this.firstname, this.user.id ,parent.vtable[index][1],parent.vtable[index][2],parent.vtable[index][3]);
					    				view.render();
					    			}
					    			else {
					    				var view = new UserCBTableView(this.lastname + ' ' + this.firstname, this.user.id,0,0,0);
					    				view.render(); 
					    			}
					    		});
							}
							// In the case when there is only one user.
							else{
								var index = JQuery.inArray(data.userDetailsDTO.user.id, parent.vtableid);
				    			if (index != -1){
				    				var view = new UserCBTableView(data.userDetailsDTO.lastname + ' ' +data.userDetailsDTO.firstname, data.userDetailsDTO.user.id ,parent.vtable[index][1],parent.vtable[index][2],parent.vtable[index][3]);
				    				view.render();
				    			}
								var view = new UserCBTableView(data.userDetailsDTO.lastname + ' ' + data.userDetailsDTO.firstname,  data.userDetailsDTO.user.id, 0, 0, 0);
								view.render();
							}		
						}
					});
				}
			});		
		
						
			$(this.el).html(html);
		},
		
		reload: function(){
			this.initialize();
			this.render();
		},
		render: function(){
			return this;
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