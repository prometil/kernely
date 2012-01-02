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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.GroupCreationRequestDTO;
import org.kernely.core.dto.GroupDTO;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.GroupService;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;

import com.google.inject.Inject;

public class GroupServiceTest extends AbstractServiceTest{
	@Inject
	private GroupService serviceGroup;
	
	@Inject
	private UserService serviceUser;
	
	@Inject
	private RoleService roleService;
	
	private final String TEST_STRING = "test_string";
	
	private long creationOfTestUser() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);

		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = TEST_STRING;
		request.password = TEST_STRING;
		request.firstname = TEST_STRING;
		request.lastname = TEST_STRING;
		serviceUser.createUser(request);
		return serviceUser.getAllUsers().get(0).id;
	}
		
	@Test
	public void  createGroup(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		serviceGroup.createGroup(request);
		GroupDTO groupdto = new GroupDTO() ;
		groupdto = serviceGroup.getAllGroups().get(0);
		assertEquals("Test Group", groupdto.name);
	}
	
	@Test(expected= IllegalArgumentException.class)
	public void  createGroupAlreadyExist(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		serviceGroup.createGroup(request);
		serviceGroup.createGroup(request);
	}
	
	
	@Test
	public void  updateGroupName(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		serviceGroup.createGroup(request);
		GroupDTO groupdto = new GroupDTO() ;
		groupdto = serviceGroup.getAllGroups().get(0);
		GroupCreationRequestDTO gcr = new GroupCreationRequestDTO(groupdto.id, groupdto.name, new ArrayList<UserDTO>());
		gcr.name = "Test Group Modified";
		serviceGroup.updateGroup(gcr);
		groupdto = serviceGroup.getAllGroups().get(0);
		assertEquals("Test Group Modified", groupdto.name);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void updateGroupNull(){
		serviceGroup.updateGroup(null);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void updateGroupNameNull(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		serviceGroup.createGroup(request);
		GroupDTO groupdto = new GroupDTO() ;
		groupdto = serviceGroup.getAllGroups().get(0);
		GroupCreationRequestDTO gcr = new GroupCreationRequestDTO(groupdto.id, "", new ArrayList<UserDTO>());
		serviceGroup.updateGroup(gcr);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void updateGroupNameEmpty(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		serviceGroup.createGroup(request);
		GroupDTO groupdto = new GroupDTO() ;
		groupdto = serviceGroup.getAllGroups().get(0);
		GroupCreationRequestDTO gcr = new GroupCreationRequestDTO(groupdto.id, " ", new ArrayList<UserDTO>());
		serviceGroup.updateGroup(gcr);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void updateGroupNameExist(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		serviceGroup.createGroup(request);
		GroupCreationRequestDTO request2 = new GroupCreationRequestDTO();
		request2.name="Test Group2";
		serviceGroup.createGroup(request2);
		GroupDTO groupdto = new GroupDTO() ;
		groupdto = serviceGroup.getAllGroups().get(0);
		GroupCreationRequestDTO gcr = new GroupCreationRequestDTO(groupdto.id, "Test Group2", new ArrayList<UserDTO>());
		serviceGroup.updateGroup(gcr);
	}
	
	
	@Test
	public void getGroupUser(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		serviceGroup.createGroup(request);
		GroupDTO groupdto = new GroupDTO() ;
		groupdto = serviceGroup.getAllGroups().get(0);

		this.creationOfTestUser();
		
		UserDTO userdto = new UserDTO() ;
		userdto = serviceUser.getAllUsers().get(0);
		
		List<UserDTO> users = new ArrayList<UserDTO>();
		users.add(userdto);
		GroupCreationRequestDTO gcr = new GroupCreationRequestDTO(groupdto.id, groupdto.name, users);
		serviceGroup.updateGroup(gcr);
		
		assertEquals(1, serviceGroup.getGroupUsers(groupdto.id).size());
	}
	
	@Test
	public void addGroupUser(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		serviceGroup.createGroup(request);
		GroupDTO groupdto = new GroupDTO() ;
		groupdto = serviceGroup.getAllGroups().get(0);

		this.creationOfTestUser();
		
		UserDTO userdto = new UserDTO() ;
		userdto = serviceUser.getAllUsers().get(0);
		
		List<UserDTO> users = new ArrayList<UserDTO>();
		users.add(userdto);
		GroupCreationRequestDTO gcr = new GroupCreationRequestDTO(groupdto.id, groupdto.name, users);
		serviceGroup.updateGroup(gcr);
		
		groupdto = serviceGroup.getAllGroups().get(0);
		assertEquals(1, groupdto.users.size());
		assertEquals(TEST_STRING, groupdto.users.get(0).username);
		
		gcr = new GroupCreationRequestDTO(groupdto.id, groupdto.name, new ArrayList<UserDTO>());
		serviceGroup.updateGroup(gcr);
		
		groupdto = serviceGroup.getAllGroups().get(0);
		assertEquals(0, groupdto.users.size());
		assertEquals(1, serviceUser.getAllUsers().size());
	}
	
	@Test
	public void deleteGroup(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		serviceGroup.createGroup(request);
		assertEquals(1, serviceGroup.getAllGroups().size());
		GroupDTO groupdto = new GroupDTO() ;
		groupdto = serviceGroup.getAllGroups().get(0);
		serviceGroup.deleteGroup(groupdto.id);
		assertEquals(0, serviceGroup.getAllGroups().size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createGroupWithNullRequest(){
		serviceGroup.createGroup(null);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createGroupWithEmptyName(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="";
		serviceGroup.createGroup(request);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createGroupWithSpace(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="         ";
		serviceGroup.createGroup(request);		
	}
	
	@Test
	public void  getGroup(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="toto";
		serviceGroup.createGroup(request);
		assertEquals(1,serviceGroup.getAllGroups().size());
	}
	
	@Test
	public void getNullUser(){
		assertEquals(0, serviceGroup.getAllGroups().size());
	}
}
