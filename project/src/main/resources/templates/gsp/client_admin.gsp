<script type="text/javascript" src="/js/client_admin.js"></script>

<div id="group_header" style=""></div>

<div id="mask"></div>

<div id="modal_window_client">
</div>

<!-- Template for the create view  -->
<script type="text/html" id="popup-client-admin-create-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
	<fieldset>
	<legend><%= i18n.t("client_informations") %></legend>
	<%= i18n.t("client_name") %>: <input type="text" name="name"/><br/>
	<%= i18n.t("client_address") %>: <input type="text" name="address"/><br/>
	<%= i18n.t("client_email") %>: <input type="text" name="email"/><br/>
	<%= i18n.t("client_zip") %>: <input type="text" name="zip" maxlenght="5"/><br/>
	<%= i18n.t("client_city") %>: <input type="text" name="city"/><br/>
	<%= i18n.t("client_phone") %>: <input type="text" name="phone" maxlenght="10"/><br/>
	<%= i18n.t("client_fax") %>: <input type="text" name="fax" maxlenght="10"/><br/>
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("create") %>" class="createClient"/>
	<span id="errors_message"></span>
</script>

<!-- Template for the update view  -->
<script type="text/html" id="popup-client-admin-update-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
	<fieldset>
	<legend><%= i18n.t("client_informations") %></legend>
	<%= i18n.t("client_name") %>: <input type="text" name="name" value="{{name}}"/><br/>
	<%= i18n.t("client_address") %>: <input type="text" name="address" value="{{address}}"/><br/>
	<%= i18n.t("client_email") %>: <input type="text" name="email" value="{{email}}"/><br/>
	<%= i18n.t("client_zip") %>: <input type="text" name="zip" maxlenght="5" value="{{zip}}"/><br/>
	<%= i18n.t("client_city") %>: <input type="text" name="city" value="{{city}}"/><br/>
	<%= i18n.t("client_phone") %>: <input type="text" name="phone" maxlenght="10" value="{{phone}}"/><br/>
	<%= i18n.t("client_fax") %>: <input type="text" name="fax" maxlenght="10" value="{{fax}}"/><br/>
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("update") %>" class="updateClient"/>
	<span id="errors_message"></span>
</script>

<script type="text/html" id="confirm-client-deletion-template">
<%= i18n.t("confirm_client_deletion") %>
</script>

<script type="text/html" id="client-deleted-template">
<%= i18n.t("client_deleted") %>
</script>

<script type="text/html" id="client-created-updated-template">
<%= i18n.t("client_created_updated") %>
</script>

<script type="text/html" id="table-header-template">
<tr><th><%= i18n.t("client_name") %><th><%= i18n.t("client_email") %></th><th><%= i18n.t("client_phone")%></th></tr>
</script>

<div id="client_admin_container">
	<div id="client_admin_buttons">
		<input type="button" class="createButton" value="<%= i18n.t("create") %>"/>
		<input type="button" class="editButton" value="<%= i18n.t("edit") %>" disabled="disabled"/>
		<input type="button" class="deleteButton" value="<%= i18n.t("delete") %>" disabled="disabled"/>
		<span id="span_notifications"></span>
	</div>
	<table id="client_admin_table">
		
	</table>
</div>