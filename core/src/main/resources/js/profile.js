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
	
	ProfileView = Backbone.View.extend({
		el:'#profile_information',
		
		events:{
			"click .edit_button_civility" : "saveCivility", 
			"click .edit_button_mail" : "editMail",
			"click .valid_button_mail" : "saveMail",
			"click .edit_button_name" : "editName",
			"click .valid_button_name" : "saveName",
			"click .edit_button_firstname" : "editFirstname",
			"click .valid_button_firstname" : "saveFirstname",
			"click .edit_button_adress" : "editAdress",
			"click .valid_button_adress" : "saveAdress",
			"click .edit_button_zip" : "editZip",
			"click .valid_button_zip" : "saveZip",
			"click .edit_button_city" : "editCity",
			"click .valid_button_city" : "saveCity",
			"click .edit_button_homephone" : "editHomephone",
			"click .valid_button_homephone" : "saveHomephone",
			"click .edit_button_mobilephone" : "editMobilephone",
			"click .valid_button_mobilephone" : "saveMobilephone",
			"click .edit_button_businessphone" : "editBusinessphone",
			"click .valid_button_businessphone" : "saveBusinessphone",
			"click .edit_button_birth" : "editBirth",
			"click .valid_button_birth" : "saveBirth",
			"click .edit_button_nationality" : "editNationality",
			"click .valid_button_nationality" : "saveNationality",
			"click .edit_button_ssn" : "editSsn",
			"click .valid_button_ssn" : "saveSsn",	
			"keypress input[type=text]": "filterOnEnter",
			 "blur input[type=text]" : "onBlur"
			
		},
		userd:null,
		flag:null,
		
		initialize:function(){
			$("input[name=civility]").filter("[value="+$('#profile_civility').text()+"]").attr("checked","checked");
			userd = new userdetails();
			flag=new flag();
			userd.email= $('#profile_mail').text();
			userd.lastname=  $('#profile_name').text();
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
			
		},

		
		render:function(){},
		
		saveCivility : function(){
			userd.civility=$('input[name=civility]:checked').val();
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
			
		},
		editMail: function(){
			$('#button_mail').html("<img class='button valid_button_mail' src='/images/icons/save.png' style='margin-left : 15px;'/>");
			$('#profile_mail').html("<input id='edit_mail_field' name='mail' type='text' MAXLENGTH=50 value='" + $('#profile_mail').text() + "'/>");
			document.getElementById('edit_mail_field').focus();
			flag.edit="mail";
			
		
		},
		saveMail: function(){
			userd.email=$("#edit_mail_field").val();
			var json = JSON.stringify(userd);
			var save = $("#edit_mail_field").val();
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
							vmail = data.email;
							$('#profile_mail').html("<a href='mailto:'"+ $('#edit_mail_field').text() +"'>"+ vmail +"</a>");
						}
					});
				}
			});
			$('#button_mail').html("<img class='button edit_button_mail' src='/images/icons/edit.png' style='margin-left : 15px;'/>");
			$('#profile_mail').html("<a href='mailto:'"+ $('#edit_mail_field').text() +"'>"+ $('#edit_mail_field').text() +"</a>");
		},
		
		editName: function(){
			$('#button_name').html("<img class='button valid_button_name' src='/images/icons/save.png' style='margin-left : 15px;'/>");
			$('#profile_name').html("<input id='edit_name_field' name='name' type='text' MAXLENGTH=50 value='" + $('#profile_name').text() + "'/>");
			document.getElementById('edit_name_field').focus();
			flag.edit="name";
		},
		saveName: function(){
			userd.lastname=$("#edit_name_field").val();
			var json = JSON.stringify(userd);
			var save = $("#edit_name_field").val();
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
							vname = data.lastname;
							$('#profile_name').html(""+vname);
						}
					});
				}
			});
			$('#button_name').html("<img class='button edit_button_name' src='/images/icons/edit.png' style='margin-left : 15px;'/>");
			$('#profile_name').html($('#edit_name_field').text());
		},
		
		editFirstname: function(){
			$('#button_firstname').html("<img class='button valid_button_firstname' src='/images/icons/save.png' style='margin-left : 15px;'/>");
			$('#profile_firstname').html("<input id='edit_firstname_field' name='firstname' type='text' MAXLENGTH=50 value='" + $('#profile_firstname').text() + "'/>");
			document.getElementById('edit_firstname_field').focus();
			flag.edit="firstname";
		},
		saveFirstname: function(){
			userd.firstname=$("#edit_firstname_field").val();
			var json = JSON.stringify(userd);
			var save = $("#edit_firstname_field").val();
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
							vfirstname = data.firstname;
							$('#profile_firstname').html(""+ vfirstname);
						}
					});
					userdetails	}
			});
			$('#button_firstname').html("<img class='button edit_button_firstname' src='/images/icons/edit.png' style='margin-left : 15px;'/>");
			$('#profile_firstname').html( $('#edit_firstname_field').text());
		},
		
		editAdress: function(){
			$('#button_adress').html("<img class='button valid_button_adress' src='/images/icons/save.png' style='margin-left : 15px;'/>");
			$('#profile_adress').html("<input id='edit_adress_field' name='adress' type='text' MAXLENGTH=100 value='" + $('#profile_adress').text() + "'/>");
			document.getElementById('edit_adress_field').focus();
			flag.edit="adress";
		},
		saveAdress: function(){
			userd.adress=$("#edit_adress_field").val();
			var json = JSON.stringify(userd);
			var save = $("#edit_adress_field").val();
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
							vadress = data.adress;
							$('#profile_adress').html(""+ vadress);
						}
					});
				}
			});
			$('#button_adress').html("<img class='button edit_button_adress' src='/images/icons/edit.png' style='margin-left : 15px;'/>");
			$('#profile_adress').html( $('#edit_adress_field').text());
		},
		
		editZip: function(){
			$('#button_zip').html("<img class='button valid_button_zip' src='/images/icons/save.png' style='margin-left : 15px;'/>");
			$('#profile_zip').html("<input id='edit_zip_field' name='zip' type='text' MAXLENGTH=5 value='" + $('#profile_zip').text() + "'/>");
			document.getElementById('edit_zip_field').focus();
			flag.edit="zip";
		},
		saveZip: function(){
			userd.zip=$("#edit_zip_field").val();
			var json = JSON.stringify(userd);
			var save = $("#edit_zip_field").val();
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
							vzip = data.zip;
							$('#profile_zip').html(""+ vzip);
						}
					});
				}
			});
			$('#button_zip').html("<img class='button edit_button_zip' src='/images/icons/edit.png' style='margin-left : 15px;'/>");
			$('#profile_zip').html($('#edit_zip_field').text());
		},
		
		editCity: function(){
			$('#button_city').html("<img class='button valid_button_city' src='/images/icons/save.png' style='margin-left : 15px;'/>");
			$('#profile_city').html("<input id='edit_city_field' name='city' type='text' MAXLENGTH=30 value='" + $('#profile_city').text() + "'/>");
			document.getElementById('edit_city_field').focus();
			flag.edit="city";
		},
		saveCity: function(){
			userd.city=$("#edit_city_field").val();
			var json = JSON.stringify(userd);
			var save = $("#edit_city_field").val();
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
							vcity = data.city;
							$('#profile_city').html(""+ vcity);
						}
					});
				}
			});
			$('#button_city').html("<img class='button edit_button_city' src='/images/icons/edit.png' style='margin-left : 15px;'/>");
			$('#profile_city').html($('#edit_city_field').text());
		},
		
		editHomephone: function(){
			$('#button_homephone').html("<img class='button valid_button_homephone' src='/images/icons/save.png' style='margin-left : 15px;'/>");
			$('#profile_homephone').html("<input id='edit_homephone_field' name='homephone' type='text' MAXLENGTH=10 value='" + $('#profile_homephone').text() + "'/>");
			document.getElementById('edit_homephone_field').focus();
			flag.edit="homephone";
		},
		saveHomephone: function(){
			userd.homephone=$("#edit_homephone_field").val();
			var json = JSON.stringify(userd);
			var save = $("#edit_homephone_field").val();
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
							vhomephone = data.homephone;
							$('#profile_homephone').html(vhomephone);
						}
					});
				}
			});
			$('#button_homephone').html("<img class='button edit_button_homephone' src='/images/icons/edit.png' style='margin-left : 15px;'/>");
			$('#profile_homephone').html( $('#edit_homephone_field').text());
		},
		
		editMobilephone: function(){
			$('#button_mobilephone').html("<img class='button valid_button_mobilephone' src='/images/icons/save.png' style='margin-left : 15px;'/>");
			$('#profile_mobilephone').html("<input id='edit_mobilephone_field' name='mobilephone' type='text' MAXLENGTH=10 value='" + $('#profile_mobilephone').text() + "'/>");
			document.getElementById('edit_mobilephone_field').focus();
			flag.edit="mobilephone";
		},
		saveMobilephone: function(){
			userd.mobilephone=$("#edit_mobilephone_field").val();
			var json = JSON.stringify(userd);
			var save = $("#edit_mobilephone_field").val();
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
							vmobilephone = data.mobilephone;
							$('#profile_mobilephone').html(""+vmobilephone);
						}
					});
				}
			});
			$('#button_mobilephone').html("<img class='button edit_button_mobilephone' src='/images/icons/edit.png' style='margin-left : 15px;'/>");
			$('#profile_mobilephone').html($('#edit_mobilephone_field').text() );
		},
		
		editBusinessphone: function(){
			$('#button_businessphone').html("<img class='button valid_button_businessphone' src='/images/icons/save.png' style='margin-left : 15px;'/>");
			$('#profile_businessphone').html("<input id='edit_businessphone_field' name='businessphone' type='text' MAXLENGTH=10 value='" + $('#profile_businessphone').text() + "'/>");
			document.getElementById('edit_businessphone_field').focus();
			flag.edit="businessphone";
		},
		saveBusinessphone: function(){
			userd.businessphone=$("#edit_businessphone_field").val();
			var json = JSON.stringify(userd);
			var save = $("#edit_businessphone_field").val();
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
							vbusinessphone = data.businessphone;
							$('#profile_businessphone').html(""+ vbusinessphone);
						}
					});
				}
			});
			$('#button_businessphone').html("<img class='button edit_button_businessphone' src='/images/icons/edit.png' style='margin-left : 15px;'/>");
			$('#profile_businessphone').html($('#edit_businessphone_field').text());
		},
		
		editBirth: function(){
			$('#button_birth').html("<img class='button valid_button_birth' src='/images/icons/save.png' style='margin-left : 15px;'/>");
			$('#profile_birth').html("<input id='edit_birth_field' name='birth' type='text' MAXLENGTH=10 value='" + $('#profile_birth').text() + "'/>");
			document.getElementById('edit_birth_field').focus();
			flag.edit="birth";
		},
		saveBirth: function(){
			userd.birth=$("#edit_birth_field").val();
			var json = JSON.stringify(userd);
			var save = $("#edit_birth_field").val();
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
							vbirth = data.birth;
							$('#profile_birth').html(""+vbirth );
						}
					});
				}
			});
			$('#button_birth').html("<img class='button edit_button_birth' src='/images/icons/edit.png' style='margin-left : 15px;'/>");
			$('#profile_birth').html( $('#edit_birth_field').text());
		},
		
		editNationality: function(){
			$('#button_nationality').html("<img class='button valid_button_nationality' src='/images/icons/save.png' style='margin-left : 15px;'/>");
			$('#profile_nationality').html("<input id='edit_nationality_field' name='nationality' type='text' MAXLENGTH=30 value='" + $('#profile_nationality').text() + "'/>");
			document.getElementById('edit_nationality_field').focus();
			flag.edit="nationality";
		},
		saveNationality: function(){
			userd.nationality=$("#edit_nationality_field").val();
			var json = JSON.stringify(userd);
			var save = $("#edit_nationality_field").val();
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
							vnationality = data.nationality;
							$('#profile_nationality').html(""+ vnationality);
						}
					});
				}
			});
			$('#button_nationality').html("<img class='button edit_button_nationality' src='/images/icons/edit.png' style='margin-left : 15px;'/>");
			$('#profile_nationality').html($('#edit_nationality_field').text() );
		},
		
		editSsn: function(){
			$('#button_ssn').html("<img class='button valid_button_ssn' src='/images/icons/save.png' style='margin-left : 15px;'/>");
			$('#profile_ssn').html("<input id='edit_ssn_field' name='ssn' type='text' MAXLENGTH=20 value='" + $('#profile_ssn').text() + "'/>");
			document.getElementById('edit_ssn_field').focus();
			flag.edit="ssn";
		},
		saveSsn: function(){
			userd.ssn=$("#edit_ssn_field").val();
			var json = JSON.stringify(userd);
			var save = $("#edit_ssn_field").val();
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
							vssn = data.ssn;
							$('#profile_ssn').html("" + vssn );
						}
					});
				}
			});
			$('#button_ssn').html("<img class='button edit_button_ssn' src='/images/icons/edit.png' style='margin-left : 15px;'/>");
			$('#profile_ssn').html($('#edit_ssn_field').text());
		},
		
		filterOnEnter:function(e){
			 if (e.keyCode == 13){
				 if (flag.edit=="mail"){
					 this.saveMail();
				 }
				 if (flag.edit=="adress"){
					 this.saveAdress();
				 }
				 if (flag.edit=="birth"){
					 this.saveBirth();
				 }
				 if (flag.edit=="businessphone"){
					 this.saveBusinessphone();
				 }
				 if (flag.edit=="city")  {
					 this.saveCity(); 
				 }
				 if (flag.edit=="firstname"){
					 this.saveFirstname();
				 }
				 if (flag.edit=="ssn"){
					 this.saveSsn();
				 }
				 if (flag.edit=="homephone"){
					 this.saveHomephone();
				 }
				 if (flag.edit=="mobilephone"){
					 this.saveMobilephone();
				 }
				 if (flag.edit=="nationality"){
					 this.saveNationality();
				 }
				 if (flag.edit=="zip"){
					 this.saveZip();
				 }
				 if (flag.edit=="name"){
					 this.saveName();
				 }
			 }
		},
		
		onBlur : function(){
			 if (flag.edit=="mail"){
				 this.saveMail();
			 }
			 if (flag.edit=="adress"){
				 this.saveAdress();
			 }
			 if (flag.edit=="birth"){
				 this.saveBirth();
			 }
			 if (flag.edit=="businessphone"){
				 this.saveBusinessphone();
			 }
			 if (flag.edit=="city")  {
				 this.saveCity(); 
			 }
			 if (flag.edit=="firstname"){
				 this.saveFirstname();
			 }
			 if (flag.edit=="ssn"){
				 this.saveSsn();
			 }
			 if (flag.edit=="homephone"){
				 this.saveHomephone();
			 }
			 if (flag.edit=="mobilephone"){
				 this.saveMobilephone();
			 }
			 if (flag.edit=="nationality"){
				 this.saveNationality();
			 }
			 if (flag.edit=="zip"){
				 this.saveZip();
			 }
			 if (flag.edit=="name"){
				 this.saveName();
			 }
			
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