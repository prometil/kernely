<script type="text/javascript" src="/js/profile.js"></script>

<div id="profile_header" style="height:50px;text-align:center;font-size:20px;font-weight:bold;">${username}'s profile page</div>
<div id="profile_top_container" style="height:250px;">



	<div id="profile_image" style="height:249px;width:400px;float:left;text-align:left;">
		<img src="${image}" id="image_name" name="${imagename}" style="max-width:295px; max-height: 245px;"/>
	<form action="/user/upload" method="post" enctype="multipart/form-data">
 		Select a file : <input type="file" name="file"/>
	   <input type="submit" value="Upload It" />
	</form>
</div>
	</div>
	<div id="profile_information" style="height:249px;width:575px;float:right;">
		Civility : <span id="profile_civility" style="display: none;">${civility}</span><input type="radio" class="edit_button_civility" name="civility" value="1"> Mister <input type="radio" class="edit_button_civility" name="civility" value="2"> Madam <input type="radio" class="edit_button_civility" name="civility" value="3"> Miss
		<br/>
		Name : <span id="profile_name">${name}</span><span id="button_name"><img class="button edit_button_name" src="/images/icons/edit.png" style="margin-left : 15px;"/></span><br/>
		<br/>
		First name : <span id="profile_firstname">${firstname}</span><span id="button_firstname"><img class="button edit_button_firstname" src="/images/icons/edit.png" style="margin-left : 15px;"/></span><br/>
		<br/>
		Mail address : <span id="profile_mail"><a href="mailto:${mail}">${mail}</a></span><span id="button_mail"><img class="button edit_button_mail" src="/images/icons/edit.png" style="margin-left : 15px;"/></span><br/>
		<br/>
		<textarea readonly="readonly" style="width:650px;">${description}</textarea>

		Adress : <span id="profile_adress">${adress}</span><span id="button_adress"><img class="button edit_button_adress" src="/images/icons/edit.png" style="margin-left : 15px;"/></span><br/>
		<br/>
		Zip code : <span id="profile_zip">${zip}</span><span id="button_zip"><img class="button edit_button_zip" src="/images/icons/edit.png" style="margin-left : 15px;"/></span><br/>
		<br/>
		City : <span id="profile_city">${city}</span><span id="button_city"><img class="button edit_button_city" src="/images/icons/edit.png" style="margin-left : 15px;"/></span><br/>
		<br/>
		Home phone : <span id="profile_homephone">${homephone}</span><span id="button_homephone"><img class="button edit_button_homephone" src="/images/icons/edit.png" style="margin-left : 15px;"/></span><br/>
		<br/>
		Mobile phone : <span id="profile_mobilephone">${mobilephone}</span><span id="button_mobilephone"><img class="button edit_button_mobilephone" src="/images/icons/edit.png" style="margin-left : 15px;"/></span><br/>
		<br/>
		Business phone : <span id="profile_businessphone">${businessphone}</span><span id="button_businessphone"><img class="button edit_button_businessphone" src="/images/icons/edit.png" style="margin-left : 15px;"/></span><br/>
		<br/>
		Date of birth : <span id="profile_birth">${birth}</span><span id="button_birth"><img class="button edit_button_birth" src="/images/icons/edit.png" style="margin-left : 15px;"/></span><br/>
		<br/>
		Nationality : <span id="profile_nationality">${nationality}</span><span id="button_nationality"><img class="button edit_button_nationality" src="/images/icons/edit.png" style="margin-left : 15px;"/></span><br/>
		<br/>
		Security social number  : <span id="profile_ssn">${ssn}</span><span id="button_ssn"><img class="button edit_button_ssn" src="/images/icons/edit.png" style="margin-left : 15px;"/></span><br/>
		
	</div>
</div>
<div id="profile_bottom_container"><div>