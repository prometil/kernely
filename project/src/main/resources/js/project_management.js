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

AppProjectManagement = (function($){
	var lineSelected = null;
	var statusSelected = null;
	var tableView = null;
	
	ProjectAdminTableView = Backbone.View.extend({
		el:"#project_table",
		
		table:null,
		initialize:function(){
			var parent = this;
			
			var templateNameColumn = $("#table-project-name-column").text();
			var templateStatusColumn = $("#table-project-status-column").text();
			this.table = $(parent.el).kernely_table({
				columns:[{"name":templateNameColumn, style:""},
				         {"name":templateStatusColumn, style:""},
				         {"name":"", style:"invisible"},
						],
				idField:"id",
				elements:["name","statusString","status"],
				eventNames:["click"],
				events:{
					"click": parent.selectLine
				},
				editable:true
			});
			
		},
		selectLine : function(e){
			$("#changeStatusButton").removeAttr('disabled');
			lineSelected = e.data.line;
			console.log(e.data);
			statusSelected = e.data.data.status;
			$("#changeStatusButton").val($("#button-"+e.data.data.status+"-template").html());
		},
		reload: function(){
			this.render();
		},
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/project/list/managed",
				dataType:"json",
				success: function(data){
					if(data != null){
						if ($.isArray(data.projectDTO)){
							$.each(data.projectDTO, function(){
								this.statusString = $("#status-"+this.status).html();
							});
						} else {
							var dataProject = data.projectDTO;
							data.projectDTO.statusString = $("#status-"+data.projectDTO.status).html();
						}
						parent.table.reload(data.projectDTO);
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
		el:"#project_buttons",
		
		events: {
			"click #changeStatusButton" : "changeStatus",
		},
		
		changeStatus: function(){
			var newStatus;
			if (statusSelected == 1){
				// An "opened" project (0) can be "closed"
				newStatus = 2;
			} else {
				// A pending project (1) can be open, a closed project (2) too
				newStatus = 1;
			}
			
			$.ajax({
				url:"/project/status",
				data:{projectId : lineSelected, status : newStatus},
				dataType:"json",
				success: function(){
					var successHtml = $("#project-status-changed-template").html();
					$.writeMessage("success",successHtml);
					$("#changeStatusButton").attr("disabled,disabled");
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
		tableView = new ProjectAdminTableView().render();
		new ProjectAdminButtonsView().render();
	}
	return self;
})
$( function() {
	console.log("Starting project management application")
	new AppProjectManagement(jQuery).start();
})