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
import org.kernely.core.dto.UserDetailsDTO;
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
		request.firstname="toto";
		request.lastname="tata";
		service.createUser(request);
		UserDTO userdto = new UserDTO("", false) ;
		userdto = service.getAllUsers().get(0);
		UserDetailsDTO uddto = new UserDetailsDTO();
		uddto = service.getUserDetails(userdto.username);
		assertEquals("toto", userdto.username);
		assertEquals("toto", uddto.firstname);
		assertEquals("tata", uddto.lastname);
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
		request.firstname="toto";
		request.lastname="tata";
		service.createUser(request);
		assertEquals(1,service.getAllUsers().size());
		assertEquals(1,service.getAllUserDetails().size());
	}
	
	@Test
	public void lockedUser(){
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username="test";
		request.password="test";
		request.firstname="test";
		request.lastname="test";
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
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username="test";
		request.password="test";
		request.firstname="test";
		request.lastname="test";
		service.createUser(request);
		UserDTO userdto = new UserDTO() ;
		userdto = service.getAllUsers().get(0);
		UserDetailsDTO uddto = new UserDetailsDTO();
		uddto = service.getUserDetails(userdto.username);
		UserCreationRequestDTO ucr = new UserCreationRequestDTO();
		ucr.id = uddto.id;
		ucr.username = "test MODIFIED 1";
		ucr.firstname = "test MODIFIED 2";
		ucr.lastname = "test MODIFIED 3";
		service.updateUser(ucr);
		userdto = service.getAllUsers().get(0);
		uddto = service.getUserDetails(userdto.username);
		assertEquals("test MODIFIED 1", userdto.username);
		assertEquals("test MODIFIED 2", uddto.firstname);
		assertEquals("test MODIFIED 3", uddto.lastname);
	}
	
	@Test
	public void getNullUser(){
		assertEquals(0, service.getAllUsers().size());
	}
	
}
