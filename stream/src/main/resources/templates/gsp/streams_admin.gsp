<script type="text/javascript" src="/js/streams_admin.js"></script>

<div id="group_header" style=""></div>
<div id="mask" style="position:absolute;z-index:9000;background-color:#000;display:none;top:0;left:0;"></div>
<div id="modal_window" style=" position:absolute;width:440px;height:150px;display:none;z-index:9999;padding:20px;top:0;left:0;">
</div>

<script type="text/html" id="popup-stream-admin-template">
	<input type="button" value="Close" class="closeModal" style="right:0;"/><br/>
	<fieldset>
	<legend>Stream informations</legend>
	Name : <input type="text" name="name" value="{{name}}"/><br/>
	Category : <input type="text" name="category" value="{{category}}"/><br/>
	</fieldset>
	<br/>
	<input type="button" value="Send" class="sendStream"/>
</script>

<div id="stream_admin_container">
	<div id="stream_admin_buttons">
		<input type="button" class="createButton" value="Create"/>
		<input type="button" class="editButton" value="Edit" disabled="disabled"/>
		<input type="button" class="lockButton" value="Lock" disabled="disabled"/>
		<input type="button" class="unlockButton" value="Unlock" disabled="disabled"/>
		<span id="streams_notifications" style="display:none;color:green;"></span>
	</div>
	<table id="stream_admin_table" style="cursor:pointer;width:100%">
		<tr><th></th><th>Name</th><th>Category</th></tr>
		
	</table>
</div>