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
package org.kernely.core.service.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.kernely.core.dto.PermissionDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.model.Permission;
import org.kernely.core.model.User;
import org.kernely.core.service.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * The permission service
 * @author b.grandperret
 *
 */
@Singleton
public class PermissionService extends AbstractService {

	private static Logger log = LoggerFactory.getLogger(PermissionService.class);

	/**
	 * Verify if the current user has a specific permission.
	 * 
	 * @param userId
	 *            The id of the user.
	 * @param right
	 *            The right on the resource for example "write", or "delete".
	 * @param resourceType
	 *            The type of the resource, for example "user" or "stream"
	 * @param resourceId
	 *            The unique identifier for the resource
	 * 
	 * @return true if the current user has the permission.
	 */
	public boolean currentUserHasPermission(String right, String resourceType, Object resourceId) {
		String permission = this.createPermissionString(right, resourceType, resourceId.toString());
		try {
			SecurityUtils.getSubject().checkPermission(permission);
		} catch (AuthorizationException ae) {
			return false;
		}
		return true;
	}

	/**
	 * Verify if a specific user has a specific permission.
	 * 
	 * @param userId
	 *            The id of the user.
	 * @param right
	 *            The right on the resource for example "write", or "delete".
	 * @param resourceType
	 *            The type of the resource, for example "user" or "stream"
	 * @param resourceId
	 *            The unique identifier for the resource
	 * 
	 * @return true if the user has the permission.
	 */

	@Transactional
	public boolean userHasPermission(int id, String right, String resourceType, Object resourceId) {
		String permission = this.createPermissionString(right, resourceType, resourceId.toString());

		Query query = em.get().createQuery("SELECT p FROM Permission p WHERE name = :permission");
		query.setParameter("permission", permission);
		try {
			Permission p = (Permission) query.getSingleResult();

			for (User u : p.getUsers()) {
				if (u.getId() == id) {
					return true;
				}
			}
			return false;
		} catch (NoResultException nre) {
			return false;
		} catch (NonUniqueResultException nure) {
			log.error(nure.getMessage());
			return false;
		}
	}

	/**
	 * Gets the lists of all permissions contained in the database.
	 * 
	 * @return the list of all permissions contained in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<PermissionDTO> getAllPermissions() {
		Query query = em.get().createQuery("SELECT e FROM Permission e");
		List<Permission> collection = (List<Permission>) query.getResultList();
		List<PermissionDTO> dtos = new ArrayList<PermissionDTO>();
		for (Permission perm : collection) {
			dtos.add(new PermissionDTO(perm.getName()));
		}

		return dtos;
	}

	/**
	 * Grant a right on a resource to a specific user.
	 * 
	 * @param userId
	 *            The id of the user which has this permission.
	 * @param right
	 *            The right on the resource for example "write", or "delete".
	 * @param resourceType
	 *            The type of the resource, for example "user" or "stream"
	 * @param resourceId
	 *            The unique identifier for the resource
	 */
	@Transactional
	public void grantPermission(int userId, String right, String resourceType, Object resourceId) {
		// Verify if the permission already exists
		String permission = this.createPermissionString(right, resourceType, resourceId.toString());

		Query permissionQuery = em.get().createQuery("SELECT p FROM Permission p WHERE name = :permission");
		permissionQuery.setParameter("permission", permission);
		User user = em.get().find(User.class, (long) userId);
		log.debug("Grant permission {} to user id : {}", permission, userId);
		Permission p;
		try {
			p = (Permission) permissionQuery.getSingleResult();
		} catch (NoResultException nre) {
			// If there is no permission, we create it
			String[] result = permission.split(":");
			if (result.length > 3) {
				throw new IllegalArgumentException("The permission " + permission + " is malformed");
			}
			p = new Permission();
			p.setName(permission);
			em.get().persist(p);
			log.debug("Creation of the permission {}", permission);
		}
		Set<Permission> userPermissions = user.getPermissions();
		if (userPermissions == null) {
			userPermissions = new HashSet<Permission>();
		}
		userPermissions.add(p);
		user.setPermissions(userPermissions);
		p.getUsers().add(user);

		em.get().merge(user);
		em.get().merge(p);
	}

