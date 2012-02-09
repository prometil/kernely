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
		veffectivemonth : null,
		vanticipation : null,
		vcolor : null,
		
		events: {
			"click" : "selectLine",
			"mouseover" : "overLine",
			"mouseout" : "outLine"
		
		},
		
		initialize: function(id, type, unlimited, quantity, unity, effectivemonth, anticipation, color){
			this.vid = id;
			this.vtype = type;
			this.vunlimited = unlimited,
			this.vquantity = quantity;
			this.vunity = unity;
			this.veffectivemonth = effectivemonth;
			this.vanticipation = anticipation;
			this.vcolor = color;
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
			var template = '<td>{{type}}</td><td>{{unlimited}}</td><td>{{quantity}}</td><td>{{unity}}</td><td>{{effectivemonth}}</td><td>{{anticipation}}</td>';
			var stringunity ="Undefined";
			if (this.vunity == 12) {
				stringunity = $("#month-template").html();
			} else {
				stringunity = $("#year-template").html();
			}
			
			var stringmonth = $("#"+this.veffectivemonth+"-month-template").html();

			var stringanticipation ="Undefined";
			var stringunlimited ="Undefined";

			if (parseInt(this.veffectivemonth) == 12){
				stringanticipation = "-";
			} else if (this.vanticipation == "true") {
				stringanticipation = $("#yes-template").html();
			} else {
				stringanticipation = $("#no-template").html();
			}
			if (this.vunlimited == "true"){
				stringunlimited = $("#yes-template").html()
				p
			} else {
				stringunlimited = $("#no-template").html();
			}
			
			var view = {type : this.vtype, unlimited:stringunlimited, quantity: this.vquantity, unity : stringunity, effectivemonth: stringmonth, anticipation: stringanticipation};
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
			var html = $("#table-header").html();
			$(this.el).html(html);
			$.ajax({
				type:"GET",
				url:"/admin/holiday/all",
				dataType:"json",
				success: function(data){
					if (data != null){
						if(data.holidayDTO.length > 1){
				    		$.each(data.holidayDTO, function() {
				    			var view = new HolidayAdminTableLineView(this.id, this.name, this.unlimited, this.quantity, this.periodUnit, this.effectiveMonth, this.anticipation, this.color);
				    			view.render();
				    		});
						}
				    	// In the case when there is only one element
			    		else{
							var view = new HolidayAdminTableLineView(data.holidayDTO.id, data.holidayDTO.name, data.holidayDTO.unlimited, data.holidayDTO.quantity, data.holidayDTO.periodUnit, data.holidayDTO.effectiveMonth, data.holidayDTO.anticipation, data.holidayDTO.color);
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
		},
		
		viewCreate:null,
		viewUpdate:null,
		
		initialize: function(){
			this.viewCreate = new HolidayAdminCreateView();
			this.viewUpdate =  new HolidayAdminUpdateView();
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
			this.viewUpdate.setFields(lineSelected.vtype, lineSelected.vunlimited, lineSelected.vquantity, lineSelected.vfrequency, lineSelected.vunity, lineSelected.veffectivemonth, lineSelected.vanticipation, lineSelected.vid, lineSelected.vcolor);
			this.viewUpdate.render();
		},
		
		render:function(){
			return this;
		}
	})
	
	HolidayAdminCreateView = Backbone.View.extend({
		el: "#modal_window_holiday",
		
		events:{
			"click .closeModal" : "closemodal",
			"click .createHoliday" :  "registerholiday",
			"click #unlimited-holiday" : "changeUnlimited"
		},
		
		initialize:function(){
			
		},
		
		render : function(){
			var template = $("#popup-holiday-admin-create-template").html();
			
			var view = {};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			$('#colorpicker').farbtastic('#color');
			return this;
		},
		
		closemodal: function(){
			$('#modal_window_holiday').hide();
       		$('#mask').hide();
		},
		
		changeUnlimited: function(){
			if ($('input[name*="unlimited"]:checked').length == 1){
				$("#holiday-quantity").attr("disabled",true);
				$("#unity").attr("disabled",true);
				$("#effectivemonth").attr("disabled",true);
				$("#anticipated").attr("disabled",true);
			}
			else {
				$("#holiday-quantity").attr("disabled",false);
				$("#unity").attr("disabled",false);
				$("#effectivemonth").attr("disabled",false);
				$("#anticipated").attr("disabled",false);
			}
		},
		
		registerholiday: function(){
			var anticipation = false;
			var unlimited = false;
			if ($('input[name*="anticipated"]:checked').length == 1){
				anticipation = true;
			}
			if ($('input[name*="unlimited"]:checked').length == 1){
				unlimited = true;
			}
			var json = '{"type":"'+$('input[name*="type"]').val() + '", "unlimited":'+ unlimited + ', "quantity":"' + $('input[name*="quantity"]').val() +'", "frequency":"'+$('input[name*="frequency"]').val() + '", "unity":"'+$('#unity').val()+ '", "effectiveMonth":"' + $('#effectivemonth').val() + '", "anticipation":'+ anticipation +', "color":"'+ $('#color').val() +'"}';
			$.ajax({
				url:"/admin/holiday/create",
				data: json,
				type: "POST",
				dataType : "json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					if (data.result == "Ok"){
						$('#modal_window_holiday').hide();
						$('#mask').hide();
						
						var successHtml = $("#holiday-success-message-template").html();
										
						$("#holidays_notifications").text(successHtml);
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
			"click .updateHoliday" : "updateholiday",
			"click #holiday-unlimited" : "changeUnlimited"
		},
		
		initialize:function(){
			
			this.vid = null;
			this.vquantity = null;
			this.vunlimited = null;
			this.vfrequency = null;
			this.vtype= null;
			this.vunity=null;
			this.veffectivemonth = null;
			this.vanticipation = null;
			this.vcolor = null;
		},
		
		setFields: function(type, unlimited, quantity, frequency, unity,effectivemonth, anticipation, id, color){
			this.vid = id;
			this.vunlimited = unlimited;
			this.vquantity = quantity;
			this.vfrequency = frequency;
			this.vtype = type;
			this.vunity = unity;
			this.veffectivemonth = effectivemonth;
			this.vanticipation = anticipation;
			this.vcolor = color;
		},
		
		render : function(){
			
			var parent = this;
			
			var template = $("#popup-holiday-admin-update-template").html();
			
			$.ajax({
				type: "GET",
				url : "/admin/holiday/combo/" + this.vid,
				dataType:"json",
				success: function(data){
					if(data != null){
						var option = "";
						$("#unity").val(parseInt(data.periodUnit));
	
						if (parent.vanticipation == "true"){
							$("#anticipated").attr('checked','checked');
						}
						if (parent.vunlimited == "true"){
							$("#holiday-unlimited").attr('checked','checked');
							$("#holiday-quantity").attr("disabled",true);
							$("#unity").attr("disabled",true);
							$("#effectivemonth").attr("disabled",true);
							$("#anticipated").attr("disabled",true);
						}
						$("#effectivemonth").val(parseInt(parent.veffectivemonth));
						
					}
				}
			});
			var view = {type : this.vtype, unlimited : this.vunlimited, quantity : this.vquantity, frequency : this.vfrequency, unity : this.vunity, color: this.vcolor};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			$('#colorpicker').farbtastic('#color');
			return this;
		},
		changeUnlimited: function(){
			if ($('input[name*="unlimited"]:checked').length == 1){
				$("#holiday-quantity").attr("disabled",true);
				$("#unity").attr("disabled",true);
				$("#effectivemonth").attr("disabled",true);
				$("#anticipated").attr("disabled",true);
			}
			else {
				$("#holiday-quantity").attr("disabled",false);
				$("#unity").attr("disabled",false);
				$("#effectivemonth").attr("disabled",false);
				$("#anticipated").attr("disabled",false);
			}
		},
		closemodal: function(){
			$('#modal_window_holiday').hide();
       		$('#mask').hide();
		},
		
		updateholiday: function(){
			var anticipation = false;
			var unlimited = false;
			if ($('input[name*="anticipated"]:checked').length == 1){
				anticipation = true;
			}
			if ($('input[name*="unlimited"]:checked').length == 1){
				unlimited = true;
			}

			var json = '{"id":"'+this.vid+'", "type":"'+$('input[name*="type"]').val() + '", "unlimited":'+unlimited+', "quantity":"' + $('input[name*="quantity"]').val() + '", "frequency":"'+$('input[name*="frequency"]').val()+ '", "unity":"'+$('#unity').val() + '", "effectiveMonth":"' + $('#effectivemonth').val() + '", "anticipation":"' + anticipation + '", "color":"'+ $('#color').val() +'"}';
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
						
						var successHtml = $("#holiday-success-message-template").html();
						
						$("#holidays_notifications").text(successHtml);
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