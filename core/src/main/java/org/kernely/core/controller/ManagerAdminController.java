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
import javax.ws.rs.core.Response.Status;

import org.kernely.core.dto.ManagerCreationRequestDTO;
import org.kernely.core.dto.ManagerDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.SobaTemplateRenderer;

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
	public Response displayPage() {

		Map<String, Object> map = new HashMap<String, Object>();
		// Display the admin page only if the user is admin.
		if (userService.currentUserIsAdministrator()) {
			return Response.ok(templateRenderer.render("templates/admin/manager_admin.html", map)).build();
		} else {
			return Response.status(Status.FORBIDDEN).build();
		}
		
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
	public String create(ManagerCreationRequestDTO manager) {
		if (userService.currentUserIsAdministrator()) {
			userService.updateManager(manager.manager, manager.users);
			return "{\"result\":\"ok\"}";
		}
		return null;
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
	public String update(ManagerCreationRequestDTO manager) {
		if (userService.currentUserIsAdministrator()) {
			userService.updateManager(manager.manager, manager.users);
			return "{\"result\":\"ok\"}";
		}
		return null;
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
	public List<ManagerDTO> displayAllManager() {
		if (userService.currentUserIsAdministrator()) {
			Set<ManagerDTO> setManagers = userService.getAllManager();
			return new ArrayList<ManagerDTO>(setManagers);
		}
		return null;
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
	public List<UserDTO> getListManaged(@PathParam("username") String manager) {
		if (userService.currentUserIsAdministrator()) {
			return userService.getUsers(manager);
		}
		return null;

	}

	/**
	 * Delete the lanager which has the username 'username' The manager lost all
	 * his managed user
	 * 
	 * @param the
	 *            username of the manager to delete
	 * @return The result of the operation
	 */
	@GET
	@Path("/delete/{username}")
	@Produces({ MediaType.TEXT_HTML })
	public String lock(@PathParam("username") String username) {
		if (userService.currentUserIsAdministrator()) {
			userService.deleteManager(username);
			return "Ok";
		}
		return null;
	}

}
