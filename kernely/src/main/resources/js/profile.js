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
		userd.email= $('#profile_mail').text();
		userd.lastname=  $('#profile_lastname').text();
		userd.firstname = $('#profile_firstname').text();
		userd.adress=  $('#profile_adress').text();
		userd.zip=  $('#profile_zip').text();
		userd.city=  $('#profile_city').text();
		userd.homephone=  $('#profile_homephone').text();
		userd.mobilephone=  $('#profile_mobilephone').text();
		userd.businessphone=  $('#profile_businessphone').text();
		userd.birth=$('#profile_birth').text();
		userd.nationality=  $('#profile_nationality').text();
		userd.ssn=  $('#profile_ssn').text();
		userd.civility = $('input[name=civility]:checked').val();
		userd.image = $('#image_name').attr('name');
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
		
		userd:null,
		flag:null,
		vinput:null,
		vlength:null,
		vuser:null,
		
		initialize:function(input, length, user){
			userd = new userdetails();
			this.vinput=input;
			this.vlength=length;
			this.vuser=user;
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
			var template = $("#profile-"+this.vinput+"-template").html();
			var view = {input : this.vinput, data : this.vuser};
			var html = Mustache.to_html(template, view);
			$(this.el).html(html);
			return this;
		},
		
		save:function(){	
			retrieveUserDetails();
			if (this.vinput=="mail"){
				userd.email=$("#edit_"+this.vinput+"_field").val();
			}
			if (this.vinput=="lastname"){
				userd.lastname=$("#edit_"+this.vinput+"_field").val();
			}
			if (this.vinput=="firstname"){
				userd.firstname=$("#edit_"+this.vinput+"_field").val();
			}
			if (this.vinput=="city"){
				userd.city=$("#edit_"+this.vinput+"_field").val();
			}
			if (this.vinput=="zip"){
				userd.zip=$("#edit_"+this.vinput+"_field").val();
			}
			if (this.vinput=="adress"){
				userd.adress=$("#edit_"+this.vinput+"_field").val();
			}
			if (this.vinput=="homephone"){
				userd.homephone=$("#edit_"+this.vinput+"_field").val();
			}
			if (this.vinput=="mobilephone"){
				userd.mobilephone=$("#edit_"+this.vinput+"_field").val();
			}
			if (this.vinput=="businessphone"){
				userd.businessphone=$("#edit_"+this.vinput+"_field").val();
			}
			if (this.vinput=="nationality"){
				userd.nationality=$("#edit_"+this.vinput+"_field").val();
			}
			if(this.vinput=="ssn"){
				userd.ssn=$("#edit_"+this.vinput+"_field").val();		
			}
			if(this.vinput=="birth"){
				userd.birth=$("#edit_"+this.vinput+"_field").val();
			}
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
							var vresult; 
							if (parent.vinput=="mail"){
								vresult = data.email;
							}
							if (parent.vinput=="lastname"){
								vresult = data.lastname;						
							}
							if (parent.vinput=="firstname"){
								vresult = data.firstname;
							}
							if (parent.vinput=="city"){
								vresult = data.city;
							}
							if (parent.vinput=="zip"){
								vresult = data.zip;
							}
							if (parent.vinput=="adress"){
								vresult = data.adress;
							}
							if (parent.vinput=="homephone"){
								vresult = data.homephone;
							}
							if (parent.vinput=="mobilephone"){
								vresult = data.mobilephone;
							}
							if (parent.vinput=="businessphone"){
								vresult = data.businessphone;
							}
							if (parent.vinput=="nationality"){
								vresult = data.nationality ;
							}
							if(parent.vinput=="ssn"){
								vresult = data.ssn;
							}
							if(parent.vinput=="birth"){
								vresult = data.birth;
							}
							$("#profile_"+parent.vinput).html( vresult);
						}
					});
				}
			});			
			$("#profile_"+this.vinput).html($("#edit_"+this.vinput+"_field").text());
			$("#profile_"+this.vinput).addClass("span_profile");
						
		},
		
		edit:function(){
			$("#profile_"+this.vinput).removeClass("span_profile");
			$("#profile_"+this.vinput).html("<input id='edit_"+this.vinput+"_field' name='"+this.vinput+"' class='profile_"+this.vinput+"' type='text' MAXLENGTH="+this.vlength+" value='" + $("#profile_" + this.vinput).text() + "'/>");
			document.getElementById("edit_"+this.vinput+"_field").focus();
			flag.edit=this.vinput;
		},
		
		filterOnEnter:function(e){
			 if (e.keyCode == 13){
				 this.save();
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
							parent.addInput("lastname","50",details.lastname);
							parent.addInput("firstname","50",details.firstname);	
							parent.addInput("mail","50",details.email);
							parent.addInput("adress","100",details.adress); 
							parent.addInput("zip", "5",details.zip);
							parent.addInput("city","30",details.city);
							parent.addInput("nationality","30",details.nationality);
							parent.addInput("homephone","10",details.homephone);
							parent.addInput("mobilephone","10",details.mobilephone);
							parent.addInput("businessphone","10",details.businessphone);
							parent.addInput("birth","10",details.birth);
							parent.addInput("ssn","20",details.ssn);							
						}
					});
				}
			});
		},
	
		addInput:function(title, length, userdata){
			var inputView = new InputView(title, length, userdata);
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
	}
	return self;
})




$( function() {
	console.log("Starting profile application")
	new AppProfile(jQuery).start();
})