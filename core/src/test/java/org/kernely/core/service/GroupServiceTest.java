package org.kernely.core.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.GroupCreationRequestDTO;
import org.kernely.core.dto.GroupDTO;
import org.kernely.core.service.user.GroupService;

import com.google.inject.Inject;

public class GroupServiceTest extends AbstractServiceTest{
	@Inject
	private GroupService service;
	
	@Test
	public void  createGroup(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		service.createGroup(request);
		GroupDTO groupdto = new GroupDTO() ;
		groupdto = service.getAllGroups().get(0);
		assertEquals("Test Group", groupdto.name);
	}
	
	@Test
	public void  updateGroup(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		service.createGroup(request);
		GroupDTO groupdto = new GroupDTO() ;
		groupdto = service.getAllGroups().get(0);
		GroupCreationRequestDTO gcr = new GroupCreationRequestDTO(groupdto.id, groupdto.name);
		gcr.name = "Test Group Modified";
		service.updateGroup(gcr);
		groupdto = service.getAllGroups().get(0);
		assertEquals("Test Group Modified", groupdto.name);
	}
	
	@Test
	public void deleteGroup(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="Test Group";
		service.createGroup(request);
		assertEquals(1, service.getAllGroups().size());
		GroupDTO groupdto = new GroupDTO() ;
		groupdto = service.getAllGroups().get(0);
		service.deleteGroup(groupdto.id);
		assertEquals(0, service.getAllGroups().size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createGroupWithNullRequest(){
		service.createGroup(null);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createGroupWithEmptyName(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="";
		service.createGroup(request);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createGroupWithSpace(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="         ";
		service.createGroup(request);		
	}
	
	@Test
	public void  getGroup(){
		GroupCreationRequestDTO request = new GroupCreationRequestDTO();
		request.name="toto";
		service.createGroup(request);
		assertEquals(1,service.getAllGroups().size());
	}
	
	@Test
	public void getNullUser(){
		assertEquals(0, service.getAllGroups().size());
	}
}
