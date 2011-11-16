<div id="admin_header" style="height:50px;text-align:center;font-size:20px;font-weight:bold;">Administration page</div>
<div id="admin_sidebar_container" style="width:250px;">
	<% plugins.each() { key, value -> %>
		<div class="admin_plugin_menu">
			<a href="${value}">${key}</a>
		</div>
	<% };%>
</div>
<div id="admin_panel_container" style="width:710px;">
</div>
<div id="admin_bottom_container"><div>