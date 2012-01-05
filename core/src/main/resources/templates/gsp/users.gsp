<script type="text/javascript" src="/js/user.js"></script>
<%= i18n.t("users_page_text") %>
<br/>
<a href="#" class=".create"><%= i18n.t("create_user") %></a>
<div id="users">

<ul>
				<% users.each() { value -> %>
					<li> ${value.username}</li>
				<% };%>
</ul>


</div>