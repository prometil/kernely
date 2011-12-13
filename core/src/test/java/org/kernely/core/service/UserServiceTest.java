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
import org.junit.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.dto.UserDetailsUpdateRequestDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;

import com.google.inject.Inject;

public class UserServiceTest extends AbstractServiceTest{

	
	private static final String TEST_MODIFIED_3 = "test MODIFIED 3";

	private static final String TEST_MODIFIED_2 = "test MODIFIED 2";

	private static final String TEST_MODIFIED_1 = "test MODIFIED 1";

	private static final String NUMBER = "050607";

	private static final String STRING_TEST = "test";

	@Inject
	private UserService service;
	
	@Inject
	private RoleService roleService;
		
	@Test
	public void  createUser(){
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
		
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username=STRING_TEST;
		request.password=STRING_TEST;
		request.firstname=STRING_TEST;
		request.lastname=STRING_TEST;
		service.createUser(request);
		UserDTO userdto = new UserDTO("",1) ;
		userdto = service.getAllUsers().get(0);
		UserDetailsDTO uddto = new UserDetailsDTO();
		uddto = service.getUserDetails(userdto.username);
		assertEquals(STRING_TEST, userdto.username);
		assertEquals(STRING_TEST, uddto.firstname);
		assertEquals(STRING_TEST, uddto.lastname);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void updateUserWithNullRequest(){
		service.updateUserProfile(null);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void updateUserWithErrorDate(){
		UserDetailsUpdateRequestDTO request = new UserDetailsUpdateRequestDTO();
		request.birth="";
		service.updateUserProfile(request);		
	}
	
	@Test
	public void updateUserDetails(){

		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
		
		UserCreationRequestDTO requestcreation = new UserCreationRequestDTO();
		requestcreation.username=STRING_TEST;
		requestcreation.password=STRING_TEST;
		requestcreation.firstname=STRING_TEST;
		requestcreation.lastname=STRING_TEST;
		service.createUser(requestcreation);
		UserDTO userdto = service.getAllUsers().get(0);

		UserDetailsUpdateRequestDTO request = new UserDetailsUpdateRequestDTO();
		request.birth="18/12/1990";
		request.adress=STRING_TEST;
		request.businessphone=NUMBER;
		request.city=STRING_TEST;
		request.civility=1;
		request.firstname=STRING_TEST;
		request.homephone=NUMBER;
		request.id=(int) userdto.id;
		request.image="LLll.jpg";
		request.email=STRING_TEST;
		request.mobilephone=NUMBER;
		request.lastname=STRING_TEST;
		request.nationality=STRING_TEST;
		request.ssn=NUMBER;
		request.zip=NUMBER;
		service.updateUserProfile(request);
		
		UserDetailsDTO uddto = new UserDetailsDTO();
		uddto = service.getUserDetails(userdto.username);
		
		assertEquals("18/12/1990", uddto.birth);
		assertEquals(STRING_TEST,uddto.adress);
		assertEquals(NUMBER,uddto.businessphone);
		assertEquals(STRING_TEST , uddto.city);
		assertEquals(STRING_TEST, uddto.firstname);
		assertEquals(NUMBER, uddto.homephone);
		assertEquals("LLll.jpg", uddto.image);
		assertEquals(STRING_TEST, uddto.email);
		assertEquals(NUMBER, uddto.mobilephone); 
		assertEquals(STRING_TEST, uddto.lastname);
		assertEquals(STRING_TEST,uddto.nationality);
		assertEquals(NUMBER, uddto.ssn);
		assertEquals(NUMBER, uddto.zip);			
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createUserWithNullRequest(){
		service.createUser(null);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createUserWithEmptyUsername(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username="";
		request.password=STRING_TEST;
		service.createUser(request);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createUserWithEmptyPassword(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username=STRING_TEST;
		request.password="";
		service.createUser(request);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createUserWithEmptyUsernameAndPassword(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username="";
		request.password="";
		service.createUser(request);		
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void createUserWithSpace(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username="         ";
		request.password="         ";
		service.createUser(request);		
	}
	
	@Test
	public void  getUser(){
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
		
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username=STRING_TEST;
		request.password=STRING_TEST;
		request.firstname=STRING_TEST;
		request.lastname=STRING_TEST;
		service.createUser(request);
		assertEquals(1,service.getAllUsers().size());
		assertEquals(1,service.getAllUserDetails().size());
	}
	
	@Test
	public void lockedUser(){
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
		
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username=STRING_TEST;
		request.password=STRING_TEST;
		request.firstname=STRING_TEST;
		request.lastname=STRING_TEST;
		service.createUser(request);
		UserDTO userdto = new UserDTO() ;
		userdto = service.getAllUsers().get(0);
		assertEquals(false, userdto.locked);
		UserDetailsDTO uddto = new UserDetailsDTO();
		uddto = service.getUserDetails(userdto.username);
		service.lockUser(uddto.id);
		userdto = service.getAllUsers().get(0);
		assertEquals(true, userdto.locked);
	}
	
	@Test
	public void updateUser(){
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
		
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username=STRING_TEST;
		request.password=STRING_TEST;
		request.firstname=STRING_TEST;
		request.lastname=STRING_TEST;
		service.createUser(request);
		UserDTO userdto = new UserDTO() ;
		userdto = service.getAllUsers().get(0);
		UserDetailsDTO uddto = new UserDetailsDTO();
		uddto = service.getUserDetails(userdto.username);
		UserCreationRequestDTO ucr = new UserCreationRequestDTO();
		ucr.id = uddto.id;
		ucr.username = TEST_MODIFIED_1;
		ucr.firstname = TEST_MODIFIED_2;
		ucr.lastname = TEST_MODIFIED_3;
		ucr.roles = new ArrayList<RoleDTO>();
		service.updateUser(ucr);
		userdto = service.getAllUsers().get(0);
		uddto = service.getUserDetails(userdto.username);
		assertEquals(TEST_MODIFIED_1, userdto.username);
		assertEquals(TEST_MODIFIED_2, uddto.firstname);
		assertEquals(TEST_MODIFIED_3, uddto.lastname);
	}
	
	@Test
	public void getNullUser(){
		assertEquals(0, service.getAllUsers().size());
	}
	
	@Test
	public void getAuthenticatedUser() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
		
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = STRING_TEST;
		request.password = STRING_TEST;
		request.firstname = STRING_TEST;
		request.lastname = STRING_TEST;
		service.createUser(request);
		authenticateAs(STRING_TEST);
		UserDTO currentUser = service.getAuthenticatedUserDTO();
		assertEquals(STRING_TEST, currentUser.username);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void updateManagerNullArgument(){
		service.updateManager(null, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void updateManagerEmptyManager(){
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = STRING_TEST;
		request.password = STRING_TEST;
		request.firstname = STRING_TEST;
		request.lastname = STRING_TEST;
		service.createUser(request);
		List<UserDTO> users = service.getAllUsers() ;
		service.updateManager("", users);
	}
	
	
	@Test
	public void getUsersTest(){
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = STRING_TEST;
		request.password = STRING_TEST;
		request.firstname = STRING_TEST;
		request.lastname = STRING_TEST;
		service.createUser(request);
		List<UserDTO> users = service.getAllUsers() ; 
		UserCreationRequestDTO request2 = new UserCreationRequestDTO();
		request2.username = TEST_MODIFIED_1;
		request2.password = TEST_MODIFIED_1;
		request2.firstname = TEST_MODIFIED_1;
		request2.lastname = TEST_MODIFIED_1;
		service.createUser(request2);
		service.updateManager(TEST_MODIFIED_1, users) ;
		List<UserDTO> list = service.getUsers(TEST_MODIFIED_1);
		UserDTO usr= list.get(0);
		assertEquals(usr.username, STRING_TEST);
				
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getUsersNull(){
		service.getUsers(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getUsersEmptyString(){
		service.getUsers("");
	}
	
	@Test
	public void deleteManagerTest(){
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = STRING_TEST;
		request.password = STRING_TEST;
		request.firstname = STRING_TEST;
		request.lastname = STRING_TEST;
		service.createUser(request);
		List<UserDTO> users = service.getAllUsers() ; 
		UserCreationRequestDTO request2 = new UserCreationRequestDTO();
		request2.username = TEST_MODIFIED_1;
		request2.password = TEST_MODIFIED_1;
		request2.firstname = TEST_MODIFIED_1;
		request2.lastname = TEST_MODIFIED_1;
		service.createUser(request2);
		service.updateManager(TEST_MODIFIED_1, users) ;
		service.deleteManager(TEST_MODIFIED_1);
		List<UserDTO> list = service.getUsers(TEST_MODIFIED_1);
		Assert.assertEquals(list.size(),0);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void deleteManagersNull(){
		service.deleteManager(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void deleteManagerEmptyString(){
		service.deleteManager("");
	}
	
}
