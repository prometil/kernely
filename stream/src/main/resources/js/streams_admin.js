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
		
		initialize: function(){
			this.viewCreateUpdate =  new StreamAdminCreateUpdateView("", 0,"");
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
       		$("#modal_window").css('top',  winH/2-$("#modal_window").height()/2);
     		$("#modal_window").css('left', winW/2-$("#modal_window").width()/2);
     		$("#modal_window").css('background-color', "#EEEEEE");
     		$("input:text").each(function(){this.value="";});
     		//transition effect
     		$("#modal_window").fadeIn(500);
		},
		
		createstream: function(){
			this.showModalWindow();
			// We set 0 for the id to create
			this.viewCreateUpdate.setFields("", 0,"");
			this.viewCreateUpdate.render();
		},
		
		editstream: function(){
			this.showModalWindow();
			this.viewCreateUpdate.setFields(lineSelected.vname, lineSelected.vid,lineSelected.vcategory);
			this.viewCreateUpdate.render();
		},
		
		lockstream: function(){
			var answer = confirm(lineSelected.vname + " will be locked. Do you want to continue?");
			if (answer){
				$.ajax({
					url:"/admin/streams/lock/" + lineSelected.vid,
					success: function(){
						$("#streams_notifications").text("Operation completed successfully!");
						$("#streams_notifications").fadeIn(1000);
						$("#streams_notifications").fadeOut(3000);
						tableView.reload();
					}
				});
			}
		},
		
		unlockstream: function(){
			var answer = confirm(lineSelected.vname + " will be unlocked. Do you want to continue?");
			if (answer){
				$.ajax({
					url:"/admin/streams/unlock/" + lineSelected.vid,
					success: function(){
						$("#streams_notifications").text("Operation completed successfully!");
						$("#streams_notifications").fadeIn(1000);
						$("#streams_notifications").fadeOut(3000);
						tableView.reload();
					}
				});
			}
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
		el: "#modal_window",
		events:{
			"click .closeModal" : "closemodal",
			"click .updateStream" : "updatestreamrights",
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
			
			return this;
		},
		
		closemodal: function(){
			$('#modal_window').hide();
       		$('#mask').hide();
		},
		updatestreamrights: function(){
			var usersSelect = $("select");
			var count = 0;
			var users = "";
				
			if(usersSelect.length > 0){
				rights = '"rights":[';
				
				$.each(usersSelect, function(){
					rights += '{"userid":'+this.id+',"permission":"'+$("#"+this.id+" :selected").val()+'"}';
					count++;
					if(count<usersSelect.length){
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
						$('#modal_window').hide();
						$('#mask').hide();
						$("#groups_notifications").text("Operation completed successfully !");
						$("#groups_notifications").fadeIn(1000);
						$("#groups_notifications").fadeOut(3000);
					} else {
						$("#groups_errors_update").text(data.result);
						$("#groups_errors_update").fadeIn(1000);
						$("#groups_errors_update").fadeOut(3000);
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
				url:"/admin/users/all",
				dataType:"json",
				success: function(data){
					console.log(data);
					if(data != null){
						if(data.userDetailsDTO.length > 1){
							$(parent.el).append("<table>")
				    		$.each(data.userDetailsDTO, function() {
				    			$(parent.el).append( 					
				   				'<tr><td>'+ this.lastname + ' ' + this.firstname +'</td><td>'+
				    			'<select id="'+ this.user.id +'"><option value="nothing">No right</option><option value="read">Read</option><option value="write">Read / Write</option><option value="delete">Read / Write / Delete</option></select><br/>'
				    			+'</td></tr>');
				    		});
						}
						// In the case when there is only one user.
						else{
			    			$(parent.el).append(data.userDetailsDTO.lastname+ ' ' + data.userDetailsDTO.firstname +
					    			'<select id="'+ data.userDetailsDTO.user.id +'"><option value="nothing">No right</option><option value="read">Read</option><option value="write">Read / Write</option><option value="delete">Read / Write / Delete</option></select><br/>');
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
									$('#' + this.user+" option[value='"+this.right+"']").attr("selected", "selected");
					    		});
							}
						}
					});
				}
			});
			return this;
		}
	})
	
	StreamAdminCreateUpdateView = Backbone.View.extend({
		el: "#modal_window",
		
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
		
		setFields: function(name, id,category){
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
			$('#modal_window').hide();
       		$('#mask').hide();
		},
		
		registerstream: function(){
			var json = '{"id":"'+this.vid+'", "name":"'+$('input[name*="name"]').val() + '", "category":"'+$('input[name*="category"]').val() +'"}';
			$.ajax({
				url:"/admin/streams/create",
				data: json,
				type: "POST",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "ok"){
						$('#modal_window').hide();
	       				$('#mask').hide();
						$("#streams_notifications").text("Operation completed successfully!");
						$("#streams_notifications").fadeIn(1000);
						$("#streams_notifications").fadeOut(3000);
						tableView.reload();
					} else {
						$("#streams_errors").text(data.result);
						$("#streams_errors").fadeIn(1000);
						$("#streams_errors").fadeOut(3000);
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