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
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
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
		UserDTO dto = new UserDTO("") ;
		dto = service.getAllUsers().get(0);
		assertEquals("toto", dto.username);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createUserWithNullRequest(){
		service.createUser(null);		
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
		assertEquals(1,service.getAllUsers().size());
	}
	
	@Test
	public void getNullUser(){
		assertEquals(0, service.getAllUsers().size());
	}
	
}
