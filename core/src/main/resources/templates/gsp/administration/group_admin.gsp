<script type="text/javascript" src="/js/administration/group_admin.js"></script>

<div id="group_header" style=""></div>

<div id="mask" style="position:absolute;z-index:9000;background-color:#000;display:none;top:0;left:0;"></div>

<div id="modal_window_group" style=" position:absolute;width:440px;height:300px;display:none;z-index:9999;padding:20px;top:0;left:0;">
</div>

<!-- Template for the create view  -->
<script type="text/html" id="popup-group-admin-create-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal" style="right:0;"/><br/>
	<fieldset>
	<legend><%= i18n.t("group_informations") %></legend>
	<%= i18n.t("group.name") %>: <input type="text" name="name"/><br/>
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("create") %>" class="createGroup"/>
	<span id="groups_errors_create" style="display:none;font-weight:bold;color:red;"></span>
</script>

<!-- Template for the edit view  -->
<script type="text/html" id="popup-group-admin-update-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal" style="right:0;"/><br/>
	<fieldset>
	<legend><%= i18n.t("group_informations") %></legend>
	<%= i18n.t("group.name") %>: <input type="text" name="name" value="{{name}}"/><br/>
	</fieldset>
	
	<fieldset>
	<legend><%= i18n.t("group_users") %></legend>
		<div id="usersToLink" style="height:150px;overflow-y:auto;">
			<!-- Filled by ajax  -->
		</div>
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("update") %>" class="updateGroup"/>
	<span id="groups_errors_update" style="display:none;font-weight:bold;color:red;"></span>
</script>

<script type="text/html" id="table-header-template">
<tr><th><%= i18n.t("group.name") %></th><th><%= i18n.t("group_users") %></th></tr>
</script>

<script type="text/html" id="confirm-group-deletion-template">
<%= i18n.t("confirm_group_deletion") %>
</script>

<script type="text/html" id="group-deleted-template">
<%= i18n.t("group_deleted") %>
</script>

<script type="text/html" id="group-created-updated-template">
<%= i18n.t("group_created_updated") %>
</script>


<div id="group_admin_container">
	<div id="group_admin_buttons">
		<input type="button" class="createButton" value="<%= i18n.t("create") %>"/>
		<input type="button" class="editButton" value="<%= i18n.t("edit") %>" disabled="disabled"/>
		<input type="button" class="deleteButton" value="<%= i18n.t("delete") %>" disabled="disabled"/>
		<span id="groups_notifications" style="display:none;color:green;"></span>
	</div>
	<table id="group_admin_table" style="cursor:pointer;width:100%">
		
	</table>
</div>