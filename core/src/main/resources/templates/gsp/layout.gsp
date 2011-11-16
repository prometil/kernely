<!DOCTYPE html>
<html>
	<head>
	<title>Kernely</title>
	
	<!-- js files -->
	<script type="text/javascript" src="/js/mustache.js"></script>
	<script type="text/javascript" src="/js/json2.js"></script>
	<script type="text/javascript" src="/js/jquery.js"></script>
	<script type="text/javascript" src="/js/underscore.js"></script>
	<script type="text/javascript" src="/js/backbone.js"></script>
	<script type="text/javascript" src="/js/miniprofile.js"></script>
	
	<!-- css files -->
	<link rel="stylesheet" type="text/css" href="/css/style.css"/>
	<% css.each() {  value -> %>
		<link rel="stylesheet" type="text/css" href="${value}"/>
					
	<% };%>
	</head>
	<body>
			<script type="text/html" id="profile-template">
				<div id='profilePU-content' style="border:1px solid black; padding:5px;">
					<div id='profilePU-main' style="height:80px;">
						<div id='profilePU-image' style="height:80px;width:125px;float:left;">
							<img id="profilePU-img" src='{{image}}' style="max-width:120px; max-height:80px;"/>
						</div>
						<div id='profilePU-information' style="height:80px;width:173px;float:left;">
							{{fullname}} ({{username}})<br/>
							{{mail}}<br/>
							<br/>
							<a href="/user/{{username}}/profile">Profile</a>
						</div>
					</div>
					<div id="profilePU-footer" style="height:30px;text-align:center;">
						<hr/>
						<a href="/user/logout">logout</a>
					</div>
				</div>
			</script>
		
			<!-- display all applications in the menu -->
			<div id="header">
				<div id="menu">
					<div id="title">Kernely</div>
					<% menu.each() { key, value -> %>
						<a href="${value}">${key}</a>
					<% };%>
					<a href="/group">${groups}</a>
					<a href="/user">${users}</a>
					<% if (admin != ""){ %>
						<a href="/admin">${admin}</a>
					<% } %>
				</div>
				<div id="menu_header_profile" style="right:5px;	height: 30px; position: absolute;top: 0;">
					<a id="username_menu" href="javascript:void(0)" class="button displayProfilePU"></a>
					<a id="userimg_menu" href="javascript:void(0)" class="button displayProfilePU"></a>
				</div>
			</div>
			<div id="profile_popup" style="display:none;right:5px;position:absolute;top:29px;width:310px;height:135px;background-color:#FFFFFF;"></div>
			<div id="container">
				<%= content %>
			</div>
	</body>
</html>