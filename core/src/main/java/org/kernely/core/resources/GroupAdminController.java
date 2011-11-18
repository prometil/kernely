package org.kernely.core.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kernely.core.dto.GroupDTO;
import org.kernely.core.service.user.GroupService;
import org.kernely.core.template.TemplateRenderer;

import com.google.inject.Inject;

@Path("/admin/groups")
public class GroupAdminController extends AbstractController {

	@Inject
	private TemplateRenderer templateRenderer;
	
	@Inject
	private GroupService groupService;
	
	@GET
	@Path("/all")
	@Produces({"application/json"})
	public List<GroupDTO> displayAllGroups()
	{
		log.debug("Call to GET on all users");
		List<GroupDTO> groups = groupService.getAllGroups();
		return groups;
	}
	
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String displayPage()
	{
		return templateRenderer.create("/templates/gsp/administration/group_admin.gsp").withoutLayout().render() ;
	}
}
