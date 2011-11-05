package org.kernely.core.service;

import static org.junit.Assert.assertNotNull;
import junit.framework.Assert;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.service.user.UserService;

import com.google.inject.Inject;

public class UserServiceTest extends AbstractServiceTest{

	
	@Inject
	private UserService service;
		
	@Test
	public void  createUser(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username="toto";
		request.password="tata";
		service.createUser(request);
		Assert.assertNotNull(service);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createUserWithEmptyUsername(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username="";
		request.password="tata";
		service.createUser(request);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createUserWithEmptyPassword(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username="tata";
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
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username="toto";
		request.password="tata";
		service.createUser(request);
		assertNotNull(service.getAllUsers());
	}
	
	@Test
	public void getNullUser(){
		assertNotNull(service.getAllUsers());
	}
	
	

}
