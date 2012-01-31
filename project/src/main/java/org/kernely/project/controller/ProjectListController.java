package org.kernely.project.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.project.dto.ProjectDTO;
import org.kernely.project.service.ProjectService;

import com.google.inject.Inject;

/**
 * Admin controller for project
 */
@Path("/project")
public class ProjectListController extends AbstractController {
	@Inject
	private TemplateRenderer templateRenderer;

	@Inject
	private UserService userService;

	@Inject
	private ProjectService projectService;
	
	/**
	 * Set the template
	 * @return the page 
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response getHolidayRequestPage(){
		List<ProjectDTO> projDTO = projectService.getAllProjects();  
		return ok(templateRenderer.create("/templates/gsp/project_list.gsp").with("project",projDTO).addCss("/css/project_list.css"));
	}
	
	
	
}