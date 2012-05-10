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
	var selectorView = null;
	var monthSelected = 0;
	var yearSelected = 0;
	
	HolidaySummaryLineView = Backbone.View.extend({
		tagName: "tr",
		
		vid: null,
		vuser: null,
		vtypes: null,
		
		events: {
		},
		
		initialize: function(id, user, types){
			this.vid = id;
			this.vuser = vuser;
			this.vtypes = types;
		},
		selectLine : function(){

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
			
			var line;
			
			if ($.isArray(this.vypes)){
				$.each(this.vtypes, function() {
					line += '<td>'+this+'</td>';
	    		});
			}
			else {
				line += '<td>'+this.vtypes+'</td>';
			}
			
			$(this.el).html(line);
			$(this.el).appendTo($("#holiday_admin_table"));
			return this;
		}
		
	})
	
	HolidaySummaryTableView = Backbone.View.extend({
		el:"#holiday-summary",
		
		profiles:null,
		userTypes:null,
		
		events:{
		
		},
		initialize:function(){
			
		},
		
		reload: function(){
			var parent = this;
			var html = $("#table-header").html();
			$(this.el).html(html);
 			$.ajax({
				type:"GET",
				url:"/holiday/summary/allprofiles",
				dataType:"json",
				data:{month:monthSelected,year:yearSelected},
				success: function(data){
					if (data != null){
						// Build each table
						// Create array from single profile summary
						var view;
						if (! $.isArray(data.holidayProfilesSummaryDTO)){
							// Update the date in month selector
							monthSelected = data.holidayProfilesSummaryDTO.month;
							yearSelected = data.holidayProfilesSummaryDTO.year;
							
							view = new HolidayProfileTableView(data.holidayProfilesSummaryDTO.name, data.holidayProfilesSummaryDTO.usersSummaries);
							view.render();
						}
						else{

							$.each(data.holidayProfilesSummaryDTO, function() {
								// Update the date in month selector
								monthSelected = this.month;
								yearSelected = this.year;
								
								view = new HolidayProfileTableView(this.name, this.usersSummaries);
								view.render();
							});
						}
						
					}
				}
			});
		},
		change: function(month, year){
			monthSelected = month;
			yearSelected = year;
			tableView.reload();
		},
		render: function(){
			return this;
		}
	})
	
	
	HolidayProfileTableView = Backbone.View.extend({
		tagName:"table",
		className:"holiday_summary_profile_table",
		
		name:null,
		usersSummaries:null,
		
		events:{
			
		},
		initialize:function(name, usersSummaries){
			this.name = name;
			this.usersSummaries = usersSummaries;
		},
		
		reload: function(){
			this.initialize();
			this.render();
		},
		render: function(){
			var parent = this;
			
			// Create array from single users in the summary
			if (! $.isArray(this.usersSummaries)){
				var newTab = new Array();
				newTab.push(this.usersSummaries);
				this.usersSummaries = newTab;
			}
			
			$(this.el).html('<caption class="holiday-summary-table-caption">'+this.name+'</caption>');

			// Build header (profile types)
			var header = '<tr><th class="holiday-summary-table-header">Users</th>';
			
			var firstUser = this.usersSummaries[0];
			
			if (firstUser != null){
			// Get all types for this profile
				var types = firstUser.typesSummaries;
				
				// Create array from single element
				if (! $.isArray(types)){
					var newTab = new Array();
					newTab.push(types);
					types = newTab;
				}
				
				$.each(types, function() {
					header += '<th class="holiday-summary-table-header">' + this.type.name + '</th><th style="width:15px; background-color:'+this.type.color+'; border:1px solid black;"></th>';
				});
				header += "</tr>";
				$(this.el).append(header);

				// Build users lines
				$.each(this.usersSummaries, function(){
					var line = "<tr><td class=>"+this.details.firstname+" "+this.details.lastname+"</td>";
					
					var typesSummaries = this.typesSummaries;
					// Create array from single element
					if (! $.isArray(typesSummaries)){
						var newTab = new Array();
						newTab.push(typesSummaries);
						typesSummaries = newTab;
					}
					
					// Search the value for each holiday type
					$.each(typesSummaries, function(){
						line += '<td class="holiday-summary-taken-pending" colspan="2">'+this.taken+" (+ "+this.pending+")</td>";
					});
					line += "</tr>";
					$(parent.el).append(line);
				});
			}
			
			$(this.el).appendTo($("#holiday-summary"));
			
			return this;
		},
		
	})
	
	HolidayMonthSelectorView = Backbone.View.extend({
		el:"#monthSelector",
		render: function(){
			var selector = $("#monthSelector").kernely_date_navigator(
					{
						"onchange":tableView.change
					}
			);
			return this;
		},
	})
	

// define the application initialization
	var self = {};
	self.start = function(){
		tableView = new HolidaySummaryTableView().render();
		selectorView = new HolidayMonthSelectorView().render();
	}
	return self;
})

$( function() {
	console.log("Starting holiday administration application")
	new AppHolidayAdmin(jQuery).start();
})