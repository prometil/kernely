package org.kernely.user.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.hibernate.EntityManagerProvider;
import org.kernely.user.dto.RoleDTO;
import org.kernely.user.model.RoleModel;

import com.google.inject.Inject;

public class RoleService {
	@Inject
	private EntityManagerProvider entityManagerProvider;
	
	/**
	 * Gets the lists of all roles contained in the database.
	 * @return the list of all roles contained in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<RoleDTO> getAllRoles() {
		EntityManager em = entityManagerProvider.getEM();
		em.getTransaction().begin();
		Query query = em.createQuery("SELECT e FROM RoleModel e");
		List<RoleModel> collection = (List<RoleModel>) query.getResultList();
		List<RoleDTO> dtos = new ArrayList<RoleDTO>();
		for (RoleModel role : collection) {
			dtos.add(new RoleDTO(role.getName()));
		}
		em.getTransaction().commit();
		em.close();

		return dtos;

	}
}
