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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.kernely.controller.AbstractController;
import org.kernely.core.dto.ManagerCreationRequestDTO;
import org.kernely.core.dto.ManagerDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.UserService;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * The controller of the manager admin page
 */
@Path("/admin/manager")
public class ManagerAdminController extends AbstractController {
	@Inject
	private SobaTemplateRenderer templateRenderer;

	@Inject
	private UserService userService;

	/**
	 * Display the manager administration page
	 * 
	 * @return the manager administration page
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML })
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public Response displayPage() {

		Map<String, Object> map = new HashMap<String, Object>();
		// Display the admin page only if the user is admin.
		return Response.ok(templateRenderer.render("templates/admin/manager_admin.html", map)).build();
	}

	/**
	 * Create the list for the combobox
	 * 
	 * @return a json which contains all the user that are not manager
	 */
	@GET
	@Path("/combobox")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<UserDTO> getListUsers() {
		Set<ManagerDTO> manager = userService.getAllManager();
		List<UserDTO> allUser = userService.getEnabledUsers();
		List<UserDTO> removeUser = new ArrayList<UserDTO>();
		for (ManagerDTO man : manager) {
			for (UserDTO usr : allUser) {
				if (man.name.equals(usr.username)) {
					removeUser.add(usr);
				}
			}
		}
		allUser.removeAll(removeUser);
		return allUser;
	}

	/**
	 * Create a new manager with the given informations
	 * 
	 * @param manager
	 *            The DTO containing all informations about the new manager
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/create")
	@Produces({ MediaType.APPLICATION_JSON })
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public Response create(ManagerCreationRequestDTO manager) {
		userService.updateManager(manager.manager, manager.users);
		return Response.ok().build();
	}

	/**
	 * Update a manager with the given informations
	 * 
	 * @param manager
	 *            The DTO containing all informations about the manager
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/update")
	@Produces({ MediaType.APPLICATION_JSON })
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public Response update(ManagerCreationRequestDTO manager) {
		userService.updateManager(manager.manager, manager.users);
		return Response.ok().build();
	}

	/**
	 * Get all existing manager in the database
	 * 
	 * @return A list of all DTO associated to the existing manager in the
	 *         database
	 */
	@GET
	@Path("/all")
	@Produces({ MediaType.APPLICATION_JSON })
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public List<ManagerDTO> displayAllManager() {
			Set<ManagerDTO> setManagers = userService.getAllManager();
			return new ArrayList<ManagerDTO>(setManagers);
	}

	/**
	 * Get the list of members that are managed by a manager
	 * 
	 * @param username
	 *            of the manager
	 * @return list of users managed
	 */
	@GET
	@Path("/users/{username}")
	@Produces({ MediaType.APPLICATION_JSON })
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public List<UserDTO> getListManaged(@PathParam("username") String manager) {
		return userService.getUsers(manager);
	}

	/**
	 * Remove the user from managers. The user is not removed.
	 * 
	 * @param the id of the user.
	 * @return The result of the operation.
	 */
	@GET
	@Path("/delete/{id}")
	@Produces({ MediaType.TEXT_HTML })
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public Response lock(@PathParam("id") long id) {
		userService.deleteManager(id);
		return Response.ok().build();
	}

	/**
	 * Get the manager and his users
	 * 
	 * @param the id of the manager to get
	 * @return The manager DTO, containing his users
	 */
	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public ManagerDTO getManager(@PathParam("id") long id) {
		return userService.getManager(id);
	}
}
