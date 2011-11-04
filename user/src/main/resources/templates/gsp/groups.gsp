This is the groups page.
<br/>
<div id="users">

<ul>
				<% groups.each() { value -> %>
					<li> ${value.name}</li>
				<% };%>
</ul>


</div>