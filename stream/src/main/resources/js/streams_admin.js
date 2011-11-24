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
		el:"stream_admin_table",
		events:{
		
		},
		initialize:function(){
			var parent = this
			$.ajax({
				type:"GET",
				url:"/streams/admin/all",
				dataType:"json",
				success: function(data){
					if(data.streamDTO.length > 1){
			    		$.each(data.streamDTO, function() {
			    			var view = new StreamAdminTableLineView(this.id, this.title,this.category,this.locked);
			    			view.render();
			    		});
					}
			    	// In the case when there is only one element
		    		else{
						var view = new StreamAdminTableLineView(data.streamDTO.id, data.streamDTO.name,data.streamDTO.category, data.streamDTO.locked);
		    			view.render();
					}
				}
			});
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
			"click .unlockButton" : "unlockstream"
		},
		
		viewCreate:null,
		viewUpdate:null,
		
		initialize: function(){
			this.viewCreateUpdate =  new StreamAdminCreateUpdateView("", 0,"");
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
					url:"/streams/admin/lock/" + lineSelected.vid,
					success: function(){
						$("#streams_notifications").text("Operation completed successfully!");
						$("#streams_notifications").fadeIn(1000);
						$("#streams_notifications").fadeOut(3000);
					}
				});
			}
		},
		
		unlockstream: function(){
			var answer = confirm(lineSelected.vname + " will be unlocked. Do you want to continue?");
			if (answer){
				$.ajax({
					url:"/streams/admin/unlock/" + lineSelected.vid,
					success: function(){
						$("#streams_notifications").text("Operation completed successfully!");
						$("#streams_notifications").fadeIn(1000);
						$("#streams_notifications").fadeOut(3000);
					}
				});
			}
		},
		
		render:function(){
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
				url:"/streams/admin/create",
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