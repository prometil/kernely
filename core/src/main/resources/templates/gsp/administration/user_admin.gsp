 <script type="text/javascript" src="/js/administration/user_admin.js"></script>

<div id="user_header" style=""></div>
<div id="mask" style="position:absolute;z-index:9000;background-color:#000;display:none;top:0;left:0;"></div>
<div id="modal_window_user" style=" position:absolute;width:440px;height:440px;display:none;z-index:9999;padding:20px;top:0;left:0;">
</div>

<script type="text/html" id="popup-user-admin-create-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal" style="right:0;"/><br/>
	<fieldset>
	<legend><%= i18n.t("connection_informations") %></legend>
	<%= i18n.t("username") %>: <input type="text" name="login" value="{{login}}"/><br/>
	<%= i18n.t("password") %>: <input type="text" name="password"/>
	</fieldset>
	<br/>
	<fieldset>
	<legend><%= i18n.t("user_informations") %></legend>
	<%= i18n.t("name") %>:<input type="text" name="lastname" value="{{lastname}}"/><br/>
	<%= i18n.t("firstname") %>: <input type="text" name="firstname" value="{{firstname}}"/>
	</fieldset>
	<br/>
	
	<input type="button" value="<%= i18n.t("create") %>" class="createUser"/>
	<span id="users_errors_create" style="display:none;font-weight:bold;color:red;"></span>
</script>

<script type="text/html" id="popup-user-admin-update-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal" style="right:0;"/><br/>
	<fieldset>
	<legend><%= i18n.t("connection_informations") %></legend>
	<%= i18n.t("login") %>: <input type="text" name="login" value="{{login}}"/><br/>
	<%= i18n.t("password") %>: <input type="text" name="password"/>
	</fieldset>
	<br/>
	<fieldset>
	<legend><%= i18n.t("user_informations") %></legend>
	<%= i18n.t("name") %>: <input type="text" name="lastname" value="{{lastname}}"/><br/>
	<%= i18n.t("firstname") %>: <input type="text" name="firstname" value="{{firstname}}"/>
	</fieldset>
	<fieldset>
	<legend><%= i18n.t("user_roles") %></legend>
		<div id="rolesToLink" style="height:150px;overflow-y:auto;">
			<!-- Filled by ajax  -->
		</div>
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("update") %>" class="updateUser"/>
	<span id="users_errors_update" style="display:none;font-weight:bold;color:red;"></span>
</script>

<div id="user_admin_container">
        <div id="user_admin_buttons">
                <input type="button" class="createButton" value="<%= i18n.t("create") %>"/>
                <input type="button" class="editButton" value="<%= i18n.t("edit") %>" disabled="disabled"/>
                <input type="button" class="lockButton" value="<%= i18n.t("lock") %>" disabled="disabled"/>
                <span id="users_notifications" style="display:none;color:green;"></span>
        </div>
        <table id="user_admin_table" style="cursor:pointer;width:100%">
                
        </table>
</div>