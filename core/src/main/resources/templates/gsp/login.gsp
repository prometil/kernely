<html>
	<head>
	<link rel="stylesheet" type="text/css" href="/css/login.css"/>	
	</head>
	<body>
	<div id="header" >
		<div id="logo"><%= i18n.t("title") %></div>
	
	</div>
	
	<div id="space">
	
	</div>
	<div id="information">
		
	</div>
	
	<div id="login">
		<form action="" method="POST">
			<%= i18n.t("connection") %><br/>
			<p><input name="username" type="text" placeholder="<%= i18n.t("username") %>"/></p>
			<p><input name="password" type="password"  placeholder="<%= i18n.t("password") %>"/></p>
			<div id="connection"> 
				<input type="submit" value="<%= i18n.t("login") %>"/>
			</div> 
			<div id="remember">
				<input type="checkbox" name="rememberMe" value="true"/><%= i18n.t("remember_me") %>?<br/>
			</div>
		</form>
	</div>
 
	</body>
</html>