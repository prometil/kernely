package org.kernely.core.service.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.dto.RoleDTO;
import org.kernely.core.model.Role;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
public class RoleService {
	@Inject
	private Provider<EntityManager> em;

	/**
	 * Gets the lists of all roles contained in the database.
	 * 
	 * @return the list of all roles contained in the database.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<RoleDTO> getAllRoles() {
		Query query = em.get().createQuery("SELECT e FROM Role e WHERE name !='"+ Role.ROLE_USER +"'");
		List<Role> collection = (List<Role>) query.getResultList();
		List<RoleDTO> dtos = new ArrayList<RoleDTO>();
		for (Role role : collection) {
			dtos.add(new RoleDTO(role.getId(),role.getName()));
		}
		return dtos;

	}
	
	@Transactional
	/**
	 * Use for Unit tests
	 */
	public void createRole(RoleDTO request){
		Role role = new Role();
		role.setName(request.name.trim());
		em.get().persist(role);
	}
}
