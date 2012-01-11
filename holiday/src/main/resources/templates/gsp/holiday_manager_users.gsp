<script type="text/javascript" src="/js/holiday_manager_users.js"></script>

<script type="text/html" id="balance-cells-legend">
	<span style="font-weight:bold;">{{name}}</span>
</script>

<script type="text/html" id="calendarSelector">
		<img class="minusMonth" src="/images/previous.png"/>
		<span id="month_current" style="width:100px;">{{month}}</span>
		<img class="plusMonth" src="/images/next.png"/>
</script>

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

<div id="main-manager-page-content">
	<div id="monthSelector">
	</div>
	<div id="main-manager-table-content">
		<table id="usersHoliday" cellspacing="0">
			
		</table>
	</div>
	
	<div id="color-legend">
	
	</div>
</div>