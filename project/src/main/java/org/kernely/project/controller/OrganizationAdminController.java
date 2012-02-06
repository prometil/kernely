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
import org.kernely.core.dto.UserDTO;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.project.dto.OrganizationCreationRequestDTO;
import org.kernely.project.dto.OrganizationDTO;
import org.kernely.project.service.OrganizationService;

import com.google.inject.Inject;

/**
 * Admin controller for organization
 */
@Path("/admin/organizations")
public class OrganizationAdminController extends AbstractController {
	@Inject
	private TemplateRenderer templateRenderer;

	@Inject
	private UserService userService;

	@Inject 
	OrganizationService organizationService;
	
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
			page = ok(templateRenderer.create("/templates/gsp/organization_admin.gsp").addCss("/css/admin.css").addCss("/css/organization_admin.css").withLayout(TemplateRenderer.ADMIN_LAYOUT));
		} else {
			page = ok(templateRenderer.create("/templates/gsp/home.gsp"));
		}
		return page;
	}
	
	/**
	 * Get all existing organizations in the database
	 * 
	 * @return A list of all DTO associated to the existing organizations in the
	 *         database
	 */
	@GET
	@Path("/all")
	@Produces({MediaType.APPLICATION_JSON})
	public List<OrganizationDTO> displayAllOrganizations() {
		if (userService.currentUserIsAdministrator()) {
			log.debug("Call to GET on all organizations");
			return organizationService.getAllOrganizations();
		}
		return null;
	}
	
	/**
	 * Create a new organization with the given informations
	 * @param organization The DTO containing all informations about the new organization
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/create")
	@Produces({MediaType.APPLICATION_JSON})
	public String create(OrganizationCreationRequestDTO organization)
	{
		if (userService.currentUserIsAdministrator()){
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
		return null;
	}

	
	/**
	 * Delete the organization which has the id 'id'
	 * @param id The id of the organization to delete
	 * @return The result of the operation
	 */
	@GET
	@Path("/delete/{id}")
	@Produces( { MediaType.TEXT_HTML })
	public String deleteOrganization(@PathParam("id") int id){
		if (userService.currentUserIsAdministrator()){
			organizationService.deleteOrganization(id);
			return "Ok";
		}
		return null;
	}
	
	/**
	 * Get all users associated to the organization which has the id 'id'
	 * @param id The id of the organization
	 * @return A list of all DTO associated to the users contained in this organization
	 */
	@GET
	@Path("/{id}/users")
	@Produces({MediaType.APPLICATION_JSON})
	public List<UserDTO> getOrganizationUsers(@PathParam("id") int id){
		if (userService.currentUserIsAdministrator()){
			return organizationService.getOrganizationUsers(id);
		}
		return null;
	}
	
}
