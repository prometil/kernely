<script type="text/javascript" src="/js/timesheet_main.js"></script>

<script type="text/html" id="locale-lang"><%= i18n.t("locale_lang") %></script>

<script type="text/html" id="locale-country"><%= i18n.t("locale_country") %></script>

<script type="text/html" id="project-title-template"><%= i18n.t("timesheet_project_title") %></script>

<script type="text/html" id="confirm-remove-line-template"><%= i18n.t("timesheet_remove_line_confirm") %></script>

<script type="text/html" id="total-template"><%= i18n.t("timesheet_total") %></script>

<script type="text/html" id="expense-line-title-template"><%= i18n.t("expense_line_title") %></script>

<script type="text/html" id="delete-button-template"><input type="button" class="deleteButton" value="<%= i18n.t("delete") %>"/></script>

<script type="text/html" id="detail-template"><span class="editAmount">{{amount}}</span><img class="editButton" src="../images/edit.png"/></script>
<script type="text/html" id="detail-edit-template"><input type="text" class="editAmount" value="{{amount}}"/></script>

<div id="mask"></div>

<div id="modal_window_expense"></div>

<script type="text/html" id="time-amount-025">15 <%= i18n.t("timesheet_minutes") %></script>
<script type="text/html" id="time-amount-05">30 <%= i18n.t("timesheet_minutes") %></script>
<script type="text/html" id="time-amount-1">1 <%= i18n.t("timesheet_hour") %></script>
<script type="text/html" id="time-amount-2">2 <%= i18n.t("timesheet_hours") %></script>
<script type="text/html" id="time-amount-4">4 <%= i18n.t("timesheet_hours") %></script>
<script type="text/html" id="time-amount-6">6 <%= i18n.t("timesheet_hours") %></script>
<script type="text/html" id="time-amount-8">8 <%= i18n.t("timesheet_hours") %></script>

<script type="text/html" id="time-cell-template">
	<span id="time-cell-name">{{amount}}</span>
</script>

<script type="text/html" id="calendarSelector">
		<img class="minusWeek" alt="<" src="/images/previous.png"/>
		<span id="week_current" style="width:100px;">{{week}} ({{year}})</span>
		<img class="plusWeek" alt=">" src="/images/next.png"/>
</script>

<script type="text/html" id="week-selector-template">
	<%=i18n.t("timesheet_week_title")%> {{week}}
</script>

<script type="text/html" id="expense-window">
<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
<fieldset>
	<legend><%= i18n.t("expense-header-table") %></legend>
	<table id="expense-table">
		<tr>
			<th><%= i18n.t("expense_amount") %></th>
			<th><%= i18n.t("expense_type") %></th>
			<th><!-- Column for the edit button --></th>
			<th><!-- Column for the delete button --></th>
		</tr>
		<tbody id="expenses-lines">
		</tbody>
	</table>
	<div id="expense-form">
		<%= i18n.t("expense_amount") %>: <input type="text" name="amount" id="expense-amount"/>
		<select id="expense-type-select"></select>
		<input type="button" id="submit-expense" value="<%= i18n.t("create") %>"/><br/>
		<%= i18n.t("expense_comment") %><br/><textarea disabled="disabled" id="expense-comment"></textarea>
	</div>
</fieldset>
<input type="button" value="<%= i18n.t("ok") %>" class="create_expense"/>
<br/>
<span id="errors_message"></span>
</script>

<div id="timesheet-main">
	<div id="weekSelector">
	</div>
	<select class="timesheet-project-select" id="project-select"></select>
	<input type="button" id="add-project-button" value="<%= i18n.t("timesheet_add_project") %>"/><a href="/timesheet/view"><%= i18n.t("timesheet_view_month") %></a>
	<div id="timesheet-div">
		<table id="timesheet-table" border="0">
			<tr>
				<th></th>
				<th><%= i18n.t("day_1_short") %></th>
				<th><%= i18n.t("day_2_short") %></th>
				<th><%= i18n.t("day_3_short") %></th>
				<th><%= i18n.t("day_4_short") %></th>
				<th><%= i18n.t("day_5_short") %></th>
				<th><%= i18n.t("day_6_short") %></th>
				<th><%= i18n.t("day_7_short") %></th></tr>
			<tbody id="timesheet-content">
				<tr id="date-line"></tr>
			<tbody>
			<tbody>
			<tr id="columnTotalsRow">
				<td><%= i18n.t("timesheet_total") %></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</tbody>
			<tbody id="timesheet-expense">
				<tr id="expense-line"><td></td></tr>
			<tbody>
		</table>
	</div>
	
	<div id="timePicker">
		<!-- Time picker to set time into days and projects -->
	</div>
</div>