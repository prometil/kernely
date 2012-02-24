<script type="text/javascript" src="/js/project_admin.js"></script>

<div id="group_header" style=""></div>

<div id="mask"></div>

<div id="modal_window_project">
</div>

<!-- Template for the create view  -->
<script type="text/html" id="popup-project-admin-create-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
	<fieldset>
	<legend><%= i18n.t("project_informations") %></legend>
	<%= i18n.t("project_name") %>: <input type="text" name="name"/><br/>
	</fieldset>
	<fieldset>
	<legend><%= i18n.t("project_organizations") %></legend>
	<div id="combo"><%= i18n.t("organization") %>:
	
	</div>	
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("create") %>" class="createProject"/>
	<span id="errors_message"></span>
</script>

<!-- Template for the edit view  -->
<script type="text/html" id="popup-project-admin-update-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
	<fieldset>
	<legend><%= i18n.t("project_informations") %></legend>
	<%= i18n.t("project_name") %>: <input type="text" name="name" value="{{name}}"/><br/>
	</fieldset>
	<fieldset>
	<legend><%= i18n.t("project_organizations") %></legend>
	<div id="combo"><%= i18n.t("organization") %>:
	
	</div>	
	</fieldset>
	<fieldset>
	<legend><%= i18n.t("project_users") %></legend>
		<div id="usersToLink">
			<table id="project_user_table">
		
			</table>
		</div>
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("update") %>" class="updateProject"/>
	<span id="errors_message"></span>
</script>


<!-- Template for the icon view  -->
<script type="text/html" id="popup-project-admin-icon-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
	<fieldset>
	<legend><%= i18n.t("project_informations") %></legend>
		<img src="/images/{{icon}}" id="image_name" name="{{icon}}"/>
		<form action="/admin/projects/upload/{{name}}" method="post" enctype="multipart/form-data">
 			<input type="file" name="file"/>
		   <input type="submit" value="<%= i18n.t("upload") %>" />
		</form>
	</fieldset> <br/>
</script>


<script type="text/html" id="confirm-project-deletion-template">
<%= i18n.t("confirm_project_deletion") %>
</script>

<script type="text/html" id="project-deleted-template">
<%= i18n.t("project_deleted") %>
</script>

<script type="text/html" id="project-created-updated-template">
<%= i18n.t("project_created_updated") %>
</script>

<script type="text/html" id="table-header-template" class="table_header">
<tr><th><%= i18n.t("project_name") %></tr>
</script>

<script type="text/html" id="table-header-template2" class="table_header">
<tr><th></th><th><%= i18n.t("contributor") %><th><%= i18n.t("project_manager") %></th><th><%= i18n.t("client") %></th></tr>
</script>


<div id="project_admin_container">
	<div id="project_admin_buttons">
		<input type="button" class="createButton" value="<%= i18n.t("create") %>"/>
		<input type="button" class="editButton" value="<%= i18n.t("edit") %>" disabled="disabled"/>
		<input type="button" class="deleteButton" value="<%= i18n.t("delete") %>" disabled="disabled"/>
		<input type="button" class="imageButton" value="<%= i18n.t("image") %>"  disabled="disabled"/>
		<span id="span_notifications"></span>
	</div>
	<table id="project_admin_table">
		
	</table>
</div>