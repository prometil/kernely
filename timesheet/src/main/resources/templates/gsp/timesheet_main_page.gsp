<script type="text/javascript" src="/js/timesheet_main.js"></script>

<script type="text/html" id="locale-lang"><%= i18n.t("locale_lang") %></script>

<script type="text/html" id="locale-country"><%= i18n.t("locale_country") %></script>

<script type="text/html" id="project-title-template"><%= i18n.t("timesheet_project_title") %></script>

<script type="text/html" id="delete-button-template"><input type="button" class="deleteButton" value="<%= i18n.t("delete") %>"/></script>

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

<div id="timesheet-main">
	<div id="weekSelector">
	</div>
	<select id="project-select"></select>
	<input type="button" id="add-project-button" value="<%= i18n.t("timesheet_add_project") %>"/>
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
		</table>
	</div>
	<div id="timePicker">
		<!-- Time picker to set time into days and projects -->
	</div>
</div>