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

package org.kernely.core.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.RoleService;

import com.google.inject.Inject;

public class RoleServiceTest extends AbstractServiceTest{


	@Inject
	private RoleService roleService;
	
	@Test
	public void getAllRolesTest(){
		assertEquals(0,roleService.getAllRoles().size());
		
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_ADMINISTRATOR);
		roleService.createRole(requestRole);
		
		assertEquals(1,roleService.getAllRoles().size());
		assertEquals(Role.ROLE_ADMINISTRATOR,roleService.getAllRoles().get(0).name);
	}
	
}