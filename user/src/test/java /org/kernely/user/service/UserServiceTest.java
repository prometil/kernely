package org.kernely.user.service;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.kernely.core.test.StreamTestModule;
import org.kernely.user.dto.UserCreationRequestDTO;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Inject;

public class UserServiceTest {

	@Rule
	public final GuiceBerryRule guiceBerry = new GuiceBerryRule(StreamTestModule.class);
	
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