	/**
	 * Ungrant a specific permission for a specific user.
	 * 
	 * @param userId
	 *            The id of the user which has this permission.
	 * @param right
	 *            The right on the resource for example "write", or "delete".
	 * @param resourceType
	 *            The type of the resource, for example "user" or "stream"
	 * @param resourceId
	 *            The unique identifier for the resource
	 */
	@Transactional
	public void ungrantPermission(int userId, String right, String resourceType, Object resourceId) {
		String permission = this.createPermissionString(right, resourceType, resourceId.toString());

		// Verify if the permission already exists
		Query permissionQuery = em.get().createQuery("SELECT p FROM Permission p WHERE name = :permission");
		permissionQuery.setParameter("permission", permission);
		Permission p;
		User user = em.get().find(User.class, (long) userId);
		try {
			log.debug("Ungrant permission {} to user id : {}", permission, userId);
			p = (Permission) permissionQuery.getSingleResult();

			// Remove the permission from the user
			Set<Permission> permissions = user.getPermissions();
			permissions.remove(p);
			em.get().merge(user);

			// Remove the user from the permission
			p.getUsers().remove(user);
			em.get().merge(p);
		} catch (NoResultException nre) {
			// If there is no such permission, there is nothing to do : the user has already not the permission.
		}
	}

	/**
	 * Get all permissions matching a permission type, for a specific user.
	 * 
	 * @param userId
	 *            The id of the user.
	 * @param resourceType
	 *            The type of the resource, for example "streams".
	 * @return
	 */
	public List<PermissionDTO> getTypeOfPermissionForOneUser(long userId, String resourceType) {
		User user = em.get().find(User.class, userId);
		Set<Permission> permissions = user.getPermissions();
		Set<Permission> filteredPermissions = this.filterPermissionByType(permissions, resourceType);
		List<PermissionDTO> list = new ArrayList<PermissionDTO>();
		for (Permission p : filteredPermissions) {
			list.add(new PermissionDTO(p.getName()));
		}
		return list;

	}

	/**
	 * Filter the permission by type
	 * @param collection all the permission
	 * @param type the filter
	 * @return a set of permission
	 */
	private Set<Permission> filterPermissionByType(Collection<Permission> collection, String type) {
		Set<Permission> filteredPermissions = new HashSet<Permission>();
		for (Permission p : collection) {
			if (p.getResourceType().equals(type)) {
				filteredPermissions.add(p);
			}
		}
		return filteredPermissions;
	}

	/**
	 * Create a permission with is right, type, id
	 * @param right
	 * @param resourceType
	 * @param resourceId
	 * @return the permission
	 */
	private String createPermissionString(String right, String resourceType, String resourceId) {
		return right + ":" + resourceType + ":" + resourceId;
	}

	/**
	 * Retrieves all users who have the given permission
	 * 
	 * @param right
	 *            Right needed on the permission
	 * @param resourceType
	 *            Type of the resource needed
	 * @param resourceId
	 *            Id of the resource needed
	 * @return A list of DTO corresponding to the users who have this permission
	 */
	public List<UserDTO> getUsersWithPermission(String right, String resourceType, Object resourceId) {
		String permission = this.createPermissionString(right, resourceType, resourceId.toString());

		Query permissionQuery = em.get().createQuery("SELECT p FROM Permission p WHERE name = :permission");
		permissionQuery.setParameter("permission", permission);
		List<UserDTO> usersDTO = new ArrayList<UserDTO>();
		try {
			Permission p = (Permission) permissionQuery.getSingleResult();
			Set<User> users = p.getUsers();

			UserDTO dto;
			for (User u : users) {
				dto = new UserDTO(u.getUsername(), u.getId());
				dto.userDetails = new UserDetailsDTO(u.getUserDetails());
				usersDTO.add(dto);
			}
			return usersDTO;
		} catch (NoResultException nre) {
			log.debug("No permission founded for {}", permission);
			return usersDTO;
		}

	}
}
