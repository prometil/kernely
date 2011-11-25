/*
Copyright 2011 Prometil SARL

This file is part of Kernely.

Kernely is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

Kernely is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public
License along with Kernely.
If not, see <http://www.gnu.org/licenses/>.
*/

package org.kernely.core.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.PermissionService;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;

import com.google.inject.Inject;

public class PermissionServiceTest extends AbstractServiceTest{

	
	@Inject
	private UserService userService;

	@Inject
	private PermissionService permissionService;
	
	@Inject
	private RoleService roleService;

	private final String PERMISSION_STRING = "test";
	
	@Test
	public void grantPermission(){
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
		
		userService.createUser(new UserCreationRequestDTO(1,"test","test","test","test",false,null));
		UserDetailsDTO details = userService.getUserDetails("test");
		
		assertEquals(false,permissionService.userHasPermission(details.id,PERMISSION_STRING));
		permissionService.grantPermission(details.id, PERMISSION_STRING);
//		assertEquals(true,permissionService.userHasPermission(details.id,PERMISSION_STRING));
	}
	
	@Test
	public void ungrantPermission(){
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
		
		userService.createUser(new UserCreationRequestDTO(31,"test","test","test","test",false,null));
		UserDetailsDTO details = userService.getUserDetails("test");
		assertEquals(false,permissionService.userHasPermission(details.id,PERMISSION_STRING));
		permissionService.grantPermission(details.id, PERMISSION_STRING);
		permissionService.ungrantPermission(details.id, PERMISSION_STRING);
		assertEquals(false,permissionService.userHasPermission(details.id,PERMISSION_STRING));
	}
	
}
