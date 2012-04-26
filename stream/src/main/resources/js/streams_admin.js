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
	
	
	StreamAdminTableLineView = Backbone.View.extend({
		tagName: "tr",
		className: 'stream_list_line',
		
		vid: null,
		vname : null,
		vcategory : null,
		vlocked : null,
		
		events: {
			"click" : "selectLine",
			"mouseover" : "overLine",
			"mouseout" : "outLine"
		},
		
		initialize: function(id, name, category,locked){
			this.vid = id;
			this.vname = name;
			this.vcategory = category;
			this.vlocked = locked;
		},
		selectLine : function(){
			$(".editButton").removeAttr('disabled');
			$(".rightsButton").removeAttr('disabled');
			if (this.vlocked=="true"){
				$(".unlockButton").removeAttr('disabled');
				$(".lockButton").attr('disabled','disabled');
			} else {
				$(".lockButton").removeAttr('disabled');
				$(".unlockButton").attr('disabled','disabled');
			}
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
			var image;
			var template = '<td><img src="{{icon}}"></img></td><td>{{name}}</td><td>{{category}}</td>';

			if(this.vlocked == "true"){
				image = "/img/stream_locked.png";
			}
			else if (this.vcategory == "streams/users") {
				image = "/images/icons/user.png";
			} else if (this.vcategory == "streams/plugins"){
				image = "/img/plugin.png";
			} else {
				image = "";
			}
			var view = {icon: image, name : this.vname, category : this.vcategory};
			var html = Mustache.to_html(template, view);
			
			$(this.el).html(html);
			$(this.el).appendTo($("#stream_admin_table"));
			return this;
		}
		
	})

	StreamAdminTableView = Backbone.View.extend({
		el:"#stream_admin_table",
		events:{
		
		},
		initialize:function(){
			var parent = this;
			$(this.el).html("<tr><th></th><th>Name</th><th>Category</th></tr>");
			$.ajax({
				type:"GET",
				url:"/admin/streams/all",
				dataType:"json",
				success: function(data){
					if (data != null){
						if(data.streamDTO.length > 1){
				    		$.each(data.streamDTO, function() {
				    			var view = new StreamAdminTableLineView(this.id, this.title,this.category,this.locked);
				    			view.render();
				    		});
						}
				    	// In the case when there is only one element
			    		else{
							var view = new StreamAdminTableLineView(data.streamDTO.id, data.streamDTO.title,data.streamDTO.category, data.streamDTO.locked);
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
	
	
	StreamAdminButtonsView = Backbone.View.extend({
		el:"#stream_admin_container",
		
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
			this.viewRightsUpdate = new StreamRightsUpdateView("",0);
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
       		$("#streams_modal_window").css('top',  winH/2-$("#streams_modal_window").height()/2);
     		$("#streams_modal_window").css('left', winW/2-$("#streams_modal_window").width()/2);
     		$("#streams_modal_window").css('background-color', "#EEEEEE");
     		$("input:text").each(function(){this.value="";});
     		//transition effect
     		$("#streams_modal_window").fadeIn(500);
		},
		
		createstream: function(){
			this.showModalWindow();
			this.viewCreate.render();
		},
		
		editstream: function(){
			this.showModalWindow();
			this.viewUpdate.setFields(lineSelected.vname, lineSelected.vid,lineSelected.vcategory);
			this.viewUpdate.render();
		},
		
		lockstream: function(){
			var template = $("#confirm-stream-lock-template").html();
			
			var view = {stream: lineSelected.vname};
			var html = Mustache.to_html(template, view);
			
			$.kernelyConfirm(html,this.confirmLockStream);
		},
		
		confirmLockStream: function(){
			$.ajax({
				url:"/admin/streams/lock/" + lineSelected.vid,
				success: function(){
					var successHtml = $("#stream-locked-template").html();
					$.writeMessage("success",successHtml);
					tableView.reload();
				}
			});
		},
		
		unlockstream: function(){
			var template = $("#confirm-stream-unlock-template").html();
			var view = {stream: lineSelected.vname};
			var html = Mustache.to_html(template, view);

			$.kernelyConfirm(html, this.confirmUnlockStream);
		},
		
		confirmUnlockStream: function(){
			$.ajax({
				url:"/admin/streams/unlock/" + lineSelected.vid,
				success: function(){
					var successHtml = $("#stream-unlocked-template").html();
					$.writeMessage("success",successHtml);
					tableView.reload();
				}
			});
		},
		
		changestreamrights:function (){
			this.showModalWindow();
			this.viewRightsUpdate.setFields(lineSelected.vname, lineSelected.vid);
			this.viewRightsUpdate.render();
		},
		
		render:function(){
			return this;
		}
	})
	
	StreamRightsUpdateView = Backbone.View.extend({
		el: "#streams_modal_window",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .updateStream" : "updatestreamrights",
			"click #usersTab" : "showUsersRights",
			"click #groupsTab" : "showGroupsRights"
		},
		
		vid: null,
		vname: null,
		vcategory: null,
		initialize:function(name, id,category){
			this.vid = id;
			this.vname = name;
			this.vcategory = category;
		},
		setFields: function(name, id,category){
			this.vid = id;
			this.vname = name;
			this.vcategory = category;
		},
		render : function(){
			var template = $("#popup-stream-rights-update-template").html();
			
			var view = {title : this.vname};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			
			new UserSelectView(this.vid).render();
			new GroupSelectView(this.vid).render();
			return this;
		},
		
		closemodal: function(){
			$('#streams_modal_window').hide();
       		$('#mask').hide();
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
			var json = '{"streamid":"'+this.vid+'",'+ rights +'}';

			$.ajax({
				url:"/admin/streams/updaterights",
				data: json,
				type: "POST",
				dataType: "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$('#streams_modal_window').hide();
						$('#mask').hide();
						
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

		events:{
			"click .closeModal" : "closemodal",
			"click .sendStream" : "registerstream"
		},
		
		initialize:function(name, id,category){
			this.vid = id;
			this.vname = name;
			this.vcategory = category;
		},

		render : function(){
			var template = $("#popup-stream-admin-template").html();
			
			var view = {name : this.vname, category:this.vcategory};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		closemodal: function(){
			$('#streams_modal_window').hide();
       		$('#mask').hide();
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
						$('#streams_modal_window').hide();
	       				$('#mask').hide();

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

		events:{
			"click .closeModal" : "closemodal",
			"click .updateDataStream" : "updatestream"
		},
		
		initialize:function(name, id,category){
			this.vid = id;
			this.vname = name;
			this.vcategory = category;
		},
		
		setFields: function(name, id,category){
			this.vid = id;
			this.vname = name;
			this.vcategory = category;
		},
		
		render : function(){
			var template = $("#popup-stream-admin-update-template").html();
			$.ajax({
				type: "GET",
				url : "/admin/streams/combo/" + this.vid,
				dataType:"json",
				success: function(data){
					$("#category option[value='"+data.category+"']").attr("selected", "selected");
				}
			});
			var view = {name : this.vname, category:this.vcategory};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		closemodal: function(){
			$('#streams_modal_window').hide();
       		$('#mask').hide();
		},
		
		updatestream: function(){
			var json = '{"id":"'+this.vid+'", "name":"'+$('input[name*="name"]').val() + '", "category":"'+$("#category").val() +'"}';
			$.ajax({
				url:"/admin/streams/update",
				data: json,
				type: "POST",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$('#streams_modal_window').hide();
	       				$('#mask').hide();
	       				
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