AppUserAdmin = (function($){
	var lineSelected = null;
	var tableView = null;
	
	
	UserAdminTableLineView = Backbone.View.extend({
		tagName: "tr",
		className: 'user_list_line',
		
		vid: null,
		vlogin: null,
		vfirstname: null,
		vname: null,
		vmail:null,
		
		events: {
			"click" : "selectLine",
			"mouseover" : "overLine",
			"mouseout" : "outLine"
		},
		
		initialize: function(id, name, firstname, login, mail){
			this.vid = id;
			this.vlogin = login;
			this.vfirstname = firstname;
			this.vname = name;
			this.vmail = mail;
		},
		selectLine : function(){
			$(".editButton").removeAttr('disabled');
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
			var template = '<td><img src="/images/icons/user.png"/></td><td>{{name}}</td><td>{{firstname}}</td><td>{{username}}</td><td>{{mail}}</td>';
			var view = {id : this.vid, name: this.vname, firstname: this.vfirstname, username: this.vlogin, mail: this.vmail};
			var html = Mustache.to_html(template, view);
			
			$(this.el).html(html);
			$(this.el).appendTo($("#user_admin_table"));
			return this;
		}
		
	})

	UserAdminTableView = Backbone.View.extend({
		el:"user_admin_table",
		events:{
		
		},
		initialize:function(){
			var parent = this
			$.ajax({
				type:"GET",
				url:"/admin/users/all",
				dataType:"json",
				success: function(data){
		    		$.each(data.userDetailsDTO, function() {
		    			var view = new UserAdminTableLineView(this.id, this.name, this.firstname, this.user.username, this.mail);
		    			view.render();
		    		});
				}
			});
		},
		render: function(){
			return this;
		}
	})	
	
	
	UserAdminButtonsView = Backbone.View.extend({
		el:"#user_admin_container",
		
		events: {
			"click .createButton" : "createuser",
			"click .editButton" : "edituser",
			"click .lockButton" : "lockuser"
		},
		
		viewCreate:null,
		viewUpdate:null,
		
		initialize: function(){
			this.viewCreateUpdate =  new UserAdminCreateUpdateView("","","", 0);
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
		
		createuser: function(){
			this.showModalWindow();
			// We set 0 for the id to create
			this.viewCreateUpdate.setFields("","","",0);
			this.viewCreateUpdate.render();
		},
		
		edituser: function(){
			this.showModalWindow();
			console.log(lineSelected.vlogin);
			this.viewCreateUpdate.setFields(lineSelected.vlogin,lineSelected.vfirstname,lineSelected.vname,lineSelected.vid);
			this.viewCreateUpdate.render();
		},
		
		lockuser: function(){
		
		},
		
		render:function(){
			return this;
		}
	})
	
	UserAdminCreateUpdateView = Backbone.View.extend({
		el: "#modal_window",
		
		vid: null,
		vlogin: null,
		vfirstname: null,
		vname: null,
		
		events:{
			"click .closeModal" : "closemodal",
			"click .sendUser" : "registeruser"
		},
		
		initialize:function(login, firstname, name, id){
			this.vid = id;
			this.vlogin = login;
			this.vfirstname = firstname;
			this.vname = name;
		},
		
		setFields: function(login, firstname, name, id){
			this.vid = id;
			this.vlogin = login;
			this.vfirstname = firstname;
			this.vname = name;
		},
		
		render : function(){
			var template = $("#popup-user-admin-template").html();
			
			var view = {login : this.vlogin, firstname: this.vfirstname, name: this.vname};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		closemodal: function(){
			$('#modal_window').hide();
       		$('#mask').hide();
		},
		
		registeruser: function(){
			var json = '{"id":"'+this.vid+'", "firstname":"'+$('input[name*="firstname"]').val()+'","name":"'+$('input[name*="name"]').val()+'", "username":"'+$('input[name*="login"]').val()+'", "password":"'+$('input[name*="password"]').val()+'"}';
			$.ajax({
				url:"/admin/users/create",
				data: json,
				type: "POST",
				//dataType:"json",
				processData: false,
				contentType: "application/json; charset=utf-8",
				success: function(data){
					$('#modal_window').hide();
       				$('#mask').hide();
					$("#users_notifications").text("Operation completed successfully !");
					$("#users_notifications").fadeIn(1000);
					$("#users_notifications").fadeOut(3000);
				}
			});
		}
	}) 
	
	// define the application initialization
	var self = {};
	self.start = function(){
		tableView = new UserAdminTableView().render();
		new UserAdminButtonsView().render();
	}
	return self;
})

$( function() {
	console.log("Starting user administration application")
	new AppUserAdmin(jQuery).start();
})