package org.kernely.core.service.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.kernely.core.dto.PermissionDTO;
import org.kernely.core.model.Permission;
import org.kernely.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
public class PermissionService {

	private static final Logger log = LoggerFactory.getLogger(PermissionService.class);
	
	@Inject
	private Provider<EntityManager> em;

	

	/**
	 * Verify if the current user has a specific permission.
	 * @return true if the current user has the permission.
	 */
	public boolean currentUserHasPermission(String permission){
		try {
			SecurityUtils.getSubject().checkPermission(permission);
		} catch (AuthorizationException ae) {
			return false;
		}
		return true;
	}

	/**
	 * Verify if a specific user has a specific permission.
	 * @return true if the user has the permission.
	 */
	@Transactional
	public boolean userHasPermission(int id, String permission){
		Query query = em.get().createQuery("SELECT p FROM Permission p WHERE name ='"+ permission +"'");
		try {
			Permission p = (Permission) query.getSingleResult();

			for (User u : p.getUsers()){
				if (u.getId() == id){
					return true;
				}
			}
			return false;
		} catch (NoResultException nre) {
			return false;
		} catch (NonUniqueResultException nure){
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
	 * Grant a specific permission for a specific user.
	 */
	@Transactional
	public void grantPermission(int userId, String permission){
		// Verify if the permission already exists
		Query permissionQuery = em.get().createQuery("SELECT e FROM Permission e WHERE name='"+permission+"'");
		User user = em.get().find(User.class, (long) userId);
		log.debug("Grant permission {} to user id : {}",permission,userId);
		Permission p;
		try {
			p = (Permission) permissionQuery.getSingleResult();
		} catch (NoResultException nre){
			// If there is no permission, we create it
			p = new Permission();
			p.setName(permission);
			log.debug("Creation of the permission {}",permission);
			em.get().persist(p);
		}
		Set<Permission> userPermissions = user.getPermissions();
		if (userPermissions == null){
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
	 */
	@Transactional
	public void ungrantPermission(int userId, String permission){
		// verify if the permission already exists
		Query permissionQuery = em.get().createQuery("SELECT e FROM Permission e WHERE name='"+permission+"'");
		Permission p;
		User user = em.get().find(User.class, (long) userId);
		try {
			log.debug("Ungrant permission {} to user id : {}",permission,userId);
			p = (Permission) permissionQuery.getSingleResult();
			
			//Remove the permission from the user
			Set<Permission> permissions = user.getPermissions();
			permissions.remove(p);
			em.get().merge(user);

			//Remove the user from the permission
			p.getUsers().remove(user);
			em.get().merge(p);
		} catch (NoResultException nre){
			// If there is no such permission, there is nothing to do : the user has already not the permission.
		}
	}
}
