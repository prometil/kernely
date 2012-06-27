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
AppProfile= (function($){
	
	var userd = new Object();
	
	function userdetails(){
		this.email= null,
		this.lastname=  null,
		this.firstname = null,
		this.image=null,
		this.adress=  null,
		this.zip=  null,
		this.city=  null,
		this.homephone=  null,
		this.mobilephone=  null,
		this.businessphone=  null,
		this.birth=  null,
		this.nationality=  null,
		this.ssn=  null,
		this.civility=null
	}
	
	function flag(){
		edit=null
	}
	
	function retrieveUserDetails(){
		userd.email= $('#profile_email_div').text();
		userd.lastname=  $('#profile_lastname_div').text();
		userd.firstname = $('#profile_firstname_div').text();
		userd.adress=  $('#profile_adress_div').text();
		userd.zip=  $('#profile_zip_div').text();
		userd.city=  $('#profile_city_div').text();
		userd.homephone=  $('#profile_homephone_div').text();
		userd.mobilephone=  $('#profile_mobilephone_div').text();
		userd.businessphone=  $('#profile_businessphone_div').text();
		userd.birth=$('#profile_birth_div').text();
		userd.nationality=  $('#profile_nationality_div').text();
		userd.ssn=  $('#profile_ssn_div').text();
		userd.civility = $('input[name=civility]:checked').val();
		userd.image = $('#image_name_div').attr('name');
	}
	
	InputView = Backbone.View.extend({	 
		events:{
			"click .span_profile" : "edit",
			"keypress input[type=text]": "filterOnEnter",
			"blur input[type=text]" : "onBlur",
			"mouseover .span_profile" : "overLine",
			"mouseout .span_profile" : "outLine"
		},
		
		tagName: 'tr', 
		
		flag:null,
		vinput:null,
		vlength:null,
		vuser:null,
		validation:null,
		
		initialize:function(input, length, user, validation){
			//userd = new userdetails();
			this.vinput=input;
			this.vlength=length;
			this.vuser=user;
			this.validation = validation;
		},

		overLine : function(){
			$("#profile_"+this.vinput).css("background-color", "#e3f38d");
			$("#profile_"+this.vinput).css("cursor", "pointer");
		},
		outLine : function(){
			$("#profile_"+this.vinput).css("background-color", "transparent");
			$("#profile_"+this.vinput).css("cursor", "auto");
		},
		
		render:function(){
			var i18nName = $("#profile-"+this.vinput+"-template").html();
			var html = '<td>'+i18nName+'</td><td id="profile_'+this.vinput+'" class="span_profile"><div id="profile_'+this.vinput+'_div">'+this.vuser+'</div>'+
						'<form id="'+this.vinput+'_form"><input id="edit_'+this.vinput+'_field" name="'+this.vinput+'" class="invisible profile_'+this.vinput+' '+this.validation+'" type="text" MAXLENGTH='+this.vlength+' value="' + this.vuser + '"/></form>'+
						'<span class="text-bold-red" id="'+this.vinput+'_error"></span></td>';
			$(this.el).html(html);
			return this;
		},
		
		save:function(){
			var parent = this;
			
			$("#"+this.vinput+"_form").validate({
				// To hide jquery validation plugin messages
			    showErrors: function() {}
			 })

			if ($("#"+this.vinput+"_form").valid()){
				retrieveUserDetails();
				userd[this.vinput] = $("#edit_"+this.vinput+"_field").val();
				var parent=this;
				var json = JSON.stringify(userd);
				$.ajax({
					url:"/user/current",
					success:function(data){
						$.ajax({				
							url:"/user/" + data.username +"/profile/update",
	                        data: json,
	                        type: "POST",
	                        dataType:"json",
	                        processData: false,
	                        contentType: "application/json; charset=utf-8",
							success:function(data){
								vresult = data[parent.vinput];
								$("#profile_"+parent.vinput+"_div").html( vresult);
								$("#profile_"+parent.vinput+"_div").removeClass("invisible");
								$("#edit_"+parent.vinput+"_field").addClass("invisible");
								$("#"+parent.vinput+"_error").addClass("invisible");
							}
						});
					}
				});
			} else {
				var message = $("#profile-"+this.validation+"-message-template").html();
				$("#"+this.vinput+"_error").html(message);
				$("#"+this.vinput+"_error").removeClass("invisible");
				$("#edit_"+this.vinput+"_field").val($("#profile_"+this.vinput+"_div").html());
				$("#profile_"+this.vinput+"_div").removeClass("invisible");
				$("#edit_"+this.vinput+"_field").addClass("invisible");
			}
		},
		
		edit:function(){
			var parent = this;
			$("#profile_"+this.vinput+"_div").addClass("invisible");
			$("#edit_"+this.vinput+"_field").removeClass("invisible");
			$.datepicker.regional[lang+"-"+country];
			
			if (this.vinput == "birth"){
				$( "#edit_birth_field" ).attr("readonly", "readonly");
				// Set the datepicker for date of birth
				$( "#edit_birth_field" ).datepicker({
					changeMonth:true,
					changeYear:true,
					buttonImage: "/images/icons/calendar_icon.png",
					buttonImageOnly: true,
					onSelect: function(selectedDate,inst){
						$("#profile_birth_div").html(selectedDate);
						$("#edit_birth_field").val(selectedDate);
						parent.save();
					}
				});
			}
			$.datepicker.setDefaults($.datepicker.regional[lang+"-"+country]);
			document.getElementById("edit_"+this.vinput+"_field").focus();
			
			flag.edit=this.vinput;
		},
		
		filterOnEnter:function(e){
			 if (e.keyCode == 13){
				 this.save();
				 return false;
			 }
	},
		
		onBlur : function(){
			this.save();	
		}
		
	})
	
	ProfileView = Backbone.View.extend({
		el:'#profile_information',
		
		events:{
			"click .edit_button_civility" : "saveCivility", 
		},
		
		initialize:function(){
			$("#form-image-profile").submit(function(){
				var ext = $('#image-profile-field').val().split('.').pop().toLowerCase();
				if($.inArray(ext, ['gif','png','jpg','jpeg']) == -1) {
				    $.writeMessage("error", $("#invalid-extension-error-template").text());
				    return false;
				}
				return true;
			});
			
		},
		
		render:function(){
			this.createUser();
			return this;
		},
		
		createUser:function(){
			this.user = new userdetails();
			var parent=this;
    		$("input[name=civility]").filter("[value="+$('#profile_civility').text()+"]").attr("checked","checked");
			$.ajax({
				url:"/user/current",
				dataType:"json",
				success:function(data){
					$.ajax({
						url:"/user/"+data.username,
						dataType:"json",
						success: function(details){
							parent.addInput("lastname","50",details.lastname,"required");
							parent.addInput("firstname","50",details.firstname,"required");	
							parent.addInput("email","50",details.email,"email");
							parent.addInput("adress","100",details.adress); 
							parent.addInput("zip", "20",details.zip);
							parent.addInput("city","60",details.city);
							parent.addInput("nationality","30",details.nationality);
							parent.addInput("homephone","20",details.homephone);
							parent.addInput("mobilephone","20",details.mobilephone);
							parent.addInput("businessphone","20",details.businessphone);
							parent.addInput("birth","10",details.birth);
							parent.addInput("ssn","20",details.ssn);
						}
					});
				}
			});
		},
	
		addInput:function(title, length, userdata, validation){
			var inputView = new InputView(title, length, userdata, validation);
			$("#profile_table > tbody:last").append(inputView.render().el);
		},
	

		saveCivility : function(){
			retrieveUserDetails();
			var json = JSON.stringify(userd);
			$.ajax({
				url:"/user/current",
				success:function(data){
					vuser = data.username;
					$.ajax({				
						url:"/user/" + data.username +"/profile/update",
                        data: json,
                        type: "POST",
                        dataType:"json",
                        processData: false,
                        contentType: "application/json; charset=utf-8",
						success:function(data){
				
					}
				});
			}
		});	
	}
})

	
	
	// define the application initialization
	var self = {};
	self.start = function(){
		new ProfileView().render();
		retrieveUserDetails();
	}
	return self;
})




$( function() {
	console.log("Starting profile application")
	new AppProfile(jQuery).start();
})