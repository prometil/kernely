package org.kernely.core.service.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.dto.RoleDTO;
import org.kernely.core.model.RoleModel;

import com.google.inject.Inject;

public class RoleService {
	@Inject
	private EntityManager em;
	
	/**
	 * Gets the lists of all roles contained in the database.
	 * @return the list of all roles contained in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<RoleDTO> getAllRoles() {
		em.getTransaction().begin();
		Query query = em.createQuery("SELECT e FROM RoleModel e");
		List<RoleModel> collection = (List<RoleModel>) query.getResultList();
		List<RoleDTO> dtos = new ArrayList<RoleDTO>();
		for (RoleModel role : collection) {
			dtos.add(new RoleDTO(role.getName()));
		}
		em.getTransaction().commit();
		return dtos;

	}
}
