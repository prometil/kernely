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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.kernely.core.dto.RoleDTO;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;

import com.google.inject.Inject;

@Path("/roles")
public class RoleController extends AbstractController {

	
	@Inject
	private RoleService roleService;
	
	@Inject
	private UserService userService;
	
	/**
	 * Get all roles contained in the database
	 * @return A list of all DTO associated to the roles contained in the database
	 */
	@GET
	@Path("/all")
	@Produces({"application/json"})
	public List<RoleDTO> getAllRoles()
	{
		if (userService.currentUserIsAdministrator()){
			List<RoleDTO> roles = roleService.getAllRoles();
			return roles;
		}
		return null;
	}
	
}
