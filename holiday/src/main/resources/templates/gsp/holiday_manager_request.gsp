<script type="text/javascript" src="/js/holiday_manager_request.js"></script>


<div id="mask" style="position:absolute;z-index:9000;background-color:#000;display:none;top:0;left:0;"></div>

<div id="modal_window_holiday_request" style=" position:absolute;width:440px;height:300px;display:none;z-index:9999;padding:20px;top:0;left:0;">
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
 	 	<td>{{from}}</td><td>{{requesterComment}}</td><td>{{beginDate}}</td><td>{{endDate}}</td>
 </script>

<script type="text/html" id="popup-accepted-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal" style="right:0;"/><br/>
	<fieldset>	
	<legend><%=i18n.t("accepted_ask")%></legend>
	<%= i18n.t("accepted_commentary") %>: <input type="text" id="comment" size="40"/><br/>
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("holiday_request_create") %>" class="validateHolidayRequest"/>
</script>

<script type="text/html" id="popup-denied-template">
	<input type="button" value="<%= i18n.t("close") %>" class="closeModal" style="right:0;"/><br/>
	<fieldset>	
	<legend><%=i18n.t("denied_ask")%></legend>
	<%= i18n.t("denied_commentary") %>: <input type="text" id="comment" size="40"/><br/>
	</fieldset>
	<br/>
	<input type="button" value="<%= i18n.t("holiday_request_create") %>" class="denyHolidayRequest"/>
</script>

<div id="main_title" style="text-align:center;font-size:16px;font-weight:bold">
	<%=i18n.t("title_manager_request")%>
</div>

<div id="request-manager-main">
	<div id="holiday_button_container">
		<input type="button" id="button_accepted" value="<%=i18n.t("accepted")%>" disabled="disabled"/> <input type="button" id="button_denied" value="<%=i18n.t("denied")%>" disabled="disabled"/>
	</div>
	<table id="manager_pending_request_table" style="cursor:pointer;width:100%">
		  <caption><b><u><%=i18n.t("title_table_1")%></u></b></caption>
		  <th><%= i18n.t("from") %></th><th><%= i18n.t("requester_comment") %></th><th><%= i18n.t("begin_date")%></th><th><%= i18n.t("end_date")%></th><th></th>
	</table>
	<span id="holiday_notifications" style="text-align:center;display:none;color:green;"></span>
	
	<table id="manager_request_table" style="cursor:pointer;width:100%">
	    <caption><b><u><%= i18n.t("title_table_2")%></u></b></caption>
		<tr><th><%= i18n.t("from") %></th><th><%= i18n.t("requester_comment") %></th><th><%= i18n.t("manager_comment") %></th><th><%= i18n.t("begin_date")%></th><th><%= i18n.t("end_date")%></th><th><%= i18n.t("status")%></th></tr>
	</table>
</div>