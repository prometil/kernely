<script type="text/javascript" src="/js/administration/manager_admin.js"></script>

<div id="manager_header" style=""></div>

<div id="mask" style="position:absolute;z-index:9000;background-color:#000;display:none;top:0;left:0;"></div>

<div id="modal_window_manager" style=" position:absolute;width:440px;height:300px;display:none;z-index:9999;padding:20px;top:0;left:0;">
</div>


<!-- Template for the create view  -->
<script type="text/html" id="popup-manager-admin-create-template">
	<input type="button" value="Close" class="closeModal" style="right:0;"/><br/>
	<fieldset>
	<legend>Manager informations</legend>
	<div id="combo"></div>
	
	</fieldset>
		
	<fieldset>
	<legend>Manager users</legend>
		<div id="usersToLink" style="height:150px;overflow-y:auto;">
			<!-- Filled by ajax  -->
		</div>
	</fieldset>
	<br/>
	<input type="button" value="Send" class="createManager"/>
	<span id="managers_errors_create" style="display:none;font-weight:bold;color:red;"></span>
</script>

<!-- Template for the edit view  -->
<script type="text/html" id="popup-manager-admin-update-template">
	<input type="button" value="Close" class="closeModal" style="right:0;"/><br/>
	<fieldset>
	<legend>Manager informations</legend>
	Name : <input type="text" name="name" value="{{name}}" id="manager-username" disabled="disabled"/><br/>
	</fieldset>
	
	<fieldset>
	<legend>Manager users</legend>
		<div id="usersToLink" style="height:150px;overflow-y:auto;">
			<!-- Filled by ajax  -->
		</div>
	</fieldset>
	<br/>
	<input type="button" value="Send" class="updateManager"/>
	<span id="managers_errors_update" style="display:none;font-weight:bold;color:red;"></span>
</script>
 
 <div id="manager_admin_container">
	<div id="manager_admin_buttons">
		<input type="button" class="createButton" value="Add"/>
		<input type="button" class="editButton" value="Edit" disabled="disabled"/>
		<input type="button" class="deleteButton" value="Delete" disabled="disabled"/>
		<span id="manager_notifications" style="display:none;color:green;"></span>
	</div>
	<table id="manager_admin_table" style="cursor:pointer;width:100%">
		<tr><th>Name</th><th>Members</th></tr>
		
	</table>
</div>