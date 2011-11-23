AppGroupAdmin = (function($){
	var lineSelected = null;
	var tableView = null;
	
	
	GroupAdminTableLineView = Backbone.View.extend({
		tagName: "tr",
		className: 'group_list_line',
		
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
			$(".lockButton").removeAttr('disabled');
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
			var template = '<td>{{name}}</td><td>{{members}}</td>';
			var view = {name : this.vname, members: 0};
			var html = Mustache.to_html(template, view);
			
			$(this.el).html(html);
			$(this.el).appendTo($("#group_admin_table"));
			return this;
		}
		
	})

	GroupAdminTableView = Backbone.View.extend({
		el:"group_admin_table",
		events:{
		
		},
		initialize:function(){
			var parent = this
			$.ajax({
				type:"GET",
				url:"/admin/groups/all",
				dataType:"json",
				success: function(data){
					if(data.groupDTO.length > 1){
			    		$.each(data.groupDTO, function() {
			    			var view = new GroupAdminTableLineView(this.id, this.name);
			    			view.render();
			    		});
					}
			    	// In the case when there is only one element
		    		else{
						var view = new GroupAdminTableLineView(data.groupDTO.id, data.groupDTO.name);
		    			view.render();
					}
				}
			});
		},
		render: function(){
			return this;
		}
	})	
	
	
	GroupAdminButtonsView = Backbone.View.extend({
		el:"#group_admin_container",
		
		events: {
			"click .createButton" : "creategroup",
			"click .editButton" : "editgroup",
			"click .lockButton" : "deletegroup"
		},
		
		viewCreate:null,
		viewUpdate:null,
		
		initialize: function(){
			this.viewCreateUpdate =  new GroupAdminCreateUpdateView("", 0);
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
		
		creategroup: function(){
			this.showModalWindow();
			// We set 0 for the id to create
			this.viewCreateUpdate.setFields("", 0);
			this.viewCreateUpdate.render();
		},
		
		editgroup: function(){
			this.showModalWindow();
			this.viewCreateUpdate.setFields(lineSelected.vname, lineSelected.vid);
			this.viewCreateUpdate.render();
		},
		
		deletegroup: function(){
			var answer = confirm(lineSelected.vname + " will be deleted. Do you want to continue ?");
			if (answer){
				$.ajax({
					url:"/admin/groups/delete/" + lineSelected.vid,
					success: function(){
						$("#groups_notifications").text("Operation completed successfully !");
						$("#groups_notifications").fadeIn(1000);
						$("#groups_notifications").fadeOut(3000);
					}
				});
			}
		},
		
		render:function(){
			return this;
		}
	})
	
	GroupAdminCreateUpdateView = Backbone.View.extend({
		el: "#modal_window",
		
		vid: null,
		vname: null,
		
		events:{
			"click .closeModal" : "closemodal",
			"click .sendGroup" : "registergroup"
		},
		
		initialize:function(name, id){
			this.vid = id;
			this.vname = name;
		},
		
		setFields: function(name, id){
			this.vid = id;
			this.vname = name;
		},
		
		render : function(){
			var template = $("#popup-groupe-admin-template").html();
			
			var view = {name : this.vname};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		closemodal: function(){
			$('#modal_window').hide();
       		$('#mask').hide();
		},
		
		registergroup: function(){
			var json = '{"id":"'+this.vid+'", "name":"'+$('input[name*="name"]').val() + '"}';
			$.ajax({
				url:"/admin/groups/create",
				data: json,
				type: "POST",
				//dataType:"json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					$('#modal_window').hide();
       				$('#mask').hide();
					$("#groups_notifications").text("Operation completed successfully !");
					$("#groups_notifications").fadeIn(1000);
					$("#groups_notifications").fadeOut(3000);
				}
			});
		}
	}) 
	
	// define the application initialization
	var self = {};
	self.start = function(){
		tableView = new GroupAdminTableView().render();
		new GroupAdminButtonsView().render();
	}
	return self;
})

$( function() {
	console.log("Starting group administration application")
	new AppGroupAdmin(jQuery).start();
})