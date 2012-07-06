package org.kernely.project.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.kernely.controller.AbstractController;
import org.kernely.core.model.Role;
import org.kernely.core.service.UserService;
import org.kernely.menu.Menu;
import org.kernely.project.dto.ProjectDTO;
import org.kernely.project.model.Project;
import org.kernely.project.service.ProjectService;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * Controller for project
 */
@Path("/project")
public class ProjectController extends AbstractController {
	@Inject
	private SobaTemplateRenderer templateRenderer;

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
		Map<String,Object> bindings = new HashMap<String,Object>();
		if (userService.currentUserIsProjectManager()) {
			bindings.put("project", projectService.getAllProjects());
			return Response.ok(templateRenderer.render("templates/project_list.html",bindings)).build();
		} else {
			bindings.put("project", new ArrayList<ProjectDTO>());
			return Response.ok(templateRenderer.render("templates/project_list.html",bindings)).build();
		}
	}

	
	/**
	 * Display the page to manage the project.
	 */
	@GET
	@Path("manage")
	@Menu("projects")
	@RequiresRoles(value = { Role.ROLE_PROJECTMANAGER})
	@Produces({ MediaType.TEXT_HTML})
	public Response getManagedProjectsPage() {
		Map<String,Object> bindings = new HashMap<String,Object>();
		return Response.ok(templateRenderer.render("templates/project_management.html", bindings)).build();
	}
	
	/**
	 * Set the template for a specific project
	 */
	@GET
	@Path("/{name}")
	@Produces({ MediaType.TEXT_HTML})
	public Response getProjectPage(@PathParam("name")String projectName) {
		ProjectDTO projDTO = projectService.getProject(projectName);
		Map<String,Object> bindings = new HashMap<String,Object>();
		bindings.put("project", projDTO);
		return Response.ok(templateRenderer.render("templates/project_view.html",bindings)).build();	
	}
	
	/**
	 * Get the list of projects for the current user
	 */
	@GET
	@Path("/list/managed")
	@RequiresRoles(value = { Role.ROLE_PROJECTMANAGER})
	@Produces({ MediaType.APPLICATION_JSON})
	public List<ProjectDTO> getManagedProjects() {
		return projectService.getManagedProjects();
	}
	
	/**
	 * Get the list of projects for the current user
	 */
	@GET
	@Path("/list")
	@Produces({ MediaType.APPLICATION_JSON})
	public List<ProjectDTO> getCurrentUserProjects() {
		return projectService.getAllProjectsForUser(userService.getAuthenticatedUserDTO().id);	
	}
	
	/**
	 * Get the list of projects for the current user
	 */
	@GET
	@Path("/opened/list")
	@Produces({ MediaType.APPLICATION_JSON})
	public List<ProjectDTO> getCurrentUserOpenedProjects() {
		return projectService.getAllProjectsForUserWithStatus(userService.getAuthenticatedUserDTO().id, Project.STATUS_OPEN);
	}
	
	/**
	 * Get the list of projects linked to a specific organization
	 */
	@GET
	@Path("/orga")
	@Produces({ MediaType.APPLICATION_JSON})
	public List<ProjectDTO> getProjectsLinkedToOrganization(@QueryParam("organizationId") long organizationId) {
		return projectService.getProjectsLinkedToOrganization(organizationId);	
	}
	
	/**
	 * Changes the status of a project.
	 */
	@GET
	@Path("/status")
	@Produces({ MediaType.APPLICATION_JSON})
	public Response changeProjectStatus(@QueryParam("projectId") long projectId, @QueryParam("status") int status){
		projectService.updateProjectStatus(projectId, status);
		return Response.ok().build();
	}

}