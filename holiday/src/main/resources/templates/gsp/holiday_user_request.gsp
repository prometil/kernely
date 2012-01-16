<script type="text/javascript" src="/js/holiday_user_request.js"></script>

<script type="text/html" id="cancel-ask-template">
	<%=i18n.t("canceled_ask")%>
</script>

<script type="text/html" id="holiday-canceled-template">
 	<%=i18n.t("canceled")%>
 </script>

 <script type="text/html" id="status-denied-template">
 	<%=i18n.t("denied")%>
 </script>

<script type="text/html" id="status-accepted-template">
 	<%=i18n.t("accepted")%>
 </script>
 
<script type="text/html" id="status-accepted-or-denied-template">
 	 	<td>{{requesterComment}}</td><td>{{beginDate}}</td><td>{{endDate}}</td>
 </script>
 
 <div id="main_title">
	<%=i18n.t("title_user_request")%>
</div>
 
<div id="request-manager-main">
	<div id="holiday_button_container">
		<input type="button" id="button_canceled" value="<%=i18n.t("canceled")%>" disabled="disabled"/> 
	</div>
	<table id="user_pending_request_table">
		  <caption><b><u><%=i18n.t("title_table_1")%></u></b></caption>
		 <th><%= i18n.t("requester_comment") %></th><th><%= i18n.t("begin_date")%></th><th><%= i18n.t("end_date")%></th><th></th>
	</table>
	<span id="holiday_notifications"></span>
	
	<table id="user_request_table">
	    <caption><b><u><%= i18n.t("title_table_2")%></u></b></caption>
		<th><%= i18n.t("manager") %></th><th><%= i18n.t("requester_comment") %></th><th><%= i18n.t("manager_comment") %></th><th><%= i18n.t("begin_date")%></th><th><%= i18n.t("end_date")%></th><th><%= i18n.t("status")%></th></tr>
	</table>
</div>
