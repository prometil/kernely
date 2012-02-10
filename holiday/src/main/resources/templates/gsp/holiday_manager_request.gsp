<script type="text/javascript" src="/js/holiday_manager_request.js"></script>


<div id="mask"></div>

<div id="modal_window_holiday_request">
</div>

 <script type="text/html" id="status-denied-template">
 	<%=i18n.t("denied")%>
 </script>

<script type="text/html" id="status-accepted-template">
 	<%=i18n.t("accepted")%>
</script>

<script type="text/html" id="holiday-accept-template">
 	<%=i18n.t("accepted_done")%>
 </script>
 
 <script type="text/html" id="holiday-deny-template">
 	<%=i18n.t("denied_done")%>
 </script>
 
<script type="text/html" id="status-accepted-or-denied-template">
 	 	<td>{{from}}</td><td>{{requesterComment}}</td><td>{{beginDate}}</td><td>{{endDate}}</td><td><input type="button" class="button_accepted" value="<%=i18n.t("accepted")%>"/></td><td><input type="button" class="button_denied" value="<%=i18n.t("denied")%>"/></td><td><input type="button" class="button_visualize" value="<%=i18n.t("visualize")%>"/></td>
</script>

<script type="text/html" id="balance-cell-template">
		<span id="balance-cell-name">{{name}}</span><br/>
		<%= i18n.t("holiday_balance_available") %>: <span class="available-cpt">{{available}}</span>
</script>

<script type="text/html" id="locale-lang"><%= i18n.t("locale_lang") %></script>

<script type="text/html" id="locale-country"><%= i18n.t("locale_country") %></script>

<script type="text/html" id="popup-accepted-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
	<fieldset>	
	<legend><%=i18n.t("accepted_ask")%></legend>
	<%= i18n.t("accepted_commentary") %>: <input type="text" id="comment" size="40"/><br/>
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("holiday_request_create") %>" class="validateHolidayRequest"/>
</script>

<script type="text/html" id="popup-visualize-request">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
	
	<div id="request-main">  
	<div id="calendarRequest">
		<table id="calendarTable" border="0">
			<tr>
				<th><%= i18n.t("day_1_short") %></th>
				<th><%= i18n.t("day_2_short") %></th>
				<th><%= i18n.t("day_3_short") %></th>
				<th><%= i18n.t("day_4_short") %></th>
				<th><%= i18n.t("day_5_short") %></th>
			</tr>
			<tbody id="calendarContent">
			<tbody>
		</table>
	</div>
	<input type="button" id="button_accepted" value="<%=i18n.t("accepted")%>"/> <input type="button" id="button_denied" value="<%=i18n.t("denied")%>" />
	
</script>

<script type="text/html" id="popup-denied-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal"/><br/>
	<fieldset>	
	<legend><%=i18n.t("denied_ask")%></legend>
	<%= i18n.t("denied_commentary") %>: <input type="text" id="comment" size="40"/><br/>
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("holiday_request_create") %>" class="denyHolidayRequest"/>
</script>

<div id="main_title">
	<%=i18n.t("title_manager_request")%>
</div>

<div id="request-manager-main">
	<hr/>
	<table id="manager_pending_request_table">
		  <caption><b><u><%=i18n.t("title_table_1")%></u></b></caption>
		  <tr><th><%= i18n.t("from") %></th><th><%= i18n.t("requester_comment") %></th><th><%= i18n.t("begin_date")%></th><th><%= i18n.t("end_date")%></th><th></th><th></th><th></th></tr>
	</table>
	<span id="holiday_notifications"></span>
	<hr/>	
	<table id="manager_request_table">
	    <caption><b><u><%= i18n.t("title_table_2")%></u></b></caption>
		<tr><th><%= i18n.t("from") %></th><th><%= i18n.t("requester_comment") %></th><th><%= i18n.t("manager_comment") %></th><th><%= i18n.t("begin_date")%></th><th><%= i18n.t("end_date")%></th><th><%= i18n.t("status")%></th></tr>
	</table>
</div>