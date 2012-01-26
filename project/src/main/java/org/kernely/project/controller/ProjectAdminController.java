package org.kernely.project.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.project.dto.ProjectCreationRequestDTO;
import org.kernely.project.dto.ProjectDTO;
import org.kernely.project.service.ProjectService;

import com.google.inject.Inject;

/**
 * Admin controller for project
 */
@Path("/admin/projects")
public class ProjectAdminController extends AbstractController {
	@Inject
	private TemplateRenderer templateRenderer;

	@Inject
	private UserService userService;

	@Inject
	private ProjectService projectService;

	/**
	 * Set the template
	 * 
	 * @return the page admin
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML })
	public Response getPluginAdminPanel() {
		Response page;
		if (userService.currentUserIsAdministrator()) {
			page = ok(templateRenderer.create("/templates/gsp/project_admin.gsp").addCss("/css/admin.css").addCss("/css/project_admin.css").withLayout(TemplateRenderer.ADMIN_LAYOUT));
		} else {
			page = ok(templateRenderer.create("/templates/gsp/home.gsp"));
		}
		return page;
	}

	/**
	 * Get all existing projects in the database
	 * 
	 * @return A list of all DTO associated to the existing projects in the
	 *         database
	 */
	@GET
	@Path("/all")
	@Produces({MediaType.APPLICATION_JSON})
	public List<ProjectDTO> displayAllProjects() {
		if (userService.currentUserIsAdministrator()) {
			log.debug("Call to GET on all projects");
			return projectService.getAllProjects();
		}
		return null;
	}
	
	/**
	 * Create a new project with the given informations
	 * @param project The DTO containing all informations about the new project
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/create")
	@Produces({MediaType.APPLICATION_JSON})
	public String create(ProjectCreationRequestDTO project)
	{
		if (userService.currentUserIsAdministrator()){
			try{
				if(project.id==0){
					projectService.createProject(project);
				}
				else{
					projectService.updateProject(project);
				}
				return "{\"result\":\"ok\"}";
			} catch (IllegalArgumentException iae) {
				log.debug(iae.getMessage());
				return "{\"result\":\""+iae.getMessage()+"\"}";
			}
		}
		return null;
	}

	/**
	 * Delete the project which has the id 'id'
	 * @param id The id of the project to delete
	 * @return The result of the operation
	 */
	@GET
	@Path("/delete/{id}")
	@Produces( { MediaType.TEXT_HTML })
	public String deleteProject(@PathParam("id") int id){
		if (userService.currentUserIsAdministrator()){
			projectService.deleteProject(id);
			return "Ok";
		}
		return null;
	}
	
}
