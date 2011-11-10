AppProfile= (function($){
	ProfileView = Backbone.View.extend({
		el:'#profile_information',
		
		events:{
			"click .edit_button_mail" : "editMail",
			"click .valid_button_mail" : "saveMail"
		},
		render:function(){},
		editMail: function(){
			$('#button_mail').html("<img class='button valid_button_mail' src='/images/icons/save.png' style='margin-left : 15px;'/>");
			$('#profile_mail').html("<input id='edit_mail_field' name='mail' type='text' value='" + $('#profile_mail').text() + "'/>");
		
		},
		saveMail: function(){
			$.ajax({
				// Put here request to update mail.
			});
			$('#button_mail').html("<img class='button edit_button_mail' src='/images/icons/edit.png' style='margin-left : 15px;'/>");
			$('#profile_mail').html("<a href='mailto:'"+ $('#edit_mail_field').text() +"'>"+ $('#edit_mail_field').text() +"</a>");
		}
		
	})

	// define the application initialization
	var self = {};
	self.start = function(){
		new ProfileView().render()
	}
	return self;
})

$( function() {
	console.log("Starting profile application")
	new AppProfile(jQuery).start();
})