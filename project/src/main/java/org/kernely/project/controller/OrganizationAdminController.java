package org.kernely.project.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.kernely.controller.AbstractController;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.project.dto.OrganizationCreationRequestDTO;
import org.kernely.project.dto.OrganizationDTO;
import org.kernely.project.service.OrganizationService;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * Admin controller for organization
 */
@Path("/admin/organizations")
public class OrganizationAdminController extends AbstractController {
	@Inject
	private SobaTemplateRenderer templateRenderer;

	@Inject 
	private OrganizationService organizationService;
	
	/**
	 * Set the template
	 * 
	 * @return the page admin
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML })
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public Response getPluginAdminPanel() {
		return Response.ok(templateRenderer.render("templates/organization_admin.html")).build();
	}
	
	/**
	 * Get all existing organizations in the database
	 * 
	 * @return A list of all DTO associated to the existing organizations in the
	 *         database
	 */
	@GET
	@Path("/all")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces({MediaType.APPLICATION_JSON})
	public List<OrganizationDTO> displayAllOrganizations() {
		log.debug("Call to GET on all organizations");
		return organizationService.getAllOrganizations();
	}
	
	/**
	 * Create a new organization with the given informations
	 * @param organization The DTO containing all informations about the new organization
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/create")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces({MediaType.APPLICATION_JSON})
	public String create(OrganizationCreationRequestDTO organization)
	{
		try{
			if(organization.id==0){
				organizationService.createOrganization(organization);
			}
			else{
				organizationService.updateOrganization(organization);
			}
			return "{\"result\":\"ok\"}";
		} catch (IllegalArgumentException iae) {
			log.debug(iae.getMessage());
			return "{\"result\":\""+iae.getMessage()+"\"}";
		}
	}

	
	/**
	 * Delete the organization which has the id 'id'
	 * @param id The id of the organization to delete
	 * @return The result of the operation
	 */
	@GET
	@Path("/delete/{id}")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces( { MediaType.TEXT_HTML })
	public Response deleteOrganization(@PathParam("id") int id){
		organizationService.deleteOrganization(id);
		return Response.ok().build();
	}
	
	/**
	 * Get all users associated to the organization which has the id 'id'
	 * @param id The id of the organization
	 * @return A list of all DTO associated to the users contained in this organization
	 */
	@GET
	@Path("/{id}/users")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces({MediaType.APPLICATION_JSON})
	public List<UserDTO> getOrganizationUsers(@PathParam("id") int id){
		return organizationService.getOrganizationUsers(id);
	}
	
	/**
	 * Get the DTO of an organization.
	 * @param id The id of the organization
	 * @return The DTO of the organization.
	 */
	@GET
	@Path("/{id}")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces({MediaType.APPLICATION_JSON})
	public OrganizationDTO getOrganization(@PathParam("id") int id){
		return organizationService.getOrganization(id);
	}
	
}
