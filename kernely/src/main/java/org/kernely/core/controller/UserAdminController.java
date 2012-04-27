/**
s * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.kernely.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.kernely.controller.AbstractController;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.service.UserService;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * The controller of the admin user page
 */
@Path("/admin/users")
public class UserAdminController extends AbstractController{

	@Inject
	private SobaTemplateRenderer templateRenderer;

	@Inject
	private UserService userService;
		
	/**
	 * Display the user page administration
	 * @return the user administration page
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response displayPage()
	{
		Map<String, Object> map =new HashMap<String, Object>();
		// Display the admin page only if the user is admin.
		if (userService.currentUserIsAdministrator()) {
			return Response.ok(templateRenderer.render("templates/admin/user_admin.html", map)).build();
		} else {
			return Response.status(Status.FORBIDDEN).build();
		}
	}

	/**
	 * Get all users stored in database in order to display them
	 * @return A list of all DTO associated to the users stored in the database
	 */
	@GET
	@Path("/all")
	@Produces({MediaType.APPLICATION_JSON})
	public List<UserDetailsDTO> displayAllUsers()
	{
		if (userService.currentUserIsAdministrator()){
			log.debug("Call to GET on all users");
			return userService.getAllUserDetails();
		}
		return null;
	}

	/**
	 * Get all clients stored in database in order to display them
	 * @return A list of all DTO associated to the clients stored in the database
	 */
	@GET
	@Path("/client")
	@Produces({MediaType.APPLICATION_JSON})
	public List<UserDetailsDTO> displayAllClients()
	{
		if (userService.currentUserIsAdministrator()){
			log.debug("Call to GET on all users");
			return userService.getAllClients();
		}	
		return null;
	}
	
	
	/**
	 * Get all users stored in database in order to display them
	 * @return A list of all DTO associated to the users stored in the database
	 */
	@GET
	@Path("/enabled")
	@Produces({MediaType.APPLICATION_JSON})
	public List<UserDetailsDTO> displayEnabledUsers()
	{
		if (userService.currentUserIsAdministrator()){
			log.debug("Call to GET on enabled users");
			return userService.getEnabledUserDetails();
		}
		return null;
	}

	/**
	 * Create a new user with the informations contained in the DTO
	 * @return A JSON string contained the result of the operation
	 */
	@POST
	@Path("/create")
	@Produces({MediaType.APPLICATION_JSON})
	public String create(UserCreationRequestDTO user)
	{
		if (userService.currentUserIsAdministrator()){
			try{
				log.debug("Create a user");
				if(user.id == 0){
					userService.createUser(user);
				}
				else{
					userService.updateUser(user);
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
	 * Locks the user who has the id 'id'
	 * @param id The id of the user locked
	 * @return The result of the operation
	 */
	@GET
	@Path("/lock/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public String lock(@PathParam("id") int id){
		if (userService.currentUserIsAdministrator()){
			userService.lockUser(id);
			return "{\"result\":\"ok\"}";
		}
		return null;
	}

	/**
	 * Get all roles associated to the user who has the id 'id'
	 * @param id the id of the needed user
	 * @return A list of all DTO associated to the roles of this user
	 */
	@GET
	@Path("/{id}/roles")
	@Produces({MediaType.APPLICATION_JSON})
	public List<RoleDTO> getUserRoles(@PathParam("id") int id){
		if (userService.currentUserIsAdministrator()){
			return userService.getUserRoles(id);
		}
		return null;
	}
}
