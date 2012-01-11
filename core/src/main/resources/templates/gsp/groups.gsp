<%= i18n.t("groups_page_text") %>
<br/>
<div id="users">

<ul>
				<% groups.each() { value -> %>
					<li> ${value.name}</li>
				<% };%>
</ul>


</div>