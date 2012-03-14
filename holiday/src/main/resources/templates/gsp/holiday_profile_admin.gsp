<script type="text/javascript" src="/js/holiday_profile_admin.js"></script>

<div id="holiday_header"></div>

<div id="mask"></div>

<div id="modal_window_holiday"></div>

<script type="text/html" id="table-header">
<tr>
	<th><%= i18n.t("holiday_profile_name") %></th>
	<th><%= i18n.t("holiday_profile_description") %></th>
	<th><%= i18n.t("holiday_profile_users") %></th>
</tr>
</script>

<script type="text/html" id="confirm-holiday-profile-deletion-template">
<%= i18n.t("confirm_holiday_profile_deletion") %>
</script>

<script type="text/html" id="confirm-holiday-type-deletion-template">
<%= i18n.t("confirm_holiday_type_deletion") %>
</script>

<script type="text/html" id="popup-holiday-profile-error-name-template">
<%= i18n.t("holiday_profile_error_name") %>
</script>

<script type="text/html" id="popup-holiday-profile-error-no-type-template">
<%= i18n.t("holiday_profile_no_type_error") %>
</script>

<script type="text/html" id="update-button-template"><%= i18n.t("update") %></script>

<script type="text/html" id="holiday-success-message-template">
<%= i18n.t("holiday_success_message") %>
</script>

<!-- Templates for months and year -->
<script type="text/html" id="month-template"><%= i18n.t("month") %></script>
<script type="text/html" id="year-template"><%= i18n.t("year") %></script>
<script type="text/html" id="1-month-template"><%= i18n.t("january") %></script>
<script type="text/html" id="2-month-template"><%= i18n.t("february") %></script>
<script type="text/html" id="3-month-template"><%= i18n.t("march") %></script>
<script type="text/html" id="4-month-template"><%= i18n.t("april") %></script>
<script type="text/html" id="5-month-template"><%= i18n.t("may") %></script>
<script type="text/html" id="6-month-template"><%= i18n.t("june") %></script>
<script type="text/html" id="7-month-template"><%= i18n.t("july") %></script>
<script type="text/html" id="8-month-template"><%= i18n.t("august") %></script>
<script type="text/html" id="9-month-template"><%= i18n.t("september") %></script>
<script type="text/html" id="10-month-template"><%= i18n.t("october") %></script>
<script type="text/html" id="11-month-template"><%= i18n.t("november") %></script>
<script type="text/html" id="12-month-template"><%= i18n.t("december") %></script>
<script type="text/html" id="0-month-template"><%= i18n.t("all_month") %></script>

<!-- Yes and no -->
<script type="text/html" id="yes-template"><%= i18n.t("yes") %></script>
<script type="text/html" id="no-template"><%= i18n.t("no") %></script>

<!-- Template for holiday types lines-->
<script type="text/html" id="holiday-type-line-template">
	<td>
		<input type="text" name="name" value="{{name}}" class="holiday-name"/>
	</td>
	<td>
		<input type="checkbox" name="unlimited" class="holiday-unlimited">
	</td>
	<td>
		<input type="text" name="quantity" value="{{quantity}}" class="holiday-quantity" size="2" maxlength="4" />
	</td>
	<td>
		<%= i18n.t("holiday_days_every") %>
	</td>
	<td>
		<select name="unity" class="holiday-unity"><option value="12"><%= i18n.t("month") %></option><option value="1"><%= i18n.t("year") %></option></select>
	</td>
	<td>
		<select name="effectivemonth" class="holiday-effectivemonth">
			<option value="0" selected="selected"><%= i18n.t("all_month") %></option>
			<option value="1"><%= i18n.t("january") %></option>
			<option value="2"><%= i18n.t("february") %></option>
			<option value="3"><%= i18n.t("march") %></option>
			<option value="4"><%= i18n.t("april") %></option>
			<option value="5"><%= i18n.t("may") %></option>
			<option value="6"><%= i18n.t("june") %></option>
			<option value="7"><%= i18n.t("july") %></option>
			<option value="8"><%= i18n.t("august") %></option>
			<option value="9"><%= i18n.t("september") %></option>
			<option value="10"><%= i18n.t("october") %></option>
			<option value="11"><%= i18n.t("november") %></option>
			<option value="12"><%= i18n.t("december") %></option>
		</select>
	</td>
	<td>
		<input class="holiday-anticipated" name="anticipated" type="checkbox"/>
	</td>
	<td>
		<input class="holiday-color" type="text" name="color" size="6" maxlength="7" value="{{color}}" />
	</td>
	<td>
		<input type="button" value="<%= i18n.t("ok") %>" class="edit_button"/>
	</td>
	<td>
		<input type="button" value="<%= i18n.t("delete") %>" class="delete_button"/>
	</td>

</script>

<!-- Template for the create view  -->
<script type="text/html" id="popup-holiday-admin-create-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
	<%= i18n.t("holiday_profile_name") %>: <input value="{{profilename}}" type="input" id="holiday-profile-name"/>
	<div id="holiday-table-div">
		<table id="holiday-types-list-table">
			<tr>
				<th><%= i18n.t("holiday_name") %></th>
				<th><%= i18n.t("unlimited_holidays") %></th>
				<th colspan="3"><%= i18n.t("holiday_gain") %></th>
				<th><%= i18n.t("holiday_effective_month") %></th>
				<th><%= i18n.t("can_be_anticipated") %></th>
				<th><%= i18n.t("holiday_color") %></th>
				<th></th>
			</tr>
		</table>
	</div>
	<input type="button" value="New type" id="newHolidayButton"/>
	<input type="button" value="<%= i18n.t("create") %>" id="createHolidayProfile"/>
	<span id="holidays_errors_create"></span>
</script>

<!-- Template for edit users view  -->
<script type="text/html" id="popup-holiday-admin-users-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
	Users for {{name}}
	<div id="holiday-users-boxes">
		<div id="out-listbox-div">

		</div>
		<div id="in-out-buttons">
			<input type="button" value="==>" id="addUserButton"/>
			<br/>
			<input type="button" value="<==" id="removeUserButton"/>
		</div>
		<div id="in-listbox-div">
		
		</div>
	</div>
	<br/>
	<input type="button" value="<%= i18n.t("update") %>" id="updateUsersButton"/>
</script>
 
 <div id="holiday_admin_container">
	<div id="holiday_admin_buttons">
		<input type="button" class="createButton" value="<%= i18n.t("create") %>"/>
		<input type="button" class="editButton" value="<%= i18n.t("edit") %>" disabled="disabled"/>
		<input type="button" class="usersButton" value="<%= i18n.t("holiday_profile_users") %>" disabled="disabled"/>
		<span id="holidays_notifications"></span>
	</div>
	<table id="holiday_admin_table">
		
	</table>
</div>