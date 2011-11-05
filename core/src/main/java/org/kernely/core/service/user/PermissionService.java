package org.kernely.core.service.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.dto.PermissionDTO;
import org.kernely.core.model.PermissionModel;

import com.google.inject.Inject;

public class PermissionService {
	@Inject
	private EntityManager em;

	/**
	 * Gets the lists of all permissions contained in the database.
	 * 
	 * @return the list of all permissions contained in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<PermissionDTO> getAllPermissions() {
		em.getTransaction().begin();
		Query query = em.createQuery("SELECT e FROM PermissionModel e");
		List<PermissionModel> collection = (List<PermissionModel>) query.getResultList();
		List<PermissionDTO> dtos = new ArrayList<PermissionDTO>();
		for (PermissionModel perm : collection) {
			dtos.add(new PermissionDTO(perm.getName()));
		}
		em.getTransaction().commit();

		return dtos;

	}
}
