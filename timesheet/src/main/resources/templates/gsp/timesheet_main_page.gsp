<script type="text/javascript" src="/js/timesheet_main.js"></script>

<script type="text/html" id="locale-lang"><%= i18n.t("locale_lang") %></script>

<script type="text/html" id="locale-country"><%= i18n.t("locale_country") %></script>

<script type="text/html" id="project-title-template"><%= i18n.t("timesheet_project_title") %></script>

<script type="text/html" id="time-cell-template">
	<span id="time-cell-name">{{amount}}</span><br/>
</script>

<div id="timesheet-main">
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