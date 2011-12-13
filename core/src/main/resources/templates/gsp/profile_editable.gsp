<script type="text/javascript" src="/js/profile.js"></script>

<div id="profile_header" style="height:50px;text-align:center;font-size:20px;font-weight:bold;">${username}'s profile page</div>
<div id="profile_top_container" style="height:560px;">



	<div id="profile_image" style="padding-top:10px;height:550px;width:380px;float:left;text-align:center;border-top:1px solid black;">
		<img src="${image}" id="image_name" name="${imagename}" style="max-width:295px; max-height: 245px;"/>
		<form action="/user/upload" method="post" enctype="multipart/form-data">
 			<input type="file" name="file"/>
		   <input type="submit" value="Upload It" />
		</form>
	</div>
	<div id="profile_information" style="height:550px;width:545px;float:left;text-align:left;border-left:1px solid black; border-top:1px solid black; padding:15px;">
		<table style="width:100%">
			<tr><td>Civility</td><td><span id="profile_civility" style="display: none;">${civility}</span><input type="radio" class="edit_button_civility" name="civility" value="1"> Mister <input type="radio" class="edit_button_civility" name="civility" value="2"> Madam <input type="radio" class="edit_button_civility" name="civility" value="3"> Miss </td><td></td></tr>
		
			<tr><td>Name</td><td><span id="profile_name" class="span_profile_lastname">${lastname}</span></td><td><span id="button_name"><img class="button edit_button_name" src="/images/icons/edit.png" style="margin-left : 15px;"/></span></td></tr>
	
			<tr><td>First name</td><td><span id="profile_firstname" class="span_profile_firstname">${firstname}</span></td><td><span id="button_firstname"><img class="button edit_button_firstname" src="/images/icons/edit.png" style="margin-left : 15px;"/></span></td></tr>

			<tr><td>Mail address</td><td><span id="profile_mail" class="span_profile_mail"><a href="mailto:${email}">${email}</a></span></td><td><span id="button_mail"><img class="button edit_button_mail" src="/images/icons/edit.png" style="margin-left : 15px;"/></span></td></tr>

			<tr><td>Address</td><td><span id="profile_adress" class="span_profile_adress">${adress}</span></td><td><span id="button_adress"><img class="button edit_button_adress" src="/images/icons/edit.png" style="margin-left : 15px;"/></span></td></tr>

			<tr><td>Zip code</td><td><span id="profile_zip" class="span_profile_zip">${zip}</span></td><td><span id="button_zip"><img class="button edit_button_zip" src="/images/icons/edit.png" style="margin-left : 15px;"/></span></td></tr>

			<tr><td>City</td><td><span id="profile_city" class="span_profile_city">${city}</span></td><td><span id="button_city"><img class="button edit_button_city" src="/images/icons/edit.png" style="margin-left : 15px;"/></span></td></tr>

			<tr><td>Home phone</td><td><span id="profile_homephone" class="span_profile_homephone">${homephone}</span></td><td><span id="button_homephone"><img class="button edit_button_homephone" src="/images/icons/edit.png" style="margin-left : 15px;"/></span></td></tr>

			<tr><td>Mobile phone</td><td><span id="profile_mobilephone" class="span_profile_mobilephone">${mobilephone}</span></td><td><span id="button_mobilephone"><img class="button edit_button_mobilephone" src="/images/icons/edit.png" style="margin-left : 15px;"/></span></td></tr>

			<tr><td>Business phone</td><td><span id="profile_businessphone" class="span_profile_businessphone">${businessphone}</span></td><td><span id="button_businessphone"><img class="button edit_button_businessphone" src="/images/icons/edit.png" style="margin-left : 15px;"/></span></td></tr>

			<tr><td>Date of birth</td><td><span id="profile_birth" class="span_profile_birth">${birth}</span></td><td><span id="button_birth"><img class="button edit_button_birth" src="/images/icons/edit.png" style="margin-left : 15px;"/></span></td></tr>

			<tr><td>Nationality</td><td><span id="profile_nationality" class="span_profile_nationality">${nationality}</span></td><td><span id="button_nationality"><img class="button edit_button_nationality" src="/images/icons/edit.png" style="margin-left : 15px;"/></span></td></tr>

		<tr><td>Security social number</td><td><span id="profile_ssn" class="span_profile_ssn">${ssn}</span></td><td><span id="button_ssn"><img class="button edit_button_ssn" src="/images/icons/edit.png" style="margin-left : 15px;"/></span></td></tr>
		</table>
	</div>
</div>
<div style="clear:both;"></div>
<div id="profile_bottom_container"><div>