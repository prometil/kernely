<script type="text/javascript" src="/js/administration/group_admin.js"></script>

<div id="group_header" style=""></div>

<div id="mask" style="position:absolute;z-index:9000;background-color:#000;display:none;top:0;left:0;"></div>

<div id="modal_window" style=" position:absolute;width:440px;height:120px;display:none;z-index:9999;padding:20px;top:0;left:0;">
</div>

<script type="text/html" id="popup-group-admin-template">
	<input type="button" value="Close" class="closeModal" style="right:0;"/><br/>
	<fieldset>
	<legend>Group informations</legend>
	Name : <input type="text" name="name" value="{{name}}"/><br/>
	</fieldset>
	<br/>
	<input type="button" value="Send" class="sendGroup"/>
</script>

<div id="group_admin_container">
	<div id="group_admin_buttons">
		<input type="button" class="createButton" value="Create"/>
		<input type="button" class="editButton" value="Edit" disabled="disabled"/>
		<input type="button" class="deleteButton" value="Delete" disabled="disabled"/>
		<span id="groups_notifications" style="display:none;color:green;"></span>
	</div>
	<table id="group_admin_table" style="cursor:pointer;width:100%">
		<tr><th>Name</th><th>Members</th></tr>
		
	</table>
</div>