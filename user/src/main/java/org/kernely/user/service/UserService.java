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
package org.kernely.user.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.hibernate.EntityManagerProvider;
import org.kernely.user.dto.UserCreationRequestDTO;
import org.kernely.user.dto.UserDTO;
import org.kernely.user.model.UserModel;

import com.google.inject.Inject;

/**
 * Service provided by the user plugin.
 */
public class UserService {

	@Inject
	private EntityManagerProvider entityManagerProvider;

	/**
	 * Create a new user in database.
	 * @param request The request, containing user data : passwod, username...
	 */
	public void createUser(UserCreationRequestDTO request) {
		if("".equals(request.username) || "".equals(request.password))
			throw new IllegalArgumentException("Username or/and password cannot be null ");
		
		if("".equals(request.username.trim()) || "".equals(request.password.trim()))
			throw new IllegalArgumentException("Username or/and password cannot be space character only ");
		
		EntityManager em = entityManagerProvider.getEM();
		em.getTransaction().begin();
		UserModel user = new UserModel();
		user.setPassword(request.password.trim());
		user.setUsername(request.username.trim());
		em.persist(user);
		em.getTransaction().commit();
		em.close();

	}

	/**
	 * Gets the lists of all users contained in the database.
	 * @return the list of all users contained in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<UserDTO> getAllUsers() {
		EntityManager em = entityManagerProvider.getEM();
		em.getTransaction().begin();
		Query query = em.createQuery("SELECT e FROM UserModel e");
		List<UserModel> collection = (List<UserModel>) query.getResultList();
		List<UserDTO> dtos = new ArrayList<UserDTO>();
		for (UserModel user : collection) {
			dtos.add(new UserDTO(user.getUsername()));
		}
		em.getTransaction().commit();
		em.close();

		return dtos;

	}
}
