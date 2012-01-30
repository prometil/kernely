<script type="text/javascript" src="/js/profile.js"></script>

<div id="profile_header"><%= i18n.t("profile_page_of") %> ${details.firstname} ${details.lastname}</div>
<div id="profile_top_container">

<script type="text/html" id="profile-mail-template">
	<td><%= i18n.t("email") %></td>
	<td><span id="profile_{{input}}" class="span_profile">{{data}}</span></td>
</script>

<script type="text/html" id="profile-lastname-template">
	<td><%= i18n.t("name") %></td>
	<td><span id="profile_{{input}}" class="span_profile">{{data}}</span></td>
</script>

<script type="text/html" id="profile-firstname-template">
	<td><%= i18n.t("firstname") %></td>
	<td><span id="profile_{{input}}" class="span_profile">{{data}}</span></td>
</script>

<script type="text/html" id="profile-city-template">
	<td><%= i18n.t("city") %></td>
	<td><span id="profile_{{input}}" class="span_profile">{{data}}</span></td>
</script>

<script type="text/html" id="profile-zip-template">
	<td><%= i18n.t("zip") %></td>
	<td><span id="profile_{{input}}" class="span_profile">{{data}}</span></td>
</script>

<script type="text/html" id="profile-adress-template">
	<td><%= i18n.t("address") %></td>
	<td><span id="profile_{{input}}" class="span_profile">{{data}}</span></td>
</script>

<script type="text/html" id="profile-nationality-template">
	<td><%= i18n.t("nationality") %></td>
	<td><span id="profile_{{input}}" class="span_profile">{{data}}</span></td>
</script>

<script type="text/html" id="profile-homephone-template">
	<td><%= i18n.t("phone.home") %></td>
	<td><span id="profile_{{input}}" class="span_profile">{{data}}</span></td>
</script>

<script type="text/html" id="profile-mobilephone-template">
	<td><%= i18n.t("phone.mobile") %></td>
	<td><span id="profile_{{input}}" class="span_profile">{{data}}</span></td>
</script>

<script type="text/html" id="profile-businessphone-template">
	<td><%= i18n.t("phone.business") %></td>
	<td><span id="profile_{{input}}" class="span_profile">{{data}}</span></td>
</script>

<script type="text/html" id="profile-ssn-template">
	<td><%= i18n.t("security_social_number") %></td>
	<td><span id="profile_{{input}}" class="span_profile">{{data}}</span></td>
</script>

<script type="text/html" id="profile-birth-template">
	<td><%= i18n.t("birthday") %></td>
	<td><span id="profile_{{input}}" class="span_profile">{{data}}</span></td>
</script>


	<div id="profile_image">
		<img src="/images/${details.image}" id="image_name" name="${details.image}"/>
		<form action="/user/upload" method="post" enctype="multipart/form-data">
 			<input type="file" name="file"/>
		   <input type="submit" value="<%= i18n.t("upload") %>" />
		</form>
	</div>
	<div id="profile_information">
		<table style="width:100%" id="profile_table">
			<tr>
				<td><%= i18n.t("civility") %></td>
				<td><span id="profile_civility" style="display: none;">${details.civility}</span>
					<input type="radio" class="edit_button_civility" name="civility" value="1"> <%= i18n.t("civility.mister") %> </input> 
					<input type="radio" class="edit_button_civility" name="civility" value="2"> <%= i18n.t("civility.madam") %> </input>
					<input type="radio" class="edit_button_civility" name="civility" value="3"> <%= i18n.t("civility.miss") %> </input>
				</td>
				<td>
				</td>
			</tr>
		</table>
	</div>
</div>
<div style="clear:both;"></div>
<div id="profile_bottom_container"><div>