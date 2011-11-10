package org.kernely.core.service.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.dto.PermissionDTO;
import org.kernely.core.model.Permission;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class PermissionService {
	@Inject
	private Provider<EntityManager> em;

	/**
	 * Gets the lists of all permissions contained in the database.
	 * 
	 * @return the list of all permissions contained in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<PermissionDTO> getAllPermissions() {
		Query query = em.get().createQuery("SELECT e FROM PermissionModel e");
		List<Permission> collection = (List<Permission>) query.getResultList();
		List<PermissionDTO> dtos = new ArrayList<PermissionDTO>();
		for (Permission perm : collection) {
			dtos.add(new PermissionDTO(perm.getName()));
		}

		return dtos;

	}
}
