package org.kernely.user.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kernely.core.resources.AbstractController;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.user.dto.GroupDTO;
import org.kernely.user.service.GroupService;

import com.google.inject.Inject;

@Path("/group")
public class GroupController extends AbstractController{
	@Inject
	private TemplateRenderer templateRenderer;
	
	@Inject
	private GroupService groupService;
	
	/**
	 * Display the list of groups.
	 * @return The html content to display the list.
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String getText()
	{
		log.debug("Call to GET on all groups");
		List<GroupDTO> groups = groupService.getAllGroups();
		return templateRenderer.create("/templates/gsp/groups.gsp").with("groups", groups).render() ;
	}
}
