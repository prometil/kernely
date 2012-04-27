<!DOCTYPE html>
<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Kernely</title>
	
	<!-- js files -->
	<script type="text/javascript" src="/js/mustache.js"></script>
	<script type="text/javascript" src="/js/json2.js"></script>
	<script type="text/javascript" src="/js/jquery.js"></script>
	<script type="text/javascript" src="/js/underscore.js"></script>
	<script type="text/javascript" src="/js/backbone.js"></script>
	<script type="text/javascript" src="/js/miniprofile.js"></script>
	
	<script type="text/javascript" src="/js/jquery-ui-1.8.16.min.js"></script>
	<script type="text/javascript" src="/js/jquery.ui.datepicker-en-US.js"></script>
	<script type="text/javascript" src="/js/jquery.ui.datepicker-fr-FR.js"></script>
	<script type="text/javascript" src="/js/jscolor/jscolor.js"></script>
	<!-- css files -->
	<link rel="stylesheet" type="text/css" href="/css/style.css"/>
	<link rel="stylesheet" type="text/css" href="/css/jquery-ui.css"/>
	<link rel="stylesheet" type="text/css" href="/css/farbtastic.css"/>
	<% css.each() {  value -> %>
		<link rel="stylesheet" type="text/css" href="${value}"/>
					
	<% };%>
	</head>
	<body>
			<script type="text/html" id="profile-template">
				<div id='profilePU-content'>
					<div id='profilePU-main'>
						<div id='profilePU-image'>
							<img id="profilePU-img" src='{{image}}'/>
						</div>
						<div id='profilePU-information'>
							{{fullname}} ({{username}})<br/>
							{{mail}}<br/>
							<br/>
							<a href="/user/{{username}}/profile"><%= i18n.t("profile") %></a>
						</div>
					</div>
					<div id="profilePU-footer">
						<hr/>
						<a href="/user/logout"><%= i18n.t("logout") %></a>
					</div>
				</div>
			</script>
		
			<!-- display all applications in the menu -->
			<div id="header">
				<div id="menu">
					<div id="title"><%= i18n.t("title") %></div>
					<% menu.each() { key, value -> %>
						<a href="${value}"><%= i18n.t(key) %></a>
					<% };%>
					
					<% if (admin != ""){ %>
						<a href="/admin">${admin}</a>
					<% } %>
				</div>
				<div id="menu_header_profile">
					<a id="username_menu" href="javascript:void(0)" class="button displayProfilePU"></a>
					<a id="userimg_menu" href="javascript:void(0)" class="button displayProfilePU"></a>
				</div>
			</div>
			<div id="profile_popup"></div>
			<div id="container">
				<%= content %>
			</div>
	</body>
</html>