<script type="text/javascript" src="/js/holiday_manager_request.js"></script>

 <script type="text/html" id="status-denied-template">
 	<%=i18n.t("denied")%>
 </script>

<script type="text/html" id="status-accepted-template">
 	<%=i18n.t("accepted")%>
 </script>


<div id="main_title" style="text-align:center;font-size:16px;font-weight:bold">
	<%=i18n.t("title_manager_request")%>
</div>

<div id="request-manager-main">
	<table id="manager_pending_request_table" style="cursor:pointer;width:100%">
		  <caption><b><u><%=i18n.t("title_table_1")%></u></b></caption>
		  <th><%= i18n.t("from") %></th><th><%= i18n.t("requester_comment") %></th><th><%= i18n.t("begin_date")%></th><th><%= i18n.t("end_date")%></th>
	</table>
		
	<table id="manager_request_table" style="cursor:pointer;width:100%">
	    <caption><b><u><%= i18n.t("title_table_2")%></u></b></caption>
		<tr><th><%= i18n.t("from") %></th><th><%= i18n.t("requester_comment") %></th><th><%= i18n.t("manager_comment") %></th><th><%= i18n.t("begin_date")%></th><th><%= i18n.t("end_date")%></th><th><%= i18n.t("status")%></th></tr>
	</table>
</div>