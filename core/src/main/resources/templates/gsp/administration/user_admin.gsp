<script type="text/javascript" src="/js/administration/user_admin.js"></script>

<div id="user_header" style=""></div>
<div id="mask" style="position:absolute;z-index:9000;background-color:#000;display:none;top:0;left:0;"></div>
<div id="modal_window" style=" position:absolute;width:440px;height:250px;display:none;z-index:9999;padding:20px;top:0;left:0;">
</div>

<script type="text/html" id="popup-user-admin-template">
	<input type="button" value="Close" class="closeModal" style="right:0;"/><br/>
	<fieldset>
	<legend>Connection informations</legend>
	Login : <input type="text" name="login" value="{{login}}"/><br/>
	Password : <input type="text" name="password"/>
	</fieldset>
	<br/>
	<fieldset>
	<legend>User informations</legend>
	name : <input type="text" name="name" value="{{name}}"/><br/>
	Firstname : <input type="text" name="firstname" value="{{firstname}}"/>
	</fieldset>
	<br/>
	<input type="button" value="Send" class="sendUser"/>
</script>

<div id="user_admin_container">
	<div id="user_admin_buttons">
		<input type="button" class="createButton" value="Create"/>
		<input type="button" class="editButton" value="Edit" disabled="disabled"/>
		<input type="button" class="lockButton" value="Lock" disabled="disabled"/>
		<span id="users_notifications" style="display:none;color:green;"></span>
	</div>
	<table id="user_admin_table" style="cursor:pointer;width:100%">
		<tr><th></th><th>Name</th><th>First name</th><th>Login</th><th>Mail</th></tr>
		
	</table>
</div>