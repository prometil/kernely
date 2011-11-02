<script type="text/javascript" src="/js/user.js"></script>
This is the users page.
<br/>
<a href="#" class=".create">Create a new user</a>
<div id="users">

<ul>
				<% users.each() { value -> %>
					<li> ${value.username}</li>
				<% };%>
</ul>


</div>