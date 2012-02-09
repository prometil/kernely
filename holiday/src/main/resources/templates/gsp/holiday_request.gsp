<script type="text/javascript" src="/js/holiday_request.js"></script>

<script type="text/html" id="balance-cell-template">
		<span id="balance-cell-name">{{name}}</span><br/>
		<%= i18n.t("holiday_balance_available") %>: <span class="available-cpt">{{available}}</span>
</script>

<script type="text/html" id="balance-unlimited-cell-template">
	<span id="balance-cell-name">{{name}}</span>
</script>

<script type="text/html" id="locale-lang"><%= i18n.t("locale_lang") %></script>

<script type="text/html" id="locale-country"><%= i18n.t("locale_country") %></script>


<div id="request-main">  
	<div id="period">
		<label for="from"><%= i18n.t("from_date") %> </label><input readonly="readonly" type="text" id="from" name="from"/>
		<label for="to"> <%= i18n.t("to_date") %> </label><input readonly="readonly" type="text" id="to" name="to"/>
		<input id="submitPeriod" type="button" value="<%= i18n.t("generate") %>"/>
	</div>
	<div id="calendarRequest">
		<div id="requester-comment-div">
			<textarea id="requester-comment"></textarea>
			<input type="button" id="validate-holidays" value="Submit"/>
		</div>
		<table id="calendarTable" border="0">
			<tr>
				<th><%= i18n.t("day_1_short") %></th>
				<th><%= i18n.t("day_2_short") %></th>
				<th><%= i18n.t("day_3_short") %></th>
				<th><%= i18n.t("day_4_short") %></th>
				<th><%= i18n.t("day_5_short") %></th></tr>
			<tbody id="calendarContent">
			<tbody>
		</table>
	</div>
	<div id="colorSelector">
		<!-- Color picker to color holidays -->
	</div>
</div>