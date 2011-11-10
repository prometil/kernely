AppProfile = (function($){
	// the mini profile view at the top-right on each page
	MenuProfileView = Backbone.View.extend({
		el:"#header_profile",
		
		vusername: null,
		vimage: null,
		vmail: null,
		
		events: {
			"click .displayProfilePU" : "showMiniProfile",
			"mouseover .displayProfilePU" : "showOver",
			"mouseout .displayProfilePU" : "hideOver",
		},
		initialize: function(){
			$.ajax({
				url:"/user/current",
				success:function(data){
					$.ajax({
						url:"/user/" + data.username + "/profile",
						success:function(data){
							vusername = data.firstname + " " + data.lastname;
							vimage = data.image;
							vmail = data.email;
						
							$("#username_menu").text(vusername);
							$("#userimg_menu").html("<img class='img_miniprofile' style='width:28px;height:28px;' src='"+ vimage +"'/>");
						}
					});
				}
			});
			
		},
		showOver: function(){
			
		},
		hideOver: function(){
		
		},
		showMiniProfile: function(){
			if ($("#profile_popup").is(':hidden')){
				$("#profile_popup").slideDown(200)
				
				var view = new ProfilePopUpView(vusername, vimage, vmail);
				view.render();
			}
			else{
				$("#profile_popup").slideUp(200)
			}
		},
		render: function(){
			return this;
		}
	})
	
	
	ProfilePopUpView = Backbone.View.extend({
		el: "#profile_popup",
		
		vusername: null,
		vimage: null,
		vmail: null,
		
		initialize:function(username, image, mail){
			this.vusername = username;
			this.vimage = image;
			this.vmail = mail;
		},
		render: function(){
			var template = $("#profile-template").html();
			var view = {username : this.vusername, mail: this.vmail, image: this.vimage};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;			
		}
		
	})

	// define the application initialization
	var self = {};
	self.start = function(){
		new MenuProfileView().render()
	}
	return self;
})

$( function() {
	console.log("Starting profile menu application")
	new AppProfile(jQuery).start();
})