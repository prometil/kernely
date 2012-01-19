AppHolidayManagerUsers = (function($){
	
	// Initialize with January
	var monthSelected = 0;
	var yearSelected = 0;
	var mainView = null;
	
	// How many days in the month
	var nbDays = 0;
	
	HolidayManagerUserMainView = Backbone.View.extend({
		el:"#main-manager-page-content",
		events:{
		
		},
		
		initialize: function(){
			
		},
		
		render: function(){
			$.ajax({
				url:"/holiday/manager/users/all",
				data: {month: monthSelected, year: yearSelected},
				dataType: "json",
				success: function(data){
					new HolidayManagerUserTableView(data).render();
					new HolidayManagerColorPartView(data).render();
					new HolidayManagerMonthSelectorView().render();
					monthSelected = data.month;
					yearSelected = data.year;
				}
			});

			return this;
		},
		reloadTable: function(){
			$.ajax({
				url:"/holiday/manager/users/all",
				data: {month: monthSelected, year: yearSelected},
				dataType: "json",
				success: function(data){
					new HolidayManagerUserTableView(data).reload();
				}
			});
		}
		
		
	})
	
	HolidayManagerMonthSelectorView = Backbone.View.extend({
		el:"#monthSelector",
		events:{
			"click .minusMonth" : "minusMonth",
			"click .plusMonth" : "plusMonth",
		},
		initialize: function(){
			
		},
		render: function(){
			var template = $("#calendarSelector").html();
			var monthTemp = monthSelected+1;
			var template2 = $("#"+ monthTemp +"-month-template").html();
			var view = {month : template2, year: yearSelected};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		plusMonth: function(){
			monthSelected ++;
			monthSelected = ((monthSelected)%13);
			if(monthSelected == 0){
				monthSelected = 1;
				yearSelected ++;
			}
			var template = $("#"+ monthSelected +"-month-template").html();
			$("#month_current").text(template + " " + yearSelected);
			mainView.reloadTable();
		},
		minusMonth: function(){
			monthSelected --;
			monthSelected = ((monthSelected)%13);
			if(monthSelected == 0){
				monthSelected = 12;
				yearSelected --;
			}
			var template = $("#"+ monthSelected +"-month-template").html();
			$("#month_current").text(template + " " + yearSelected);
			mainView.reloadTable();
		}
	})
	
	HolidayManagerUserTableLineView = Backbone.View.extend({
		tagName: "tr",
		
		user : null,
		
		events:{
		
		},
	
		initialize: function(user){
			this.user = user;
		},
		
		render: function(){
			var i = 1;
			var parent = this;
			$(this.el).append(
				$("<td>", {
					class:'row-header-user border-element-r-b',
					text: this.user.fullName
				})
			);
			
			var column1 = null;
			var column2 = null;
			for(i = 1; i<= nbDays; i ++){
				column1 = $("<td>", {
					class:'column-part-day-user border-element-r-b'
				});
				
				column2 = $("<td>", {
					class:'column-part-day-user border-element-r-b'
				});
				
				column1.css("background-color", "inherit");
				column2.css("background-color", "inherit");
				if(typeof(this.user.details) != "undefined"){
					if(this.user.details.length > 1){
						$.each(this.user.details, function(){
							if(this.dayOfMonth == i){
								if(this.am == "true"){
									
									column1.css("background-color", this.color);
								}
								if(this.pm == "true"){
									
									column2.css("background-color", this.color);
								}
							}
						});
					}
					else{
						
					}
				}
				$(parent.el).append(column1);
				$(parent.el).append(column2);
			}	
			return this;
		}
	})

	HolidayManagerUserTableView = Backbone.View.extend({
		el:"#usersHoliday",
		
		data: null,
		
		events:{
		
		},
		
		initialize: function(data){
			this.data = data;
		},
		reload: function(){
			$(this.el).html("");
			this.render();
			return this;
		},
		render: function(){
			var parent = this;
			lineHeader = $("<tr>", {
				class:'table-header border-element-r-b'
			});
			
			lineHeader.append($("<td>", {
				class: "border-element-r-b"
			}));
			for(var i = 1; i <= this.data.nbDays; i++){
				lineHeader.append($("<td>", {
					class: 'day-header-cell border-element-r-b',
					colspan: 2,
					text: i
				}));
			}
			$(this.el).append(lineHeader);
			
			
			
			nbDays = this.data.nbDays;
			
			if(this.data.usersManaged.length > 1){
				$.each(this.data.usersManaged, function(){
                  $(parent.el).append(new HolidayManagerUserTableLineView(this).render().el);
				});
			}
			else{
				$(parent.el).append(new HolidayManagerUserTableLineView(this.data.usersManaged).render().el);
			}
			return this;
		}
	})
	
	HolidayManagerColorPartView = Backbone.View.extend({
		el:"#color-legend",
		
		events:{
		
		},
		
		data : null,
		
		initialize : function(data){
			this.data = data;
		},
		render: function(){
			var parent = this;
			
			if(typeof(this.data.balances) != "undefined"){
				if(this.data.balances.length > 1){
					$.each(this.data.balances, function(){
	                    $(parent.el).append(new HolidayManagerColorCellView(this.nameOfType, this.color, this.idOfType).render().el);
					});
				}
				else{
					$(parent.el).append(new HolidayManagerColorCellView(this.data.balances.nameOfType, this.data.balances.color, this.data.balances.idOfType).render().el);
				}
			}
			return this;
		}
	})
	
	
	HolidayManagerColorCellView = Backbone.View.extend({
		tagName:"div",
		className: "balance-cell-legend",
		
		color:null,
		name:null,
		idType: null,
		
		events: {
		},
		
		initialize : function(name, color, idType){
			this.color = color;
			this.name = name;
			this.idType = idType;
		},
		
		render : function(){
			var template = $("#balance-cells-legend").html();
            var view = {name: this.name};
            var html = Mustache.to_html(template, view);
            $(this.el).html(html);
            $(this.el).css('background-color', this.color);
			return this;
		}
	})
	
	var self = {};
	self.start = function(){
		mainView = new HolidayManagerUserMainView().render();
	}
	return self;
})

$( function() {
	console.log("Starting holiday manager users application")
	new AppHolidayManagerUsers(jQuery).start();
})