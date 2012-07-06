package org.kernely.project.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.kernely.controller.AbstractController;
import org.kernely.core.dto.GroupDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.GroupService;
import org.kernely.core.service.PermissionService;
import org.kernely.core.service.UserService;
import org.kernely.project.dto.OrganizationDTO;
import org.kernely.project.dto.ProjectCreationRequestDTO;
import org.kernely.project.dto.ProjectDTO;
import org.kernely.project.dto.ProjectRightsUpdateRequestDTO;
import org.kernely.project.dto.RightOnProjectDTO;
import org.kernely.project.model.Project;
import org.kernely.project.service.OrganizationService;
import org.kernely.project.service.ProjectService;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * Admin controller for project
 */
@Path("/admin/projects")
public class ProjectAdminController extends AbstractController {
	@Inject
	private SobaTemplateRenderer templateRenderer;

	@Inject
	private AbstractConfiguration configuration;

	@Inject
	private UserService userService;

	@Inject
	private ProjectService projectService;

	@Inject
	private PermissionService permissionService;

	@Inject
	private GroupService groupService;

	@Inject
	private OrganizationService organizationService;

	/**
	 * Set the template
	 * 
	 * @return the page admin
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public Response getPluginAdminPanel() {
		return Response.ok(templateRenderer.render("templates/project_admin.html")).build();
	}

	/**
	 * Get all existing projects in the database
	 * 
	 * @return A list of all DTO associated to the existing projects in the database
	 */
	@GET
	@Path("/all")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces( { MediaType.APPLICATION_JSON })
	public List<ProjectDTO> displayAllProjects() {
		log.debug("Call to GET on all projects");
		return projectService.getAllProjects();
	}

	/**
	 * Create the list for the combobox
	 * 
	 * @return a json which contains all the organization
	 */
	@GET
	@Path("/combobox")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces( { MediaType.APPLICATION_JSON })
	public List<OrganizationDTO> getLists() {
		log.debug("Call to GET on all organization");
		return organizationService.getAllOrganizations();
	}

	/**
	 * Create a new project with the given informations
	 * 
	 * @param project
	 *            The DTO containing all informations about the new project
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/create")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces( { MediaType.APPLICATION_JSON })
	public String create(ProjectCreationRequestDTO project) {
		try {
			if (project.id == 0) {
				projectService.createProject(project);
			} else {
				projectService.updateProject(project);
			}
			return "{\"result\":\"ok\"}";
		} catch (IllegalArgumentException iae) {
			log.debug(iae.getMessage());
			return "{\"result\":\"" + iae.getMessage() + "\"}";
		}
	}

	/**
	 * Delete the project which has the id 'id'
	 * 
	 * @param id
	 *            The id of the project to delete
	 * @return The result of the operation
	 */
	@GET
	@Path("/delete/{id}")
	@Produces( { MediaType.TEXT_HTML })
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public Response deleteProject(@PathParam("id") int id) {
		projectService.deleteProject(id);
		return Response.ok().build();
	}

	/**
	 * Get all users associated to the project which has the id 'id'
	 * 
	 * @param id
	 *            The id of the project
	 * @return A list of all DTO associated to the users contained in this project
	 */
	@GET
	@Path("/{id}/users")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces( { MediaType.APPLICATION_JSON })
	public List<UserDTO> getProjectUsers(@PathParam("id") int id) {
		return projectService.getProjectUsers(id);
	}

	/**
	 * Get the project associated to a specific id.
	 * 
	 * @param id
	 *            The id of the project
	 * @return The project DTO
	 */
	@POST
	@Path("/{id}")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces( { MediaType.APPLICATION_JSON })
	public ProjectDTO getProject(@PathParam("id") int id) {
		return projectService.getProject(id);
	}

