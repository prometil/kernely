AppMiniProfile = (function($){
	// the mini profile view at the top-right on each page
	MenuProfileView = Backbone.View.extend({
		el:"#menu_header_profile",
		
		vfullname: null,
		vimage: null,
		vmail: null,
		vuser: null,
		
		events: {
			"click .displayProfilePU" : "showMiniProfile",
			"mouseover .displayProfilePU" : "showOver",
			"mouseout .displayProfilePU" : "hideOver",
		},
		initialize: function(){
			$.ajax({
				url:"/user/current",
				success:function(data){
					vuser = data.username;
					$.ajax({
						url:"/user/" + data.username,
						success:function(data){
							vfullname = data.firstname + " " + data.lastname;
							vimage = data.image;
							vmail = data.email;
							
							if(vimage == null){
								vimage = "/images/default_user.png"
							}
						
							$("#username_menu").text(vfullname + " (" + vuser + ")");
							$("#userimg_menu").html("<img class='img_miniprofile' style='width:28px;height:28px;' src='"+ vimage +"'/>");
						}
					});
				}
			});
			
		},
		showOver: function(){
			// Put some style here.
		},
		hideOver: function(){
			// Put some style here.
		},
		showMiniProfile: function(){
			if ($("#profile_popup").is(':hidden')){
				//$("#profile_popup").slideDown(200)
				$("#profile_popup").show()
				
				var view = new ProfilePopUpView(vfullname, vuser, vimage, vmail);
				view.render();
				
			}
			else{
				//$("#profile_popup").slideUp(200)
				$("#profile_popup").hide()
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
		vfullname: null,
		
		initialize:function(fullname, username, image, mail){
			this.vfullname = fullname;
			this.vusername = username;
			this.vimage = image;
			this.vmail = mail;
		},
		render: function(){
			var template = $("#profile-template").html();
			var view = {fullname : this.vfullname, mail: this.vmail, image: this.vimage, username: this.vusername};
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
	new AppMiniProfile(jQuery).start();
})