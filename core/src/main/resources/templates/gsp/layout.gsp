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
	
	<!-- css files -->
	<link rel="stylesheet" type="text/css" href="/css/style.css"/>
	<% css.each() {  value -> %>
		<link rel="stylesheet" type="text/css" href="${value}"/>
					
	<% };%>
	</head>
	<body>
			<!-- display all applications in the menu -->
			<div id="header">
				<div id="menu">
					<div id="title">Kernely</div>
					<% menu.each() { key, value -> %>
						<a href="${value}">${key}</a>
					<% };%>
					<a href="/group">${groups}</a>
					<a href="/user">${users}</a>
				</div>
				<div id="header_profile">
					<a href="#">${currentUser}</a>
					<a href="/user/logout">logout</a>
				</div>
				
			</div>
			<div id="container">
				<%= content %>
			</div>
	</body>
</html>