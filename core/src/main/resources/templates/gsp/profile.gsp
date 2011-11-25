<div id="profile_header" style="height:50px;text-align:center;font-size:20px;font-weight:bold;">${username}'s profile page</div>
<div id="profile_top_container" style="height:560px;">

	<div id="profile_image" style="padding-top:10px;height:550px;width:380px;float:left;text-align:center;border-top:1px solid black;">
		<img src="${image}" id="image_name" name="${imagename}" style="max-width:295px; max-height: 245px;"/>
	</div>
	<div id="profile_information" style="height:550px;width:545px;float:left;text-align:left;border-left:1px solid black; border-top:1px solid black; padding:15px;">
		<table style="width:100%">
			<tr><td>Civility</td><td><span id="profile_civility">
				<% if (civility == 1 ){ %>
				Mister
				<%} else if (civility == 2) {%>
				Madam
				<%} else if (civility == 3) {%>
				Miss
				<% } %>		
			</span></td></tr>
		
			<tr><td>Name</td><td><span id="profile_name">${lastname}</span>
		
			<tr><td>First name</td><td><span id="profile_firstname">${firstname}</span></td></tr>
		
			<tr><td>Mail address</td><td><span id="profile_mail"><a href="mailto:${email}">${email}</a></span></span></td></tr>
		
			<tr><td>Adress</td><td><span id="profile_adress">${adress}</span></td></tr>
		
			<tr><td>Zip code</td><td><span id="profile_zip">${zip}</span></td></tr>
		
			<tr><td>City</td><td><span id="profile_city">${city}</span></td></tr>
		
			<tr><td>Home phone</td><td><span id="profile_homephone">${homephone}</span></td></tr>
		
			<tr><td>Mobile phone</td><td><span id="profile_mobilephone">${mobilephone}</span></td></tr>
		
			<tr><td>Business phone</td><td><span id="profile_businessphone">${businessphone}</span></td></tr>
		
			<tr><td>Date of birth</td><td><span id="profile_birth">${birth}</span></td></tr>
		
			<tr><td>Nationality</td><td><span id="profile_nationality">${nationality}</span></td></tr>
		
			<tr><td>Security social number</td><td><span id="profile_ssn">${ssn}</span></td></tr>
		</table>
	</div>
</div>
<div id="profile_bottom_container"><div>