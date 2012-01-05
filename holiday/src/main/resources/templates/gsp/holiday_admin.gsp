<script type="text/javascript" src="/js/holiday_admin.js"></script>
<script type="text/javascript" src="/js/farbtastic.js"></script>

<div id="holiday_header" style=""></div>

<div id="mask" style="position:absolute;z-index:9000;background-color:#000;display:none;top:0;left:0;"></div>

<div id="modal_window_holiday" style=" position:absolute;width:450px;height:300px;display:none;z-index:9999;padding:20px;top:0;left:0;">
</div>


<!-- Template for the create view  -->
<script type="text/html" id="popup-holiday-admin-create-template">
	<input type="button" value="Close" class="closeModal" id="modal_window_holiday" style="right:0;"/><br/>
	<fieldset style="height:210px;">
	<legend>Holiday informations</legend>
		<div style="width:225px;float:left;">
			Name: <input type="text" name="type" value="{{type}}" id="holiday-type" style="width:150px;"/><br/><br/>
			<input type="text" name="quantity" value="{{quantity}}" id="holiday-quantity" size="2" maxlength="4" /> days every
			<select name="unity" id="unity">
				<option value="12">month</option>
				<option value="1">year</option>
			</select>
			<br/>Effective month :
			<select name="effectivemonth" id="effectivemonth">
				<option value="12" selected="selected">All months</option>
				<option value="0">January</option>
				<option value="1">February</option>
				<option value="2">March</option>
				<option value="3">April</option>
				<option value="4">May</option>
				<option value="5">June</option>
				<option value="6">July</option>
				<option value="7">August</option>
				<option value="8">September</option>
				<option value="9">October</option>
				<option value="10">November</option>
				<option value="12">December</option>
			</select>
			<br/><input name="anticipated" type="checkbox">Can be taken with anticipation</input>
			<br/><br/><input type="text" id="color" name="color" value="#000000" />
		</div>
		<div id="colorpicker" style="width:195px;float:left"></div>
	</fieldset>

	<br/>
	<input type="button" value="Send" class="createHoliday"/>
	<span id="holidays_errors_create" style="display:none;font-weight:bold;color:red;"></span>
</script>

<!-- Template for the edit view  -->
<script type="text/html" id="popup-holiday-admin-update-template">
	<input type="button" value="Close" class="closeModal" id="modal_window_holiday" style="right:0;"/><br/>
	<fieldset style="height:210px;">
		<legend>Holiday informations</legend>
		<div style="width:225px;float:left;">
			Name: <input type="text" name="type" value="{{type}}" id="holiday-type" /><br/><br/>
			<div id="selected">
				<input type="text" name="quantity" value="{{quantity}}" id="holiday-quantity" size="2" maxlength="4" /> days every
			</div>
			<br/>Effective month :
			<select name="effectivemonth" id="effectivemonth">
				<option value="12" selected="selected">All months</option>
				<option value="0">January</option>
				<option value="1">February</option>
				<option value="2">March</option>
				<option value="3">April</option>
				<option value="4">May</option>
				<option value="5">June</option>
				<option value="6">July</option>
				<option value="7">August</option>
				<option value="8">September</option>
				<option value="9">October</option>
				<option value="10">November</option>
				<option value="11">December</option>
			</select>
			<br/><input id="anticipated" name="anticipated" type="checkbox">Can be taken with anticipation</input>
			<br/><br/><input type="text" id="color" name="color" value="{{color}}" />
		</div>
		<div id="colorpicker" style="width:195px;float:left"></div>
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