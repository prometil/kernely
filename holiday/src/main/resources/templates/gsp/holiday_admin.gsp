<script type="text/javascript" src="/js/holiday_admin.js"></script>

<div id="holiday_header" style=""></div>

<div id="mask" style="position:absolute;z-index:9000;background-color:#000;display:none;top:0;left:0;"></div>

<div id="modal_window_holiday" style=" position:absolute;width:440px;height:300px;display:none;z-index:9999;padding:20px;top:0;left:0;">
</div>


<!-- Template for the create view  -->
<script type="text/html" id="popup-holiday-admin-create-template">
	<input type="button" value="Close" class="closeModal" id="modal_window_holiday" style="right:0;"/><br/>
	<fieldset>
	<legend>Holiday informations</legend>
		Type : <input type="text" name="type" value="{{type}}" id="holiday-type" /><br/>
		Frequency : <input type="text" name="frequency" value="{{frequency}}" id="holiday-frequency" /> by : 
		<select name="unity" id="unity">
			<option value="week">week</opton> 
			<option value="month">month</opton> 
			<option value="year">year</opton>
		</select> 
	</fieldset>

	<br/>
	<input type="button" value="Send" class="createHoliday"/>
	<span id="holidays_errors_create" style="display:none;font-weight:bold;color:red;"></span>
</script>

<!-- Template for the edit view  -->
<script type="text/html" id="popup-holiday-admin-update-template">
	<input type="button" value="Close" class="closeModal" id="modal_window_holiday" style="right:0;"/><br/>
	<fieldset>
	<legend>Holiday informations</legend>
	Type : <input type="text" name="type" value="{{type}}" id="holiday-type" /><br/>
	<div id="selected">Frequency : <input type="text" name="frequency" value="{{frequency}}" id="holiday-frequency" /> by : 	</div>
	</fieldset>
	<br/>
	<input type="button" value="Send" class="updateHoliday"/>
	<span id="holidays_errors_update" style="display:none;font-weight:bold;color:red;"></span>
</script>
 
 <div id="holiday_admin_container">
	<div id="holiday_admin_buttons">
		<input type="button" class="createButton" value="Add"/>
		<input type="button" class="editButton" value="Edit" disabled="disabled"/>
		<input type="button" class="deleteButton" value="Delete" disabled="disabled"/>
		<span id="holiday_notifications" style="display:none;color:green;"></span>
	</div>
	<table id="holiday_admin_table" style="cursor:pointer;width:100%">
		
		
	</ table>
</div>