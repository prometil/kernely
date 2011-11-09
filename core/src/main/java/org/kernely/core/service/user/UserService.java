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
package org.kernely.core.service.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.event.UserCreationEvent;
import org.kernely.core.model.User;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * Service provided by the user plugin.
 */
@Singleton
public class UserService {

	@Inject
	private Provider<EntityManager> em;

	@Inject
	private EventBus eventBus;

	/**
	 * Create a new user in database.
	 * 
	 * @param request
	 *            The request, containing user data : passwod, username...
	 */
	@Transactional
	public void createUser(UserCreationRequestDTO request) {
		if(request==null){
			throw new IllegalArgumentException("Request cannot be null ");
		}
		
		if("".equals(request.username) || "".equals(request.password)){
			throw new IllegalArgumentException("Username or/and password cannot be null ");
		}
		
		if("".equals(request.username.trim()) || "".equals(request.password.trim())){
			throw new IllegalArgumentException("Username or/and password cannot be space character only ");
		}
		User user = new User();
		user.setPassword(request.password.trim());
		user.setUsername(request.username.trim());
		em.get().persist(user);
		eventBus.post(new UserCreationEvent(user.getId(), user.getUsername()));

	}

	/**
	 * Gets the lists of all users contained in the database.
	 * 
	 * @return the list of all users contained in the database.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<UserDTO> getAllUsers() {
		Query query = em.get().createQuery("SELECT e FROM User e");
		List<User> collection = (List<User>) query.getResultList();
		List<UserDTO> dtos = new ArrayList<UserDTO>();
		for (User user : collection) {
			dtos.add(new UserDTO(user.getUsername()));
		}
		return dtos;

	}
}
