<div id="project_list_header">
	<%= i18n.t("project_list_title") %>
</div>
<div id="project_list_container">

	<%for (proj in project){%> 
		<div id="project-<%print proj.id%>" class="proj">
			<div id="project-image">
				<img class="project-icon" src="/images/<%print proj.icon%>"/>
			</div>
			<div id="project-info">
				<span id="project_name" class="span_project"><a href="/project/<%print proj.name%>"><%print proj.name%></a></span>		
			</div>
		</div>
	<%}%>	
</div>