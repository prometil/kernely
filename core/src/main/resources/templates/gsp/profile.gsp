<div id="profile_header" style="height:50px;text-align:center;font-size:20px;font-weight:bold;">${details.firstname} ${details.lastname}'s profile page</div>
<div id="profile_top_container" style="height:560px;">

	<div id="profile_image" style="padding-top:10px;height:550px;width:380px;float:left;text-align:center;border-top:1px solid black;">
		<img src="/images/${details.image}" id="image_name" name="${details.image}" style="max-width:295px; max-height: 245px;"/>
	</div>
	<div id="profile_information" style="height:550px;width:545px;float:left;text-align:left;border-left:1px solid black; border-top:1px solid black; padding:15px;">
		<table style="width:100%">
			<tr><td>Civility</td><td><span id="profile_civility">
				<% if (details.civility == 1 ){ %>
				Mister
				<%} else if (details.civility == 2) {%>
				Madam
				<%} else if (details.civility == 3) {%>
				Miss
				<% } %>		
			</span></td></tr>
		
			<tr><td>Name</td><td><span id="profile_name">${details.lastname}</span>
		
			<tr><td>First name</td><td><span id="profile_firstname">${details.firstname}</span></td></tr>
		
			<tr><td>Mail address</td><td><span id="profile_mail"><a href="mailto:${details.email}">${details.email}</a></span></span></td></tr>
		
			<tr><td>Address</td><td><span id="profile_adress">${details.adress}</span></td></tr>
		
			<tr><td>Zip code</td><td><span id="profile_zip">${details.zip}</span></td></tr>
		
			<tr><td>City</td><td><span id="profile_city">${details.city}</span></td></tr>
		
			<tr><td>Home phone</td><td><span id="profile_homephone">${details.homephone}</span></td></tr>
		
			<tr><td>Mobile phone</td><td><span id="profile_mobilephone">${details.mobilephone}</span></td></tr>
		
			<tr><td>Business phone</td><td><span id="profile_businessphone">${details.businessphone}</span></td></tr>
		
			<tr><td>Date of birth</td><td><span id="profile_birth">${details.birth}</span></td></tr>
		
			<tr><td>Nationality</td><td><span id="profile_nationality">${details.nationality}</span></td></tr>
		
			<tr><td>Security social number</td><td><span id="profile_ssn">${details.ssn}</span></td></tr>
		</table>
	</div>
</div>
<div id="profile_bottom_container"><div>