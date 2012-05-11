/*
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Softfware Foundation, either version 3 of
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
AppStreamAdmin = (function($){
	var lineSelected = null;
	var tableView = null;
	

	StreamAdminTableView = Backbone.View.extend({
		el:"#stream_admin_table",
		events:{
		
		},
		table:null,
		initialize:function(){
			var parent = this;
			
			var templateNameColumn = $("#table-stream-name-column").text();
			var templateCategoryColumn = $("#table-stream-category-column").text();
			var templateTypeColumn = $("#table-stream-type-column").text();
			this.table = $(parent.el).kernely_table({
				columns:[
				      {"name":templateTypeColumn, "style":["text-center","icon-column"]},
				      {"name":templateNameColumn, "style":""},
				      {"name":templateCategoryColumn, "style":"text-center"}],
				idField:"id",
				elements:["type", "title", "category"],
				eventNames:["click"],
				events:{
					"click": parent.selectLine
				}
			});
			
		},
		reload: function(){
			this.render();
		},
		selectLine : function(e){
			$(".editButton").removeAttr('disabled');
			$(".lockButton").removeAttr('disabled');
			$(".unlockButton").removeAttr('disabled');
			$(".rightsButton").removeAttr('disabled');
			lineSelected = e.data.line;
		},
		render: function(){
			var parent = this;
			$.ajax({
				type:"GET",
				url:"/admin/streams/all",
				dataType:"json",
				success: function(data){
					if (data != null){
						var dataStream = data.streamDTO;
						if($.isArray(dataStream)){
							$.each(dataStream, function(){
								if(this.locked == "true"){
									this.type = '<img src="/images/stream-lock-icon.png"/>';
								}
								else{
									if(this.category == "streams/users"){
										this.type = '<img src="/images/stream-user-icon.png"/>';
									}
									else{
										if(this.category == "streams/others"){
											this.type = '<img src="/images/stream-other-icon.png"/>';
										}
										else{
											if(this.category == "streams/plugins"){
												this.type = '<img src="/images/stream-plugin-icon.png"/>';
											}
											else{
												this.type='';
											}
										}
									}
								}
							});
						}
						else{
							if(dataStream.locked == "true"){
								dataStream.type = '<img src="/images/stream-lock-icon.png"/>';
							}
							else{
								if(dataStream.category == "streams/users"){
									dataStream.type = '<img src="/images/stream-user-icon.png"/>';
								}
								else{
									console.log(1);
									if(dataStream.category == "streams/others"){
										dataStream.type = '<img src="/images/stream-other-icon.png"/>';
									}
									else{
										console.log(dataStream.category);
										if(dataStream.category == "streams/plugins"){
											dataStream.type = '<img src="/images/stream-plugin-icon.png"/>';
											console.log(3);
										}
										else{
											dataStream.type='';
										}
									}
								}
							}
						}
						parent.table.reload(dataStream);
					}
				}
			});
			return this;
		}
	})	
	
	
	StreamAdminButtonsView = Backbone.View.extend({
		el:"#stream_admin_buttons",
		
		events: {
			"click .createButton" : "createstream",
			"click .editButton" : "editstream",
			"click .lockButton" : "lockstream",
			"click .unlockButton" : "unlockstream",
			"click .rightsButton" : "changestreamrights"
		},

		viewRightsUpdate:null,
		viewCreate:null,
		viewUpdate:null,
		
		initialize: function(){
			this.viewCreate =  new StreamAdminCreateView("", 0,"");
			this.viewUpdate = new StreamAdminUpdateView("", 0,"");
			this.viewRightsUpdate = new StreamRightsUpdateView();
		},
		
		createstream: function(){
			this.viewCreate.render();
		},
		
		editstream: function(){
			this.viewUpdate.setFields(lineSelected);
			this.viewUpdate.render();
		},
		
		lockstream: function(){
			var html = $("#confirm-stream-lock-template").html();
			var title = $("#lock-template").html();
			$.kernelyConfirm(title,html,this.confirmLockStream);
		},
		
		confirmLockStream: function(){
			$.ajax({
				url:"/admin/streams/lock/" + lineSelected,
				success: function(){
					var successHtml = $("#stream-locked-template").html();
					$.writeMessage("success",successHtml);
					tableView.reload();
				}
			});
		},
		
		unlockstream: function(){
			var html = $("#confirm-stream-unlock-template").html();
			var title = $("#unlock-template").html();

			$.kernelyConfirm(title,html, this.confirmUnlockStream);
		},
		
		confirmUnlockStream: function(){
			$.ajax({
				url:"/admin/streams/unlock/" + lineSelected,
				success: function(){
					var successHtml = $("#stream-unlocked-template").html();
					$.writeMessage("success",successHtml);
					tableView.reload();
				}
			});
		},
		
		changestreamrights:function (){
			this.viewRightsUpdate.render();
		},
		
		render:function(){
			return this;
		}
	})
	
	StreamRightsUpdateView = Backbone.View.extend({
		el: "#streams_modal_window",
		
		render : function(){
			var parent = this;
			var html = $("#popup-stream-rights-update-template").html();
			var title = $("#rights-template").html();
			
			$("#streams_modal_window").kernely_dialog({
				title: title,
				content: html,
				eventNames:['click .updateStream','click .usersTab','click .groupsTab'],
				events:{
					'click .updateStream' : parent.updatestreamrights,
					"click .usersTab" : parent.showUsersRights,
					"click .groupsTab" : parent.showGroupsRights
				}
			});
			
			new UserSelectView(lineSelected).render();
			new GroupSelectView(lineSelected).render();
			
			$("#streams_modal_window").kernely_dialog("open");

			return this;
		},

		showUsersRights: function(){
			$("#usersToRight").removeClass("tabHiddenContent").addClass("tabContent").show();
			$("#groupsToRight").removeClass("tabContent").addClass("tabHiddenContent").show();
			$("#usersTab").removeClass("tab").addClass("selectedTab").show();
			$("#groupsTab").removeClass("selectedTab").addClass("tab").show();
		},
		
		showGroupsRights: function(){
			$("#usersToRight").removeClass("tabContent").addClass("tabHiddenContent").show();
			$("#groupsToRight").removeClass("tabHiddenContent").addClass("tabContent").show();
			$("#groupsTab").removeClass("tab").addClass("selectedTab").show();
			$("#usersTab").removeClass("selectedTab").addClass("tab").show();
		},
	
		updatestreamrights: function(){
			var usersSelect = $("select.userscombo");
			var groupsSelect = $("select.groupscombo");
			var count = 0;
			if(usersSelect.length + groupsSelect.length > 0){
				rights = '"rights":[';
				
				$.each(usersSelect, function(){
					rights += '{"id":'+this.id+',"idType":"user", "permission":"'+$("#"+this.id+" :selected").val()+'"}';
					count++;
					if(count<usersSelect.length){
						rights += ',';
					}
				});
				if (groupsSelect.length > 0){
					rights += ',';
				}
				count  = 0;
				$.each(groupsSelect, function(){
					rights += '{"id":'+this.id+', "idType":"group", "permission":"'+$("#"+this.id+" :selected").val()+'"}';
					count++;
					if(count<groupsSelect.length){
						rights += ',';
					}
				});

				rights += "]";
			}
			else{
				rights = '"rights":{}';
			}
			var json = '{"streamid":"'+lineSelected+'",'+ rights +'}';

			$.ajax({
				url:"/admin/streams/updaterights",
				data: json,
				type: "POST",
				dataType: "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$("#streams_modal_window").kernely_dialog("close");
						var successHtml = $("#rights-updated-template").html();
						$.writeMessage("success",successHtml);
					} else {
						$.writeMessage("error",data.result,"#errors_message");
					}
				}
			});
		}
	})
	
	
	UserSelectView = Backbone.View.extend({
		el:"#usersToRight",
		
		streamId: null,
		
		events:{
		
		},
		
		initialize:function(streamid){
			this.streamId = streamid;
		},
		
		render: function(){
			var parent = this;

			// Build the table
			$.ajax({
				type: "GET",
				url:"/admin/users/enabled",
				dataType:"json",
				success: function(data){
					if(data != null){
						if(data.userDetailsDTO.length > 1){
							$(parent.el).append("<table>")
				    		$.each(data.userDetailsDTO, function() {
				    			
				    			var template = $("#stream-users-rights-combo-template").html();
				    			
				    			var view = {lastname: this.lastname, firstname: this.firstname, id: this.user.id};
				    			var html = Mustache.to_html(template, view);
				    			
				    			$(parent.el).append(html);
				    		});
						}
						// In the case when there is only one user.
						else{
			    			var template = $("#stream-users-rights-combo-template").html();
			    			
			    			var view = {lastname: data.userDetailsDTO.lastname, firstname: data.userDetailsDTO.firstname, id: data.userDetailsDTO.user.id};
			    			var html = Mustache.to_html(template, view);
			    			
			    			$(parent.el).append(html);
						}
					}
					$(parent.el).append("</table>");
					
					// Select existing rights
					$.ajax({
						type: "GET",
						url:"/admin/streams/rights/"+parent.streamId,
						dataType:"json",
						success: function(data){
							if(data != null && typeof(data) != "undefined"){
					    		$.each(data.permission, function() {
					    			if (this.user != null){
					    				$('#' + this.user+" option[value='"+this.right+"']").attr("selected", "selected");
					    			}
					    		});
							}
						}
					});
				}
			});
			return this;
		}
	})
	
	GroupSelectView = Backbone.View.extend({
		el:"#groupsToRight",
		
		streamId: null,
		
		events:{
		
		},
		
		initialize:function(streamid){
			this.streamId = streamid;
		},
		
		render: function(){
			var parent = this;

			// Build the table
			$.ajax({
				type: "GET",
				url:"/admin/groups/all",
				dataType:"json",
				success: function(data){
					if(data != null){
						if(data.groupDTO.length > 1){
							$(parent.el).append("<table>")
				    		$.each(data.groupDTO, function() {
				    			
				    			var template = $("#stream-groups-rights-combo-template").html();
				    			
				    			var view = {name: this.name, id: this.id};
				    			var html = Mustache.to_html(template, view);
				    			
				    			$(parent.el).append(html);
				    		});
						}
						// In the case when there is only one user.
						else{
			    			var template = $("#stream-groups-rights-combo-template").html();
			    			var view = {name: data.groupDTO.name, id: data.groupDTO.id};
			    			var html = Mustache.to_html(template, view);
			    			
			    			$(parent.el).append(html);
						}
					}
					$(parent.el).append("</table>");
					
					// Select existing rights
					$.ajax({
						type: "GET",
						url:"/admin/streams/rights/"+parent.streamId,
						dataType:"json",
						success: function(data){
							if(data != null && typeof(data) != "undefined"){
					    		$.each(data.permission, function() {
					    			if (this.group != null){
										$('#' + this.group+" option[value='"+this.right+"']").attr("selected", "selected");					    				
					    			}
					    		});
							}
						}
					});
				}
			});
			return this;
		}
	})
	
	StreamAdminCreateView = Backbone.View.extend({
		el: "#streams_modal_window",
		
		vid: null,
		vname: null,
		vcategory : null,

		initialize:function(name, id,category){
			this.vid = id;
			this.vname = name;
			this.vcategory = category;
		},

		render : function(){
			var parent = this;
		
			var template = $("#popup-stream-admin-template").html();
			var view = {name : this.vname, category:this.vcategory};
			var html = Mustache.to_html(template, view);
			var title = $("#create-template").html();
			$("#streams_modal_window").kernely_dialog({
				title: title,
				content: html,
				eventNames:'click .sendStream',
				events:{
					'click .sendStream' : parent.registerstream
				}
			});
			$("#streams_modal_window").kernely_dialog("open");
			return this;
		},
		
		registerstream: function(){
			var json = '{"id":"'+this.vid+'", "name":"'+$('input[name*="name"]').val() + '", "category":"'+$("#category").val() +'"}';
			$.ajax({
				url:"/admin/streams/create",
				data: json,
				type: "POST",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$("#streams_modal_window").kernely_dialog("close");
	       				var html = $("#stream-created-template").html();

						$.writeMessage("success",html);
						tableView.reload();
					} else {
						$.writeMessage("error",data.result,"#errors_message");
					}
				}
			});
		}
	})
	
		
	StreamAdminUpdateView = Backbone.View.extend({
		el: "#streams_modal_window",
		
		vid: null,
		vname: null,
		vcategory : null,

		initialize:function(id){
			this.vid = id;
		},
		
		setFields: function(id){
			this.vid = id;
		},
		
		render : function(){
			var parent = this;
			$.ajax({
				type: "GET",
				url : "/admin/streams/" + lineSelected,
				dataType:"json",
				success: function(data){
					var template = $("#popup-stream-admin-update-template").html();
					var view = {name : data.title, category:data.category};
					var html = Mustache.to_html(template, view);
					var title = $("#edit-template").html();
					$("#streams_modal_window").kernely_dialog({
						title: title,
						content: html,
						eventNames:'click .updateDataStream',
						events:{
							'click .updateDataStream' : parent.updatestream
						}
					});
					$("#streams_modal_window").kernely_dialog("open");
					
					$("#category option[value='"+data.category+"']").attr("selected", "selected");
				}
			});

			return this;
		},
		
		updatestream: function(){
			var json = '{"id":"'+lineSelected+'", "name":"'+$('input[name*="name"]').val() + '", "category":"'+$("#category").val() +'"}';
			$.ajax({
				url:"/admin/streams/update",
				data: json,
				type: "POST",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$("#streams_modal_window").kernely_dialog("close");
	       				var html = $("#stream-updated-template").html();
	       				
						$.writeMessage("success",html);
						tableView.reload();
					} else {
						$.writeMessage("error",data.result,"#errors_message");
					}
				}
			});
		}
	}) 
	
	
	// define the application initialization
	var self = {};
	self.start = function(){
		tableView = new StreamAdminTableView().render();
		new StreamAdminButtonsView().render();
	}
	return self;
})

$( function() {
	new AppStreamAdmin(jQuery).start();
})