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
	
	@Test
	public void  createGroup(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		serviceGroup.createGroup(request);
		GroupDTO groupdto = new GroupDTO() ;
		groupdto = serviceGroup.getAllGroups().get(0);
		assertEquals("Test Group", groupdto.name);
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
	
	@Test
	public void addGroupUser(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		serviceGroup.createGroup(request);
		GroupDTO groupdto = new GroupDTO() ;
		groupdto = serviceGroup.getAllGroups().get(0);

		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
		
		UserCreationRequestDTO requestUser = new UserCreationRequestDTO();
		requestUser.username="toto";
		requestUser.password="tata";
		requestUser.firstname="toto";
		requestUser.lastname="tata";
		serviceUser.createUser(requestUser);
		UserDTO userdto = new UserDTO() ;
		userdto = serviceUser.getAllUsers().get(0);
		
		List<UserDTO> users = new ArrayList<UserDTO>();
		users.add(userdto);
		GroupCreationRequestDTO gcr = new GroupCreationRequestDTO(groupdto.id, groupdto.name, users);
		serviceGroup.updateGroup(gcr);
		
		groupdto = serviceGroup.getAllGroups().get(0);
		assertEquals(1, groupdto.users.size());
		assertEquals("toto", groupdto.users.get(0).username);
		
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
