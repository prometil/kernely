package org.kernely.project.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
	private ProjectService projectService;
	
	@Inject 
	private UserService userService;

	/**
	 * Set the template
	 * 
	 * @return the page
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML })
	public Response getProjectListPage() {
		if (userService.currentUserIsProjectManager()) {
			List<ProjectDTO> projDTO = projectService.getAllProjects();
			return ok(templateRenderer.create("/templates/gsp/project_list.gsp").with("project", projDTO).addCss("/css/project_list.css"));
		}
		return ok(templateRenderer.create("/templates/gsp/project_list.gsp").with("project", null).addCss("/css/project_list.css"));
	}

	/**
	 * Set the template for a specific project
	 */
	@GET
	@Path("/{name}")
	@Produces({ MediaType.TEXT_HTML})
	public Response getProjectPage(@PathParam("name")String projectName) {
		ProjectDTO projDTO = projectService.getProject(projectName);
		return ok(templateRenderer.create("/templates/gsp/project_view.gsp").with("project", projDTO).addCss("/css/project_view.css"));	
	}
	
	
}