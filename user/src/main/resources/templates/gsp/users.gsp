<script type="text/javascript" src="/js/user.js"></script>
users

<a href="#" class=".create">Create</a>
<div id="users">

<ul>
				<% users.each() { value -> %>
					<li> ${value.username}</li>
				<% };%>
</ul>


</div>