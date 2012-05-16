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
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.kernely.controller.AbstractController;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.RoleService;

import com.google.inject.Inject;

/**
 * The controller of the role page
 */
@Path("/roles")
public class RoleController extends AbstractController {

	
	@Inject
	private RoleService roleService;
	
	/**
	 * Get all roles contained in the database
	 * @return A list of all DTO associated to the roles contained in the database
	 */
	@GET
	@Path("/all")
	@Produces({MediaType.APPLICATION_JSON})
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public List<RoleDTO> getAllRoles()
	{
		return roleService.getAllRoles();
	}
	
}
