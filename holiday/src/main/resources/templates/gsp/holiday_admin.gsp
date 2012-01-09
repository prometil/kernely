<script type="text/javascript" src="/js/holiday_admin.js"></script>
<script type="text/javascript" src="/js/farbtastic.js"></script>

<div id="holiday_header" style=""></div>

<div id="mask" style="position:absolute;z-index:9000;background-color:#000;display:none;top:0;left:0;"></div>

<div id="modal_window_holiday" style=" position:absolute;width:450px;height:300px;display:none;z-index:9999;padding:20px;top:0;left:0;">
</div>

<script type="text/html" id="table-header">
<tr>
	<th><%= i18n.t("holiday_name") %></th>
	<th><%= i18n.t("holiday_quantity") %></th>
	<th><%= i18n.t("holiday_each") %></th>
	<th><%= i18n.t("holiday_effective_month") %></th>
	<th><%= i18n.t("can_be_anticipated") %></th></tr>
</script>

<script type="text/html" id="confirm-holiday-deletion-template">
<%= i18n.t("confirm_holiday_deletion") %>
</script>

<script type="text/html" id="holiday-success-message-template">
<%= i18n.t("holiday_success_message") %>
</script>

<!-- Templates for months and year -->
<script type="text/html" id="month-template"><%= i18n.t("month") %></script>
<script type="text/html" id="year-template"><%= i18n.t("year") %></script>
<script type="text/html" id="0-month-template"><%= i18n.t("january") %></script>
<script type="text/html" id="1-month-template"><%= i18n.t("february") %></script>
<script type="text/html" id="2-month-template"><%= i18n.t("march") %></script>
<script type="text/html" id="3-month-template"><%= i18n.t("april") %></script>
<script type="text/html" id="4-month-template"><%= i18n.t("may") %></script>
<script type="text/html" id="5-month-template"><%= i18n.t("june") %></script>
<script type="text/html" id="6-month-template"><%= i18n.t("july") %></script>
<script type="text/html" id="7-month-template"><%= i18n.t("august") %></script>
<script type="text/html" id="8-month-template"><%= i18n.t("september") %></script>
<script type="text/html" id="9-month-template"><%= i18n.t("october") %></script>
<script type="text/html" id="10-month-template"><%= i18n.t("november") %></script>
<script type="text/html" id="11-month-template"><%= i18n.t("december") %></script>
<script type="text/html" id="12-month-template"><%= i18n.t("all_month") %></script>

<!-- Yes and no -->
<script type="text/html" id="yes-template"><%= i18n.t("yes") %></script>
<script type="text/html" id="no-template"><%= i18n.t("no") %></script>

<!-- Template for the create view  -->
<script type="text/html" id="popup-holiday-admin-create-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal" id="modal_window_holiday" style="right:0;"/><br/>
	<fieldset style="height:210px;">
	<legend><%= i18n.t("holiday_informations") %></legend>
		<div style="width:225px;float:left;">
			<%= i18n.t("holiday_name") %>: <input type="text" name="type" value="{{type}}" id="holiday-type" style="width:150px;"/><br/><br/>
			<input type="text" name="quantity" value="{{quantity}}" id="holiday-quantity" size="2" maxlength="4" /> <%= i18n.t("holiday_days_every") %>
			<select name="unity" id="unity">
				<option value="12"><%= i18n.t("month") %></option>
				<option value="1"><%= i18n.t("year") %></option>
			</select>
			<br/><%= i18n.t("holiday_effective_month") %>
			<select name="effectivemonth" id="effectivemonth">
				<option value="12" selected="selected"><%= i18n.t("all_month") %></option>
				<option value="0"><%= i18n.t("january") %></option>
				<option value="1"><%= i18n.t("february") %></option>
				<option value="2"><%= i18n.t("march") %></option>
				<option value="3"><%= i18n.t("april") %></option>
				<option value="4"><%= i18n.t("may") %></option>
				<option value="5"><%= i18n.t("june") %></option>
				<option value="6"><%= i18n.t("july") %></option>
				<option value="7"><%= i18n.t("august") %></option>
				<option value="8"><%= i18n.t("september") %></option>
				<option value="9"><%= i18n.t("october") %></option>
				<option value="10"><%= i18n.t("november") %></option>
				<option value="11"><%= i18n.t("december") %></option>
			</select>
			<br/><input name="anticipated" type="checkbox"><%= i18n.t("can_be_anticipated") %></input>
			<br/><br/><input type="text" id="color" name="color" value="#000000" />
		</div>
		<div id="colorpicker" style="width:195px;float:left"></div>
	</fieldset>

	<br/>
	<input type="button" value="<%= i18n.t("create") %>" class="createHoliday"/>
	<span id="holidays_errors_create" style="display:none;font-weight:bold;color:red;"></span>
</script>

<!-- Template for the edit view  -->
<script type="text/html" id="popup-holiday-admin-update-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal" id="modal_window_holiday" style="right:0;"/><br/>
	<fieldset style="height:210px;">
		<legend><%= i18n.t("holiday_informations") %></legend>
		<div style="width:225px;float:left;">
			<%= i18n.t("holiday_name") %>: <input type="text" name="type" value="{{type}}" id="holiday-type" /><br/><br/>
			<input type="text" name="quantity" value="{{quantity}}" id="holiday-quantity" size="2" maxlength="4" /> <%= i18n.t("holiday_days_every") %>
			<select name="unity" id="unity">
				<option value="12"><%= i18n.t("month") %></option>
				<option value="1"><%= i18n.t("year") %></option>
			</select>
			<br/><%= i18n.t("holiday_effective_month") %> :
			<select name="effectivemonth" id="effectivemonth">
				<option value="12" selected="selected"><%= i18n.t("all_month") %></option>
				<option value="0"><%= i18n.t("january") %></option>
				<option value="1"><%= i18n.t("february") %></option>
				<option value="2"><%= i18n.t("march") %></option>
				<option value="3"><%= i18n.t("april") %></option>
				<option value="4"><%= i18n.t("may") %></option>
				<option value="5"><%= i18n.t("june") %></option>
				<option value="6"><%= i18n.t("july") %></option>
				<option value="7"><%= i18n.t("august") %></option>
				<option value="8"><%= i18n.t("september") %></option>
				<option value="9"><%= i18n.t("october") %></option>
				<option value="10"><%= i18n.t("november") %></option>
				<option value="11"><%= i18n.t("december") %></option>
			</select>
			<br/><input id="anticipated" name="anticipated" type="checkbox"><%= i18n.t("can_be_anticipated") %></input>
			<br/><br/><input type="text" id="color" name="color" value="{{color}}" />
		</div>
		<div id="colorpicker" style="width:195px;float:left"></div>
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("update") %>" class="updateHoliday"/>
	<span id="holidays_errors_update" style="display:none;font-weight:bold;color:red;"></span>
</script>
 
 <div id="holiday_admin_container">
	<div id="holiday_admin_buttons">
		<input type="button" class="createButton" value="<%= i18n.t("create") %>"/>
		<input type="button" class="editButton" value="<%= i18n.t("edit") %>" disabled="disabled"/>
		<span id="holidays_notifications" style="display:none;color:green;"></span>
	</div>
	<table id="holiday_admin_table" style="cursor:pointer;width:100%">
		
		
	</ table>
</div>