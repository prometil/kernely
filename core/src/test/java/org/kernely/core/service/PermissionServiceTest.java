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
import org.kernely.core.dto.PermissionDTO;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.PermissionService;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;

import com.google.inject.Inject;

public class PermissionServiceTest extends AbstractServiceTest {

	@Inject
	private UserService userService;

	@Inject
	private PermissionService permissionService;

	@Inject
	private RoleService roleService;

	private final String TEST_STRING = "test_string";
	private final String FAKE_RESOURCE_TYPE1 = "firstresource";
	private final String FAKE_RESOURCE_TYPE2 = "secondresource";
	private final String FAKE_RIGHT = "doing";

	private long creationOfTestUser() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);

		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = TEST_STRING;
		request.password = TEST_STRING;
		request.firstname = TEST_STRING;
		request.lastname = TEST_STRING;
		userService.createUser(request);
		return userService.getAllUsers().get(0).id;
	}
	
	private PermissionDTO createPermissionDTO(String pName){
		return new PermissionDTO(pName);
	}

	@Test
	public void grantPermission() {
		long userId = this.creationOfTestUser();

		assertEquals(false, permissionService.userHasPermission((int) userId, FAKE_RIGHT,FAKE_RESOURCE_TYPE1,1));
		permissionService.grantPermission((int) userId, FAKE_RIGHT,FAKE_RESOURCE_TYPE1,1);

		assertEquals(true, permissionService.userHasPermission((int) userId, FAKE_RIGHT,FAKE_RESOURCE_TYPE1,1));
	}

	@Test
	public void currentUserHasPermission(){
		long userId = this.creationOfTestUser();
		authenticateAs(userService.getAllUsers().get(0).username);
	    //assertEquals(false,permissionService.currentUserHasPermission(FAKE_RIGHT, FAKE_RESOURCE_TYPE1, 1));
		permissionService.grantPermission((int) userId, FAKE_RIGHT,FAKE_RESOURCE_TYPE1,1);
		assertEquals(true,permissionService.currentUserHasPermission(FAKE_RIGHT, FAKE_RESOURCE_TYPE1, 1));	
	}
	
	@Test
	public void ungrantPermission() {
		long userId = this.creationOfTestUser();

		assertEquals(false, permissionService.userHasPermission((int) userId, FAKE_RIGHT,FAKE_RESOURCE_TYPE1,1));
		permissionService.grantPermission((int) userId, FAKE_RIGHT,FAKE_RESOURCE_TYPE1,1);
		permissionService.ungrantPermission((int) userId, FAKE_RIGHT,FAKE_RESOURCE_TYPE1,1);
		assertEquals(false, permissionService.userHasPermission((int) userId, FAKE_RIGHT,FAKE_RESOURCE_TYPE1,1));
	}
	
	
	/*@Test(expected = NoResultException.class)
	public void ungrantPermissionNotExist() {
		long userId = this.creationOfTestUser();
		permissionService.ungrantPermission((int) userId, FAKE_RIGHT,FAKE_RESOURCE_TYPE1,1);
	}*/

	@Test
	public void typeOfPermissionForOneUser() {
		long userId = this.creationOfTestUser();
		permissionService.grantPermission((int) userId, FAKE_RIGHT, FAKE_RESOURCE_TYPE1, 1);
		permissionService.grantPermission((int) userId, FAKE_RIGHT, FAKE_RESOURCE_TYPE2, 1);
		permissionService.grantPermission((int) userId, FAKE_RIGHT, FAKE_RESOURCE_TYPE2, 2);
		permissionService.grantPermission((int) userId, FAKE_RIGHT, FAKE_RESOURCE_TYPE2, 3);
		permissionService.grantPermission((int) userId, FAKE_RIGHT, FAKE_RESOURCE_TYPE2, 4);
		permissionService.grantPermission((int) userId, FAKE_RIGHT, FAKE_RESOURCE_TYPE2, 5);

		int nbFirstPermissions = permissionService.getTypeOfPermissionForOneUser(userId, FAKE_RESOURCE_TYPE1).size();
		int nbSecondPermissions = permissionService.getTypeOfPermissionForOneUser(userId, FAKE_RESOURCE_TYPE2).size();
		int nbOtherPermissions = permissionService.getTypeOfPermissionForOneUser(userId, "something_else").size();

		assertEquals(1, nbFirstPermissions);
		assertEquals(5, nbSecondPermissions);
		assertEquals(0, nbOtherPermissions);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void illegalPermission(){
		permissionService.grantPermission(1, "test:test", "resource", "else");
	}

	@Test
	public void getAllPermissionTest(){
		long userId = this.creationOfTestUser();
		permissionService.grantPermission((int) userId, FAKE_RIGHT, FAKE_RESOURCE_TYPE1, 1);
		permissionService.grantPermission((int) userId, FAKE_RIGHT, FAKE_RESOURCE_TYPE2, 1);
		PermissionDTO pDto1 = createPermissionDTO(FAKE_RIGHT + ":" + FAKE_RESOURCE_TYPE1 + ":1");
		PermissionDTO pDto2 = createPermissionDTO(FAKE_RIGHT + ":" + FAKE_RESOURCE_TYPE2 + ":1");
		assertEquals(pDto1.name, permissionService.getAllPermissions().get(0).name);
		assertEquals(pDto2.name, permissionService.getAllPermissions().get(1).name);	
	}
	
	@Test 
	public void getUserWithPermissionTest(){
		long userId = this.creationOfTestUser();
		permissionService.grantPermission((int) userId, FAKE_RIGHT, FAKE_RESOURCE_TYPE1, 1);
		UserDTO udto = new UserDTO(TEST_STRING, userId);
		assertEquals(udto.id, permissionService.getUsersWithPermission(FAKE_RIGHT, FAKE_RESOURCE_TYPE1, 1).get(0).id);
		assertEquals(udto.username, permissionService.getUsersWithPermission(FAKE_RIGHT, FAKE_RESOURCE_TYPE1, 1).get(0).username);
	}
	
/*	@Test(expected = NonUniqueResultException.class)
	public void  userHasMultiplePermission(){
		long userId = this.creationOfTestUser();
		
		permissionService.grantPermission((int) userId, FAKE_RIGHT,FAKE_RESOURCE_TYPE1,1);
		permissionService.grantPermission((int) userId, FAKE_RIGHT,FAKE_RESOURCE_TYPE1,1);
		permissionService.userHasPermission((int) userId, FAKE_RIGHT,FAKE_RESOURCE_TYPE1,1);	
	}*/

}
