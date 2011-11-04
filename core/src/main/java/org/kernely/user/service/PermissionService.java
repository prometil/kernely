package org.kernely.user.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.hibernate.EntityManagerProvider;
import org.kernely.user.dto.PermissionDTO;
import org.kernely.user.model.PermissionModel;

import com.google.inject.Inject;

public class PermissionService {
	@Inject
	private EntityManagerProvider entityManagerProvider;
	
	/**
	 * Gets the lists of all permissions contained in the database.
	 * @return the list of all permissions contained in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<PermissionDTO> getAllPermissions() {
		EntityManager em = entityManagerProvider.getEM();
		em.getTransaction().begin();
		Query query = em.createQuery("SELECT e FROM PermissionModel e");
		List<PermissionModel> collection = (List<PermissionModel>) query.getResultList();
		List<PermissionDTO> dtos = new ArrayList<PermissionDTO>();
		for (PermissionModel perm : collection) {
			dtos.add(new PermissionDTO(perm.getName()));
		}
		em.getTransaction().commit();
		em.close();

		return dtos;

	}
}
