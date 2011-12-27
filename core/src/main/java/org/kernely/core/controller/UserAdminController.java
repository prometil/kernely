/**
 * Copyright 2011 Prometil SARL
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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;

import com.google.inject.Inject;

@Path("/admin/users")
public class UserAdminController extends AbstractController{

	@Inject
	private TemplateRenderer templateRenderer;

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
		if (userService.currentUserIsAdministrator()){
			return ok(templateRenderer.create("/templates/gsp/administration/user_admin.gsp").withLayout(TemplateRenderer.ADMIN_LAYOUT));
		}
		return ok(templateRenderer.create("/templates/gsp/home.gsp"));
	}

	/**
	 * Get all users stored in database in order to display them
	 * @return A list of all DTO associated to the users stored in the database
	 */
	@GET
	@Path("/all")
	@Produces({"application/json"})
	public List<UserDetailsDTO> displayAllUsers()
	{
		if (userService.currentUserIsAdministrator()){
			log.debug("Call to GET on all users");
			return userService.getAllUserDetails();
		}
		return null;
	}

	/**
	 * Create a new user with the informations contained in the DTO
	 * @return A JSON string contained the result of the operation
	 */
	@POST
	@Path("/create")
	@Produces({"application/json"})
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
	@Produces({"application/json"})
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
	@Produces({"application/json"})
	public List<RoleDTO> getUserRoles(@PathParam("id") int id){
		if (userService.currentUserIsAdministrator()){
			return userService.getUserRoles(id);
		}
		return null;
	}
}
