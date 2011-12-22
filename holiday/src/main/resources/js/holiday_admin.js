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
AppHolidayAdmin = (function($){
	var lineSelected = null;
	var tableView = null;
	

	HolidayAdminTableLineView = Backbone.View.extend({
		tagName: "tr",
		className: 'holiday_list_line',
		
		vid: null,
		vtype : null,
		vquantity : null,
		vunity : null,
		
		events: {
			"click" : "selectLine",
			"mouseover" : "overLine",
			"mouseout" : "outLine"
		},
		
		initialize: function(id, type, quantity, unity){
			this.vid = id;
			this.vtype = type;
			this.vquantity = quantity;
			this.vunity = unity;
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
			var template = '<td>{{type}}</td><td>{{quantity}}</td><td>{{unity}}</td>';
			var stringunity ="Undefined";
			if (this.vunity == 12) {
				stringunity = "Month";
			} else {
				stringunity = "Year";
			}
			var view = {type : this.vtype, quantity: this.vquantity, unity : stringunity};
			var html = Mustache.to_html(template, view);
			
			$(this.el).html(html);
			$(this.el).appendTo($("#holiday_admin_table"));
			return this;
		}
		
	})
	
	HolidayAdminTableView = Backbone.View.extend({
		el:"#holiday_admin_table",
		events:{
		
		},
		initialize:function(){
			var parent = this;
			$(this.el).html("<tr><th>Name</th><th>Quantity</th><th>Unit</th></tr>");
			$.ajax({
				type:"GET",
				url:"/admin/holiday/all",
				dataType:"json",
				success: function(data){
					if (data != null){
						if(data.holidayDTO.length > 1){
				    		$.each(data.holidayDTO, function() {
				    			var view = new HolidayAdminTableLineView(this.id, this.name, this.quantity, this.periodUnit);
				    			view.render();
				    		});
						}
				    	// In the case when there is only one element
			    		else{
							var view = new HolidayAdminTableLineView(data.holidayDTO.id, data.holidayDTO.name, data.holidayDTO.quantity, data.holidayDTO.periodUnit);
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
	
	HolidayAdminButtonsView = Backbone.View.extend({
		el:"#holiday_admin_container",
		
		events: {
			"click .createButton" : "createholiday",
			"click .editButton" : "editholiday",
			"click .deleteButton" : "deleteholiday"
		},
		
		viewCreate:null,
		viewUpdate:null,
		
		initialize: function(){
			this.viewCreate = new HolidayAdminCreateView();
			this.viewUpdate =  new HolidayAdminUpdateView("", 0, "", 0);
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
       		$("#modal_window_holiday").css('top',  winH/2-$("#modal_window_holiday").height()/2);
     		$("#modal_window_holiday").css('left', winW/2-$("#modal_window_holiday").width()/2);
     		$("#modal_window_holiday").css('background-color', "#EEEEEE");
     		$("input:text").each(function(){this.value="";});
     		//transition effect
     		$("#modal_window_holiday").fadeIn(500);
		},
		
		createholiday: function(){
			this.showModalWindow();
			this.viewCreate.render();
		},
		
		editholiday: function(){
			this.showModalWindow();
			this.viewUpdate.setFields(lineSelected.vtype, lineSelected.vquantity, lineSelected.vfrequency, lineSelected.vunity, lineSelected.vid);
			this.viewUpdate.render();
		},
		
		deleteholiday: function(){
			var answer = confirm(lineSelected.vname + " will be deleted. Do you want to continue ?");
			if (answer){
				$.ajax({
					url:"/admin/holiday/delete/" + lineSelected.vid,
					success: function(){
						$("#holidays_notifications").text("Operation completed successfully !");
						$("#holidays_notifications").fadeIn(1000);
						$("#holidays_notifications").fadeOut(3000);
						tableView.reload();
					}
				});
			}
		},
		
		render:function(){
			return this;
		}
	})
	
	HolidayAdminCreateView = Backbone.View.extend({
		el: "#modal_window_holiday",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .createHoliday" :  "registerholiday"
		},
		
		initialize:function(){
			
		},
		
		render : function(){
			var template = $("#popup-holiday-admin-create-template").html();
			
			var view = {};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_holiday').hide();
       		$('#mask').hide();
		},
		
		registerholiday: function(){
			var json = '{"type":"'+$('input[name*="type"]').val() + '", "quantity":"' + $('input[name*="quantity"]').val() +'", "frequency":"'+$('input[name*="frequency"]').val() + '", "unity":"'+$('#unity').val()+ '", "effectiveMonth":"0"}';
			$.ajax({
				url:"/admin/holiday/create",
				data: json,
				type: "POST",
				dataType : "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "Ok"){
						console.log("success") ; 
						$('#modal_window_holiday').hide();
						$('#mask').hide();
						$("#holidays_notifications").text("Operation completed successfully !");
						$("#holidays_notifications").fadeIn(1000);
						$("#holidays_notifications").fadeOut(3000);
						tableView.reload();
						
					} else {
						$("#holidays_errors_create").text(data.result);
						$("#holidays_errors_create").fadeIn(1000);
						$("#holidays_errors_create").fadeOut(3000);
					}
				}
			});
		}
	})
	
	HolidayAdminUpdateView = Backbone.View.extend({
		el: "#modal_window_holiday",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .updateHoliday" : "updateholiday"
		},
		
		initialize:function(type, quantity, frequency, unity, id){
			this.vid = id;
			this.vquantity = quantity;
			this.vfrequency = frequency;
			this.vtype= type;
			this.vunity=unity;
		},
		
		setFields: function(type, quantity, frequency, unity, id){
			this.vid = id;
			this.vquantity = quantity;
			this.vfrequency = frequency;
			this.vtype = type;
			this.vunity = unity;
		},
		
		render : function(){
			var template = $("#popup-holiday-admin-update-template").html();
			$.ajax({
				type: "GET",
				url : "/admin/holiday/combo/" + this.vid,
				dataType:"json",
				success: function(data){
					if(data != null){
						var option = "";
						var unity = data.periodUnit; 
						if (unity == 12){
							option = '<option value="12" selected="selected">month</option><option value="1">year</option>';
						}
						if (unity == 1){
							option = '<option value="12">month</opton><option value="1" selected="selected">year</opton>';
						}
						else {
							option = '<option value="12">month</opton><option value="1">year</option>';
						}
						$("#selected").append('<select name="unity" id="unity">'+ option + '</select>'); 		
					}
				}
			});
			var view = {type : this.vtype, quantity : this.vquantity, frequency : this.vfrequency, unity : this.vunity};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_holiday').hide();
       		$('#mask').hide();
		},
		
		updateholiday: function(){
			var json = '{"id":"'+this.vid+'", "type":"'+$('input[name*="type"]').val() + '", "quantity":"' + $('input[name*="quantity"]').val() + '", "frequency":"'+$('input[name*="frequency"]').val()+ '", "unity":"'+$('#unity').val() + '", "effectiveMonth":"0"}';
			$.ajax({
				url:"/admin/holiday/update",
				data: json,
				type: "POST",
				dataType: "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "Ok"){
						$('#modal_window_holiday').hide();
						$('#mask').hide();
						$("#holidays_notifications").text("Operation completed successfully !");
						$("#holidays_notifications").fadeIn(1000);
						$("#holidays_notifications").fadeOut(3000);
						tableView.reload();
					} else {
						$("#holidays_errors_update").text(data.result);
						$("#holidays_errors_update").fadeIn(1000);
						$("#holidays_errors_update").fadeOut(3000);
					}
				}
			});
		}
	}) 	

// define the application initialization
	var self = {};
	self.start = function(){
		tableView = new HolidayAdminTableView().render();
		new HolidayAdminButtonsView().render();
		
	}
	return self;
})

$( function() {
	console.log("Starting holiday administration application")
	new AppHolidayAdmin(jQuery).start();
})