<script type="text/javascript" src="/js/holiday_request.js"></script>

<script type="text/html" id="balance-cell-template">
		<span style="font-weight:bold;">{{name}}</span><br/>
		Available : <span class="available-cpt">{{available}}</span>
</script>

<div id="request-main">  
	<div id="period">
		<label for="from">From </label><input type="text" id="from" name="from"/>
		<label for="to">to </label><input type="text" id="to" name="to"/>
		<input id="submitPeriod" type="button" value="Submit"/>
	</div>
	<div id="calendarRequest">
		<table id="calendarTable">
			<tr><th>Mo</th><th>Tu</th><th>We</th><th>Th</th><th>Fr</th></tr>
			<tbody id="calendarContent">
			<tbody>
		</table>
	</div>
	<div id="colorSelector">
		<!-- Color picker to color holidays -->
	</div>
</div>