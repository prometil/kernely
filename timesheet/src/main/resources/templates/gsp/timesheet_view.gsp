<script type="text/javascript" src="/js/timesheet_view.js"></script>

<script type="text/html" id="locale-lang"><%= i18n.t("locale_lang") %></script>

<script type="text/html" id="locale-country"><%= i18n.t("locale_country") %></script>

<script type="text/html" id="project-title-template"><%= i18n.t("timesheet_project_title") %></script>

<script type="text/html" id="confirm-remove-line-template"><%= i18n.t("timesheet_remove_line_confirm") %></script>

<script type="text/html" id="total-template"><%= i18n.t("timesheet_total") %></script>

<script type="text/html" id="detail-template"><span class="editAmount">{{amount}}</span></script>
<script type="text/html" id="detail-edit-template"><input type="text" class="editAmount" value="{{amount}}"/></script>

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
		<img class="minusMonth" alt="<" src="/images/previous.png"/>
		<span id="month_current" style="width:100px;">{{month}} ({{year}})</span>
		<img class="plusMonth" alt=">" src="/images/next.png"/>
</script>

<script type="text/html" id="month-selector-template">
	<%=i18n.t("timesheet_month_title")%> {{month}}
</script>

<script type="text/html" id="timesheet-table-template">
	<table id="timesheet-table-{{tableId}}" border="0">
	<tbody id="timesheet-content-{{tableId}}">
		<tr id="date-line-{{tableId}}"></tr>
	<tbody>
	<tbody>
	<tr id="columnTotalsRow-{{tableId}}">
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
	<tbody id="timesheet-expense-{{tableId}}">
		<tr id="expense-line-{{tableId}}"><td></td></tr>
	<tbody>
	</table>
</script>

<div id="timesheet-main">
	<div id="monthSelector">
	</div>
	<table>
		<tr>
		<th></th>
		<th><%= i18n.t("day_1_short") %></th>
		<th><%= i18n.t("day_2_short") %></th>
		<th><%= i18n.t("day_3_short") %></th>
		<th><%= i18n.t("day_4_short") %></th>
		<th><%= i18n.t("day_5_short") %></th>
		<th><%= i18n.t("day_6_short") %></th>
		<th><%= i18n.t("day_7_short") %></th></tr>
	</table>
	<div id="timesheet-div">
	</div>
	<input class="hidden" type="button" id="validate-month" value="<%= i18n.t("validate_timesheet") %>"/>
	<span class="hidden" id="month-validated-message"><%= i18n.t("timesheet_month_validated_message") %></span>
</div>

