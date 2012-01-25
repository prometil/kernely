<h1><%= i18n.t("holiday_main_title") %></h1>
<div id="holiday_main_page_container">
	<a href="/holiday/request"><%= i18n.t("holiday_request_link") %></a><br/><br/>
	<a href="/holiday/users/request"><%= i18n.t("holiday_request_view_link") %></a><br/><br/>
	<% if (manager != ""){ %>
		<a href="/holiday/managers/request"><%= i18n.t("holiday_manage_requests_link") %></a><br/><br/>
		<a href="/holiday/manager/users"><%= i18n.t("holiday_requests_all_link") %></a><br/><br/>
	<% } %>
	<% if (human_resource != ""){%>
		<a href="/holiday/human/resource"> <%= i18n.t("holiday_human_resource_link")%> </a> <br/><br/>
	<% } %>
</div>