<script type="text/javascript" src="/js/holiday_request.js"></script>

<script type="text/html" id="balance-cell-template">
		<span id="balance-cell-name">{{name}}</span><br/>
		Available : <span class="available-cpt">{{available}}</span>
</script>

<div id="request-main">  
	<div id="period">
		<label for="from">From </label><input readonly="readonly" type="text" id="from" name="from"/>
		<label for="to">to </label><input readonly="readonly" type="text" id="to" name="to"/>
		<input id="submitPeriod" type="button" value="Generate"/>
	</div>
	<div id="calendarRequest">
		<div id="requester-comment-div">
			<textarea id="requester-comment"></textarea>
			<input type="button" id="validate-holidays" value="Submit"/>
		</div>
		<table id="calendarTable" border="0">
			<tr><th>Mo</th><th>Tu</th><th>We</th><th>Th</th><th>Fr</th></tr>
			<tbody id="calendarContent">
			<tbody>
		</table>
	</div>
	<div id="colorSelector">
		<!-- Color picker to color holidays -->
	</div>
</div>