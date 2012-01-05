<script type="text/javascript" src="/js/streams_admin.js"></script>

<div id="group_header" style=""></div>
<div id="mask" style="position:absolute;z-index:9000;background-color:#000;display:none;top:0;left:0;"></div>
<div id="modal_window" style=" position:absolute;width:440px;height:250px;display:none;z-index:9999;padding:20px;top:0;left:0;">
</div>

<script type="text/html" id="popup-stream-admin-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal" style="right:0;"/><br/>
	<fieldset>
	<legend><%= i18n.t("stream_informations") %></legend>
	<%= i18n.t("name") %>: <input type="text" name="name" value="{{name}}"/><br/>
	<%= i18n.t("stream_category") %>:
	<select name="category" id="category">
		<option value="streams/users"><%= i18n.t("stream_category_user") %></option>
		<option value="streams/plugins"><%= i18n.t("stream_category_plugin") %></option>
		<option value="streams/others"><%= i18n.t("stream_category_other") %></option>
	</select>
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("create") %>" class="sendStream"/><br/>
	<span id="streams_errors" style="display:none;font-weight:bold;color:red;"></span>
</script>

<script type="text/html" id="popup-stream-admin-update-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal" style="right:0;"/><br/>
	<fieldset>
	<legend><%= i18n.t("stream_informations") %></legend>
	<%= i18n.t("name") %>: <input type="text" name="name" value="{{name}}"/><br/>
	<div id="selected"><%= i18n.t("stream_category") %>:
		<select name="category" id="category">
			<option value="streams/users"><%= i18n.t("stream_category_user") %></option>
			<option value="streams/plugins"><%= i18n.t("stream_category_plugin") %></option>
			<option value="streams/others"><%= i18n.t("stream_category_other") %></option>
		</select>
	</div> 
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("update") %>" class="updateDataStream"/><br/>
	<span id="streams_errors" style="display:none;font-weight:bold;color:red;"></span>
</script>

<script type="text/html" id="confirm-stream-lock-template">
<%= i18n.t("confirm_stream_lock") %>
</script>

<script type="text/html" id="confirm-stream-unlock-template">
<%= i18n.t("confirm_stream_unlock") %>
</script>

<script type="text/html" id="stream-locked-template">
<%= i18n.t("stream_locked") %>
</script>

<script type="text/html" id="stream-unlocked-template">
<%= i18n.t("stream_unlocked") %>
</script>

<script type="text/html" id="rights-updated-template">
<%= i18n.t("stream_rights_updated") %>
</script>

<script type="text/html" id="stream-created-template">
<%= i18n.t("stream_created") %>
</script>

<script type="text/html" id="stream-updated-template">
<%= i18n.t("stream_updated") %>
</script>

<script type="text/html" id="stream-rights-combo-template">
<tr>
	<td>{{lastname}} {{firstname}}</td>
	<td><select id="{{id}}">
			<option value="nothing"><%= i18n.t("no_right") %></option>
			<option value="read"><%= i18n.t("read_right") %></option>
			<option value="write"><%= i18n.t("read_write_right") %></option>
			<option value="delete"><%= i18n.t("read_write_delete_right") %></option>
		</select><br/>
	</td>
</tr>
</script>

<!-- Template for the rights view  -->
<script type="text/html" id="popup-stream-rights-update-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal" style="right:0;"/><br/>
	<fieldset>
	<legend><%= i18n.t("stream_rights_text") %> {{title}}</legend>
		<div id="usersToRight" style="height:150px;overflow-y:auto;">
			<!-- Filled by ajax  -->
		</div>
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("update") %>" class="updateStream"/>
</script>

<div id="stream_admin_container">
	<div id="stream_admin_buttons">
		<input type="button" class="createButton" value="<%= i18n.t("create") %>"/>
		<input type="button" class="editButton" value="<%= i18n.t("edit") %>" disabled="disabled"/>
		<input type="button" class="lockButton" value="<%= i18n.t("lock") %>" disabled="disabled"/>
		<input type="button" class="unlockButton" value="<%= i18n.t("unlock") %>" disabled="disabled"/>
		<input type="button" class="rightsButton" value="<%= i18n.t("rights") %>" disabled="disabled"/>
		<span id="streams_notifications" style="display:none;color:green;"></span>
	</div>
	<table id="stream_admin_table" style="cursor:pointer;width:100%">
		
	</table>
</div>