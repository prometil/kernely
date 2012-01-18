<div id="profile_header" style="height:50px;text-align:center;font-size:20px;font-weight:bold;"><%= i18n.t("profile_page_of") %> ${details.firstname} ${details.lastname}</div>
<div id="profile_top_container" style="height:560px;">

	<div id="profile_image">
		<img src="/images/${details.image}" id="image_name" name="${details.image}"/>
	</div>
	<div id="profile_information">
		<table style="width:100%">
			<tr><td><%= i18n.t("civility") %></td><td><span id="profile_civility">
				<% if (details.civility == 1 ){ %>
				<%= i18n.t("civility.mister") %>
				<%} else if (details.civility == 2) {%>
				<%= i18n.t("civility.madam") %>
				<%} else if (details.civility == 3) {%>
				<%= i18n.t("civility.miss") %>
				<% } %>		
			</span></td></tr>
		
			<tr><td><%= i18n.t("name") %></td><td><span id="profile_name">${details.lastname}</span>
		
			<tr><td><%= i18n.t("firstname") %></td><td><span id="profile_firstname">${details.firstname}</span></td></tr>
		
			<tr><td><%= i18n.t("email") %></td><td><span id="profile_mail"><a href="mailto:${details.email}">${details.email}</a></span></span></td></tr>
		
			<tr><td><%= i18n.t("address") %></td><td><span id="profile_adress">${details.adress}</span></td></tr>
		
			<tr><td><%= i18n.t("zip") %></td><td><span id="profile_zip">${details.zip}</span></td></tr>
		
			<tr><td><%= i18n.t("city") %></td><td><span id="profile_city">${details.city}</span></td></tr>
		
			<tr><td><%= i18n.t("phone.home") %></td><td><span id="profile_homephone">${details.homephone}</span></td></tr>
		
			<tr><td><%= i18n.t("phone.mobile") %></td><td><span id="profile_mobilephone">${details.mobilephone}</span></td></tr>
		
			<tr><td><%= i18n.t("phone.busines") %></td><td><span id="profile_businessphone">${details.businessphone}</span></td></tr>
		
			<tr><td><%= i18n.t("birthday") %></td><td><span id="profile_birth">${details.birth}</span></td></tr>
		
			<tr><td><%= i18n.t("nationality") %></td><td><span id="profile_nationality">${details.nationality}</span></td></tr>
		
			<tr><td><%= i18n.t("security_social_number") %></td><td><span id="profile_ssn">${details.ssn}</span></td></tr>
		</table>
	</div>
</div>
<div id="profile_bottom_container"><div>