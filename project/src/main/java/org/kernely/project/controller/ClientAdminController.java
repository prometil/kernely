package org.kernely.project.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.project.dto.ClientCreationRequestDTO;
import org.kernely.project.dto.ClientDTO;
import org.kernely.project.service.ClientService;

import com.google.inject.Inject;

/**
 * Admin controller for client
 */
@Path("/admin/clients")
public class ClientAdminController extends AbstractController {
	@Inject
	private TemplateRenderer templateRenderer;

	@Inject
	private UserService userService;

	@Inject 
	ClientService clientService;
	
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
			page = ok(templateRenderer.create("/templates/gsp/client_admin.gsp").addCss("/css/admin.css").addCss("/css/client_admin.css").withLayout(TemplateRenderer.ADMIN_LAYOUT));
		} else {
			page = ok(templateRenderer.create("/templates/gsp/home.gsp"));
		}
		return page;
	}
	
	/**
	 * Get all existing clients in the database
	 * 
	 * @return A list of all DTO associated to the existing clients in the
	 *         database
	 */
	@GET
	@Path("/all")
	@Produces({MediaType.APPLICATION_JSON})
	public List<ClientDTO> displayAllClients() {
		if (userService.currentUserIsAdministrator()) {
			log.debug("Call to GET on all clients");
			return clientService.getAllClients();
		}
		return null;
	}
	
	/**
	 * Create a new client with the given informations
	 * @param client The DTO containing all informations about the new client
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/create")
	@Produces({MediaType.APPLICATION_JSON})
	public String create(ClientCreationRequestDTO client)
	{
		if (userService.currentUserIsAdministrator()){
			try{
				if(client.id==0){
					clientService.createClient(client);
				}
				else{
//					clientService.updateClient(client);
				}
				return "{\"result\":\"ok\"}";
			} catch (IllegalArgumentException iae) {
				log.debug(iae.getMessage());
				return "{\"result\":\""+iae.getMessage()+"\"}";
			}
		}
		return null;
	}

	
}
