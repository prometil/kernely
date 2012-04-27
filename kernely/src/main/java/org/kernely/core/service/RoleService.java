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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.kernely.core.dto.RoleDTO;
import org.kernely.core.model.Role;
import org.kernely.core.model.User;
import org.kernely.service.AbstractService;

import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * The role service
 * @author b.grandperret
 *
 */
@Singleton
public class RoleService extends AbstractService {

	/**
	 * Gets the lists of all roles contained in the database.
	 * 
	 * @return the list of all roles contained in the database.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<RoleDTO> getAllRoles() {
		Query query = em.get().createQuery("SELECT e FROM Role e WHERE name !=:name ");
		query.setParameter("name", Role.ROLE_USER);
		List<Role> collection = (List<Role>) query.getResultList();
		List<RoleDTO> dtos = new ArrayList<RoleDTO>(0);
		for (Role role : collection) {
			dtos.add(new RoleDTO(role.getId(), role.getName()));
		}
		return dtos;
	}

	/**
	 * Use for unit test, create a new role 
	 * @param request a role dto
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public RoleDTO createRole(RoleDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if ("".equals(request.name)) {
			throw new IllegalArgumentException("Role name cannot be null ");
		}

		if ("".equals(request.name.trim())) {
			throw new IllegalArgumentException("Role name cannot be space character only ");
		}

		Query verifExist = em.get().createQuery("SELECT r FROM Role r WHERE name=:name");
		verifExist.setParameter("name", request.name);
		List<User> list = (List<User>) verifExist.getResultList();
		if (!list.isEmpty()) {
			throw new IllegalArgumentException("Another role with this name already exists");
		}
		Role role = new Role();
		role.setName(request.name.trim());
		em.get().persist(role);
		return new RoleDTO(role.getId(),role.getName());
	}
}