	/**
	 * Upload a specified file
	 * 
	 * @param uploadedInputStream
	 *            the InputStream corresponding to the file uploaded
	 * @param fileDetail
	 *            the informations about file uploaded
	 */
	@POST
	@Path("/upload/{name}")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces( { MediaType.TEXT_HTML })
	public Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail,
			@PathParam("name") String projectName) {
		if (fileDetail.getFileName().equals("")) {
			if (userService.currentUserIsAdministrator()) {
				return Response.ok(templateRenderer.render("templates/project_admin.html")).build();
			} else {
				return Response.status(Status.FORBIDDEN).build();
			}
		}
		// get extension
		String[] extension = fileDetail.getFileName().split("\\.");
		if (extension.length < 2) {
			throw new IllegalArgumentException("the file need an extansion");
		}

		SecureRandom random = new SecureRandom();
		String fileName = new BigInteger(130, random).toString(32) + "." + extension[extension.length - 1];
		String prefix = configuration.getString("workpath.url");
		String uploadedFileLocation = prefix + "/images/" + fileName; // /core/src/main/resources
		try {
			FileUtils.copyInputStreamToFile(uploadedInputStream, new File(uploadedFileLocation));
		} catch (IOException e) {
			log.debug(e.getMessage());
		}

		// up to date the database

		projectService.updateProjectIcon(projectName, fileName);

		// get the dto modified
		return Response.ok(templateRenderer.render("templates/project_admin.html")).build();
	}

	/**
	 * Gets all rights associated to a project
	 * 
	 * @param id
	 *            The id of the project
	 * @return A JSON String containing the rights of all users for the project
	 */
	@GET
	@Path("/rights/{id}")
	@Produces( { MediaType.APPLICATION_JSON })
	public String getStreamRights(@PathParam("id") int id) {
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{\"permission\":[");
		List<UserDTO> allUsers = userService.getAllUsers();
		List<GroupDTO> allGroups = groupService.getAllGroups();

		for (UserDTO user : allUsers) {
			boolean contributor = permissionService.userHasPermission((int) user.id, false, Project.RIGHT_CONTRIBUTOR, Project.PROJECT_RESOURCE, id);
			boolean manager = permissionService.userHasPermission((int) user.id, false, Project.RIGHT_PROJECTMANAGER, Project.PROJECT_RESOURCE, id);
			boolean client = permissionService.userHasPermission((int) user.id, false, Project.RIGHT_CLIENT, Project.PROJECT_RESOURCE, id);

			if (contributor) {
				jsonBuilder.append("{\"user\":\"");
				jsonBuilder.append(user.id + "\"");
				jsonBuilder.append(",\"right\":\"contributor\"},");
			}
			if (manager) {
				jsonBuilder.append("{\"user\":\"");
				jsonBuilder.append(user.id + "\"");
				jsonBuilder.append(",\"right\":\"project_manager\"},");
			}
			if (client) {
				jsonBuilder.append("{\"user\":\"");
				jsonBuilder.append(user.id + "\"");
				jsonBuilder.append(",\"right\":\"client\"},");
			}
		}
		for (GroupDTO group : allGroups) {
			boolean contributor = permissionService.groupHasPermission((int) group.id, Project.RIGHT_CONTRIBUTOR, Project.PROJECT_RESOURCE, id);
			boolean manager = permissionService.groupHasPermission((int) group.id, Project.RIGHT_PROJECTMANAGER, Project.PROJECT_RESOURCE, id);
			boolean client = permissionService.groupHasPermission((int) group.id, Project.RIGHT_CLIENT, Project.PROJECT_RESOURCE, id);

			if (contributor) {
				jsonBuilder.append("{\"group\":\"");
				jsonBuilder.append(group.id + "\"");
				jsonBuilder.append(",\"right\":\"contributor\"},");
			}
			if (manager) {
				jsonBuilder.append("{\"group\":\"");
				jsonBuilder.append(group.id + "\"");
				jsonBuilder.append(",\"right\":\"project_manager\"},");
			}
			if (client) {
				jsonBuilder.append("{\"group\":\"");
				jsonBuilder.append(group.id + "\"");
				jsonBuilder.append(",\"right\":\"client\"},");
			}
		}
		String json = jsonBuilder.toString();
		if (json.charAt(json.length() - 1) == ',') {
			json = json.substring(0, json.length() - 1);
		}
		json += "]}";
		log.debug(json);
		return json;
	}

	/**
	 * Update right on a project
	 * 
	 */
	@POST
	@Path("/updaterights")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces( { MediaType.APPLICATION_JSON })
	public Response updateRights(ProjectRightsUpdateRequestDTO request) {
		log.debug("Update {} rights of the project : {}", request.rights.size(), request.projectid);

		// delete permissions on the project for the user
		List<UserDTO> allUsers = userService.getEnabledUsers();
		for (UserDTO user : allUsers) {
			permissionService.ungrantPermission((int) user.id, Project.RIGHT_CONTRIBUTOR, Project.PROJECT_RESOURCE, request.projectid);
			permissionService.ungrantPermission((int) user.id, Project.RIGHT_PROJECTMANAGER, Project.PROJECT_RESOURCE, request.projectid);
			permissionService.ungrantPermission((int) user.id, Project.RIGHT_CLIENT, Project.PROJECT_RESOURCE, request.projectid);
		}

		for (RightOnProjectDTO right : request.rights) {
			if (right.idType.equals("user")) {
				log.debug("Right {} for user with id {}", right.permission, right.id);
				boolean correct = permissionService.userHasPermission(right.id, false, right.permission, Project.PROJECT_RESOURCE, request.projectid);
				if (!correct) {
					// The right requested is not the same than the existing
					// right :

					if (!right.permission.equals("nothing")) {
						// Add the requested permission
						permissionService.grantPermission(right.id, right.permission, Project.PROJECT_RESOURCE, request.projectid);
						if (right.permission.equals("project_manager")) {
							log.debug("User {} has now role Project manager.",right.id);
							userService.addRoleToUser(right.id, Role.ROLE_PROJECTMANAGER);
						}
					}
				}
			}
		}
		return Response.ok().build();
	}
}
